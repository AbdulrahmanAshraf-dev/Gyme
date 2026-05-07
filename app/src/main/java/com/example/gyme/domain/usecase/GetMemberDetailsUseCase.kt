package com.example.gyme.domain.usecase

import com.example.gyme.domain.model.Member
import com.example.gyme.domain.repository.MemberRepository
class GetMemberDetailsUseCase(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(id: String): Result<Member> {
        return repository.getMemberById(id)
    }
}
