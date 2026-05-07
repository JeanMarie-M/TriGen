package com.example.trigen.screens.cpr

import android.os.PowerManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel



@Composable
fun CprScreen(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: CprViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // Keep screen on during CPR
    DisposableEffect(Unit) {
        val powerManager = context.getSystemService(PowerManager::class.java)
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "TriGen:CprWakeLock"
        )
        wakeLock.acquire(10 * 60 * 1000L) // 10 minutes max
        onDispose { if (wakeLock.isHeld) wakeLock.release() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                viewModel.stop()
                onBack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.onBackground
                )
            }
            Text(
                text = "CPR Guide",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Call 911 First",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pulse circle
            item {
                Spacer(modifier = Modifier.height(16.dp))
                PulseCircle(isActive = uiState.isRunning)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // BPM display
            item {
                Text(
                    text = "${uiState.bpm} BPM",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (uiState.isRunning) colorScheme.primary else colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (uiState.isRunning) "PUSH HARD AND FAST" else "Ready to start",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isRunning) colorScheme.primary else colorScheme.onBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Compression counter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.surface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = "${uiState.compressionCount}",
                        label = "Compressions"
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    StatItem(
                        value = "${uiState.cycleCount}",
                        label = "Cycles (30)"
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    StatItem(
                        value = uiState.elapsedTime,
                        label = "Elapsed"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Rate Rate slider
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rate",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "100–120 BPM recommended",
                            fontSize = 11.sp,
                            color = colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Slider(
                        value = uiState.bpm.toFloat(),
                        onValueChange = { viewModel.setBpm(it.toInt()) },
                        valueRange = 100f..120f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = colorScheme.primary,
                            activeTrackColor = colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Start / Stop button
            item {
                Button(
                    onClick = {
                        if (uiState.isRunning) viewModel.stop()
                        else viewModel.start()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isRunning) colorScheme.onSurface.copy(alpha = 0.4f) else colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (uiState.isRunning) Icons.Default.Stop
                        else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isRunning) "Stop" else "Start CPR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // CPR steps
            item {
                Text(
                    text = "CPR STEPS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val cprSteps = listOf(
                Triple("Check response", "Tap shoulders firmly, shout 'Are you okay?'", Icons.Default.RecordVoiceOver),
                Triple("Call for help", "Call 911 immediately or ask bystander to call", Icons.Default.Phone),
                Triple("Open airway", "Tilt head back gently, lift chin to open airway", Icons.Default.AirlineSeatReclineNormal),
                Triple("Check breathing", "Look, listen and feel for breathing for 10 seconds", Icons.Default.Visibility),
                Triple("30 compressions", "Push down 5–6cm on centre of chest, hard and fast", Icons.Default.Favorite),
                Triple("2 rescue breaths", "Pinch nose, seal mouth, give 2 breaths of 1 second each", Icons.Default.Air),
                Triple("Repeat cycle", "Continue 30:2 ratio until help arrives or person recovers", Icons.Default.Refresh)
            )

            items(cprSteps.size) { index ->
                val (title, description, icon) = cprSteps[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.surface)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${index + 1}. $title",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = description,
                            fontSize = 12.sp,
                            color = colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null)
                        Spacer(Modifier.padding(4.dp))
                        Text("Done CPR -> Home")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Based on Red Cross & AHA CPR guidelines",
                    fontSize = 11.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PulseCircle(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isActive) colorScheme.primary
                else colorScheme.onSurface.copy(alpha = 0.1f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    val colorScheme = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun CprScreenPreview() {
    CprScreen(onBack = {},
        onNavigateToHome = {})
}