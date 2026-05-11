package com.example.gyme.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDto(
    @SerialName("id")                 val id: String?               = null,
    @SerialName("name")               val name: String              = "",
    @SerialName("phone")              val phone: String             = "",
    @SerialName("subscription_start") val subscriptionStart: String = "",
    @SerialName("subscription_end")   val subscriptionEnd: String   = "",
    @SerialName("status")             val status: String            = "active",
    @SerialName("created_at")         val createdAt: String?        = null
)

@Serializable
data class CreateMemberRequest(
    @SerialName("name")               val name: String,
    @SerialName("phone")              val phone: String,
    @SerialName("subscription_start") val subscriptionStart: String,
    @SerialName("subscription_end")   val subscriptionEnd: String,
    @SerialName("status")             val status: String = "active"
)

@Serializable
data class UpdateMemberRequest(
    @SerialName("name")   val name: String?   = null,
    @SerialName("phone")  val phone: String?  = null,
    @SerialName("status") val status: String? = null
)

@Serializable
data class UserDto(
    @SerialName("id")                       val id: String?     = null,
    @SerialName("name")                     val name: String?   = null,
    @SerialName("email")                    val email: String?  = null,
    @SerialName("role")                     val role: String?   = null,
    @SerialName("can_add_member")           val canAddMember: Boolean           = false,
    @SerialName("can_edit_member")          val canEditMember: Boolean          = false,
    @SerialName("can_delete_member")        val canDeleteMember: Boolean        = false,
    @SerialName("can_manage_subscriptions") val canManageSubscriptions: Boolean = false
)

@Serializable
data class CreateUserRequest(
    @SerialName("name")                    val name: String,
    @SerialName("email")                   val email: String,
    @SerialName("role")                    val role: String   = "staff",
    @SerialName("can_add_member")          val canAddMember: Boolean          = false,
    @SerialName("can_edit_member")         val canEditMember: Boolean         = false,
    @SerialName("can_delete_member")       val canDeleteMember: Boolean       = false,
    @SerialName("can_manage_subscriptions")val canManageSubscriptions: Boolean = false
)

@Serializable
data class UpdateUserRequest(
    @SerialName("role")                    val role: String?  = null,
    @SerialName("can_add_member")          val canAddMember: Boolean?          = null,
    @SerialName("can_edit_member")         val canEditMember: Boolean?         = null,
    @SerialName("can_delete_member")       val canDeleteMember: Boolean?       = null,
    @SerialName("can_manage_subscriptions")val canManageSubscriptions: Boolean? = null
)

@Serializable
data class AttendanceDto(
    @SerialName("id")        val id: String?      = null,
    @SerialName("member_id") val memberId: String = "",
    @SerialName("check_in")  val checkIn: String  = ""
)

@Serializable
data class CreateAttendanceRequest(
    @SerialName("member_id") val memberId: String,
    @SerialName("check_in")  val checkIn: String
)

@Serializable
data class TransactionDto(
    @SerialName("id")          val id: String?          = null,
    @SerialName("amount")      val amount: Double       = 0.0,
    @SerialName("type")        val type: String         = "income",
    @SerialName("description") val description: String  = "",
    @SerialName("created_by")  val createdBy: String?   = null,
    @SerialName("approved_by") val approvedBy: String?  = null,
    @SerialName("status")      val status: String       = "pending",
    @SerialName("created_at")  val createdAt: String?   = null
)

@Serializable
data class CreateTransactionRequest(
    @SerialName("amount")      val amount: Double,
    @SerialName("type")        val type: String,
    @SerialName("description") val description: String,
    @SerialName("created_by")  val createdBy: String,
    @SerialName("status")      val status: String = "pending"
)

@Serializable
data class UpdateTransactionRequest(
    @SerialName("status")      val status: String?     = null,
    @SerialName("approved_by") val approvedBy: String? = null
)

@Serializable
data class NotificationDto(
    @SerialName("id")             val id: String?       = null,
    @SerialName("user_id")        val userId: String    = "",
    @SerialName("title")          val title: String     = "",
    @SerialName("message")        val message: String   = "",
    @SerialName("type")           val type: String?     = null,
    @SerialName("is_read")        val isRead: Boolean   = false,
    @SerialName("reminder_days")  val reminderDays: Int = 0,
    @SerialName("follow_up_days") val followUpDays: Int = 0,
    @SerialName("created_at")     val createdAt: String? = null
)

@Serializable
data class CreateNotificationRequest(
    @SerialName("user_id")        val userId: String,
    @SerialName("title")          val title: String,
    @SerialName("message")        val message: String,
    @SerialName("type")           val type: String      = "system",
    @SerialName("is_read")        val isRead: Boolean   = false,
    @SerialName("reminder_days")  val reminderDays: Int = 0,
    @SerialName("follow_up_days") val followUpDays: Int = 0
)

@Serializable
data class SettingsDto(
    @SerialName("id")              val id: String?       = null,
    @SerialName("gym_name")        val gymName: String   = "",
    @SerialName("billing_address") val billingAddress: String = "",
    @SerialName("language")        val language: String  = "en"
)

@Serializable
data class UpsertSettingsRequest(
    @SerialName("gym_name")        val gymName: String,
    @SerialName("billing_address") val billingAddress: String,
    @SerialName("language")        val language: String
)
