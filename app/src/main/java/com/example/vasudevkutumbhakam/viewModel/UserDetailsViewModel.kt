package com.example.vasudevkutumbhakam.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vasudevkutumbhakam.model.UserDetails
import com.example.vasudevkutumbhakam.repository.UserRepository

class UserDetailsViewModel: ViewModel() {
    private val repository = UserRepository()

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> = _uploadStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _userDetails = MutableLiveData<UserDetails?>()
    val userDetails: LiveData<UserDetails?> = _userDetails

    fun loadUserDetails() {
        repository.getUserDetails(
            onSuccess = { _userDetails.value = it },
            onError = { _errorMessage.value = it }
        )
    }

    fun loadUserName() {
        repository.getUserName(
            onResult = { name -> _userName.value = name },
            onError = { error -> _errorMessage.value = error }
        )
    }

    fun saveUserDetails(details: UserDetails) {
        repository.saveUserDetails(details,
            onSuccess = {
                repository.markStep1Completed(
                    onSuccess = { _uploadStatus.value = true },
                    onError = { err -> _errorMessage.value = err }
                )
            },
            onError = { err -> _errorMessage.value = err }
        )
    }
}