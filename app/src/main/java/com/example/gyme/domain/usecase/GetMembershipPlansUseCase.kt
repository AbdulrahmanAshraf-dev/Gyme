package com.example.gyme.domain.usecase

import com.example.gyme.domain.model.MembershipPlan
import com.example.gyme.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
class GetMembershipPlansUseCase(
    private val repository: MemberRepository
) {
    operator fun invoke(): Flow<List<MembershipPlan>> {
        return repository.getMembershipPlans()
    }
}
