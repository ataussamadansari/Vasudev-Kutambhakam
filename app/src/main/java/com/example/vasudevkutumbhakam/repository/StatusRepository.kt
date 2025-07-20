package com.example.vasudevkutumbhakam.repository

import com.example.vasudevkutumbhakam.model.ApplicationStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class StatusRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    fun setApplicationStatus(status: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val userId = getUserId()
        if (userId == null) {
            onError("User not logged in")
            return
        }

        val statusData = ApplicationStatus(status)
        firestore.collection("application_status")
            .document(userId)
            .set(statusData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Unknown error") }
    }


    fun getApplicationStatus(
        onSuccess: (ApplicationStatus?) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = getUserId()
        if (userId == null) {
            onError("User not logged in")
            return
        }

        firestore.collection("application_status")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val status = document.toObject(ApplicationStatus::class.java)
                onSuccess(status)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error fetching application status")
            }
    }

}