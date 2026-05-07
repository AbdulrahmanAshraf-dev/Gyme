package com.example.gyme.domain.model

data class StaffStats(
    val totalActiveStaff: Int,
    val growthText: String,
    val onShiftNow: Int,
    val pendingRequests: Int
)

data class StaffMember(
    val id: String,
    val name: String,
    val role: String,
    val email: String,
    val accessLabel: String,
    val accessDescription: String,
    val isAccessEnabled: Boolean,
    val rating: Double? = null,
    val avatarUrl: String? = null
)
