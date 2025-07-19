package com.example.vasudevkutumbhakam.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vasudevkutumbhakam.model.IncomeDetails
import com.example.vasudevkutumbhakam.repository.UserRepository

class IncomeDetailsViewModel : ViewModel() {

    private val repository = UserRepository()

    val isLoading = MutableLiveData<Boolean>()
    val resultMessage = MutableLiveData<String?>()
    val inSuccess = MutableLiveData<Boolean>()

    val incomeDetails = MutableLiveData<IncomeDetails?>()

    fun submitIncomeDetails(occupation: String, income: String, isAccepted: Boolean) {
        isLoading.value = true

        val details = IncomeDetails(occupation, income, isAccepted)
        repository.saveIncomeDetails(details) { success, message ->
            if (success) {
                // 🔧 Also update eligibility Step 2
                repository.markStep2Completed(
                    onSuccess = {
                        isLoading.value = false
                        inSuccess.value = true
                        resultMessage.value = "Income and eligibility saved"
                    },
                    onError = {
                        isLoading.value = false
                        inSuccess.value = false
                        resultMessage.value = "Income saved, but eligibility update failed: $it"
                    }
                )
            } else {
                isLoading.value = false
                inSuccess.value = false
                resultMessage.value = message
            }
        }
    }

    fun getIncomeDetails() {
        isLoading.value = true
        repository.getIncomeDetails(
            onSuccess = { details ->
                isLoading.value = false
                incomeDetails.value = details
            },
            onError = { error ->
                isLoading.value = false
                resultMessage.value = error
            }
        )
    }

}