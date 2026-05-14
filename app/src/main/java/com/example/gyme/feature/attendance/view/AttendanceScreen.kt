package com.example.gyme.feature.attendance.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.gyme.R
import com.example.gyme.feature.attendance.viewmodel.AttendanceUiState
import com.example.gyme.feature.attendance.viewmodel.AttendanceViewModel
import com.example.gyme.theme.*
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToMore: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val isFlashOn by viewModel.isFlashOn.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        bottomBar = {
            GymeBottomNav(
                currentRoute = "attendance",
                onHomeClick = onNavigateToHome,
                onMembersClick = onNavigateToMembers,
                onAttendanceClick = { },
                onFinanceClick = onNavigateToFinance,
                onMoreClick = onNavigateToMore
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            if (hasCameraPermission) {
                CameraPreview(
                    isFlashOn = isFlashOn,
                    onBarcodeScanned = { barcode ->
                        viewModel.onBarCodeScanned(barcode)
                    }
                )
            }

            ScannerOverlay()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                ScannerHeader()
                
                Spacer(modifier = Modifier.weight(1f))
                
                ScannerInstructions()
                
                Spacer(modifier = Modifier.weight(1f))
                
                ScannerActions(
                    isFlashOn = isFlashOn,
                    onFlashToggle = { viewModel.toggleFlash() },
                    onManualEntry = { /* TODO */ }
                )
                
                Spacer(modifier = Modifier.height(120.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 140.dp)
            ) {
                when (val state = uiState) {
                    is AttendanceUiState.SubscriptionExpired -> {
                        StatusAlert(
                            title = stringResource(R.string.subscription_expired),
                            message = stringResource(R.string.renewal_required),
                            color = GymeError, 
                            onDismiss = { viewModel.resetState() }
                        )
                    }
                    is AttendanceUiState.Success -> {
                        StatusAlert(
                            title = stringResource(R.string.checkin_success),
                            message = stringResource(R.string.welcome_back, state.memberName),
                            color = GymePrimary,
                            onDismiss = { viewModel.resetState() }
                        )
                    }
                    is AttendanceUiState.Error -> {
                        StatusAlert(
                            title = stringResource(R.string.error),
                            message = state.message,
                            color = Color.Gray,
                            onDismiss = { viewModel.resetState() }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    isFlashOn: Boolean,
    onBarcodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    var camera: Camera? by remember { mutableStateOf(null) }

    LaunchedEffect(isFlashOn) {
        camera?.cameraControl?.enableTorch(isFlashOn)
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val scanner = BarcodeScanning.getClient(
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                )

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                barcodes.firstOrNull()?.rawValue?.let {
                                    onBarcodeScanned(it)
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, executor)
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ScannerOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 3.dp.toPx()
        val cornerLength = 40.dp.toPx()
        val rectSize = 280.dp.toPx()
        val cornerRadius = 24.dp.toPx()
        
        val left = (size.width - rectSize) / 2
        val top = (size.height - rectSize) / 2
        val right = left + rectSize
        val bottom = top + rectSize

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
            
            addRoundRect(
                RoundRect(
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom,
                    cornerRadius = CornerRadius(cornerRadius)
                )
            )
        }
        drawPath(path, color = Color.Black.copy(alpha = 0.6f), style = Fill)

        val frameColor = GymePrimary
        
        drawPath(
            Path().apply {
                moveTo(left, top + cornerLength)
                lineTo(left, top + cornerRadius)
                quadraticTo(left, top, left + cornerRadius, top)
                lineTo(left + cornerLength, top)
            },
            color = frameColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawPath(
            Path().apply {
                moveTo(right - cornerLength, top)
                lineTo(right - cornerRadius, top)
                quadraticTo(right, top, right, top + cornerRadius)
                lineTo(right, top + cornerLength)
            },
            color = frameColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawPath(
            Path().apply {
                moveTo(left, bottom - cornerLength)
                lineTo(left, bottom - cornerRadius)
                quadraticTo(left, bottom, left + cornerRadius, bottom)
                lineTo(left + cornerLength, bottom)
            },
            color = frameColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawPath(
            Path().apply {
                moveTo(right, bottom - cornerLength)
                lineTo(right, bottom - cornerRadius)
                quadraticTo(right, bottom, right - cornerRadius, bottom)
                lineTo(right - cornerLength, bottom)
            },
            color = frameColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ScannerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.gym_manager),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ScannerInstructions() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
    ) {
        Text(
            text = stringResource(R.string.scan_qr_instruction),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.scan_qr_frame_hint),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ScannerActions(
    isFlashOn: Boolean,
    onFlashToggle: () -> Unit,
    onManualEntry: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .clickable { onFlashToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Row(
            modifier = Modifier
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .clickable { onManualEntry() }
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Keyboard,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.manual_entry),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun StatusAlert(
    title: String,
    message: String,
    color: Color,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GymeBottomNav(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onMembersClick: () -> Unit,
    onAttendanceClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.GridView,
                label = stringResource(R.string.nav_home),
                isSelected = currentRoute == "home",
                onClick = onHomeClick
            )
            NavItem(
                icon = Icons.Default.Group,
                label = stringResource(R.string.nav_members),
                isSelected = currentRoute == "members",
                onClick = onMembersClick
            )
            
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (currentRoute == "attendance") GymePrimaryLight else Color.Transparent)
                    .clickable { onAttendanceClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = if (currentRoute == "attendance") GymePrimary else GymeTextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = stringResource(R.string.nav_attendance),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentRoute == "attendance") GymePrimary else GymeTextSecondary
                    )
                }
            }

            NavItem(
                icon = Icons.Default.Payments,
                label = stringResource(R.string.nav_finance),
                isSelected = currentRoute == "finance",
                onClick = onFinanceClick
            )
            NavItem(
                icon = Icons.Default.Menu,
                label = stringResource(R.string.nav_more),
                isSelected = currentRoute == "more",
                onClick = onMoreClick
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) GymePrimary else GymeTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) GymePrimary else GymeTextSecondary
        )
    }
}
