package com.example.parserapp.ui.factories


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parserapp.data.repository.ExitRepository
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.viewmodel.ExitViewModel

class ExitViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExitViewModel(
                application,
                exitRepository = ExitRepository(application.applicationContext),
                dataStoreManager = DataStoreManager(application.applicationContext)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}