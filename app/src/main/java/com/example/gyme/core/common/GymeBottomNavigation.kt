package com.example.gyme.core.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gyme.theme.*

enum class GymeBottomTab {
    HOME, MEMBERS, ATTENDANCE, FINANCE, MORE
}

@Composable
fun GymeBottomNavigation(
    selectedTab: GymeBottomTab,
    onNavigateToHome: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToMore: () -> Unit
) {
    NavigationBar(
        containerColor = GymeCardBg,
        tonalElevation = 8.dp
    ) {
        GymeBottomNavigationItem(
            label = "HOME",
            icon = if (selectedTab == GymeBottomTab.HOME) Icons.Filled.GridView else Icons.Outlined.GridView,
            selected = selectedTab == GymeBottomTab.HOME,
            onClick = onNavigateToHome
        )
        GymeBottomNavigationItem(
            label = "MEMBERS",
            icon = if (selectedTab == GymeBottomTab.MEMBERS) Icons.Filled.People else Icons.Outlined.People,
            selected = selectedTab == GymeBottomTab.MEMBERS,
            onClick = onNavigateToMembers
        )
        GymeBottomNavigationItem(
            label = "ATTENDANCE",
            icon = if (selectedTab == GymeBottomTab.ATTENDANCE) Icons.Filled.QrCodeScanner else Icons.Outlined.QrCodeScanner,
            selected = selectedTab == GymeBottomTab.ATTENDANCE,
            onClick = onNavigateToAttendance
        )
        GymeBottomNavigationItem(
            label = "FINANCE",
            icon = if (selectedTab == GymeBottomTab.FINANCE) Icons.Filled.Payments else Icons.Outlined.Payments,
            selected = selectedTab == GymeBottomTab.FINANCE,
            onClick = onNavigateToFinance
        )
        GymeBottomNavigationItem(
            label = "MORE",
            icon = if (selectedTab == GymeBottomTab.MORE) Icons.Filled.Menu else Icons.Outlined.Menu,
            selected = selectedTab == GymeBottomTab.MORE,
            onClick = onNavigateToMore
        )
    }
}

@Composable
private fun RowScope.GymeBottomNavigationItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = GymePrimary,
            selectedTextColor = GymePrimary,
            indicatorColor = GymePrimaryLight,
            unselectedIconColor = GymeTextSecondary,
            unselectedTextColor = GymeTextSecondary
        )
    )
}
