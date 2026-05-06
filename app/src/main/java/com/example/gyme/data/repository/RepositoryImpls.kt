package com.example.gyme.data.repository

import com.example.gyme.data.remote.NetworkModule
import com.example.gyme.data.remote.api.*
import com.example.gyme.data.remote.dto.*
import com.example.gyme.domain.model.*
import com.example.gyme.domain.repository.*
import com.example.gyme.util.ApiResult
import com.example.gyme.util.safeApiCall


private fun MemberDto.toDomain() = Member(
    id = id ?: "", name = name, phone = phone,
    subscriptionStart = subscriptionStart, subscriptionEnd = subscriptionEnd,
    status = status, createdAt = createdAt ?: ""
)

private fun UserDto.toDomain() = User(
    id    = id    ?: "",
    name  = name  ?: "Guest",
    email = email ?: "",
    role  = role  ?: "staff",
    canAddMember          = canAddMember,
    canEditMember         = canEditMember,
    canDeleteMember       = canDeleteMember,
    canManageSubscriptions = canManageSubscriptions
)

private fun AttendanceDto.toDomain() = Attendance(
    id = id ?: "", memberId = memberId, checkIn = checkIn
)

private fun TransactionDto.toDomain() = Transaction(
    id = id ?: "", amount = amount, type = type, description = description,
    createdBy = createdBy ?: "", approvedBy = approvedBy,
    status = status, createdAt = createdAt ?: ""
)

private fun NotificationDto.toDomain() = Notification(
    id = id ?: "", userId = userId, title = title, message = message,
    isRead = isRead, reminderDays = reminderDays, followUpDays = followUpDays,
    createdAt = createdAt ?: ""
)

private fun SettingsDto.toDomain() = Settings(
    id = id ?: "", gymName = gymName, billingAddress = billingAddress, language = language
)


class MembersRepositoryImpl : MembersRepository {
    private val api = NetworkModule.create(MembersApi::class.java)

    override suspend fun getAll() = safeApiCall { api.getAll().map { it.toDomain() } }
    override suspend fun getById(id: String): ApiResult<Member> = safeApiCall {
        api.getById("eq.$id").first().toDomain()
    }
    override suspend fun create(name: String, phone: String, subscriptionStart: String, subscriptionEnd: String) = safeApiCall {
        api.create(CreateMemberRequest(name, phone, subscriptionStart, subscriptionEnd)).first().toDomain()
    }
    override suspend fun update(id: String, name: String?, phone: String?, status: String?) = safeApiCall {
        api.update("eq.$id", UpdateMemberRequest(name, phone, status)).first().toDomain()
    }
    override suspend fun delete(id: String) = safeApiCall { api.delete("eq.$id") }
}


class UsersRepositoryImpl : UsersRepository {
    private val api = NetworkModule.create(UsersApi::class.java)

    override suspend fun getAll() = safeApiCall { api.getAll().map { it.toDomain() } }
    override suspend fun getById(id: String) = safeApiCall {
        api.getById("eq.$id").first().toDomain()
    }
    override suspend fun getByEmail(email: String) = safeApiCall {
        api.getByEmail("eq.$email").firstOrNull()?.toDomain()
    }
    override suspend fun create(name: String, email: String, role: String) = safeApiCall {
        api.create(CreateUserRequest(name = name, email = email, role = role)).first().toDomain()
    }
    override suspend fun update(id: String, role: String?, canDeleteMember: Boolean?) = safeApiCall {
        api.update("eq.$id", UpdateUserRequest(role = role, canDeleteMember = canDeleteMember)).first().toDomain()
    }
    override suspend fun delete(id: String) = safeApiCall { api.delete("eq.$id") }
}


class AttendanceRepositoryImpl : AttendanceRepository {
    private val api = NetworkModule.create(AttendanceApi::class.java)

    override suspend fun getAll() = safeApiCall { api.getAll().map { it.toDomain() } }
    override suspend fun getByMember(memberId: String) = safeApiCall {
        api.getByMember("eq.$memberId").map { it.toDomain() }
    }
    override suspend fun checkIn(memberId: String, checkIn: String) = safeApiCall {
        api.checkIn(CreateAttendanceRequest(memberId, checkIn)).first().toDomain()
    }
    override suspend fun delete(id: String) = safeApiCall { api.delete("eq.$id") }
}


class TransactionsRepositoryImpl : TransactionsRepository {
    private val api = NetworkModule.create(TransactionsApi::class.java)

    override suspend fun getAll() = safeApiCall { api.getAll().map { it.toDomain() } }
    override suspend fun getById(id: String) = safeApiCall {
        api.getById("eq.$id").first().toDomain()
    }
    override suspend fun create(amount: Double, type: String, description: String, createdBy: String) = safeApiCall {
        api.create(CreateTransactionRequest(amount, type, description, createdBy)).first().toDomain()
    }
    override suspend fun update(id: String, status: String?, approvedBy: String?) = safeApiCall {
        api.update("eq.$id", UpdateTransactionRequest(status, approvedBy)).first().toDomain()
    }
    override suspend fun delete(id: String) = safeApiCall { api.delete("eq.$id") }
}


class NotificationsRepositoryImpl : NotificationsRepository {
    private val api = NetworkModule.create(NotificationsApi::class.java)

    override suspend fun getAll() = safeApiCall { api.getAll().map { it.toDomain() } }
    override suspend fun getByUser(userId: String) = safeApiCall {
        api.getByUser("eq.$userId").map { it.toDomain() }
    }
    override suspend fun create(userId: String, title: String, message: String, reminderDays: Int, followUpDays: Int) = safeApiCall {
        api.create(CreateNotificationRequest(userId, title, message, reminderDays = reminderDays, followUpDays = followUpDays)).first().toDomain()
    }
    override suspend fun markAsRead(id: String) = safeApiCall {
        api.markAsRead("eq.$id", mapOf("is_read" to true)).first().toDomain()
    }
    override suspend fun delete(id: String) = safeApiCall { api.delete("eq.$id") }
}


class SettingsRepositoryImpl : SettingsRepository {
    private val api = NetworkModule.create(SettingsApi::class.java)

    override suspend fun get() = safeApiCall {
        api.get().first().toDomain()
    }
    override suspend fun create(gymName: String, billingAddress: String, language: String) = safeApiCall {
        api.create(UpsertSettingsRequest(gymName, billingAddress, language)).first().toDomain()
    }
    override suspend fun update(id: String, gymName: String, billingAddress: String, language: String) = safeApiCall {
        api.update("eq.$id", UpsertSettingsRequest(gymName, billingAddress, language)).first().toDomain()
    }
}

class AuthRepositoryImpl : AuthRepository {

    private val api = NetworkModule.createAuth(SupabaseAuthApi::class.java)

    override suspend fun signIn(email: String, password: String): ApiResult<User> =
        safeApiCall {
            val response = api.signIn(body = AuthRequest(email = email, password = password))
            response.toUser()
        }

    override suspend fun signUp(email: String, password: String): ApiResult<User> =
        safeApiCall {
            val response = api.signUp(SignUpRequest(email = email, password = password))
            response.toUser()
        }

    private fun AuthResponse.toUser(): User {
        val meta = user?.userMetadata
        return User(
            id    = user?.id    ?: "",
            email = user?.email ?: "",
            name  = (meta?.get("full_name") as? String)
                 ?: (meta?.get("name")      as? String)
                 ?: "Guest",
            role  = user?.role ?: "staff"
        )
    }
}
