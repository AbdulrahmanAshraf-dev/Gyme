package com.example.gyme.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login      : Screen("login")
    object Home       : Screen("home")
}
