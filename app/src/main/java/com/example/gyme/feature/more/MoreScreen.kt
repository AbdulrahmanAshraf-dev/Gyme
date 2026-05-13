package com.example.gyme.feature.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gyme.R
import com.example.gyme.core.ui.GymeBottomNavigation
import com.example.gyme.core.ui.GymeBottomTab
import com.example.gyme.theme.*
import com.example.gyme.util.LanguageManager
import com.example.gyme.util.SessionManager
import kotlinx.coroutines.launch

@Composable
fun MoreScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToPlans: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val languageManager = remember { LanguageManager(context) }
    val currentLanguage by languageManager.languageCode.collectAsState(initial = LanguageManager.ENGLISH)
    
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = GymeBackground,
        bottomBar = {
            GymeBottomNavigation(
                selectedTab = GymeBottomTab.MORE,
                onNavigateToHome = onNavigateToHome,
                onNavigateToMembers = onNavigateToMembers,
                onNavigateToAttendance = onNavigateToAttendance,
                onNavigateToFinance = onNavigateToFinance,
                onNavigateToMore = {}
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { MoreHeader(onNavigateToNotifications) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Section: Manage Plans
            item {
                Text(
                    "Manage Plans",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymeTextPrimary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            item {
                MoreMenuItem(
                    title = "Membership Plans",
                    icon = Icons.Outlined.CreditCard,
                    onClick = onNavigateToPlans
                )
            }
            item {
                MoreMenuItem(
                    title = "Gym Services",
                    icon = Icons.Outlined.List,
                    onClick = { /* TODO */ }
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item {
                Text(
                    "Gym Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymeTextPrimary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            item {
                MoreMenuItem(
                    title = "Attendance QR Code",
                    icon = Icons.Outlined.QrCode,
                    onClick = { /* TODO */ }
                )
            }
            item {
                MoreMenuItem(
                    title = stringResource(R.string.staff_management),
                    icon = Icons.Outlined.People,
                    onClick = onNavigateToStaff
                )
            }
            item {
                MoreMenuItem(
                    title = stringResource(R.string.language_settings),
                    subtitle = if (currentLanguage == LanguageManager.ENGLISH) "English" else "العربية",
                    icon = Icons.Outlined.Language,
                    onClick = { showLanguageDialog = true }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
                MoreMenuItem(
                    title = stringResource(R.string.logout),
                    icon = Icons.Outlined.Logout,
                    tint = GymeError,
                    onClick = {
                        SessionManager.clear(context)
                        onLogout()
                    }
                )
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { lang ->
                scope.launch {
                    languageManager.setLanguage(lang)
                    showLanguageDialog = false
                }
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

@Composable
fun MoreHeader(onNavigateToNotifications: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = GymeDarkSurface, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Gym Manager", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GymeTextPrimary)
        }
        IconButton(onClick = onNavigateToNotifications) {
            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = GymeTextPrimary)
        }
    }
}

@Composable
fun MoreMenuItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    tint: Color = GymeTextPrimary,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GymeDivider),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = tint)
                if (subtitle != null) {
                    Text(subtitle, fontSize = 13.sp, color = GymeTextSecondary)
                }
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GymeTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                LanguageOption(
                    title = "English",
                    isSelected = currentLanguage == LanguageManager.ENGLISH,
                    onClick = { onLanguageSelected(LanguageManager.ENGLISH) }
                )
                LanguageOption(
                    title = "العربية",
                    isSelected = currentLanguage == LanguageManager.ARABIC,
                    onClick = { onLanguageSelected(LanguageManager.ARABIC) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = GymePrimary)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
fun LanguageOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) GymePrimary else GymeTextPrimary
        )
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = GymePrimary, modifier = Modifier.size(20.dp))
        }
    }
}
