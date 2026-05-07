package com.example.gyme.domain.usecase

import com.example.gyme.domain.model.PaymentRecord
import com.example.gyme.domain.repository.MemberRepository
class GetMemberPaymentHistoryUseCase(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(id: String): Result<List<PaymentRecord>> {
        return repository.getMemberPaymentHistory(id)
    }
}
