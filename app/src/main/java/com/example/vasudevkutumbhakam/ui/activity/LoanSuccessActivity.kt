package com.example.vasudevkutumbhakam.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityLoanSuccessBinding
import com.example.vasudevkutumbhakam.model.Amount
import com.example.vasudevkutumbhakam.viewModel.AmountViewModel

class LoanSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoanSuccessBinding

    private lateinit var viewModel:  AmountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoanSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[AmountViewModel::class.java]

        observeViewModel()

        binding.btnBorrowNow.setOnClickListener {
            val amount = "80,000"
            if (viewModel.fetchAmount().equals("")) {
                Toast.makeText(this, "80,000 added successfully", Toast.LENGTH_SHORT).show()
                viewModel.addBarrowAmount(Amount(amount))
                finish()
            } else {
                Toast.makeText(this, "80,000 already added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    private fun observeViewModel() {
        viewModel.amountLiveData.observe(this) {
        }

        viewModel.resultMessage.observe(this) {
            it?.let { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}