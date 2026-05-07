package com.example.trigen.screens.scan

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.example.trigen.triage.classifier.ClassificationResult
import com.example.trigen.triage.classifier.InjuryLabel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    onInjuryDetected: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var torchEnabled by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    var showManualSelection by remember { mutableStateOf(false) }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA) { granted ->
        if (granted) viewModel.onPermissionGranted()
        else viewModel.onPermissionDenied()
    }

    LaunchedEffect(Unit) {
        if (cameraPermission.status.isGranted) viewModel.onPermissionGranted()
        else cameraPermission.launchPermissionRequest()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Camera preview
        if (uiState is ScanUiState.Scanning || uiState is ScanUiState.Result || uiState is ScanUiState.Error) {
            CameraPreview(
                context = context,
                lifecycleOwner = lifecycleOwner,
                onFrameCaptured = { bitmap, rotation ->
                    if (uiState is ScanUiState.Scanning) viewModel.analyzeFrame(bitmap, rotation)
                },
                onCameraReady = { cam -> camera = cam }
            )
        }

        when (uiState) {
            is ScanUiState.Idle -> {}

            is ScanUiState.RequestingPermission -> {
                CenteredMessage("Requesting camera permission...")
            }

            is ScanUiState.PermissionDenied -> {
                PermissionDeniedContent(onBack = onBack)
            }

            is ScanUiState.Scanning -> {
                ScannerOverlay(isScanning = true)
                ScanningBottomBar()
            }

            is ScanUiState.Result -> {
                val result = (uiState as ScanUiState.Result).result
                ScannerOverlay(isScanning = false)
                ResultOverlay(
                    result = result,
                    onConfirm = { onInjuryDetected(result.label.name) },
                    onRescan = { viewModel.resetScan() }
                )
            }

            is ScanUiState.Error -> {
                ScannerOverlay(isScanning = false)
                ErrorOverlay(
                    message = (uiState as ScanUiState.Error).message,
                    onRescan = { viewModel.resetScan() }
                )
            }
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Injury Scanner",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
            )
            IconButton(onClick = {
                torchEnabled = !torchEnabled
                camera?.cameraControl?.enableTorch(torchEnabled)
            }) {
                Icon(
                    imageVector = if (torchEnabled) Icons.Default.FlashOn
                    else Icons.Default.FlashOff,
                    contentDescription = "Torch",
                    tint = if (torchEnabled) Color.Yellow else Color.White
                )
            }
        }

        // Manual Selection Button - Positioned just above the scan frame
        if (uiState is ScanUiState.Scanning || uiState is ScanUiState.Error) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = { showManualSelection = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.3f),
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .offset(y = (-180).dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Manually", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        if (showManualSelection) {
            ManualSelectionDialog(
                onDismiss = { showManualSelection = false },
                onSelect = { label ->
                    showManualSelection = false
                    onInjuryDetected(label.name)
                }
            )
        }
    }
}

