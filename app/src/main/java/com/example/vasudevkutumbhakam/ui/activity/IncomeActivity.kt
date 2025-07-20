package com.example.vasudevkutumbhakam.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityIncomeBinding
import com.example.vasudevkutumbhakam.viewModel.IncomeDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class IncomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomeBinding

    private lateinit var viewModel: IncomeDetailsViewModel


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

        viewModel = ViewModelProvider(this)[IncomeDetailsViewModel::class.java]

        observeViewModel()

        viewModel.getIncomeDetails()

        viewModel.incomeDetails.observe(this) { details ->
            details?.let {
                binding.editTextIncome.setText(it.income)

                when (it.occupation) {
                    "Farmer" -> binding.switchFarmer.isChecked = true
                    "Salaried" -> binding.switchSalaried.isChecked = true
                    "Business" -> binding.switchBusiness.isChecked = true
                }

                binding.checkBoxAccept.isChecked = it.isAccepted
                binding.btnNext.isEnabled = it.isAccepted
                binding.btnNext.alpha = if (it.isAccepted) 1f else 0.5f
            }
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

            saveIncomeDetails()
        }

    }

    private fun saveIncomeDetails() {
        val occupation = when {
            binding.switchFarmer.isChecked -> "Farmer"
            binding.switchSalaried.isChecked -> "Salaried"
            binding.switchBusiness.isChecked -> "Business"
            else -> ""
        }

        val income = binding.editTextIncome.text.toString()
        val isAccepted = binding.checkBoxAccept.isChecked

        if (occupation.isEmpty() || income.isEmpty()) {
            Toast.makeText(this, "Please select occupation and enter income", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.submitIncomeDetails(occupation, income, isAccepted)
    }


    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.inSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Step 2 saved successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, IdProofActivity::class.java))
                finish()
            }
        }

        viewModel.resultMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }


}