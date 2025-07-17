package com.example.vasudevkutumbhakam.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.adapter.ProcessAdapter
import com.example.vasudevkutumbhakam.adapter.ReviewAdapter
import com.example.vasudevkutumbhakam.databinding.FragmentHomeBinding
import com.example.vasudevkutumbhakam.model.Process
import com.example.vasudevkutumbhakam.model.Review


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!  // Safe access

    private val processList = listOf(
        Process("Add Bank Account", "Add your bank account", R.drawable.bank),
        Process("Credit Tracker", "Check you CIBIL Score", R.drawable.credit_tracker),
        Process("Reward coins", "Use your reward coins", R.drawable.reward_coins),
        Process("Help and Support", "Feel Free to reachout", R.drawable.help_and_support)
    )

    val reviews = listOf(
        Review("Sri Ram Babu", "Farmer, Bihar", 5f, R.drawable.sample_image_1),
        Review("Asha Devi", "Tailor, UP", 4f, R.drawable.sample_image_3),
        Review("Ravi Kumar", "Shop Owner, MP", 5f, R.drawable.sample_image_2),
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set RecyclerView to use a 2-column grid layout
        binding.processRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.processRv.adapter = ProcessAdapter(processList)

        binding.reviewRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ReviewAdapter(reviews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Prevent memory leaks
    }

}