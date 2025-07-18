package com.example.vasudevkutumbhakam.utils

import com.google.firebase.firestore.FirebaseFirestore

object EligibilityUtil {

    fun fetchEligibilityStatus(
        userId: String,
        firestore: FirebaseFirestore,
        onResult: (EligibilitySteps) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("eligibility")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val steps = EligibilitySteps(
                        step1 = document.getBoolean("step1_userDetails") ?: false,
                        step2 = document.getBoolean("step2_incomeDetails") ?: false,
                        step3 = document.getBoolean("step3_idProof") ?: false,
                        step4 = document.getBoolean("step4_kyc") ?: false,
                        step5 = document.getBoolean("step5_bankDetails") ?: false
                    )
                    onResult(steps)
                } else {
                    onError("Eligibility not initialized.")
                }
            }
            .addOnFailureListener {
                onError("Fetch failed: ${it.message}")
            }
    }

    data class EligibilitySteps(
        val step1: Boolean,
        val step2: Boolean,
        val step3: Boolean,
        val step4: Boolean,
        val step5: Boolean
    )
}
