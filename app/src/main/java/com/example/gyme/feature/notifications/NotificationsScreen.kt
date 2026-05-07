package com.example.gyme.feature.notifications

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gyme.core.ui.GymeBottomNavigation
import com.example.gyme.core.ui.GymeBottomTab
import com.example.gyme.domain.model.Notification
import com.example.gyme.domain.model.NotificationType
import com.example.gyme.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = GymeBackground,
        topBar = {
            NotificationsTopBar(onNavigateBack)
        },
        bottomBar = {
            GymeBottomNavigation(
                selectedTab = GymeBottomTab.MORE,
                onNavigateToHome = onNavigateToHome,
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
                is NotificationsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GymePrimary
                    )
                }
                is NotificationsUiState.Success -> {
                    NotificationsContent(
                        notifications = state.notifications,
                        selectedFilter = state.filter,
                        onFilterSelected = viewModel::setFilter
                    )
                }
                is NotificationsUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = GymeError
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationsTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = GymeDarkSurface,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.padding(8.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Gym Manager",
                color = GymeTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Box {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = GymeTextPrimary,
                modifier = Modifier.size(24.dp)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(GymePrimary)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun NotificationsContent(
    notifications: List<Notification>,
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
    ) {
        item {
            Text(
                text = "Notification Center",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GymeTextPrimary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        
        item {
            FilterRow(selectedFilter, onFilterSelected)
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (notifications.isEmpty()) {
            item {
                EmptyNotificationsState()
            }
        } else {
            items(notifications) { notification ->
                NotificationCard(notification)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "END OF HISTORY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymeTextSecondary.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FilterRow(selected: NotificationFilter, onSelected: (NotificationFilter) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GymeDivider)
            .padding(4.dp)
    ) {
        FilterChip(
            text = "All",
            isSelected = selected == NotificationFilter.ALL,
            onClick = { onSelected(NotificationFilter.ALL) }
        )
        FilterChip(
            text = "Unread",
            isSelected = selected == NotificationFilter.UNREAD,
            onClick = { onSelected(NotificationFilter.UNREAD) }
        )
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        modifier = Modifier.height(36.dp).width(100.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) GymeTextPrimary else GymeTextSecondary,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun NotificationCard(notification: Notification) {
    data class NotificationStyle(
        val icon: ImageVector,
        val bgColor: Color,
        val tint: Color,
        val badgeText: String?,
        val badgeColor: Color,
        val badgeTextColor: Color
    )

    val style = when (notification.type) {
        NotificationType.SUBSCRIPTION -> NotificationStyle(
            Icons.Default.CalendarToday, GymePrimaryLight, GymePrimary, "Action Required", GymePrimaryLight, GymePrimary
        )
        NotificationType.PAYMENT -> NotificationStyle(
            Icons.Default.Wallet, GymePendingBg, GymePendingText, "Billing", GymeDivider, GymeTextSecondary
        )
        NotificationType.SYSTEM -> NotificationStyle(
            Icons.Default.Refresh, GymeDivider, GymeTextSecondary, null, Color.Transparent, Color.Transparent
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = style.bgColor,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = null,
                    tint = style.tint,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymeTextPrimary
                    )
                    Text(
                        text = notification.createdAt,
                        fontSize = 12.sp,
                        color = GymeTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = GymeTextSecondary,
                    lineHeight = 20.sp
                )
                
                if (style.badgeText != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = style.badgeColor,
                        modifier = Modifier.height(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text(
                                text = style.badgeText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = style.badgeTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder for illustration
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(GymeDivider),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CloudQueue,
                contentDescription = null,
                tint = GymeTextSecondary.copy(alpha = 0.3f),
                modifier = Modifier.size(80.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "You're all caught up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GymeTextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "No new notifications to show right now.\nEnjoy your focused workflow.",
            fontSize = 15.sp,
            color = GymeTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}
