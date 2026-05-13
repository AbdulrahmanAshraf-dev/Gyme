package com.example.gyme.feature.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gyme.core.model.StaffStats
import com.example.gyme.core.model.StaffMember
import com.example.gyme.theme.*

import com.example.gyme.core.ui.GymeBottomNavigation
import com.example.gyme.core.ui.GymeBottomTab
import com.example.gyme.core.ui.GymeStatCard
import com.example.gyme.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(
    viewModel: StaffViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToMembers: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToFinance: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is StaffUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = GymePrimary)
                is StaffUiState.Error -> Text(state.message, modifier = Modifier.align(Alignment.Center))
                is StaffUiState.Success -> {
                    StaffContent(
                        stats = state.stats,
                        topTrainer = state.topTrainer,
                        staffList = state.staffList,
                        onToggleAccess = viewModel::onToggleAccess
                    )
                }
            }
        }
    }
}

@Composable
fun StaffContent(
    stats: StaffStats,
    topTrainer: StaffMember,
    staffList: List<StaffMember>,
    onToggleAccess: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { StaffHeader() }
        item { Spacer(modifier = Modifier.height(32.dp)) }
        item { StaffPageHeader() }
        item { AddMemberButton() }
        item { StaffStatsSection(stats) }
        item { TopTrainerCard(topTrainer) }
        item { StaffSearchBar() }
        item { FilterSortSection() }
        items(staffList) { staff ->
            StaffListItem(staff, onToggleAccess)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun StaffHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = GymePrimaryLight, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(6.dp), tint = GymeButtonText)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Gym Manager", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GymeTextPrimary)
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = GymeTextPrimary)
        }
    }
}

@Composable
fun StaffPageHeader() {
    Column {
        Text("Staff Management", fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = GymeTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Oversee team roles, adjust system access levels, and monitor key performance indicators across your facility.",
            fontSize = 14.sp, color = GymeTextSecondary, lineHeight = 20.sp
        )
    }
}

@Composable
fun AddMemberButton() {
    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(containerColor = GymeDarkSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("ADD MEMBER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun StaffStatsSection(stats: StaffStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GymeStatCard(
            title = "TOTAL ACTIVE STAFF",
            value = stats.totalActiveStaff.toString(),
            subtext = stats.growthText,
            isPositive = true,
            icon = Icons.Outlined.ContactPage,
            iconBgColor = Color(0xFFEAF7F2),
            iconColor = GymePrimary
        )
        GymeStatCard(
            title = "ON SHIFT NOW",
            value = stats.onShiftNow.toString(),
            subtext = "Active sessions",
            isPositive = null,
            icon = Icons.Outlined.WatchLater,
            iconBgColor = Color(0xFFEAF7F2),
            iconColor = GymePrimary
        )
        GymeStatCard(
            title = "PENDING REQUESTS",
            value = stats.pendingRequests.toString(),
            subtext = "Requires review",
            isPositive = null,
            icon = Icons.Outlined.WorkOutline,
            iconBgColor = Color(0xFFF3F4F6),
            iconColor = GymeTextSecondary
        )
    }
}

@Composable
fun TopTrainerCard(trainer: StaffMember) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeDarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(140.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Placeholder for background pattern
            Icon(
                Icons.Default.Stars,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.size(100.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp)
            )
            
            Column(modifier = Modifier.padding(20.dp)) {
                Text("TOP PERFORMING TRAINER", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, border = androidx.compose.foundation.BorderStroke(2.dp, GymePrimary), modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(12.dp), tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(trainer.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.StarBorder, contentDescription = null, tint = GymePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${trainer.rating} Avg Rating", color = GymePrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)),
        placeholder = { Text("Search staff by name or role...", color = GymeTextSecondary, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GymeTextSecondary) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GymeDivider,
            unfocusedContainerColor = GymeDivider,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun FilterSortSection() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(shape = RoundedCornerShape(8.dp), color = GymeDivider) {
            Text("FILTER", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextPrimary)
        }
        Surface(shape = RoundedCornerShape(8.dp), color = GymeDivider) {
            Text("SORT: ROLE", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextPrimary)
        }
    }
}

@Composable
fun StaffListItem(staff: StaffMember, onToggleAccess: (String, Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(14.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(staff.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GymeTextPrimary)
                        Text(staff.email, fontSize = 12.sp, color = GymeTextSecondary)
                    }
                }
                Surface(shape = RoundedCornerShape(12.dp), color = GymePendingBg) {
                    Text(staff.role, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GymePendingText)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = GymeDivider)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(staff.accessLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GymeTextPrimary)
                    Text(staff.accessDescription, fontSize = 12.sp, color = GymeTextSecondary)
                }
                Switch(
                    checked = staff.isAccessEnabled,
                    onCheckedChange = { onToggleAccess(staff.id, it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GymePrimary)
                )
            }
        }
    }
}
