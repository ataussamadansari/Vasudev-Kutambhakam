package com.example.vasudevkutumbhakam.repository

import android.net.Uri
import com.example.vasudevkutumbhakam.model.BankDetails
import com.example.vasudevkutumbhakam.model.IdProof
import com.example.vasudevkutumbhakam.model.IncomeDetails
import com.example.vasudevkutumbhakam.model.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid


    // step 1
    fun getUserName(onResult: (String?) -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return

        firestore.collection("vasudev_user").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.getString("name"))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Unknown error")
            }
    }

    fun getUserDetails(onSuccess: (UserDetails?) -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return

        firestore.collection("vasudev_user_details")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val details = UserDetails(
                        fullName = document.getString("name") ?: "",
                        fatherName = document.getString("fatherName") ?: "",
                        dob = document.getString("dob") ?: "",
                        gender = document.getString("gender") ?: "",
                        address = document.getString("address") ?: "",
                        pinCode = document.getString("pinCode") ?: "",
                        state = document.getString("state") ?: "",
                        district = document.getString("district") ?: ""
                    )
                    onSuccess(details)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to fetch details")
            }
    }

    fun saveUserDetails(details: UserDetails, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return
        val userMap = mapOf(
            "name" to details.fullName,
            "fatherName" to details.fatherName,
            "dob" to details.dob,
            "gender" to details.gender,
            "address" to details.address,
            "pinCode" to details.pinCode,
            "state" to details.state,
            "district" to details.district
        )

        firestore.collection("vasudev_user_details")
            .document(uid)
            .set(userMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error saving details") }
    }

    // step 2 income details
    fun saveIncomeDetails(incomeDetails: IncomeDetails, onResult: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }

        val data = mapOf(
            "occupation" to incomeDetails.occupation,
            "monthly_income" to incomeDetails.income,
            "income_accepted" to incomeDetails.isAccepted
        )

        firestore.collection("vasudev_user_details")
            .document(userId)
            .update(data)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }


    fun getIncomeDetails(onSuccess: (IncomeDetails?) -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return

        firestore.collection("vasudev_user_details")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val details = IncomeDetails(
                        occupation = doc.getString("occupation") ?: "",
                        income = doc.getString("monthly_income") ?: "",
                        isAccepted = doc.getBoolean("income_accepted") ?: false
                    )
                    onSuccess(details)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                onError("Failed to fetch income details: ${e.message}")
            }
    }

    // step 3
    fun saveIdProof(idProof: IdProof, onResult: (Boolean, String?) -> Unit) {
        val uid = getCurrentUserId() ?: return

        val data = mapOf(
            "id_type" to idProof.idType,
            "id_number" to idProof.idNumber,
            "front_image" to idProof.frontImage,
            "back_image" to idProof.backImage,
            "pan_image" to idProof.panImage,
            "id_accepted" to idProof.idAccepted
        )

        firestore.collection("vasudev_user_details")
            .document(uid)
            .update(data)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun uploadIdImage(type: String, uri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return onError("User not logged in")
        val ref = FirebaseStorage.getInstance().reference.child("id_proofs/$uid/$type.jpg")

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onSuccess(downloadUrl.toString())
                }.addOnFailureListener {
                    onError("Failed to get download URL")
                }
            }
            .addOnFailureListener {
                onError("Upload failed: ${it.message}")
            }
    }


    fun getIdProof(onSuccess: (IdProof?) -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return
        firestore.collection("vasudev_user_details")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val idProof = IdProof(
                    idType = doc.getString("id_type") ?: "",
                    idNumber = doc.getString("id_number") ?: "",
                    frontImage = doc.getString("front_image") ?: "",
                    backImage = doc.getString("back_image") ?: "",
                    panImage = doc.getString("pan_image") ?: "",
                    idAccepted = doc.getBoolean("id_accepted") ?: false
                )
                onSuccess(idProof)
            }
            .addOnFailureListener { e ->
                onError("Failed to fetch ID proof: ${e.message}")
            }
    }

    // step 5
    fun saveBankDetails(bankDetails: BankDetails, onResult: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }

        val data = mapOf(
            "holderName" to bankDetails.holderName,
            "bankName" to bankDetails.bankName,
            "accountNumber" to bankDetails.accountNumber,
            "ifscCode" to bankDetails.ifscCode
        )

        firestore.collection("vasudev_user_details")
            .document(userId)
            .update(data)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }


    fun getBankDetails(onSuccess: (BankDetails) -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return
        firestore.collection("vasudev_user_details")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val bankDetails = BankDetails(
                    holderName = doc.getString("holderName") ?: "",
                    bankName = doc.getString("bankName") ?: "",
                    accountNumber = doc.getString("accountNumber") ?: "",
                    ifscCode = doc.getString("ifscCode") ?: ""
                )
                onSuccess(bankDetails)
            }
            .addOnFailureListener { e ->
                onError("Failed to fetch ID proof: ${e.message}")
            }
    }


    fun markStep1Completed(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return
        firestore.collection("eligibility")
            .document(uid)
            .update("step1_userDetails", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error updating step1") }
    }

    fun markStep2Completed(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return
        firestore.collection("eligibility")
            .document(uid)
            .update("step2_incomeDetails", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error updating step2") }
    }

    fun markStep3Completed(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return
        firestore.collection("eligibility")
            .document(uid)
            .update("step3_idProof", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error updating step3") }
    }

    fun markStep4Completed(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return
        firestore.collection("eligibility")
            .document(uid)
            .update("step4_kyc", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error updating step4") }
    }

    fun markStep5Completed(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = getCurrentUserId() ?: return
        firestore.collection("eligibility")
            .document(uid)
            .update("step5_bankDetails", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error updating step5") }
    }
}