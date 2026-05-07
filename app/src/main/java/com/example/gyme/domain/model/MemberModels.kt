package com.example.gyme.domain.model

data class MemberStats(
    val totalActive: Int,
    val activeGrowth: String,
    val pendingActivation: Int,
    val recentlyExpired: Int
)

data class MemberSummary(
    val id: String,
    val name: String,
    val plan: String,
    val status: MemberStatus,
    val avatarUrl: String? = null,
    val initials: String
)

enum class MemberStatus {
    ACTIVE, PENDING, EXPIRED
}
