package com.example.gyme.domain.model

import java.util.Date

data class Member(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val gender: Gender = Gender.MALE,
    val subscriptionStart: Date = Date(),
    val subscriptionEnd: Date? = null,
    val planId: String = "",
    val status: MemberStatus = MemberStatus.ACTIVE,
    val avatarUrl: String? = null,
    val discount: Double = 0.0,
    val discountType: DiscountType = DiscountType.PERCENTAGE,
    val sessionsThisMonth: Int = 0,
    val goals: List<String> = emptyList(),
    val createdAt: String = ""
)

data class PaymentRecord(
    val id: String,
    val planName: String,
    val amount: Double,
    val date: String,
    val isPaid: Boolean
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
    val type: NotificationType = NotificationType.SYSTEM,
    val isRead: Boolean   = false,
    val reminderDays: Int = 0,
    val followUpDays: Int = 0,
    val createdAt: String = ""
)

enum class NotificationType {
    SUBSCRIPTION, PAYMENT, SYSTEM
}

data class Settings(
    val id: String             = "",
    val gymName: String        = "",
    val billingAddress: String = "",
    val language: String       = "en"
)

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class DiscountType {
    PERCENTAGE, FIXED
}

data class MembershipPlan(
    val id: String,
    val name: String,
    val price: Double,
    val durationMonths: Int
)
