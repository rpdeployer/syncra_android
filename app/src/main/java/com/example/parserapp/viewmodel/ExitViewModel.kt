package com.example.parserapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.parserapp.data.repository.ExitRepository
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.di.AppDatabase
import com.example.parserapp.di.SmsReceiverManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExitViewModel(
    application: Application,
    private val exitRepository: ExitRepository,
    private val dataStoreManager: DataStoreManager,
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(ExitState())
    val state: StateFlow<ExitState> = _state

    private val logDao = AppDatabase.getDatabase(application).logMessageDao()

    fun handleIntent(intent: ExitIntent) {
        when (intent) {
            is ExitIntent.Exit -> exit(intent.apiKey, intent.context)
        }
    }

    private fun exit(apiKey: String, context: Context) {
        _state.value = _state.value.copy(isValidating = true)

        viewModelScope.launch {
            val isExited = exitRepository.exit(apiKey)
            _state.value = _state.value.copy(
                isValidating = false
            )
            if (isExited) {
                dataStoreManager.saveKey("")
                dataStoreManager.saveName("")
                WorkManager.getInstance(context)
                    .cancelAllWorkByTag("SMS_SEND_TO_SERVER_JOB")
                WorkManager.getInstance(context)
                    .cancelAllWorkByTag("STATUS_SEND_TO_SERVER_JOB")
                logDao.clearLogs()
                SmsReceiverManager.unregisterReceiver(context)
            } else {
            }
        }
    }

}

data class ExitState(
    val isValidating: Boolean = false,
)

sealed class ExitIntent {
    data class Exit(val apiKey: String, val context: Context) : ExitIntent()
}