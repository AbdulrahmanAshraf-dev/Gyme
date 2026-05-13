package com.example.gyme.feature.members

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gyme.core.model.MemberStats
import com.example.gyme.core.model.MemberSummary
import com.example.gyme.theme.*

import com.example.gyme.core.ui.GymeBottomNavigation
import com.example.gyme.core.ui.GymeBottomTab
import com.example.gyme.core.ui.GymeStatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    viewModel: MembersViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToFinance: () -> Unit = {},
    onNavigateToMore: () -> Unit = {},
    onNavigateToAddMember: () -> Unit = {},
    onNavigateToUpdateMember: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = GymeBackground,
        topBar = { MembersTopBar(onNavigateToNotifications) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddMember,
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("ADD MEMBER", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            GymeBottomNavigation(
                selectedTab = GymeBottomTab.MEMBERS,
                onNavigateToHome = onNavigateToHome,
                onNavigateToMembers = {},
                onNavigateToAttendance = onNavigateToAttendance,
                onNavigateToFinance = onNavigateToFinance,
                onNavigateToMore = onNavigateToMore
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is MembersUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = GymePrimary)
                is MembersUiState.Error -> Text(state.message, modifier = Modifier.align(Alignment.Center))
                is MembersUiState.Success -> {
                    MembersContent(
                        stats = state.stats,
                        members = state.members,
                        selectedFilter = state.selectedFilter,
                        searchQuery = searchQuery,
                        onSearchQueryChanged = { 
                            searchQuery = it
                            viewModel.onSearchQueryChanged(it)
                        },
                        onFilterSelected = viewModel::onFilterSelected,
                        onMemberMenuClicked = viewModel::onMemberMenuClicked,
                        onMemberClicked = onNavigateToUpdateMember
                    )
                }
            }
        }
    }
}

@Composable
fun MembersContent(
    stats: MemberStats,
    members: List<MemberSummary>,
    selectedFilter: String,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onMemberMenuClicked: (String) -> Unit,
    onMemberClicked: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { MembersPageHeader() }
        item { MembersSearchBar(searchQuery, onSearchQueryChanged) }
        item { MembersStatsSection(stats) }
        item { FilterChipRow(selectedFilter, onFilterSelected) }
        items(members) { member ->
            MemberListItem(member, onMemberMenuClicked, onMemberClicked)
        }
        item { Spacer(modifier = Modifier.height(80.dp)) } // Space for FAB
    }
}

@Composable
fun MembersTopBar(onNavigateToNotifications: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GymeDarkSurface,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Gym Manager", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GymeTextPrimary)
        }
        IconButton(onClick = onNavigateToNotifications) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = GymeTextPrimary)
        }
    }
}

@Composable
fun MembersPageHeader() {
    Column {
        Text("Member Roster", fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = GymeTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Manage memberships, track statuses, and maintain your facility's operational precision.",
            fontSize = 14.sp, color = GymeTextSecondary, lineHeight = 20.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersSearchBar(query: String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)),
        placeholder = { Text("Search members by name or ID...", color = GymeTextSecondary, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GymeTextSecondary) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GymeDivider,
            unfocusedContainerColor = GymeDivider,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun MembersStatsSection(stats: MemberStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GymeStatCard(
            title = "TOTAL ACTIVE",
            value = stats.totalActive.toString(),
            subtext = stats.activeGrowth,
            isPositive = true,
            icon = Icons.Default.CheckCircle,
            iconColor = GymePrimary,
            iconBgColor = GymePrimaryLight
        )
        GymeStatCard(
            title = "PENDING ACTIVATION",
            value = stats.pendingActivation.toString(),
            subtext = "Requires action",
            isPositive = null,
            icon = Icons.Default.HourglassEmpty,
            iconColor = GymeTextSecondary,
            iconBgColor = GymeDivider
        )
        GymeStatCard(
            title = "RECENTLY EXPIRED",
            value = stats.recentlyExpired.toString(),
            subtext = "Past 7 days",
            isPositive = null,
            icon = Icons.Default.History,
            iconColor = GymeTextSecondary,
            iconBgColor = GymeDivider
        )
    }
}

@Composable
fun FilterChipRow(selected: String, onSelected: (String) -> Unit) {
    val filters = listOf("All Members", "Active Only", "VIP Plan")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { filter ->
            val isSelected = selected == filter
            Surface(
                onClick = { onSelected(filter) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color.Black else GymeDivider,
                modifier = Modifier.height(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        filter,
                        color = if (isSelected) Color.White else GymeTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MemberListItem(member: MemberSummary, onMenuClicked: (String) -> Unit, onMemberClicked: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onMemberClicked(member.id) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = GymeDivider, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(member.initials, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GymeTextPrimary)
                Text(member.plan, fontSize = 12.sp, color = GymeTextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(member.status)
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(onClick = { onMenuClicked(member.id) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = GymeTextSecondary)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String?) {
    val (bgColor, textColor, label) = when (status?.lowercase()) {
        "active" -> Triple(GymePrimaryLight, GymePrimary, "ACTIVE")
        "pending" -> Triple(GymePendingBg, GymePendingText, "PENDING")
        "expired" -> Triple(GymeExpiredBg, GymeExpiredText, "EXPIRED")
        else -> Triple(Color.LightGray, Color.DarkGray, status?.uppercase() ?: "UNKNOWN")
    }
    Surface(shape = RoundedCornerShape(16.dp), color = bgColor) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
