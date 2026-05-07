package com.example.gyme.domain.usecase

import com.example.gyme.domain.model.Attendance
import com.example.gyme.domain.model.Member
import com.example.gyme.domain.model.MemberStatus
import com.example.gyme.domain.repository.AttendanceRepository
import com.example.gyme.domain.repository.MembersRepository
import com.example.gyme.util.ApiResult
import java.util.Date

class CheckInMemberUseCase(
    private val membersRepository: MembersRepository,
    private val attendanceRepository: AttendanceRepository
) {
    suspend operator fun invoke(memberId: String): CheckInResult {
        // 1. Get member details
        val memberResult = membersRepository.getById(memberId)
        
        if (memberResult is ApiResult.Error) {
            return CheckInResult.Error("Member not found")
        }
        
        val member = (memberResult as ApiResult.Success).data
        
        // 2. Check subscription status
        val now = Date()
        val isExpired = member.subscriptionEnd?.before(now) ?: true
        
        if (isExpired || member.status == MemberStatus.EXPIRED) {
            return CheckInResult.SubscriptionExpired(member)
        }
        
        // 3. Perform check-in
        val checkInTime = now.toString() // Or use a proper formatter
        val result = attendanceRepository.checkIn(memberId, checkInTime)
        
        return when (result) {
            is ApiResult.Success -> CheckInResult.Success(member, result.data)
            is ApiResult.Error -> CheckInResult.Error(result.message)
        }
    }
}

sealed class CheckInResult {
    data class Success(val member: Member, val attendance: Attendance) : CheckInResult()
    data class SubscriptionExpired(val member: Member) : CheckInResult()
    data class Error(val message: String) : CheckInResult()
}
