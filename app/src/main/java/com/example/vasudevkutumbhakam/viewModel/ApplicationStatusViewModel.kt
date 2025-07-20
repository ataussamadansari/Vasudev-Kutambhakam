package com.example.vasudevkutumbhakam.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vasudevkutumbhakam.model.ApplicationStatus
import com.example.vasudevkutumbhakam.repository.StatusRepository

class ApplicationStatusViewModel : ViewModel() {

    private val repository = StatusRepository()

    val statusLiveData = MutableLiveData<ApplicationStatus?>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()


    fun submitStatusPending() {
        isLoading.value = true
        repository.setApplicationStatus(
            status = "Pending",
            onSuccess = {
                isLoading.value = false
                statusLiveData.value = ApplicationStatus("Pending")
            },
            onError = {
                isLoading.value = false
                errorMessage.value = it
            }
        )
    }

    fun fetchApplicationStatus() {
        isLoading.value = true
        repository.getApplicationStatus(
            onSuccess = {
                isLoading.value = false
                statusLiveData.value = it
            },
            onError = {
                isLoading.value = false
                errorMessage.value = it
            }
        )
    }
}
