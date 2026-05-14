package com.example.gyme.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gyme.core.model.DashboardStats
import com.example.gyme.theme.*
import com.example.gyme.core.ui.GymeBottomNavigation
import com.example.gyme.core.ui.GymeBottomTab
import com.example.gyme.data.remote.supabaseSdk.supabase
import com.example.gyme.util.CurrencyUtils
import io.github.jan.supabase.auth.auth
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToMembers: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToFinance: () -> Unit = {},
    onNavigateToMore: () -> Unit = {},
    onNavigateToAddMember: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Scaffold(
        containerColor = GymeBackground,
        bottomBar = {
            GymeBottomNavigation(
                selectedTab = GymeBottomTab.HOME,
                onNavigateToHome = {},
                onNavigateToMembers = onNavigateToMembers,
                onNavigateToAttendance = onNavigateToAttendance,
                onNavigateToFinance = onNavigateToFinance,
                onNavigateToMore = onNavigateToMore
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GymePrimary
                    )
                }
                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = "Error",
                            tint = GymeTextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = state.message, color = GymeTextPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadDashboardData() },
                            colors = ButtonDefaults.buttonColors(containerColor = GymePrimary)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                is HomeUiState.Success -> {
                    DashboardContent(
                        stats = state.stats,
                        onNavigateToAddMember = onNavigateToAddMember,
                        onNavigateToAttendance = onNavigateToAttendance,
                        onNavigateToNotifications = onNavigateToNotifications
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    stats: DashboardStats,
    onNavigateToAddMember: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HomeHeader(userName = stats.userName, onNavigateToNotifications = onNavigateToNotifications) }
        item { Spacer(modifier = Modifier.height(32.dp)) }
        item { HomeGreeting(userName = stats.userName) }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HomeQuickActions(onNavigateToAddMember, onNavigateToAttendance) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { RevenueCard(stats) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { ActiveAttendanceCard(stats) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { MemberStatusCard(stats) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun HomeHeader(userName: String, onNavigateToNotifications: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = GymePrimaryLight,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.padding(6.dp),
                    tint = GymeButtonText
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = userName,
                color = GymeTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        
        IconButton(onClick = onNavigateToNotifications) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = GymeTextPrimary
            )
        }
    }
}

@Composable
fun HomeGreeting(userName: String) {
    Text(text = "Overview", color = GymeTextSecondary, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Good Morning,\n$userName",
        color = GymeTextPrimary,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 36.sp
    )
}

@Composable
fun HomeQuickActions(
    onNavigateToAddMember: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onNavigateToAddMember,
            colors = ButtonDefaults.buttonColors(
                containerColor = GymeButtonBg,
                contentColor = GymeButtonText
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).height(48.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Member", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("NEW MEMBER", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onNavigateToAttendance,
            colors = ButtonDefaults.buttonColors(
                containerColor = GymeDarkSurface,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).height(48.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan ID", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("SCAN ID", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
fun RevenueCard(stats: DashboardStats) {
    
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = GymeTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Today's Revenue", color = GymeTextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = GymeTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = CurrencyUtils.formatEGP(stats.totalRevenue),
                    color = GymeTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = GymeIconBg,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "↑${stats.revenueGrowthPercentage}%",
                        color = GymeIconTint,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "vs. same day last week (${CurrencyUtils.formatEGP(stats.previousRevenue)})",
                color = GymeTextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ActiveAttendanceCard(stats: DashboardStats) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeDarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = GymePrimaryLight,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Currently Active", color = Color(0xFF9CA3AF), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${stats.currentlyActive}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF374151))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(stats.capacityPercentage / 100f)
                        .fillMaxHeight()
                        .background(GymePrimaryLight)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${stats.capacityPercentage}% of daily average capacity",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MemberStatusCard(stats: DashboardStats) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Member Status", color = GymeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("VIEW ALL", color = GymeTextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            StatusRow("Active Members", "${stats.totalActiveMembers}", GymePrimary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = GymeDivider)
            
            StatusRow("Expiring < 7 Days", "${stats.expiringSoonMembers}", GymeOrangeDot)
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = GymeDivider)
            
            StatusRow("Expired This Month", "${stats.expiredMembers}", GymeTextSecondary)
        }
    }
}

@Composable
fun StatusRow(label: String, value: String, dotColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = GymeTextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = GymeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
    }
}
