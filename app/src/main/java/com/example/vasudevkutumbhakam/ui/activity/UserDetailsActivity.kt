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
import androidx.lifecycle.ViewModelProvider
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityUserDetailsBinding
import com.example.vasudevkutumbhakam.model.UserDetails
import com.example.vasudevkutumbhakam.viewModel.UserDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding

    private lateinit var viewModel: UserDetailsViewModel

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

        //init
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

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

        viewModel.userName.observe(this) {
            if (it != null) binding.editTextName.setText(it)
        }

        viewModel.userDetails.observe(this) { details ->
            details?.let {
                binding.editTextName.setText(it.fullName)
                binding.editTextFatherName.setText(it.fatherName)
                binding.editTextDob.setText(it.dob)
                binding.editTextGender.setText(it.gender)
                binding.editTextAddress.setText(it.address)
                binding.editTextPincode.setText(it.pinCode)
                binding.editTextState.setText(it.state)
                binding.editTextDistict.setText(it.district)
            }
        }

        viewModel.uploadStatus.observe(this) {
            if (it) {
                Toast.makeText(this, "Step 1 saved successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, IncomeActivity::class.java))
                finish()
            }
        }

        viewModel.errorMessage.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadUserName()
        viewModel.loadUserDetails()

        binding.btnNext.setOnClickListener {
            saveUserDetails()
        }

    }

    private fun saveUserDetails() {
        val fullName = binding.editTextName.text.toString().trim()
        val fatherName = binding.editTextFatherName.text.toString().trim()
        val dob = binding.editTextDob.text.toString().trim()
        val gender = binding.editTextGender.text.toString().trim()
        val address = binding.editTextAddress.text.toString().trim()
        val pinCode = binding.editTextPincode.text.toString().trim()
        val state = binding.editTextState.text.toString().trim()
        val district = binding.editTextDistict.text.toString().trim()

        // Validation
        if (fullName.isEmpty()) {
            binding.editTextName.error = "Required"
            return
        }

        if (fatherName.isEmpty()) {
            binding.editTextFatherName.error = "Required"
            return
        }

        if (dob.isEmpty()) {
            binding.editTextDob.error = "Required"
            return
        }

        if (gender.isEmpty()) {
            binding.editTextGender.error = "Required"
            return
        }

        if (address.isEmpty()) {
            binding.editTextAddress.error = "Required"
            return
        }

        if (pinCode.isEmpty()) {
            binding.editTextPincode.error = "Required"
            return
        }

        if (state.isEmpty()) {
            binding.editTextState.error = "Required"
            return
        }

        if (district.isEmpty()) {
            binding.editTextDistict.error = "Required"
            return
        }

        // All fields valid — save details
        val details = UserDetails(
            fullName = fullName,
            fatherName = fatherName,
            dob = dob,
            gender = gender,
            address = address,
            pinCode = pinCode,
            state = state,
            district = district
        )

        viewModel.saveUserDetails(details)
    }

}