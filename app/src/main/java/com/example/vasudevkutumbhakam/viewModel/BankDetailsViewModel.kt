package com.example.vasudevkutumbhakam.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vasudevkutumbhakam.model.BankDetails
import com.example.vasudevkutumbhakam.repository.UserRepository

class BankDetailsViewModel : ViewModel() {

    val repository = UserRepository()

    val isLoading = MutableLiveData<Boolean>()
    val resultMessage = MutableLiveData<String?>()
    val inSuccess = MutableLiveData<Boolean>()

    val bankDetails = MutableLiveData<BankDetails?>()


    fun saveBankDetails(bankDetails: BankDetails) {
        isLoading.value = true

        repository.saveBankDetails(bankDetails) { success, message ->

            if (success) {
                // 🔧 Also update eligibility Step 2
                repository.markStep5Completed(
                    onSuccess = {
                        isLoading.value = false
                        inSuccess.value = true
                        resultMessage.value = "Bank Details and eligibility saved"
                    },
                    onError = {
                        isLoading.value = false
                        inSuccess.value = false
                        resultMessage.value =
                            "Bank Details saved, but eligibility update failed: $it"
                    }
                )
            } else {
                isLoading.value = false
                inSuccess.value = false
                resultMessage.value = message
            }
        }
    }

    fun getBankDetails() {
        isLoading.value = true
        repository.getBankDetails(
            onSuccess = { bankDetail ->
                isLoading.value = false
                this.bankDetails.value = bankDetail
            },
            onError = { error ->
                isLoading.value = false
                resultMessage.value = error
            }
        )
    }

}