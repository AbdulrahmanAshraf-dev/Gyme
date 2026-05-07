package com.example.gyme.data.repository

import com.example.gyme.domain.model.*
import com.example.gyme.domain.repository.MemberRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class MemberRepositoryImpl : MemberRepository {

    private val plans = listOf(
        MembershipPlan("1", "Basic Monthly", 29.99, 1),
        MembershipPlan("2", "Pro Unlimited", 120.00, 1),
        MembershipPlan("3", "Pro Quarterly", 79.99, 3),
        MembershipPlan("4", "Elite Annual", 249.99, 12),
        MembershipPlan("5", "VIP Platinum", 499.99, 12)
    )

    override suspend fun addMember(member: Member): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }

    override suspend fun updateMember(member: Member): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }

    override fun getMembershipPlans(): Flow<List<MembershipPlan>> = flow {
        emit(plans)
    }

    override suspend fun getNextMemberId(): String {
        return "UGC-" + (1000 + (Math.random() * 900).toInt()).toString()
    }

    override suspend fun getMemberById(id: String): Result<Member> {
        delay(500)
        // Returning mock data for Elena Rodriguez as per screenshot
        return Result.success(
            Member(
                id = "UGC-1248",
                name = "Elena Rodriguez",
                email = "elena.r@example.com",
                phone = "+1 (555) 019-2834",
                gender = Gender.FEMALE,
                planId = "2", // Pro Unlimited
                status = MemberStatus.ACTIVE,
                sessionsThisMonth = 15,
                goals = listOf("Strength Training", "Hypertrophy")
            )
        )
    }

    override suspend fun getMemberPaymentHistory(id: String): Result<List<PaymentRecord>> {
        delay(500)
        return Result.success(
            listOf(
                PaymentRecord("1", "Pro Unlimited", 120.00, "Oct 12, 2023", true),
                PaymentRecord("2", "Pro Unlimited", 120.00, "Sep 12, 2023", true),
                PaymentRecord("3", "Pro Unlimited", 120.00, "Aug 12, 2023", true)
            )
        )
    }
}
