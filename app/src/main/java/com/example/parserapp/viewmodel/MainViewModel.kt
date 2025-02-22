package com.example.parserapp.viewmodel

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parserapp.data.repository.AuthRepository
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.di.PermissionManager
import com.example.parserapp.di.SmsReceiverManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel (
    val permissionManager: PermissionManager,
    private val authRepository: AuthRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(
        PermissionState(
            permissionManager.isPhonePermissionGranted(),
            permissionManager.isSmsPermissionGranted(),
            permissionManager.isPushPermissionGranted(),
            permissionManager.isNetworkPermissionGranted(),
            permissionManager.isIgnoringBatteryOptimizations(),
            permissionManager.isCameraPermissionGranted()
        )
    )
    val state: StateFlow<PermissionState> = _state

    val apiKey: StateFlow<String?> = dataStoreManager.getKey()
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun initializePermissionLaunchers(
        phoneLauncher: ActivityResultLauncher<String>,
        smsLauncher: ActivityResultLauncher<String>,
        cameraLauncher: ActivityResultLauncher<String>,
        networkLauncher: ActivityResultLauncher<String>,
        pushLauncher: ActivityResultLauncher<Intent>,
        batteryLauncher: ActivityResultLauncher<Intent>
    ) {
        permissionManager.initializePermissionLaunchers(
            phoneLauncher,
            smsLauncher,
            cameraLauncher,
            networkLauncher,
            pushLauncher,
            batteryLauncher
        )
    }

    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.RequestSmsPermission -> requestSmsPermission()
            is MainIntent.RequestNetworkPermission -> requestNetworkPermission()
            is MainIntent.RequestBatteryOptimization -> requestBatteryOptimization()
            is MainIntent.RequestCameraPermission -> requestCameraPermission()
            is MainIntent.RequestPushPermission -> requestPushPermission()
            is MainIntent.RequestPhonePermission -> requestPhonePermission()
            is MainIntent.EnterApiKey -> validateApiKeyFormat(intent.key)
            is MainIntent.ValidateKey -> validateApiKey(intent.context)
        }
    }

    private fun requestSmsPermission() {
        permissionManager.requestSmsPermission()
    }

    private fun requestPhonePermission() {
        permissionManager.requestPhonePermission()
    }

    private fun requestPushPermission() {
        permissionManager.requestPushPermission()
    }

    private fun requestNetworkPermission() {
        permissionManager.requestNetworkPermission()
    }

    private fun requestBatteryOptimization() {
        permissionManager.requestBatteryOptimizationDisabling()
    }

    private fun requestCameraPermission() {
        permissionManager.requestCameraPermission()
    }

    fun updateSmsPermissionGranted(isGranted: Boolean) {
        _state.value = _state.value.copy(isSmsPermissionGranted = isGranted)
    }

    fun updatePhonePermissionGranted(isGranted: Boolean) {
        _state.value = _state.value.copy(isPhonePermissionGranted = isGranted)
    }

    fun updateCameraPermissionGranted(isGranted: Boolean) {
        _state.value = _state.value.copy(isCameraPermissionGranted = isGranted)
    }

    fun updateNetworkPermissionGranted(isGranted: Boolean) {
        _state.value = _state.value.copy(isNetworkPermissionGranted = isGranted)
    }

    fun updatePushPermissionGranted(isGranted: Boolean) {
        _state.value = _state.value.copy(isPushPermissionGranted = isGranted)
    }

    fun updateBatteryOptimizationIgnored(isGranted: Boolean) {
        _state.value = _state.value.copy(isBatteryOptimizationIgnored = isGranted)
    }

    private fun validateApiKeyFormat(apiKey: String) {
        val isValid = apiKey.matches(
            Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
        )
        _state.value = _state.value.copy(apiKey = TextFieldValue(text = apiKey, selection = TextRange(apiKey.length)), isApiKeyFormatValid = isValid)
    }

    private fun validateApiKey(context: Context) {
        _state.value = _state.value.copy(isValidating = true)

        viewModelScope.launch {
            val resp = authRepository.validateKey(_state.value.apiKey.text)
            if (resp.second) {
                saveKey(_state.value.apiKey.text)
                saveName(resp.first)
                SmsReceiverManager.registerReceiver(context)
                _state.value = _state.value.copy(apiKey = TextFieldValue(text = "", selection = TextRange(0)))
            }
            _state.value = _state.value.copy(
                isValidating = false,
                validationSuccess = resp.second
            )
        }
    }

    fun saveKey(key: String) {
        viewModelScope.launch {
            dataStoreManager.saveKey(key)
        }
    }

    fun saveName(name: String) {
        viewModelScope.launch {
            dataStoreManager.saveName(name)
        }
    }

    fun resetCursorToStart() {
        _state.value = _state.value.copy(apiKey = TextFieldValue(text = _state.value.apiKey.text, selection = TextRange(0)))
    }

}

data class PermissionState(
    val isPhonePermissionGranted: Boolean = false,
    val isSmsPermissionGranted: Boolean = false,
    val isPushPermissionGranted: Boolean = false,
    val isNetworkPermissionGranted: Boolean = false,
    val isBatteryOptimizationIgnored: Boolean = false,
    val isCameraPermissionGranted: Boolean = false,
    val isValidating: Boolean = false,
    val isApiKeyFormatValid: Boolean = false,
    val apiKey: TextFieldValue = TextFieldValue(""),
    val qrCodeResult: String? = null,
    val validationSuccess: Boolean? = null
)

sealed class MainIntent {
    data object RequestSmsPermission : MainIntent()
    data object RequestPushPermission : MainIntent()
    data object RequestPhonePermission : MainIntent()
    data object RequestBatteryOptimization : MainIntent()
    data object RequestNetworkPermission : MainIntent()
    data object RequestCameraPermission : MainIntent()
    data class EnterApiKey(val key: String) : MainIntent()
    data class ValidateKey(val context: Context) : MainIntent()
}

