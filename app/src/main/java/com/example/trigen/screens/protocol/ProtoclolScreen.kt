package com.example.trigen.screens.protocol

import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel



data class ProtocolStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val warning: String? = null
)

data class InjuryProtocol(
    val injuryType: String,
    val displayName: String,
    val severity: String,
    val severityColor: Color,
    val icon: ImageVector,
    val steps: List<ProtocolStep>,
    val doNot: List<String>,
    val callEmergency: Boolean
)

@Composable
fun ProtocolScreen(
    injuryType: String,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: ProtocolViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(injuryType) {
        viewModel.loadProtocol(injuryType)
    }

    when (uiState) {
        is ProtocolUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
        }

        is ProtocolUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (uiState as ProtocolUiState.Error).message,
                        color = colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = onBack) { Text("Go Back") }
                }
            }
        }

        is ProtocolUiState.Success -> {
            ProtocolContent(
                protocol = (uiState as ProtocolUiState.Success).protocol,
                onBack = onBack,
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}

@Composable
private fun ProtocolContent(
    protocol: InjuryProtocol,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var showAllSteps by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

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
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.onBackground
                )
            }
            Text(
                text = "First Aid Guide",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (protocol.callEmergency) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Call For Help 911",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colorScheme.surface)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(protocol.severityColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = protocol.icon,
                                contentDescription = null,
                                tint = protocol.severityColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = protocol.displayName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(protocol.severityColor)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Severity: ${protocol.severity}",
                                    fontSize = 12.sp,
                                    color = protocol.severityColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Step progress indicator
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    protocol.steps.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (index <= currentStep) colorScheme.primary
                                    else colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                        )
                    }
                }
            }

            // Guided mode — current step
            if (!showAllSteps) {
                item {
                    val step = protocol.steps[currentStep]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "STEP ${step.stepNumber} OF ${protocol.steps.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(colorScheme.surface)
                                .padding(20.dp)
                        ) {
                            Column {
                                Text(
                                    text = step.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = step.instruction,
                                    fontSize = 15.sp,
                                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 22.sp
                                )
                                if (step.warning != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(colorScheme.tertiary.copy(alpha = 0.1f))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = colorScheme.tertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = step.warning,
                                            fontSize = 12.sp,
                                            color = colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (currentStep > 0) {
                                OutlinedButton(
                                    onClick = { currentStep-- },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Previous")
                                }
                            }
                            Button(
                                onClick = {
                                    if (currentStep < protocol.steps.size - 1) currentStep++
                                    else onNavigateToHome()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),

                                colors = if (currentStep < protocol.steps.size - 1) ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                                else ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary),
                                enabled = true

                            ) {
                                Text(
                                    if (currentStep < protocol.steps.size - 1) "Next Step"
                                    else "Complete"
                                )
                                if (currentStep < protocol.steps.size - 1) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                else {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        TextButton(
                            onClick = { showAllSteps = true },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("View all steps", fontSize = 12.sp, color = colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // All steps view
            if (showAllSteps) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ALL STEPS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                        TextButton(onClick = { showAllSteps = false }) {
                            Text("Guided mode", fontSize = 12.sp, color = colorScheme.primary)
                        }
                    }
                }

                itemsIndexed(protocol.steps) { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index <= currentStep) colorScheme.primary
                                        else colorScheme.onSurface.copy(alpha = 0.1f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${step.stepNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            if (index < protocol.steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(40.dp)
                                        .background(colorScheme.onSurface.copy(alpha = 0.1f))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(
                                text = step.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = step.instruction,
                                fontSize = 13.sp,
                                color = colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                }
            }

            // Do NOT section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.primary.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DO NOT",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    protocol.doNot.forEach { item ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "×",
                                fontSize = 14.sp,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item,
                                fontSize = 13.sp,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Source credit
            item {
                Text(
                    text = "Protocol based on Red Cross & WHO guidelines",
                    fontSize = 11.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
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
                        Text("Home")
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProtocolScreenPreview() {
    ProtocolScreen(injuryType = "BURN", onNavigateToHome = {}, onBack = {})
}