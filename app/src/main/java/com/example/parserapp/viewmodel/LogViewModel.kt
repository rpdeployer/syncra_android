package com.example.parserapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.parserapp.data.entities.LogMessage
import com.example.parserapp.di.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogViewModel(application: Application) : AndroidViewModel(application) {

    private val logDao = AppDatabase.getDatabase(application).logMessageDao()

    private val _logs = MutableStateFlow<List<LogMessage>>(emptyList())
    val logs: StateFlow<List<LogMessage>> = _logs

    init {
        viewModelScope.launch {
            logDao.getLogMessages().collect { logList ->
                _logs.value = logList
            }
        }
    }

    fun addLog(message: String, isSuccess: Boolean) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())

            val log = LogMessage(
                message = message,
                dateTime = timestamp,
                isSuccess = isSuccess
            )
            logDao.insertLogMessage(log)
        }
    }

}