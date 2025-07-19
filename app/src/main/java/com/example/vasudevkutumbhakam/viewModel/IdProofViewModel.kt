package com.example.vasudevkutumbhakam.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vasudevkutumbhakam.model.IdProof
import com.example.vasudevkutumbhakam.repository.UserRepository

class IdProofViewModel: ViewModel() {

    private val repository = UserRepository()

    val isLoading = MutableLiveData<Boolean>()
    val resultMessage = MutableLiveData<String?>()
    val inSuccess = MutableLiveData<Boolean>()

    val idProof = MutableLiveData<IdProof?>()

    val imageUploadStatus = MutableLiveData<Pair<String, String>>()  // (type, url)
    val uploadError = MutableLiveData<String>()


    fun submitIdProof(idProof: IdProof) {
        isLoading.value = true

        repository.saveIdProof(idProof) { success, message ->
            if (success) {
                // 🔧 Also update eligibility Step 2
                repository.markStep3Completed(
                    onSuccess = {
                        isLoading.value = false
                        inSuccess.value = true
                        resultMessage.value = "IdProof and eligibility saved"
                    },
                    onError = {
                        isLoading.value = false
                        inSuccess.value = false
                        resultMessage.value = "IdProof saved, but eligibility update failed: $it"
                    }
                )
            }  else {
                isLoading.value = false
                inSuccess.value = false
                resultMessage.value = message
            }
        }
    }


    fun uploadImage(type: String, uri: Uri) {
        isLoading.value = true
        repository.uploadIdImage(type, uri,
            onSuccess = { url ->
                imageUploadStatus.value = Pair(type, url)
                isLoading.value = false
            },
            onError = { error ->
                uploadError.value = error
                isLoading.value = false
            }
        )
    }

    fun getIdProof() {
        isLoading.value = true
        repository.getIdProof(
            onSuccess = {idProof ->
                isLoading.value = false
                this.idProof.value = idProof
            },
            onError = { error ->
                isLoading.value = false
                resultMessage.value = error
            }
        )

    }
}