package com.example.vasudevkutumbhakam.ui.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityUserDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(16, systemBars.top, 16, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.editTextGender.setOnClickListener {
            binding.editTextGender.showDropDown()
        }

        val genderOptions = listOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        (binding.editTextGender as AutoCompleteTextView).setAdapter(adapter)

        binding.editTextDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate =
                    "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
                binding.editTextDob.setText(formattedDate)
            }, year, month, day)

            datePicker.show()
        }


        loadUserName()

        binding.btnNext.setOnClickListener {
            saveUserDetails()
        }
    }

    private fun loadUserName() {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        firestore.collection("vasudev_user").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    binding.editTextName.setText(name)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch name", Toast.LENGTH_SHORT).show()
            }
    }



    private fun saveUserDetails() {
        val userId = auth.currentUser?.uid ?: return

        val userDetails = mapOf(
            "name" to binding.editTextName.text.toString(),
            "fatherName" to binding.editTextFatherName.text.toString(),
            "dob" to binding.editTextDob.text.toString(),
            "gender" to binding.editTextGender.text.toString(),
            "address" to binding.editTextAddress.text.toString(),
            "pincode" to binding.editTextPincode.text.toString(),
            "state" to binding.editTextState.text.toString(),
            "district" to binding.editTextDistict.text.toString()
        )

        firestore.collection("vasudev_user_details")
            .document(userId)
            .set(userDetails)
            .addOnSuccessListener {
                markStep1Completed(userId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user details: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markStep1Completed(userId: String) {
        firestore.collection("eligibility")
            .document(userId)
            .update("step1_userDetails", true)
            .addOnSuccessListener {
                Toast.makeText(this, "Step 1 saved successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, IncomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update step 1 status", Toast.LENGTH_SHORT).show()
            }
    }
}