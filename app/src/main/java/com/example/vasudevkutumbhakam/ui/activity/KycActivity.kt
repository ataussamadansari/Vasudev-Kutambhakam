package com.example.vasudevkutumbhakam.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vasudevkutumbhakam.databinding.ActivityKycBinding
import com.example.vasudevkutumbhakam.viewModel.UserDetailsViewModel
import com.example.vasudevkutumbhakam.viewModel.VideoKycViewModel

class KycActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKycBinding

    private lateinit var viewModel: VideoKycViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKycBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(16, systemBars.top, 16, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[VideoKycViewModel::class.java]

        binding.backBtn.setOnClickListener { finish() }
        setupNextButton()

    }

    private fun setupNextButton() {
        binding.btnStartVideoCall.isEnabled = false
        binding.btnStartVideoCall.alpha = 0.5f

        binding.consentCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.btnStartVideoCall.isEnabled = isChecked
            binding.btnStartVideoCall.alpha = if (isChecked) 1f else 0.5f
        }

        binding.btnStartVideoCall.setOnClickListener {
            if (!binding.consentCheckBox.isChecked) {
                Toast.makeText(this, "Please accept the declaration first", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            viewModel.saveVideo()
            finish()
        }
    }
}