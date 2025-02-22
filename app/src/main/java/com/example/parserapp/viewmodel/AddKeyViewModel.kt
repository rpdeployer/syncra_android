package com.example.parserapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddKeyViewModel: ViewModel() {

    private val _state = MutableStateFlow(AddKeyState())
    val state: StateFlow<AddKeyState> = _state

    fun handleIntent(intent: AddKeyIntent) {
        when (intent) {
            is AddKeyIntent.ScanQRCode -> {
                _state.value = _state.value.copy(qrCodeResult = intent.result)
            }
        }
    }

}

data class AddKeyState(
    val qrCodeResult: String? = null,
)

sealed class AddKeyIntent {
    data class ScanQRCode(val result: String) : AddKeyIntent()
}
