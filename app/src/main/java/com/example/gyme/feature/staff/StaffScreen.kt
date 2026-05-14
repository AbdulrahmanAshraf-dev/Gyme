package com.example.gyme.feature.staff

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gyme.core.model.StaffStats
import com.example.gyme.core.model.StaffMember
import com.example.gyme.theme.*

import com.example.gyme.core.ui.GymeBottomNavigation
import com.example.gyme.core.ui.GymeBottomTab
import com.example.gyme.core.ui.GymeStatCard

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

    var showAddStaffSheet by remember { mutableStateOf(false) }

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddStaffSheet = true },
                containerColor = GymePrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Staff")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is StaffUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = GymePrimary)
                is StaffUiState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = Color.Red, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Row {
                            Button(onClick = { 
                                // Instead of retry, if it's a permission error, suggest login
                                viewModel.loadStaffData() 
                            }, colors = ButtonDefaults.buttonColors(containerColor = GymePrimary)) {
                                Text("Retry")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedButton(onClick = { onNavigateToHome() }) {
                                Text("Login as Admin")
                            }
                        }
                    }
                }
                is StaffUiState.Success -> {
                    StaffContent(
                        stats = state.stats,
                        topTrainer = state.topTrainer,
                        staffList = state.staffList,
                        onToggleAccess = viewModel::onToggleAccess,
                        onTogglePermission = viewModel::togglePermission,
                        onDeleteStaff = viewModel::onDeleteStaff
                    )
                }
            }

            if (showAddStaffSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showAddStaffSheet = false },
                    containerColor = Color.White,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    AddStaffForm(
                        onAddStaff = { name, email, password, permissions ->
                            viewModel.createStaffAccount(name, email, password, permissions)
                            showAddStaffSheet = false
                        },
                        onCancel = { showAddStaffSheet = false }
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
    onToggleAccess: (String, Boolean) -> Unit,
    onTogglePermission: (String, String, Boolean) -> Unit,
    onDeleteStaff: (String) -> Unit
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
            StaffListItem(staff, onToggleAccess, onTogglePermission, onDeleteStaff)
            Spacer(modifier = Modifier.height(16.dp))
        }
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
fun StaffListItem(
    staff: StaffMember, 
    onToggleAccess: (String, Boolean) -> Unit,
    onTogglePermission: (String, String, Boolean) -> Unit,
    onDeleteStaff: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, modifier = Modifier.size(56.dp), color = GymeDivider) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(14.dp), tint = GymeTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(staff.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GymeTextPrimary)
                        Text(staff.email, fontSize = 12.sp, color = GymeTextSecondary)
                    }
                }
                Surface(shape = RoundedCornerShape(12.dp), color = if (staff.isAccessEnabled) GymePrimaryLight else GymeDivider) {
                    Text(
                        staff.role, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = if (staff.isAccessEnabled) GymePrimary else GymeTextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = GymeDivider)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("System Access", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (staff.isAccessEnabled) GymeTextPrimary else Color.Red)
                    Text(if (staff.isAccessEnabled) "Account is Active" else "Account is Blocked", fontSize = 12.sp, color = GymeTextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onDeleteStaff(staff.id) }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = staff.isAccessEnabled,
                        onCheckedChange = { onToggleAccess(staff.id, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White, 
                            checkedTrackColor = GymePrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Red.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            
            if (staff.isAccessEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Permissions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GymeTextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                
                PermissionToggle(
                    title = "Add Members",
                    icon = Icons.Default.PersonAdd,
                    isEnabled = staff.canAddMember,
                    onToggle = { onTogglePermission(staff.id, "add", it) }
                )
                PermissionToggle(
                    title = "Edit Members",
                    icon = Icons.Default.Edit,
                    isEnabled = staff.canEditMember,
                    onToggle = { onTogglePermission(staff.id, "edit", it) }
                )
                PermissionToggle(
                    title = "Delete Members",
                    icon = Icons.Default.Delete,
                    isEnabled = staff.canDeleteMember,
                    onToggle = { onTogglePermission(staff.id, "delete", it) }
                )
                PermissionToggle(
                    title = "Manage Finance",
                    icon = Icons.Default.Payments,
                    isEnabled = staff.canManageFinance,
                    onToggle = { onTogglePermission(staff.id, "finance", it) }
                )
            }
        }
    }
}

@Composable
fun PermissionToggle(title: String, icon: ImageVector, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isEnabled) GymePrimary.copy(alpha = 0.05f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(22.dp), 
                tint = if (isEnabled) GymePrimary else Color(0xFF9CA3AF)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                title, 
                fontSize = 15.sp, 
                fontWeight = if (isEnabled) FontWeight.Bold else FontWeight.Medium,
                color = if (isEnabled) Color.Black else Color(0xFF6B7280)
            )
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, 
                checkedTrackColor = GymePrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB)
            ),
            modifier = Modifier.scale(0.85f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffForm(
    onAddStaff: (String, String, String, Map<String, Boolean>) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var canAdd by remember { mutableStateOf(false) }
    var canEdit by remember { mutableStateOf(false) }
    var canDelete by remember { mutableStateOf(false) }
    var canFinance by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Create Staff Account", fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = Color.Black)
        Text("Enter details and set permissions for the new staff member.", fontSize = 14.sp, color = Color(0xFF4B5563))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GymeAddStaffField(label = "Full Name", value = name, onValueChange = { name = it }, icon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(20.dp))
        GymeAddStaffField(label = "Email Address", value = email, onValueChange = { email = it }, icon = Icons.Default.Email)
        Spacer(modifier = Modifier.height(20.dp))
        GymeAddStaffField(label = "Password", value = password, onValueChange = { password = it }, icon = Icons.Default.Lock, isPassword = true)
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("Initial Permissions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        
        PermissionToggle(title = "Add Members", icon = Icons.Default.PersonAdd, isEnabled = canAdd, onToggle = { canAdd = it })
        PermissionToggle(title = "Edit Members", icon = Icons.Default.Edit, isEnabled = canEdit, onToggle = { canEdit = it })
        PermissionToggle(title = "Delete Members", icon = Icons.Default.Delete, isEnabled = canDelete, onToggle = { canDelete = it })
        PermissionToggle(title = "Manage Finance", icon = Icons.Default.Payments, isEnabled = canFinance, onToggle = { canFinance = it })
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                onAddStaff(name, email, password, mapOf(
                    "add" to canAdd,
                    "edit" to canEdit,
                    "delete" to canDelete,
                    "finance" to canFinance
                ))
            },
            enabled = name.isNotBlank() && 
                      android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && 
                      password.length >= 6,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GymePrimary,
                disabledContainerColor = GymeDivider
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel", color = GymeTextSecondary, fontWeight = FontWeight.Medium)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GymeAddStaffField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, contentDescription = null, tint = GymePrimary, modifier = Modifier.size(22.dp)) },
            visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = GymePrimary,
                unfocusedContainerColor = Color(0xFFF9FAFB),
                focusedContainerColor = Color.White,
                unfocusedLabelColor = GymeTextSecondary,
                focusedLabelColor = GymePrimary,
                cursorColor = GymePrimary
            )
        )
    }
}
