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
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityIdProofBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class IdProofActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIdProofBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var selectedIdType: String = ""

    private val PICK_FRONT_IMAGE = 1001
    private val PICK_BACK_IMAGE = 1002
    private val PICK_PAN_IMAGE = 1003
    private var selectedFrontUri: Uri? = null
    private var selectedBackUri: Uri? = null
    private var selectedPanUri: Uri? = null


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


        setupSpinner()
        setupUploadButtons()
        setupNextButton()
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
        val userId = auth.currentUser?.uid ?: return
        val idNumber = binding.editTextIdProofNumber.text.toString().trim()

        if (selectedIdType == "Select ID Type" || idNumber.isEmpty()) {
            Toast.makeText(this, "Please select ID type and enter the number", Toast.LENGTH_SHORT).show()
            return
        }

        val data = mapOf(
            "id_type" to selectedIdType,
            "id_number" to idNumber
        )

        firestore.collection("vasudev_user_details")
            .document(userId)
            .update(data)
            .addOnSuccessListener {
                markStep3Completed(userId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save ID details: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebase(uri: Uri, type: String) {
        val userId = auth.currentUser?.uid ?: return
        val ref = storage.reference.child("id_proofs/$userId/${type}.jpg")

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveImageUrlToFirestore(type, downloadUrl.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToFirestore(field: String, url: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("vasudev_user_details")
            .document(userId)
            .update(field, url)
            .addOnSuccessListener {
                Toast.makeText(this, "$field uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Firestore update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markStep3Completed(userId: String) {
        firestore.collection("eligibility")
            .document(userId)
            .update("step3_idProof", true)
            .addOnSuccessListener {
                Toast.makeText(this, "Step 3 completed", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update eligibility", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data?.data != null) {
            val imageUri = data.data

            when (requestCode) {
                PICK_FRONT_IMAGE -> imageUri?.let {
                    selectedFrontUri = it
                    binding.aadharFrontUploadCamBtn.setImageURI(it)
                    uploadImageToFirebase(it, "aadhar_front")
                }

                PICK_BACK_IMAGE -> imageUri?.let {
                    selectedBackUri = it
                    binding.aadharBackUploadCamBtn.setImageURI(it)
                    uploadImageToFirebase(it, "aadhar_back")
                }

                PICK_PAN_IMAGE -> imageUri?.let {
                    selectedPanUri = it
                    binding.panUploadCamBtn.setImageURI(it)
                    uploadImageToFirebase(it, "pan_front")
                }
            }
        }
    }


}