package com.example.vasudevkutumbhakam.model

data class ApplicationStatus(
    val status: String = ApplicationStatusType.PENDING
)

object ApplicationStatusType {
    const val APPROVED = "Approved"
    const val REJECTED = "Rejected"
    const val PENDING = "Pending"
}
