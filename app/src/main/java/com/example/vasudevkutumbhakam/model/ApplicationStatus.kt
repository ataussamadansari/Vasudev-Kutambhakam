package com.example.vasudevkutumbhakam.model

object ApplicationStatusType {
    const val APPROVED = "Approved"
    const val REJECTED = "Rejected"
    const val PENDING = "Pending"
}

data class ApplicationStatus(
    val status: String = ApplicationStatusType.PENDING
)