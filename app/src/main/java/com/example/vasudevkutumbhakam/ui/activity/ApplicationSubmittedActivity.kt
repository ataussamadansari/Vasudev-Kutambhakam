package com.example.vasudevkutumbhakam.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityApplicationSubmittedBinding
import com.example.vasudevkutumbhakam.model.ApplicationStatusType
import com.example.vasudevkutumbhakam.viewModel.ApplicationStatusViewModel

class ApplicationSubmittedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplicationSubmittedBinding

    private lateinit var viewModel: ApplicationStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApplicationSubmittedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[ApplicationStatusViewModel::class.java]

        viewModel.statusLiveData.observe(this) { status ->
            when (status?.status) {
                "APPROVED", "approved" , "Approved" -> {
                    startActivity(Intent(this, LoanSuccessActivity::class.java))
                    finish()
                }
                "REJECTED", "rejected", "Rejected", "Failed" -> {
                    startActivity(Intent(this, LoanFailedActivity::class.java))
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Unknown status: ${status?.status}", Toast.LENGTH_SHORT).show()
                    Log.d("Status", "Unknown status: ${status?.status}")
                }
            }
        }



        viewModel.fetchApplicationStatus()


    }
}