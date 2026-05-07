package com.example.gyme.domain.usecase

import com.example.gyme.domain.model.Member
import com.example.gyme.domain.repository.MemberRepository
class AddMemberUseCase(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(member: Member): Result<Unit> {
        // Validation logic can go here (e.g. check if phone number is valid)
        if (member.name.isBlank()) {
            return Result.failure(Exception("Full name is required"))
        }
        if (member.phone.isBlank()) {
            return Result.failure(Exception("Phone number is required"))
        }
        return repository.addMember(member)
    }
}
