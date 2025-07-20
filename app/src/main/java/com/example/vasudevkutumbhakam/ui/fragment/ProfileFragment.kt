package com.example.vasudevkutumbhakam.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.adapter.OptionAdapter
import com.example.vasudevkutumbhakam.databinding.FragmentProfileBinding
import com.example.vasudevkutumbhakam.model.OptionItem
import com.example.vasudevkutumbhakam.ui.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val options = listOf(
        OptionItem(R.drawable.user, "Manage Account"),
        OptionItem(R.drawable.ic_bank, "Bank Account"),
        OptionItem(R.drawable.about, "About Us"),
        OptionItem(R.drawable.help, "Help Centre"),
        OptionItem(R.drawable.term_and_condition, "Terms and conditions"),
        OptionItem(R.drawable.privacy_policy, "Privacy Policy"),
        OptionItem(R.drawable.help, "Rate App"),
        OptionItem(R.drawable.logout, "Log Out"),
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.visibility = View.GONE

        // option menu recyclerview setup
        binding.optionBtnRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = OptionAdapter(options) { item ->
                when (item.title) {
                    "Log Out" -> performLogout()
                }
            }
        }

        loadUserData()
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        // Option 1: Redirect to LoginActivity (recommended)
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Option 2: Or just pop back (if you’re using Navigation Component)
        // findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }


    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            firestore.collection("vasudev_user").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "N/A"
                        val email = document.getString("email") ?: "N/A"
                        val phone = document.getString("phone") ?: "N/A"

                        binding.tvName.text = name
                        binding.tvEmail.text = email
                        binding.tvPhone.text = phone
                    }
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}