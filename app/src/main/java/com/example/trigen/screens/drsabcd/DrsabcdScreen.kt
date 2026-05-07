package com.example.trigen.screens.drsabcd

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val RedPrimary = Color(0xFFE24B4A)
private val AmberColor = Color(0xFFEF9F27)
private val TealColor = Color(0xFF1D9E75)
private val SurfaceLight = Color(0xFFF8F7F4)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)

@Composable
fun DrsabcdScreen(
    onNavigateToCpr: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToUnsafeScene: () -> Unit,
    onNavigateToAedUnavailable: () -> Unit,
    onNavigateToSecondarySurvay: () -> Unit,
    onBack: () -> Unit,
    viewModel: DrsabcdViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
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
                if (state.currentStep == DrsabcdStep.DANGER) onBack()
                else viewModel.goBack()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Emergency Response",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "DRSABCD Protocol",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            // Elapsed timer
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFFFCEBEB))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                val minutes = state.elapsedSeconds / 60
                val seconds = state.elapsedSeconds % 60
                Text(
                    text = "$minutes:${seconds.toString().padStart(2, '0')}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedPrimary
                )
            }
        }

        // Step progress bar
        StepProgressBar(currentStep = state.currentStep)

        // Step content
        AnimatedContent(
            targetState = state.currentStep,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            },
            label = "step"
        ) { step ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (step) {
                    DrsabcdStep.DANGER -> DangerStep(
                        onSafe = { viewModel.markSceneSafe(true) },
                        onNotSafe =  onNavigateToUnsafeScene
                    )
                    DrsabcdStep.RESPONSE -> ResponseStep(
                        onResponsive = { viewModel.markResponse(ResponseLevel.RESPONSIVE) },
                        onUnresponsive = { viewModel.markResponse(ResponseLevel.UNRESPONSIVE) }
                    )
                    DrsabcdStep.SEND_HELP -> SendHelpStep(
                        onHelpCalled = { viewModel.markHelpCalled() }
                    )
                    DrsabcdStep.AIRWAY -> AirwayStep(
                        onAirwayClear = { viewModel.markAirwayClear() }
                    )
                    DrsabcdStep.BREATHING -> BreathingStep(
                        onBreathingNormal = { viewModel.markBreathing(BreathingStatus.NORMAL) },
                        onNotBreathing = { viewModel.markBreathing(BreathingStatus.NOT_BREATHING) },
                        onAbnormal = { viewModel.markBreathing(BreathingStatus.ABNORMAL) }
                    )
                    DrsabcdStep.CPR -> CprStep(
                        onStartCpr = {
                            viewModel.markCprStarted()
                            onNavigateToCpr()
                        }
                    )
                    DrsabcdStep.DEFIBRILLATION -> DefibrillationStep(
                        onComplete = { onNavigateToSecondarySurvay() },
                        onNavigateToAedUnavailable = { onNavigateToAedUnavailable() }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepProgressBar(currentStep: DrsabcdStep) {
    val steps = DrsabcdStep.values()
    val letters = listOf("D", "R", "S", "A", "B", "C", "D")
    val currentIndex = steps.indexOf(currentStep)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, _ ->
            val isCompleted = index < currentIndex
            val isCurrent = index == currentIndex

            Box(
                modifier = Modifier
                    .size(if (isCurrent) 36.dp else 28.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> TealColor
                            isCurrent -> RedPrimary
                            else -> Color.LightGray.copy(alpha = 0.4f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = letters[index],
                        color = if (isCurrent) Color.White
                        else Color.Gray,
                        fontSize = if (isCurrent) 14.sp else 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (index < currentIndex) TealColor
                            else Color.LightGray.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
fun StepCard(
    letter: String,
    letterColor: Color,
    title: String,
    subtitle: String,
    instruction: String,
    warning: String? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Letter badge
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(letterColor.copy(alpha = 0.1f))
                .border(2.dp, letterColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = letterColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Instruction card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = instruction,
                fontSize = 15.sp,
                color = TextSecondary,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )
        }

        // Warning box
        if (warning != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAEEDA))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = AmberColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = warning,
                    fontSize = 13.sp,
                    color = Color(0xFF633806)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        content()
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    color: Color = RedPrimary,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DangerStep(onSafe: () -> Unit, onNotSafe: () -> Unit) {
    StepCard(
        letter = "D",
        letterColor = RedPrimary,
        title = "Check for Danger",
        subtitle = "Ensure the scene is safe",
        instruction = "Before approaching, check for ongoing hazards:\n\n" +
                "• Traffic or moving vehicles\n" +
                "• Fire, smoke or toxic fumes\n" +
                "• Electrical hazards\n" +
                "• Unstable structures\n" +
                "• Aggressive persons or animals\n\n" +
                "Only approach if YOU are safe.",
        warning = "Never put yourself at risk. A second casualty helps no one."
    ) {
        ActionButton(
            text = "✓ Scene is Safe",
            onClick = onSafe,
            color = TealColor
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = onNotSafe,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text("⚠ Scene is NOT Safe", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ResponseStep(onResponsive: () -> Unit, onUnresponsive: () -> Unit) {
    StepCard(
        letter = "R",
        letterColor = AmberColor,
        title = "Check Response",
        subtitle = "Use the COWS method",
        instruction = "Approach and check responsiveness using COWS:\n\n" +
                "C — Can you hear me?\n" +
                "O — Open your eyes\n" +
                "W — What is your name?\n" +
                "S — Squeeze my hand\n\n" +
                "Speak loudly and clearly. Tap their shoulders firmly.",
        warning = "Do not shake if spinal injury is suspected."
    ) {
        ActionButton(
            text = "✓ Person is Responsive",
            onClick = onResponsive,
            color = TealColor
        )
        Spacer(modifier = Modifier.height(10.dp))
        ActionButton(
            text = "✗ No Response",
            onClick = onUnresponsive,
            color = RedPrimary
        )
    }
}

@Composable
fun SendHelpStep(onHelpCalled: () -> Unit) {
    StepCard(
        letter = "S",
        letterColor = Color(0xFF378ADD),
        title = "Send for Help",
        subtitle = "Call emergency services now",
        instruction = "Call emergency services immediately.\n\n" +
                "If others are present:\n" +
                "• Point to a specific person\n" +
                "• Say 'YOU — call 911 now'\n" +
                "• Ask someone to find an AED\n\n" +
                "Stay on the line with the dispatcher.",
        warning = "Always direct a SPECIFIC person — not just 'someone'."
    ) {
        ActionButton(
            text = "📞 Help Has Been Called",
            onClick = onHelpCalled,
            color = Color(0xFF378ADD)
        )
    }
}

@Composable
private fun AirwayStep(onAirwayClear: () -> Unit) {
    StepCard(
        letter = "A",
        letterColor = Color(0xFF8B5CF6),
        title = "Open Airway",
        subtitle = "Head-tilt chin-lift maneuver",
        instruction = "1. Place one hand on their forehead\n" +
                "2. Gently tilt the head back\n" +
                "3. Place fingertips under the chin\n" +
                "4. Lift the chin upward\n\n" +
                "Look inside the mouth for visible obstructions. " +
                "Remove any visible debris carefully with your finger.",
        warning = "If spinal injury suspected, use jaw-thrust maneuver instead."
    ) {
        ActionButton(
            text = "✓ Airway is Open and Clear",
            onClick = onAirwayClear,
            color = Color(0xFF8B5CF6)
        )
    }
}

@Composable
private fun BreathingStep(
    onBreathingNormal: () -> Unit,
    onNotBreathing: () -> Unit,
    onAbnormal: () -> Unit
) {
    var counting by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(10) }

    LaunchedEffect(counting) {
        if (counting) {
            while (seconds > 0) {
                kotlinx.coroutines.delay(1000)
                seconds--
            }
            counting = false
        }
    }

    StepCard(
        letter = "B",
        letterColor = TealColor,
        title = "Check Breathing",
        subtitle = "Look, listen and feel for 10 seconds",
        instruction = "With the airway open:\n\n" +
                "LOOK — for chest rise and fall\n" +
                "LISTEN — for breath sounds\n" +
                "FEEL — for air on your cheek\n\n" +
                "Check for no more than 10 seconds.",
        warning = "Occasional gasping is NOT normal breathing — treat as not breathing."
    ) {
        if (!counting) {
            ActionButton(
                text = "▶ Start 10-Second Check",
                onClick = { counting = true; seconds = 10 },
                color = TealColor
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(TealColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$seconds",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "What did you find?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        ActionButton(
            text = "✓ Breathing Normally",
            onClick = onBreathingNormal,
            color = TealColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        ActionButton(
            text = "⚠ Abnormal / Gasping",
            onClick = onAbnormal,
            color = AmberColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        ActionButton(
            text = "✗ Not Breathing",
            onClick = onNotBreathing,
            color = RedPrimary
        )
    }
}

@Composable
fun CprStep(onStartCpr: () -> Unit) {
    StepCard(
        letter = "C",
        letterColor = RedPrimary,
        title = "CPR",
        subtitle = "Cardiopulmonary Resuscitation",
        instruction = "Person is not breathing — start CPR immediately:\n\n" +
                "1. Place heel of hand on centre of chest\n" +
                "2. Push down 5–6 cm (2–2.5 inches)\n" +
                "3. Give 30 compressions at 100–120 BPM\n" +
                "4. Give 2 rescue breaths\n" +
                "5. Repeat 30:2 cycle\n\n" +
                "The CPR metronome will guide your rate.",
        warning = "Do not stop CPR unless the person recovers, an AED is ready, or you are physically unable to continue."
    ) {
        ActionButton(
            text = "▶ Start CPR Metronome",
            onClick = onStartCpr,
            color = RedPrimary
        )
    }
}

@Composable
private fun DefibrillationStep(onComplete: () -> Unit, onNavigateToAedUnavailable: () -> Unit) {
    StepCard(
        letter = "D",
        letterColor = AmberColor,
        title = "Defibrillation",
        subtitle = "AED — Automated External Defibrillator",
        instruction = "As soon as an AED arrives:\n\n" +
                "1. Power ON — press the green button\n" +
                "2. Follow the voice prompts\n" +
                "3. Attach pads as shown on diagrams\n" +
                "   • One pad: right of breastbone, below collarbone\n" +
                "   • One pad: left side, below armpit\n" +
                "4. Ensure NOBODY is touching the person\n" +
                "5. Press SHOCK if advised\n" +
                "6. Resume CPR immediately after shock",
        warning = "Stand clear when AED is analyzing or delivering shock."
    ) {
        ActionButton(
            text = "✓ AED Applied",
            onClick = onComplete,
            color = AmberColor
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = onNavigateToAedUnavailable,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text("No AED Available", fontWeight = FontWeight.Bold)
        }
    }
}
