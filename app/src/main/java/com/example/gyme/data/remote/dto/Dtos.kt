package com.example.gyme.data.remote.dto

import com.google.gson.annotations.SerializedName


data class MemberDto(
    @SerializedName("id")                 val id: String?               = null,
    @SerializedName("name")               val name: String              = "",
    @SerializedName("phone")              val phone: String             = "",
    @SerializedName("subscription_start") val subscriptionStart: String = "",
    @SerializedName("subscription_end")   val subscriptionEnd: String   = "",
    @SerializedName("status")             val status: String            = "active",
    @SerializedName("created_at")         val createdAt: String?        = null
)

data class CreateMemberRequest(
    @SerializedName("name")               val name: String,
    @SerializedName("phone")              val phone: String,
    @SerializedName("subscription_start") val subscriptionStart: String,
    @SerializedName("subscription_end")   val subscriptionEnd: String,
    @SerializedName("status")             val status: String = "active"
)

data class UpdateMemberRequest(
    @SerializedName("name")   val name: String?   = null,
    @SerializedName("phone")  val phone: String?  = null,
    @SerializedName("status") val status: String? = null
)


data class UserDto(
    @SerializedName("id")                       val id: String?     = null,
    @SerializedName("name")                     val name: String?   = null,
    @SerializedName("email")                    val email: String?  = null,
    @SerializedName("role")                     val role: String?   = null,
    @SerializedName("can_add_member")           val canAddMember: Boolean           = false,
    @SerializedName("can_edit_member")          val canEditMember: Boolean          = false,
    @SerializedName("can_delete_member")        val canDeleteMember: Boolean        = false,
    @SerializedName("can_manage_subscriptions") val canManageSubscriptions: Boolean = false
)

data class CreateUserRequest(
    @SerializedName("name")                    val name: String,
    @SerializedName("email")                   val email: String,
    @SerializedName("role")                    val role: String   = "staff",
    @SerializedName("can_add_member")          val canAddMember: Boolean          = false,
    @SerializedName("can_edit_member")         val canEditMember: Boolean         = false,
    @SerializedName("can_delete_member")       val canDeleteMember: Boolean       = false,
    @SerializedName("can_manage_subscriptions")val canManageSubscriptions: Boolean = false
)

data class UpdateUserRequest(
    @SerializedName("role")                    val role: String?  = null,
    @SerializedName("can_add_member")          val canAddMember: Boolean?          = null,
    @SerializedName("can_edit_member")         val canEditMember: Boolean?         = null,
    @SerializedName("can_delete_member")       val canDeleteMember: Boolean?       = null,
    @SerializedName("can_manage_subscriptions")val canManageSubscriptions: Boolean? = null
)


data class AttendanceDto(
    @SerializedName("id")        val id: String?      = null,
    @SerializedName("member_id") val memberId: String = "",
    @SerializedName("check_in")  val checkIn: String  = ""
)

data class CreateAttendanceRequest(
    @SerializedName("member_id") val memberId: String,
    @SerializedName("check_in")  val checkIn: String
)


data class TransactionDto(
    @SerializedName("id")          val id: String?          = null,
    @SerializedName("amount")      val amount: Double       = 0.0,
    @SerializedName("type")        val type: String         = "income",
    @SerializedName("description") val description: String  = "",
    @SerializedName("created_by")  val createdBy: String?   = null,
    @SerializedName("approved_by") val approvedBy: String?  = null,
    @SerializedName("status")      val status: String       = "pending",
    @SerializedName("created_at")  val createdAt: String?   = null
)

data class CreateTransactionRequest(
    @SerializedName("amount")      val amount: Double,
    @SerializedName("type")        val type: String,
    @SerializedName("description") val description: String,
    @SerializedName("created_by")  val createdBy: String,
    @SerializedName("status")      val status: String = "pending"
)

data class UpdateTransactionRequest(
    @SerializedName("status")      val status: String?     = null,
    @SerializedName("approved_by") val approvedBy: String? = null
)


data class NotificationDto(
    @SerializedName("id")             val id: String?       = null,
    @SerializedName("user_id")        val userId: String    = "",
    @SerializedName("title")          val title: String     = "",
    @SerializedName("message")        val message: String   = "",
    @SerializedName("type")           val type: String?     = null,
    @SerializedName("is_read")        val isRead: Boolean   = false,
    @SerializedName("reminder_days")  val reminderDays: Int = 0,
    @SerializedName("follow_up_days") val followUpDays: Int = 0,
    @SerializedName("created_at")     val createdAt: String? = null
)

data class CreateNotificationRequest(
    @SerializedName("user_id")        val userId: String,
    @SerializedName("title")          val title: String,
    @SerializedName("message")        val message: String,
    @SerializedName("type")           val type: String      = "system",
    @SerializedName("is_read")        val isRead: Boolean   = false,
    @SerializedName("reminder_days")  val reminderDays: Int = 0,
    @SerializedName("follow_up_days") val followUpDays: Int = 0
)


data class SettingsDto(
    @SerializedName("id")              val id: String?       = null,
    @SerializedName("gym_name")        val gymName: String   = "",
    @SerializedName("billing_address") val billingAddress: String = "",
    @SerializedName("language")        val language: String  = "en"
)

data class UpsertSettingsRequest(
    @SerializedName("gym_name")        val gymName: String,
    @SerializedName("billing_address") val billingAddress: String,
    @SerializedName("language")        val language: String
)
