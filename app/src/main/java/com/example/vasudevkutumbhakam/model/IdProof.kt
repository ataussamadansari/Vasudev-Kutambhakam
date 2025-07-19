package com.example.vasudevkutumbhakam.model

data class IdProof(
    val idType: String,
    val idNumber: String,
    val frontImage: String?,
    val backImage: String?,
    val panImage: String?,
    val idAccepted: Boolean = false
)
