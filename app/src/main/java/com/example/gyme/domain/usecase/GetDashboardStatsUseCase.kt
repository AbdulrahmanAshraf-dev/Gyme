package com.example.gyme.domain.usecase

import com.example.gyme.domain.model.DashboardStats
import kotlinx.coroutines.delay

/**
 * UseCase to fetch dashboard statistics.
 * In a real scenario, this would aggregate data from multiple repositories 
 * (e.g., MembersRepository, TransactionsRepository).
 */
class GetDashboardStatsUseCase {
    
    suspend operator fun invoke(): Result<DashboardStats> {
        // Simulate network delay for realistic loading state
        delay(800)
        
        // Returning dummy data that matches the UI/UX specification exactly
        val dummyData = DashboardStats(
            userName = "Abdulrahman",
            totalRevenue = 425000.00,
            revenueGrowthPercentage = 15.2,
            previousRevenue = 368000.00,
            currentlyActive = 142,
            capacityPercentage = 65,
            totalActiveMembers = 2845,
            expiringSoonMembers = 124,
            expiredMembers = 48
        )
        
        return Result.success(dummyData)
    }
}
