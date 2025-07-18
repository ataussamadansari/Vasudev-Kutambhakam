package com.example.vasudevkutumbhakam.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vasudevkutumbhakam.databinding.ActivityCheckEligibilityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckEligibilityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckEligibilityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckEligibilityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(16, systemBars.top, 16, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.appCompatButton.setOnClickListener {
            checkWhichStepToResume()
        }

    }

    private fun checkWhichStepToResume() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("eligibility")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    when {
                        document.getBoolean("step1_userDetails") == false -> {
                            startActivity(Intent(this, UserDetailsActivity::class.java))
                        }
                        document.getBoolean("step2_incomeDetails") == false -> {
                            startActivity(Intent(this, IncomeActivity::class.java))
                        }
                        document.getBoolean("step3_idProof") == false -> {
                            startActivity(Intent(this, IdProofActivity::class.java))
                        }
                        document.getBoolean("step4_kyc") == false -> {
                            startActivity(Intent(this, KycActivity::class.java))
                        }
                        document.getBoolean("step5_bankDetails") == false -> {
                            startActivity(Intent(this, BankDetailsActivity::class.java))
                        }
                        else -> {
                            Toast.makeText(this, "All steps completed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // First time user
                    initializeEligibilitySteps()
                    startActivity(Intent(this, UserDetailsActivity::class.java))
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to check steps: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initializeEligibilitySteps() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Set all eligibility steps to false initially
        val eligibilityData = hashMapOf(
            "step1_userDetails" to false,
            "step2_incomeDetails" to false,
            "step3_idProof" to false,
            "step4_kyc" to false,
            "step5_bankDetails" to false,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("eligibility")
            .document(userId)
            .set(eligibilityData)
            .addOnSuccessListener {
                Toast.makeText(this, "Eligibility steps initialized", Toast.LENGTH_SHORT).show()

                // Navigate to step 1 activity (replace with your actual activity class)
                val intent = Intent(this, UserDetailsActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Initialization failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}