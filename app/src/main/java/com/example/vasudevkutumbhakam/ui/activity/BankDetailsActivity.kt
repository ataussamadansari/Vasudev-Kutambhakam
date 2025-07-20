package com.example.vasudevkutumbhakam.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityBankDetailsBinding
import com.example.vasudevkutumbhakam.viewModel.ApplicationStatusViewModel
import com.example.vasudevkutumbhakam.viewModel.BankDetailsViewModel
import com.example.vasudevkutumbhakam.viewModel.UserDetailsViewModel

class BankDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBankDetailsBinding

    private lateinit var viewModel: BankDetailsViewModel
    private lateinit var userViewModel: UserDetailsViewModel

    val bankList = listOf(
        "State Bank of India",
        "HDFC Bank",
        "ICICI Bank",
        "Axis Bank",
        "Punjab National Bank",
        "Canara Bank",
        "Bank of Baroda",
        "Union Bank of India",
        "Kotak Mahindra Bank",
        "IDFC FIRST Bank",
        "Yes Bank",
        "IndusInd Bank",
        "Bank of India",
        "Central Bank of India"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(16, systemBars.top, 16, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(this)[BankDetailsViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        observeViewModel()

        viewModel.getBankDetails()

        userViewModel.userName.observe(this) {
            if (it != null) binding.etAccountHolderName.setText(it)
        }

        viewModel.bankDetails.observe(this) {
            it?.let {
                binding.etAccountHolderName.setText(it.holderName)
                binding.etBankName.setText(it.bankName)
                binding.etAccountNumber.setText(it.accountNumber)
                binding.etConfirmAccountNumber.setText(it.accountNumber)
                binding.etIfscCode.setText(it.ifscCode)
            }
        }

        userViewModel.loadUserName()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bankList)
        binding.etBankName.setAdapter(adapter)
        binding.etBankName.threshold = 1


        binding.btnSubmit.setOnClickListener {
            saveBankDetails()
        }

    }

    private fun saveBankDetails() {
        val holderName = binding.etAccountHolderName.text.toString().trim()
        val bankName = binding.etBankName.text.toString().trim()
        val accountNumber = binding.etAccountNumber.text.toString().trim()
        val confirmAccountNumber = binding.etConfirmAccountNumber.text.toString().trim()
        val ifscCode = binding.etIfscCode.text.toString().trim()

        //  validation
        if (holderName.isEmpty()) {
            binding.etAccountHolderName.error = "Enter account holder name"
            return
        }

        if (bankName.isEmpty()) {
            binding.etBankName.error = "Select your bank"
            return
        }

        if (accountNumber.isEmpty()) {
            binding.etAccountNumber.error = "Enter account number"
            return
        }

        if (confirmAccountNumber.isEmpty()) {
            binding.etConfirmAccountNumber.error = "Confirm your account number"
            return
        }

        if (accountNumber != confirmAccountNumber) {
            binding.etConfirmAccountNumber.error = "Account numbers do not match"
            return
        }

        if (ifscCode.isEmpty()) {
            binding.etIfscCode.error = "Enter IFSC code"
            return
        }

        // Create model
        val bankDetails = com.example.vasudevkutumbhakam.model.BankDetails(
            holderName = holderName,
            bankName = bankName,
            accountNumber = accountNumber,
            ifscCode = ifscCode
        )

        // Save ViewModel
        viewModel.saveBankDetails(bankDetails)
    }


    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.inSuccess.observe(this) { success ->
            if (success) {
                val statusViewModel = ViewModelProvider(this)[ApplicationStatusViewModel::class.java]
                statusViewModel.submitStatusPending()

                Toast.makeText(this, "Step 5 saved successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ApplicationSubmittedActivity::class.java))
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