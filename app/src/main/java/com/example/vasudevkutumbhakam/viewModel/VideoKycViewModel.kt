package com.example.vasudevkutumbhakam.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vasudevkutumbhakam.repository.UserRepository

class VideoKycViewModel : ViewModel() {
    private val repository = UserRepository()

    val isLoading = MutableLiveData<Boolean>()
    val resultMessage = MutableLiveData<String?>()
    val inSuccess = MutableLiveData<Boolean>()


    fun saveVideo() {
        isLoading.value = true
        repository.markStep4Completed(
            onSuccess = {
                isLoading.value = false
                inSuccess.value = true
                resultMessage.value = "Video KYC and eligibility saved"
            },
            onError = {
                isLoading.value = false
                inSuccess.value = false
                resultMessage.value = "Video KYC saved, but eligibility update failed: $it"
            }
        )
    }
}