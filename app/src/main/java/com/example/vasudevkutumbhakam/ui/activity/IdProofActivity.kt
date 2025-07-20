package com.example.vasudevkutumbhakam.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityIdProofBinding
import com.example.vasudevkutumbhakam.model.IdProof
import com.example.vasudevkutumbhakam.viewModel.IdProofViewModel
import com.example.vasudevkutumbhakam.viewModel.UserDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class IdProofActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIdProofBinding

    private lateinit var viewModel: IdProofViewModel

    private var selectedIdType: String = ""
    private var selectedFrontUri: Uri? = null
    private var selectedBackUri: Uri? = null
    private var selectedPanUri: Uri? = null

    private var frontUrl: String? = null
    private var backUrl: String? = null
    private var panUrl: String? = null

    private val PICK_FRONT_IMAGE = 1001
    private val PICK_BACK_IMAGE = 1002
    private val PICK_PAN_IMAGE = 1003


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIdProofBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(16, systemBars.top, 16, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener { finish() }
        //init
        viewModel = ViewModelProvider(this)[IdProofViewModel::class.java]

        viewModel.getIdProof()

        setupSpinner()
        setupUploadButtons()
        setupNextButton()
        observeViewModel()
    }

    private fun setupSpinner() {
        val idTypes = listOf("Select ID Type", "Aadhar Card", "PAN Card")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, idTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerIdType.adapter = adapter

        binding.spinnerIdType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedIdType = idTypes[position]

                binding.aadharCardUploadLayout.visibility =
                    if (selectedIdType == "Aadhar Card") View.VISIBLE else View.GONE

                binding.panCardUploadLayout.visibility =
                    if (selectedIdType == "PAN Card") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupUploadButtons() {
        binding.aadharFrontUploadCamBtn.setOnClickListener {
            pickImageFromGallery(PICK_FRONT_IMAGE)
        }

        binding.aadharBackUploadCamBtn.setOnClickListener {
            pickImageFromGallery(PICK_BACK_IMAGE)
        }

        binding.panUploadCamBtn.setOnClickListener {
            pickImageFromGallery(PICK_PAN_IMAGE)
        }
    }

    private fun pickImageFromGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, requestCode)
    }

    private fun setupNextButton() {
        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f

        binding.checkBoxAccept.setOnCheckedChangeListener { _, isChecked ->
            binding.btnNext.isEnabled = isChecked
            binding.btnNext.alpha = if (isChecked) 1f else 0.5f
        }

        binding.btnNext.setOnClickListener {
            if (!binding.checkBoxAccept.isChecked) {
                Toast.makeText(this, "Please accept the declaration first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveIdProofDetails()
        }
    }

    private fun saveIdProofDetails() {
        val idNumber = binding.editTextIdProofNumber.text.toString().trim()

        if (selectedIdType == "Select ID Type" || idNumber.isEmpty()) {
            Toast.makeText(this, "Please select ID type and enter the number", Toast.LENGTH_SHORT).show()
            return
        }

        val idProof = IdProof(
            idType = selectedIdType,
            idNumber = idNumber,
            frontImage = frontUrl,
            backImage = backUrl,
            panImage = panUrl,
            idAccepted = true
        )

        viewModel.submitIdProof(idProof)
    }

    private fun observeViewModel() {
        viewModel.imageUploadStatus.observe(this) { pair ->
            when (pair.first) {
                "aadhar_front" -> frontUrl = pair.second
                "aadhar_back" -> backUrl = pair.second
                "pan_front" -> panUrl = pair.second
            }
            Toast.makeText(this, "${pair.first} uploaded successfully", Toast.LENGTH_SHORT).show()
        }

        viewModel.uploadError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.inSuccess.observe(this) {
            if (it) {
                Toast.makeText(this, "Step 3 completed", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, KycActivity::class.java))
                finish()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.resultMessage.observe(this) {
            it?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        // 👇 Handle prefilled existing ID proof
        viewModel.idProof.observe(this) { proof ->
            proof?.let {
                selectedIdType = it.idType
                val index = (binding.spinnerIdType.adapter as ArrayAdapter<String>).getPosition(it.idType)
                if (index >= 0) binding.spinnerIdType.setSelection(index)

                binding.editTextIdProofNumber.setText(it.idNumber)

                frontUrl = it.frontImage
                backUrl = it.backImage
                panUrl = it.panImage

                // Load images (optional)
                if (!frontUrl.isNullOrEmpty()) {
                    Glide.with(this).load(frontUrl).into(binding.aadharFrontIv)
                }

                if (!backUrl.isNullOrEmpty()) {
                    Glide.with(this).load(backUrl).into(binding.aadharBackIv)
                }

                if (!panUrl.isNullOrEmpty()) {
                    Glide.with(this).load(panUrl).into(binding.panFrontIv)
                }

                if (it.idAccepted) {
                    binding.checkBoxAccept.isChecked = true
                    binding.btnNext.isEnabled = true
                    binding.btnNext.alpha = 1f
                }
            }
        }
    }

    private fun uploadImageToViewModel(uri: Uri, type: String) {
        viewModel.uploadImage(type, uri)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data?.data != null) {
            val imageUri = data.data

            when (requestCode) {
                PICK_FRONT_IMAGE -> imageUri?.let {
                    selectedFrontUri = it
                    binding.aadharFrontIv.setImageURI(it)
                    viewModel.uploadImage("aadhar_front", it)
                }

                PICK_BACK_IMAGE -> imageUri?.let {
                    selectedBackUri = it
                    binding.aadharBackIv.setImageURI(it)
                    viewModel.uploadImage("aadhar_back", it)
                }

                PICK_PAN_IMAGE -> imageUri?.let {
                    selectedPanUri = it
                    binding.panFrontIv.setImageURI(it)
                    viewModel.uploadImage("pan_front", it)
                }
            }
        }
    }

}