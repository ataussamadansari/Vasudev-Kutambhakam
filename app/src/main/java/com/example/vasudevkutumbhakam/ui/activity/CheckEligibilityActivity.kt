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
import com.example.vasudevkutumbhakam.databinding.ActivityCheckEligibilityBinding
import com.example.vasudevkutumbhakam.viewModel.ApplicationStatusViewModel
import com.example.vasudevkutumbhakam.viewModel.EligibilityViewModel

class CheckEligibilityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckEligibilityBinding

    private lateinit var viewModel: EligibilityViewModel

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

        binding.backBtn.setOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(this)[EligibilityViewModel::class.java]

        viewModel.steps.observe(this) { steps ->
            // Show checkmarks and setup buttons
            if (steps.step1) {
                binding.uDArrow.setImageResource(R.drawable.checkmark)
                binding.uDArrow.rotation = 0f
                binding.userDetailsBtn.setOnClickListener {
                    startActivity(Intent(this, UserDetailsActivity::class.java))
                }
            }
            if (steps.step2) {
                binding.iDArrow.setImageResource(R.drawable.checkmark)
                binding.iDArrow.rotation = 0f
                binding.incomeDetailsBtn.setOnClickListener {
                    startActivity(Intent(this, IncomeActivity::class.java))
                }
            }
            if (steps.step3) {
                binding.iPArrow.setImageResource(R.drawable.checkmark)
                binding.iPArrow.rotation = 0f
                binding.idProofBtn.setOnClickListener {
                    startActivity(Intent(this, IdProofActivity::class.java))
                }
            }
            if (steps.step4) {
                binding.kycArrow.setImageResource(R.drawable.checkmark)
                binding.kycArrow.rotation = 0f
                binding.kycBtn.setOnClickListener {
                    startActivity(Intent(this, KycActivity::class.java))
                }
            }
            if (steps.step5) {
                binding.bDArrow.setImageResource(R.drawable.checkmark)
                binding.bDArrow.rotation = 0f
                binding.bankDetailsBtn.setOnClickListener {
                    startActivity(Intent(this, BankDetailsActivity::class.java))
                }
            }
        }

        viewModel.error.observe(this) {
            it?.let { msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
        }

        viewModel.initialized.observe(this) {
            if (it) {
                startActivity(Intent(this, UserDetailsActivity::class.java))
                finish()
            }
        }

        binding.appCompatButton.setOnClickListener {
            val currentSteps = viewModel.steps.value
            if (currentSteps != null) {
                when {
                    !currentSteps.step1 -> startActivity(
                        Intent(
                            this,
                            UserDetailsActivity::class.java
                        )
                    )

                    !currentSteps.step2 -> startActivity(Intent(this, IncomeActivity::class.java))
                    !currentSteps.step3 -> startActivity(Intent(this, IdProofActivity::class.java))
                    !currentSteps.step4 -> startActivity(Intent(this, KycActivity::class.java))
                    !currentSteps.step5 -> startActivity(Intent(this, BankDetailsActivity::class.java))
                    else -> {
                        startActivity(Intent(this, ApplicationSubmittedActivity::class.java))
                    }
                }
            } else {
                // If steps not loaded yet, initialize
                viewModel.initializeSteps()
            }
        }

        viewModel.fetchSteps()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchSteps()
    }
}