package com.example.vasudevkutumbhakam.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vasudevkutumbhakam.model.Amount
import com.example.vasudevkutumbhakam.repository.UserRepository

class AmountViewModel: ViewModel() {
    private val repository = UserRepository()

    val resultMessage = MutableLiveData<String?>()
    val inSuccess = MutableLiveData<Boolean>()
    val amountLiveData = MutableLiveData<Amount>()


    fun addBarrowAmount(amount: Amount) {
        repository.addBarrowAmount(amount) { success, message ->
            resultMessage.value = if (success) "Amount saved successfully" else message
        }
    }

    fun fetchAmount() {
        repository.getAmount(onSuccess = {
            amountLiveData.value = it
        }, onError = {
            resultMessage.value = it
        })
    }
}