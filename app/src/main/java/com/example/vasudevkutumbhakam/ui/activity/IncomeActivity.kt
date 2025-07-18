package com.example.vasudevkutumbhakam.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityIncomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class IncomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomeBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(16, systemBars.top, 16, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.switchFarmer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchSalaried.isChecked = false
                binding.switchBusiness.isChecked = false
            }
        }

        binding.switchSalaried.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchFarmer.isChecked = false
                binding.switchBusiness.isChecked = false
            }
        }

        binding.switchBusiness.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchFarmer.isChecked = false
                binding.switchSalaried.isChecked = false
            }
        }

        val selectedOccupation = when {
            binding.switchFarmer.isChecked -> "Farmer"
            binding.switchSalaried.isChecked -> "Salaried"
            binding.switchBusiness.isChecked -> "Business"
            else -> ""
        }

        // Initially disable the Next button
        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f // optional: to show it's disabled

        binding.checkBoxAccept.setOnCheckedChangeListener { _, isChecked ->
            binding.btnNext.isEnabled = isChecked
            binding.btnNext.alpha = if (isChecked) 1.0f else 0.5f
        }

        binding.btnNext.setOnClickListener {
            if (!binding.checkBoxAccept.isChecked) {
                Toast.makeText(this, "Please accept the declaration first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveIncomeDetails() // your existing function
        }

    }

    private fun saveIncomeDetails() {
        val userId = auth.currentUser?.uid ?: return

        // Check selected occupation
        val occupation = when {
            binding.switchFarmer.isChecked -> "Farmer"
            binding.switchSalaried.isChecked -> "Salaried"
            binding.switchBusiness.isChecked -> "Business"
            else -> ""
        }

        val income = binding.editTextIncome.text.toString()

        // Validation (optional)
        if (occupation.isEmpty() || income.isEmpty()) {
            Toast.makeText(this, "Please select occupation and enter income", Toast.LENGTH_SHORT).show()
            return
        }

        val incomeDetails = mapOf(
            "occupation" to occupation,
            "monthly_income" to income
        )

        // Save to user details collection
        firestore.collection("vasudev_user_details")
            .document(userId)
            .update(incomeDetails)
            .addOnSuccessListener {
                markStep2Completed(userId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save income details: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markStep2Completed(userId: String) {
        firestore.collection("eligibility")
            .document(userId)
            .update("step2_incomeDetails", true)
            .addOnSuccessListener {
                Toast.makeText(this, "Step 2 saved successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, IdProofActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update step 2 status", Toast.LENGTH_SHORT).show()
            }
    }

}