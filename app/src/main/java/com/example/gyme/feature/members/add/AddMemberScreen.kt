package com.example.gyme.feature.members.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gyme.core.model.MembershipPlan
import com.example.gyme.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    viewModel: AddMemberViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = GymeBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Member", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
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
                    onClick = { viewModel.addMember() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GymeButtonBg),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Member", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            ProfileImagePicker(uiState.memberId)
            Spacer(modifier = Modifier.height(32.dp))
            
            // Personal Information Section
            SectionCard(title = "Personal Information") {
                GymeTextField(
                    label = "FULL NAME",
                    value = uiState.name,
                    onValueChange = viewModel::onFullNameChange,
                    placeholder = "e.g. Alex Johnson"
                )
                Spacer(modifier = Modifier.height(16.dp))
                PhoneNumberField(uiState.phone, viewModel::onPhoneNumberChange)
                Spacer(modifier = Modifier.height(16.dp))
                GenderSelector(uiState.gender, viewModel::onGenderChange)
                Spacer(modifier = Modifier.height(16.dp))
                DatePickerField(
                    label = "SUBSCRIPTION START",
                    date = uiState.subscriptionStart,
                    onDateSelected = viewModel::onSubscriptionStartChange
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Subscription Section
            SectionCard(title = "Subscription", badge = "UNPAID") {
                PlanSelector(uiState.selectedPlan, uiState.availablePlans, viewModel::onPlanChange)
                Spacer(modifier = Modifier.height(16.dp))
                DatePickerField(
                    label = "SUBSCRIPTION START",
                    date = uiState.subscriptionStart,
                    onDateSelected = viewModel::onSubscriptionStartChange
                )
                Spacer(modifier = Modifier.height(16.dp))
                DatePickerField(
                    label = "SUBSCRIPTION END",
                    date = uiState.subscriptionEnd,
                    onDateSelected = viewModel::onSubscriptionEndChange
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Payment Summary Section
            PaymentSummary(
                subtotal = uiState.selectedPlan?.price ?: 0.0,
                discount = uiState.discount,
                discountType = uiState.discountType,
                onDiscountChange = viewModel::onDiscountChange,
                onDiscountTypeChange = viewModel::onDiscountTypeChange
            )
            
            Spacer(modifier = Modifier.height(100.dp)) // Extra space for FAB/BottomBar
        }
    }

    if (uiState.isSuccess) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }
}

@Composable
fun ProfileImagePicker(id: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            Surface(
                shape = CircleShape,
                color = GymeDivider,
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp),
                    tint = GymeTextSecondary
                )
            }
            Surface(
                shape = CircleShape,
                color = GymePrimary,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .clickable { /* Pick Image */ }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Photo",
                    modifier = Modifier.padding(6.dp),
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = GymePrimaryLight,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "NEW MEMBER",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = GymePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = GymeDivider,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "ID: $id",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = GymeTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Fill out the information below to register\na new member to the gym.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = GymeTextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SectionCard(title: String, badge: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                if (badge != null) {
                    Surface(
                        color = GymeDivider,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(GymeTextSecondary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
fun GymeTextField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            placeholder = { Text(placeholder, color = GymeTextSecondary) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = GymeDivider,
                unfocusedContainerColor = GymeDivider,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
    }
}

@Composable
fun PhoneNumberField(value: String, onValueChange: (String) -> Unit) {
    Column {
        Text("PHONE NUMBER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = GymeDivider,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("+1", fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = { Text("(555) 000-0000", color = GymeTextSecondary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GymeDivider,
                    unfocusedContainerColor = GymeDivider,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun GenderSelector(selected: String, onGenderChange: (String) -> Unit) {
    Column {
        Text("GENDER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = GymeDivider,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf("male", "female").forEach { gender ->
                    val isSelected = selected == gender
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onGenderChange(gender) },
                        color = if (isSelected) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            gender.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (isSelected) GymePrimary else GymeTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerField(label: String, date: Date?, onDateSelected: (Date) -> Unit = {}) {
    val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = GymeDivider,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    // In a real app, show material date picker
                    // For now, simulate by setting to current date or picking tomorrow
                    onDateSelected(Date()) 
                }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (date != null) format.format(date) else "mm/dd/yyyy",
                    color = if (date != null) Color.Black else GymeTextSecondary
                )
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GymeTextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun PlanSelector(selected: MembershipPlan?, plans: List<MembershipPlan>, onPlanChange: (MembershipPlan) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    
    Column {
        Text("SELECT PLAN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = GymeDivider,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?.let { "${it.name} ($${it.price})" } ?: "Select a membership plan...",
                    color = if (selected != null) Color.Black else GymeTextSecondary
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GymeTextSecondary)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Membership Plan", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn {
                    items(plans) { plan ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { 
                                    onPlanChange(plan)
                                    showDialog = false 
                                },
                            color = if (selected?.id == plan.id) GymePrimaryLight else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(plan.name, fontWeight = FontWeight.Bold)
                                    Text("${plan.durationMonths} Month(s)", fontSize = 12.sp, color = GymeTextSecondary)
                                }
                                Text("$${plan.price}", fontWeight = FontWeight.ExtraBold, color = GymePrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = GymePrimary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun PaymentSummary(
    subtotal: Double,
    discount: String,
    discountType: String,
    onDiscountChange: (String) -> Unit,
    onDiscountTypeChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeDarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Assignment, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Payment Summary", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp)
                Text("$${String.format("%.2f", subtotal)}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Discount", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            Surface(
                                color = if (discountType == "percentage") Color.White else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { onDiscountTypeChange("percentage") }
                            ) {
                                Text("%", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                color = if (discountType == "fixed") Color.White else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { onDiscountTypeChange("fixed") }
                            ) {
                                Text("$", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(discount, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Total Due", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                val total = if (discountType == "percentage") {
                    subtotal * (1 - (discount.toDoubleOrNull() ?: 0.0) / 100)
                } else {
                    subtotal - (discount.toDoubleOrNull() ?: 0.0)
                }
                Text("$${String.format("%.2f", total)}", color = GymePrimaryLight, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
