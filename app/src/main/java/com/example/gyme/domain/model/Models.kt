package com.example.gyme.domain.model

data class Member(
    val id: String                = "",
    val name: String              = "",
    val phone: String             = "",
    val subscriptionStart: String = "",
    val subscriptionEnd: String   = "",
    val status: String            = "active",
    val createdAt: String         = ""
)

data class User(
    val id: String                   = "",
    val name: String                 = "",
    val email: String                = "",
    val role: String                 = "staff",
    val canAddMember: Boolean        = false,
    val canEditMember: Boolean       = false,
    val canDeleteMember: Boolean     = false,
    val canManageSubscriptions: Boolean = false
)

data class Attendance(
    val id: String       = "",
    val memberId: String = "",
    val checkIn: String  = ""
)

data class Transaction(
    val id: String          = "",
    val amount: Double      = 0.0,
    val type: String        = "income",
    val description: String = "",
    val createdBy: String   = "",
    val approvedBy: String? = null,
    val status: String      = "pending",
    val createdAt: String   = ""
)

data class Notification(
    val id: String        = "",
    val userId: String    = "",
    val title: String     = "",
    val message: String   = "",
    val isRead: Boolean   = false,
    val reminderDays: Int = 0,
    val followUpDays: Int = 0,
    val createdAt: String = ""
)

data class Settings(
    val id: String             = "",
    val gymName: String        = "",
    val billingAddress: String = "",
    val language: String       = "en"
)
