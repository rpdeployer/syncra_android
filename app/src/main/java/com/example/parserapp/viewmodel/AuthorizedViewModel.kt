package com.example.parserapp.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parserapp.data.repository.UpdateRepository
import com.example.parserapp.data.store.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AuthorizedViewModel(
    val context: Context,
    dataStoreManager: DataStoreManager,
    private val updateRepository: UpdateRepository
): ViewModel() {

    private val _state = MutableStateFlow(
        AuthorizedState(
           false
        )
    )
    val state: StateFlow<AuthorizedState> = _state
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    val name: StateFlow<String?> = dataStoreManager.getName()
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _currentVersion = MutableStateFlow(getCurrentVersion(context))
    val currentVersion: StateFlow<String> = _currentVersion

    private val _isButtonEnabled = MutableStateFlow(true)
    val isButtonEnabled: StateFlow<Boolean> = _isButtonEnabled

    private val _remainingTime = MutableStateFlow(0L)
    val remainingTime: StateFlow<Long> = _remainingTime

    init {
        startButtonStateCheck(dataStoreManager)
    }

    fun handleIntent(intent: AuthorizedIntent) {
        when (intent) {
            is AuthorizedIntent.DownloadLogs -> {}
            is AuthorizedIntent.CheckUpdate -> {
                _isUpdating.value = true
                viewModelScope.launch {
                    val isUpdated = updateApp(intent.context)
                    _isUpdating.value = false
                    if (!isUpdated) {
                        Toast.makeText(intent.context, "Ошибка обновления", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun startButtonStateCheck(dataStoreManager: DataStoreManager) {
        viewModelScope.launch {
            while (true) {
                val lastTimestamp = runBlocking {
                    dataStoreManager.getLastClickTime().firstOrNull() ?: 0L
                }
                val currentTime = System.currentTimeMillis()
                val cooldown = 60_000L

                if (currentTime - lastTimestamp < cooldown) {
                    _isButtonEnabled.value = false
                    _remainingTime.value = (cooldown - (currentTime - lastTimestamp)) / 1000
                } else {
                    _isButtonEnabled.value = true
                    _remainingTime.value = 0L
                }

                delay(1_000)
            }
        }
    }

    fun onUploadLogsClick(dataStoreManager: DataStoreManager) {
        viewModelScope.launch {
            _isButtonEnabled.value = false
            dataStoreManager.saveLastClickTime(System.currentTimeMillis())
        }
    }

    private suspend fun updateApp(context: Context): Boolean {
        return try {
            updateRepository.checkForUpdate(context)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun checkUpdate(context: Context) {
        viewModelScope.launch {
            updateRepository.checkForUpdate(context)
        }
    }

    private fun getCurrentVersion(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName
        return versionName!!
    }

}

data class AuthorizedState(
    val isConnectedToServer: Boolean = false,
)

sealed class AuthorizedIntent {
    data object DownloadLogs : AuthorizedIntent()
    class CheckUpdate(val context: Context) : AuthorizedIntent()
}

