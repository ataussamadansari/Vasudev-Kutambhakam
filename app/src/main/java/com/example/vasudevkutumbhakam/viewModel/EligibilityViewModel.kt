package com.example.vasudevkutumbhakam.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vasudevkutumbhakam.model.EligibilitySteps
import com.example.vasudevkutumbhakam.repository.EligibilityRepository

class EligibilityViewModel : ViewModel() {

    private val repository = EligibilityRepository()

    private val _steps = MutableLiveData<EligibilitySteps>()
    val steps: LiveData<EligibilitySteps> = _steps

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _initialized = MutableLiveData<Boolean>()
    val initialized: LiveData<Boolean> = _initialized

    fun fetchSteps() {
        repository.fetchEligibilitySteps(
            onResult = {
                _steps.value = it
            },
            onError = {
                _error.value = it
            }
        )
    }

    fun initializeSteps() {
        repository.initializeEligibility(
            onSuccess = {
                _initialized.value = true
            },
            onError = {
                _error.value = it
            }
        )
    }
}