package com.example.gyme.feature.members.update

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.gyme.core.model.*
import com.example.gyme.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateMemberScreen(
    viewModel: UpdateMemberViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = GymeBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Update Member", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        BadgedBox(badge = { Badge { Text("") } }) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Button(
                    onClick = { viewModel.saveChanges() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GymeDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.member == null && uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GymePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Profile Header Card
                ProfileHeaderCard(uiState.member, uiState.displayId)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Admin Actions
                AdminActionsSection(
                    onRenew = { viewModel.renewMembership() },
                    onFreeze = { viewModel.freezeAccount() },
                    onBlock = { viewModel.blockMember() }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Personal Information
                SectionCard(title = "Personal Information") {
                    GymeUpdateTextField(
                        label = "Full Name",
                        value = uiState.member?.name ?: "",
                        onValueChange = viewModel::onNameChange
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    GymeUpdateTextField(
                        label = "Email Address",
                        value = uiState.member?.email ?: "",
                        onValueChange = viewModel::onEmailChange
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    GymeUpdateTextField(
                        label = "Phone Number",
                        value = uiState.member?.phone ?: "",
                        onValueChange = viewModel::onPhoneChange
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Membership Details
                MembershipDetailsSection(
                    selectedPlanId = uiState.member?.planId ?: "",
                    availablePlans = uiState.availablePlans,
                    goals = uiState.member?.goals ?: emptyList(),
                    onPlanChange = viewModel::onPlanChange
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Payment History
                PaymentHistorySection(uiState.paymentHistory)
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    if (uiState.isUpdateSuccess) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }
}

@Composable
fun ProfileHeaderCard(member: Member?, displayId: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Surface(
                    shape = CircleShape,
                    color = GymeDivider,
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.padding(20.dp),
                        tint = GymeTextSecondary
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = GymePrimaryLight,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Verified",
                        modifier = Modifier.padding(4.dp),
                        tint = GymePrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(member?.name ?: "Loading...", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text("ID: ${displayId.ifEmpty { "---" }}", color = GymeTextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                BadgeChip(text = member?.status?.replaceFirstChar { it.uppercase() } ?: "---", containerColor = GymePrimaryLight, contentColor = GymePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                BadgeChip(text = member?.planId ?: "---", containerColor = GymeDarkSurface, contentColor = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "0",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color.Black
            )
            Text("Sessions this Month", color = GymeTextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
fun BadgeChip(text: String, containerColor: Color, contentColor: Color) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AdminActionsSection(
    onRenew: () -> Unit,
    onFreeze: () -> Unit,
    onBlock: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Admin Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            AdminActionButton(
                text = "Renew Membership",
                icon = Icons.Default.Refresh,
                containerColor = Color(0xFFC1F1D4),
                contentColor = Color(0xFF007A33),
                onClick = onRenew
            )
            Spacer(modifier = Modifier.height(12.dp))
            AdminActionButton(
                text = "Freeze Account",
                icon = Icons.Outlined.AcUnit,
                containerColor = Color(0xFFEAEDF0),
                contentColor = Color(0xFF4B5563),
                onClick = onFreeze
            )
            Spacer(modifier = Modifier.height(12.dp))
            AdminActionButton(
                text = "Block Member",
                icon = Icons.Outlined.Block,
                containerColor = Color(0xFFFFE4E1),
                contentColor = Color(0xFFDC2626),
                onClick = onBlock
            )
        }
    }
}

@Composable
fun AdminActionButton(
    text: String, 
    icon: ImageVector, 
    containerColor: Color, 
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun GymeUpdateTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = GymeDivider,
                unfocusedContainerColor = GymeDivider,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun MembershipDetailsSection(
    selectedPlanId: String,
    availablePlans: List<MembershipPlan>,
    goals: List<String>,
    onPlanChange: (MembershipPlan) -> Unit
) {
    SectionCard(title = "Membership Details") {
        Text("Current Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        val currentPlan = availablePlans.find { it.id == selectedPlanId }
        Surface(
            color = GymeDivider,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().clickable { /* Show Dropdown */ }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    currentPlan?.let { "${it.name} - $${it.price}/mo" } ?: "Select a plan",
                    color = Color.Black
                )
                Icon(Icons.Default.UnfoldMore, contentDescription = null, tint = GymeTextSecondary)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Primary Fitness Goals", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            goals.forEach { goal ->
                Surface(
                    color = GymeDarkSurface,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        goal,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Surface(
                color = GymeDivider,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.clickable { /* Add Goal */ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Goal", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeholders = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val spacingPx = mainAxisSpacing.roundToPx()
        val crossSpacingPx = crossAxisSpacing.roundToPx()
        
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0
        
        placeholders.forEach { placeable ->
            if (currentRowWidth + placeable.width + spacingPx > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + spacingPx
        }
        rows.add(currentRow)
        
        val height = rows.sumOf { row -> row.maxOf { it.height } } + (rows.size - 1) * crossSpacingPx
        
        layout(constraints.maxWidth, height) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                val rowHeight = row.maxOf { it.height }
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + spacingPx
                }
                y += rowHeight + crossSpacingPx
            }
        }
    }
}

@Composable
fun PaymentHistorySection(history: List<PaymentRecord>) {
    SectionCard(title = "Payment History") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text("View Full History", color = GymePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { })
        }
        Spacer(modifier = Modifier.height(8.dp))
        history.forEach { record ->
            PaymentItem(record)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PaymentItem(record: PaymentRecord) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GymeDivider,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.padding(12.dp), tint = GymeTextSecondary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(record.planName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(record.date, color = GymeTextSecondary, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("$${String.format("%.2f", record.amount)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (record.isPaid) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = GymePrimary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Paid", color = GymePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
