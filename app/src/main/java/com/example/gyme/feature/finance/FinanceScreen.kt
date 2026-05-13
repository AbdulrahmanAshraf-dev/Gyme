package com.example.gyme.feature.finance

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gyme.core.model.ExpenseRequest
import com.example.gyme.core.model.FinancialStats
import com.example.gyme.theme.*
import com.example.gyme.core.ui.GymeBottomNavigation
import com.example.gyme.core.ui.GymeBottomTab
import com.example.gyme.core.ui.GymeStatCard
import com.example.gyme.util.CurrencyUtils
import com.example.gyme.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToMembers: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToMore: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = GymeBackground,
        bottomBar = {
            GymeBottomNavigation(
                selectedTab = GymeBottomTab.FINANCE,
                onNavigateToHome = onNavigateToHome,
                onNavigateToMembers = onNavigateToMembers,
                onNavigateToAttendance = onNavigateToAttendance,
                onNavigateToFinance = {},
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
                is FinanceUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GymePrimary
                    )
                }
                is FinanceUiState.Error -> {
                    Text(
                        text = state.message,
                        color = GymeTextPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is FinanceUiState.Success -> {
                    FinanceContent(
                        stats = state.stats,
                        pendingRequests = state.pendingRequests,
                        onApprove = viewModel::onApproveExpense,
                        onReview = viewModel::onReviewExpense
                    )
                }
            }
        }
    }
}

@Composable
fun FinanceContent(
    stats: FinancialStats,
    pendingRequests: List<ExpenseRequest>,
    onApprove: (String) -> Unit,
    onReview: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { FinanceHeader() }
        item { Spacer(modifier = Modifier.height(32.dp)) }
        item { PageHeader(stats.currentMonth) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { KpiCardsList(stats) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { RevenueTrendWidget() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { PendingApprovalsWidget(pendingRequests, onApprove, onReview) }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun FinanceHeader() {
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
                text = "Gym Manager",
                color = GymeTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = GymeTextPrimary
            )
        }
    }
}

@Composable
fun PageHeader(currentMonth: String) {
    Column {
        Text(
            text = "Financial Overview",
            color = GymeTextPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Track monthly revenue, manage expenses, and approve staff requests.",
            color = GymeTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = GymeCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, GymeDivider),
            modifier = Modifier.wrapContentWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = GymeTextPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = currentMonth, color = GymeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GymeTextPrimary)
            }
        }
    }
}

@Composable
fun KpiCardsList(stats: FinancialStats) {
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GymeStatCard(
            title = "TOTAL REVENUE",
            value = CurrencyUtils.formatEGP(stats.totalRevenue),
            subtext = stats.revenueTrend,
            isPositive = stats.isRevenuePositive,
            icon = Icons.Outlined.TrendingUp,
            iconBgColor = GymePrimaryLight,
            iconColor = GymePrimary
        )
        GymeStatCard(
            title = "TOTAL EXPENSES",
            value = CurrencyUtils.formatEGP(stats.totalExpenses),
            subtext = stats.expensesTrend,
            isPositive = null, 
            icon = Icons.Outlined.Receipt,
            iconBgColor = Color(0xFFF3F4F6),
            iconColor = Color(0xFF4B5563)
        )
        GymeStatCard(
            title = "NET PROFIT",
            value = CurrencyUtils.formatEGP(stats.netProfit),
            subtext = stats.netProfitTrend,
            isPositive = stats.isNetProfitPositive,
            icon = Icons.Outlined.AccountBalance,
            iconBgColor = Color(0xFFE0E7FF),
            iconColor = Color(0xFF4F46E5)
        )
    }
}

@Composable
fun RevenueTrendWidget() {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Revenue Trend", color = GymeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                Surface(
                    color = GymeDivider,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        Text(
                            text = "Weekly",
                            color = GymeTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        Surface(
                            color = Color.Black,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Monthly",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    HorizontalDivider(color = GymeDivider)
                    HorizontalDivider(color = GymeDivider)
                    HorizontalDivider(color = GymeDivider)
                    HorizontalDivider(color = GymeDivider)
                }
                
                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 16.dp, end = 8.dp)
                ) {
                    Text(
                        text = "$124.5k",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("MAY", "JUN", "JUL", "AUG", "SEP", "OCT").forEach {
                    Text(text = it, color = GymeTextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PendingApprovalsWidget(
    requests: List<ExpenseRequest>,
    onApprove: (String) -> Unit,
    onReview: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Pending Approvals", color = GymeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "${requests.size} NEW",
                        color = Color(0xFFDC2626),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            requests.forEach { request ->
                ExpenseRequestCard(request, onApprove, onReview)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "VIEW ALL REQUESTS",
                color = GymeTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ExpenseRequestCard(
    request: ExpenseRequest,
    onApprove: (String) -> Unit,
    onReview: (String) -> Unit
) {
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GymeCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, GymeDivider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0E7FF),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = request.staffInitials, color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = request.staffName, color = GymeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = request.description, color = GymeTextSecondary, fontSize = 12.sp)
                    }
                }
                Text(
                    text = CurrencyUtils.formatEGP(request.amount),
                    color = GymeTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onApprove(request.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = GymeButtonBg, contentColor = GymeButtonText),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Approve", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                
                OutlinedButton(
                    onClick = { onReview(request.id) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GymeDivider),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GymeTextPrimary)
                ) {
                    Text("Review", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
