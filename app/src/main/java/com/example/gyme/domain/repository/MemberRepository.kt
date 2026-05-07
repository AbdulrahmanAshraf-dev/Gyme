package com.example.gyme.domain.repository

import com.example.gyme.domain.model.Member
import com.example.gyme.domain.model.MembershipPlan
import com.example.gyme.domain.model.PaymentRecord
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun addMember(member: Member): Result<Unit>
    suspend fun updateMember(member: Member): Result<Unit>
    fun getMembershipPlans(): Flow<List<MembershipPlan>>
    suspend fun getNextMemberId(): String
    suspend fun getMemberById(id: String): Result<Member>
    suspend fun getMemberPaymentHistory(id: String): Result<List<PaymentRecord>>
}
