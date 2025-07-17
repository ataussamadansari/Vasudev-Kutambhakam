package com.example.vasudevkutumbhakam.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.adapter.OptionAdapter
import com.example.vasudevkutumbhakam.adapter.ReviewAdapter
import com.example.vasudevkutumbhakam.databinding.FragmentProfileBinding
import com.example.vasudevkutumbhakam.model.OptionItem

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

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

        // option menu recyclerview setup
        binding.optionBtnRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = OptionAdapter(options)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}