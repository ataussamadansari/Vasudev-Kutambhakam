package com.example.vasudevkutumbhakam.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityLoanFailedBinding
import com.example.vasudevkutumbhakam.viewModel.ApplicationStatusViewModel

class LoanFailedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoanFailedBinding

    private lateinit var viewModel: ApplicationStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoanFailedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[ApplicationStatusViewModel::class.java]


        binding.btnTryAgain.setOnClickListener {
            viewModel.submitStatusPending()
            finish()
        }
    }
}