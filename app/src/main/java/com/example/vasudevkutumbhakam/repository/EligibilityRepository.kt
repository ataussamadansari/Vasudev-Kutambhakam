package com.example.vasudevkutumbhakam.repository

import com.example.vasudevkutumbhakam.model.EligibilitySteps
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EligibilityRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getUserId(): String? = auth.currentUser?.uid

    fun fetchEligibilitySteps(
        onResult: (EligibilitySteps) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = getUserId() ?: return

        firestore.collection("eligibility").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val steps = EligibilitySteps(
                        step1 = doc.getBoolean("step1_userDetails") ?: false,
                        step2 = doc.getBoolean("step2_incomeDetails") ?: false,
                        step3 = doc.getBoolean("step3_idProof") ?: false,
                        step4 = doc.getBoolean("step4_kyc") ?: false,
                        step5 = doc.getBoolean("step5_bankDetails") ?: false
                    )
                    onResult(steps)
                } else {
                    onResult(EligibilitySteps()) // default false
                }
            }
            .addOnFailureListener { onError(it.message ?: "Failed to fetch eligibility steps") }
    }

    fun initializeEligibility(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = getUserId() ?: return

        val defaultSteps = mapOf(
            "step1_userDetails" to false,
            "step2_incomeDetails" to false,
            "step3_idProof" to false,
            "step4_kyc" to false,
            "step5_bankDetails" to false,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("eligibility")
            .document(uid)
            .set(defaultSteps)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to initialize eligibility") }
    }
}