package com.example.vasudevkutumbhakam.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    // Declare ViewBinding and FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Initialize binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.btnLoginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                val user = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "phone" to phone,
                    "email" to email,
                    "password" to password  // ⚠️ NOT RECOMMENDED to store plain password
                )

                firestore.collection("vasudev_user")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Auth error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
