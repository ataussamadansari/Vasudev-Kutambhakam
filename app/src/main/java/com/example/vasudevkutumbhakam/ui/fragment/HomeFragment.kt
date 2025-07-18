package com.example.vasudevkutumbhakam.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.adapter.ProcessAdapter
import com.example.vasudevkutumbhakam.adapter.ReviewAdapter
import com.example.vasudevkutumbhakam.databinding.FragmentHomeBinding
import com.example.vasudevkutumbhakam.model.Process
import com.example.vasudevkutumbhakam.model.Review
import com.example.vasudevkutumbhakam.ui.activity.CheckEligibilityActivity
import com.example.vasudevkutumbhakam.utils.EligibilityUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!  // Safe access


    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var processKycTV: AppCompatTextView
    private lateinit var processCheckEligibilityTV: AppCompatTextView
    private lateinit var processProfileInfoTV: AppCompatTextView
    private lateinit var lineKyc: View
    private lateinit var lineProfileInfo: View
    private lateinit var kycCircle: View
    private lateinit var profileInfoCircle: View


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
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set RecyclerView to use a 2-column grid layout
        binding.processRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.processRv.adapter = ProcessAdapter(processList)

        processKycTV = view.findViewById<AppCompatTextView>(R.id.process_kyc_tv)
        processCheckEligibilityTV =
            view.findViewById<AppCompatTextView>(R.id.process_check_eligibility_tv)
        processProfileInfoTV = view.findViewById<AppCompatTextView>(R.id.profile_info_tv)

        lineKyc = view.findViewById<View>(R.id.kyc_line)
        lineProfileInfo = view.findViewById<View>(R.id.profile_info_line)

        kycCircle = view.findViewById<View>(R.id.kyc_circle)
        profileInfoCircle = view.findViewById<View>(R.id.profile_info_circle)

        processCheckEligibilityTV.setOnClickListener {
            startActivity(Intent(requireContext(), CheckEligibilityActivity::class.java))
        }


        binding.reviewRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ReviewAdapter(reviews)
        }

        binding.getLoanBtn.setOnClickListener {
            startActivity(Intent(requireContext(), CheckEligibilityActivity::class.java))
        }

        fetchEligibilityStatus()
    }


    private fun fetchEligibilityStatus() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        EligibilityUtil.fetchEligibilityStatus(
            userId = userId,
            firestore = FirebaseFirestore.getInstance(),
            onResult = { steps ->
                if (steps.step3) {
                    lineKyc.setBackgroundResource(R.drawable.active_line)
                    binding.processPresTV.text = "50%"
                }
                if (steps.step4) {
                    kycCircle.setBackgroundResource(R.drawable.indicator_circle)
                    processKycTV.setTextColor(resources.getColor(R.color.textColor2))
                    binding.processPresTV.text = "60%"
                }
                if (steps.step5) {
                    lineProfileInfo.setBackgroundResource(R.drawable.active_line)
                    profileInfoCircle.setBackgroundResource(R.drawable.indicator_circle)
                    processProfileInfoTV.setTextColor(resources.getColor(R.color.textColor2))
                }
            },
            onError = { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Prevent memory leaks
    }

}