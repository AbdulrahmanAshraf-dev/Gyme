package com.example.gyme.domain.repository

import com.example.gyme.domain.model.*
import com.example.gyme.util.ApiResult

interface MembersRepository {
    suspend fun getAll(): ApiResult<List<Member>>
    suspend fun getById(id: String): ApiResult<Member>
    suspend fun create(name: String, phone: String, subscriptionStart: String, subscriptionEnd: String): ApiResult<Member>
    suspend fun update(id: String, name: String? = null, phone: String? = null, status: String? = null): ApiResult<Member>
    suspend fun delete(id: String): ApiResult<Unit>
}

interface UsersRepository {
    suspend fun getAll(): ApiResult<List<User>>
    suspend fun getById(id: String): ApiResult<User>
    suspend fun getByEmail(email: String): ApiResult<User?>
    suspend fun create(name: String, email: String, role: String): ApiResult<User>
    suspend fun update(id: String, role: String? = null, canDeleteMember: Boolean? = null): ApiResult<User>
    suspend fun delete(id: String): ApiResult<Unit>
}

interface AttendanceRepository {
    suspend fun getAll(): ApiResult<List<com.example.gyme.domain.model.Attendance>>
    suspend fun getByMember(memberId: String): ApiResult<List<com.example.gyme.domain.model.Attendance>>
    suspend fun checkIn(memberId: String, checkIn: String): ApiResult<com.example.gyme.domain.model.Attendance>
    suspend fun delete(id: String): ApiResult<Unit>
}

interface TransactionsRepository {
    suspend fun getAll(): ApiResult<List<Transaction>>
    suspend fun getById(id: String): ApiResult<Transaction>
    suspend fun create(amount: Double, type: String, description: String, createdBy: String): ApiResult<Transaction>
    suspend fun update(id: String, status: String? = null, approvedBy: String? = null): ApiResult<Transaction>
    suspend fun delete(id: String): ApiResult<Unit>
}

interface NotificationsRepository {
    suspend fun getAll(): ApiResult<List<Notification>>
    suspend fun getByUser(userId: String): ApiResult<List<Notification>>
    suspend fun create(userId: String, title: String, message: String, reminderDays: Int, followUpDays: Int): ApiResult<Notification>
    suspend fun markAsRead(id: String): ApiResult<Notification>
    suspend fun delete(id: String): ApiResult<Unit>
}

interface SettingsRepository {
    suspend fun get(): ApiResult<Settings>
    suspend fun create(gymName: String, billingAddress: String, language: String): ApiResult<Settings>
    suspend fun update(id: String, gymName: String, billingAddress: String, language: String): ApiResult<Settings>
}

interface AuthRepository {
    suspend fun signIn(email: String, password: String): ApiResult<User>

    suspend fun signUp(email: String, password: String): ApiResult<User>
}
