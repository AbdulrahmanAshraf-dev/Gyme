package com.example.gyme.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login      : Screen("login")
    object Home       : Screen("home")
    object Members    : Screen("members")
    object Finance    : Screen("finance")
    object More       : Screen("more")
    object AddMember  : Screen("add_member")
    object Attendance : Screen("attendance")
    object Notifications : Screen("notifications")
    object StaffManagement : Screen("staff_management")
    object Plans : Screen("plans")
    object UpdateMember : Screen("update_member/{memberId}") {
        fun createRoute(memberId: String) = "update_member/$memberId"
    }
}
