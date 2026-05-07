package com.example.gyme.domain.usecase

import com.example.gyme.domain.model.Member
import com.example.gyme.domain.repository.MemberRepository
class UpdateMemberUseCase(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(member: Member): Result<Unit> {
        if (member.name.isBlank()) {
            return Result.failure(Exception("Name cannot be empty"))
        }
        return repository.updateMember(member)
    }
}