@Composable
private fun ManualSelectionDialog(
    onDismiss: () -> Unit,
    onSelect: (InjuryLabel) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manual Selection") },
        text = {
            Column {
                Text(
                    "If the scanner is having trouble, please select the injury type manually:",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                InjuryLabel.entries
                    .filter { it != InjuryLabel.UNKNOWN }
                    .forEach { label ->
                        OutlinedButton(
                            onClick = { onSelect(label) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(label.displayName)
                        }
                    }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ScannerOverlay(isScanning: Boolean) {
    val viewfinderSize = 280.dp
    val cornerLength = 32.dp
    val cornerWidth = 4.dp
    val primaryColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    val cornerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cornerAlpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val vfSize = viewfinderSize.toPx()
        val left = (canvasWidth - vfSize) / 2
        val top = (canvasHeight - vfSize) / 2
        val right = left + vfSize
        val bottom = top + vfSize
        val cl = cornerLength.toPx()
        val cw = cornerWidth.toPx()

        val overlayColor = Color.Black.copy(alpha = 0.65f)
        drawRect(overlayColor, Offset(0f, 0f), Size(canvasWidth, top))
        drawRect(overlayColor, Offset(0f, bottom), Size(canvasWidth, canvasHeight - bottom))
        drawRect(overlayColor, Offset(0f, top), Size(left, vfSize))
        drawRect(overlayColor, Offset(right, top), Size(canvasWidth - right, vfSize))

        drawRoundRect(
            color = Color.White.copy(alpha = 0.15f),
            topLeft = Offset(left, top),
            size = Size(vfSize, vfSize),
            cornerRadius = CornerRadius(8.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )

        val cornerColor = if (isScanning) primaryColor.copy(alpha = cornerAlpha)
        else Color.Green.copy(alpha = 0.9f)

        drawCornerBrackets(left, top, right, bottom, cl, cw, cornerColor)

        if (isScanning) {
            val scanY = top + (vfSize * scanLineY)
            drawLine(
                color = primaryColor.copy(alpha = 0.8f),
                start = Offset(left + 16.dp.toPx(), scanY),
                end = Offset(right - 16.dp.toPx(), scanY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawCornerBrackets(
    left: Float, top: Float,
    right: Float, bottom: Float,
    cornerLength: Float, cornerWidth: Float,
    color: Color
) {
    val strokeWidth = cornerWidth
    val cap = StrokeCap.Round

    // Top-left
    drawLine(color, Offset(left, top), Offset(left, top + cornerLength), strokeWidth, cap)
    drawLine(color, Offset(left, top), Offset(left + cornerLength, top), strokeWidth, cap)

    // Top-right
    drawLine(color, Offset(right - cornerLength, top), Offset(right, top), strokeWidth, cap)
    drawLine(color, Offset(right, top), Offset(right, top + cornerLength), strokeWidth, cap)

    // Bottom-left
    drawLine(color, Offset(left, bottom - cornerLength), Offset(left, bottom), strokeWidth, cap)
    drawLine(color, Offset(left, bottom), Offset(left + cornerLength, bottom), strokeWidth, cap)

    // Bottom-right
    drawLine(color, Offset(right - cornerLength, bottom), Offset(right, bottom), strokeWidth, cap)
    drawLine(color, Offset(right, bottom - cornerLength), Offset(right, bottom), strokeWidth, cap)
}

@Composable
private fun ScanningBottomBar() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Point at the injured area",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Analyzing...",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ResultOverlay(
    result: ClassificationResult,
    onConfirm: () -> Unit,
    onRescan: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(colorScheme.secondaryContainer)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${"%.0f".format(result.confidence * 100)}% confidence",
                    fontSize = 12.sp,
                    color = colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = result.label.displayName,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.primary
            )
            Text(
                text = result.label.description,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Get First Aid Guidance", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onRescan,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Scan Again")
            }
        }
    }
}

@Composable
private fun ErrorOverlay(
    message: String,
    onRescan: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scanning Error",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = onRescan,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Try Again")
            }
        }
    }
}

@Composable
private fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onFrameCaptured: (Bitmap, Int) -> Unit,
    onCameraReady: (androidx.camera.core.Camera) -> Unit,
) {
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(executor) { imageProxy ->
                            val rotation = imageProxy.imageInfo.rotationDegrees
                            val bitmap = imageProxy.toBitmap()
                            onFrameCaptured(bitmap, rotation)
                            imageProxy.close()
                        }
                    }
                try {
                    cameraProvider.unbindAll()
                    val cam = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalyzer
                    )
                    onCameraReady(cam)
                } catch (e: Exception) {
                    Log.e("ScanScreen", "Camera binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@Composable
private fun PermissionDeniedContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera Permission Required",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "TriGen needs camera access to scan and identify injuries.",
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) { Text("Go Back") }
    }
}

@Composable
private fun CenteredMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = Color.White, textAlign = TextAlign.Center)
    }
}
