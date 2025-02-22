package com.example.parserapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SmsQueueViewModel(context: Context) : ViewModel() {

    private val workManager = WorkManager.getInstance(context)
    private val _queueSize = MutableStateFlow(0)
    val queueSize: StateFlow<Int> = _queueSize

    fun updateQueueSize() {
        viewModelScope.launch {
            workManager.getWorkInfosByTagLiveData("SMS_SEND_TO_SERVER_JOB")
                .observeForever { workInfos ->
                    viewModelScope.launch {
                        _queueSize.value = workInfos.count { it.state == WorkInfo.State.RUNNING }
                    }
                }
        }
    }
}