package com.example.gyme.domain.model

/**
 * Domain model representing the aggregated statistics for the Home Dashboard.
 */
data class DashboardStats(
    val userName: String,
    val totalRevenue: Double,
    val revenueGrowthPercentage: Double,
    val previousRevenue: Double,
    val currentlyActive: Int,
    val capacityPercentage: Int,
    val totalActiveMembers: Int,
    val expiringSoonMembers: Int,
    val expiredMembers: Int
)
