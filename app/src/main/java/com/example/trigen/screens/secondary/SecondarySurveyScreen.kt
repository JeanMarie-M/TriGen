package com.example.trigen.screens.secondary

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SecondarySurveyScreen(
    onNavigateToProtocol: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAftermath: () -> Unit,
    onBack: () -> Unit,
    viewModel: SecondarySurveyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
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
            IconButton(onClick = {
                if (state.currentSection == SurveySection.SAM) onBack()
                else viewModel.goBack()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colorScheme.onBackground)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Secondary Survey",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                Text(
                    text = "Assessment & Care",
                    fontSize = 12.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Section tabs
        SectionTabs(currentSection = state.currentSection)

        // Content
        AnimatedContent(
            targetState = state.currentSection,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            },
            label = "section"
        ) { section ->
            when (section) {
                SurveySection.SAM -> SamSection(
                    state = state,
                    viewModel = viewModel,
                    onNext = { viewModel.navigateTo(SurveySection.HEAD_TO_TOE) }
                )
                SurveySection.HEAD_TO_TOE -> HeadToToeSection(
                    state = state,
                    viewModel = viewModel,
                    onNext = { viewModel.navigateTo(SurveySection.VITALS) },
                    onNavigateToProtocol = onNavigateToProtocol
                )
                SurveySection.VITALS -> VitalsSection(
                    state = state,
                    viewModel = viewModel,
                    onNext = { viewModel.navigateTo(SurveySection.RECOVERY_POSITION) }
                )
                SurveySection.RECOVERY_POSITION -> RecoverySection(
                    state = state,
                    viewModel = viewModel,
                    onComplete = onNavigateToAftermath
                )
            }
        }
    }
}

@Composable
private fun SectionTabs(currentSection: SurveySection) {
    val sections = listOf(
        SurveySection.SAM to "SAM",
        SurveySection.HEAD_TO_TOE to "Head-Toe",
        SurveySection.VITALS to "Vitals",
        SurveySection.RECOVERY_POSITION to "Recovery"
    )
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        sections.forEachIndexed { index, (section, label) ->
            val isActive = section == currentSection
            val isPast = sections.indexOfFirst { it.first == currentSection } > index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isActive -> colorScheme.primary
                            isPast -> colorScheme.secondary
                            else -> colorScheme.onSurface.copy(alpha = 0.1f)
                        }
                    )
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive || isPast) colorScheme.onPrimary else colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SamSection(
    state: SecondarySurveyState,
    viewModel: SecondarySurveyViewModel,
    onNext: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "SAM Interview",
                subtitle = "Signs & Symptoms · Allergies · Medications",
                color = colorScheme.primary
            )
        }

        // Symptoms
        item {
            ChipGroup(
                title = "Signs & Symptoms",
                icon = Icons.Default.MonitorHeart,
                color = colorScheme.primary,
                options = COMMON_SYMPTOMS,
                selected = state.samData.symptoms,
                onToggle = { viewModel.toggleSymptom(it) },
                customValue = state.customSymptom,
                onCustomChange = { viewModel.updateCustomSymptom(it) },
                onCustomAdd = { viewModel.addCustomSymptom() }
            )
        }

        // Allergies
        item {
            ChipGroup(
                title = "Known Allergies",
                icon = Icons.Default.Warning,
                color = colorScheme.tertiary,
                options = COMMON_ALLERGIES,
                selected = state.samData.allergies,
                onToggle = { viewModel.toggleAllergy(it) },
                customValue = state.customAllergy,
                onCustomChange = { viewModel.updateCustomAllergy(it) },
                onCustomAdd = { viewModel.addCustomAllergy() }
            )
        }

        // Medications
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorScheme.surface)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Current Medications",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                state.samData.medications.forEach { med ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(med, fontSize = 13.sp, color = colorScheme.onSurface, modifier = Modifier.weight(1f))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.customMedication,
                        onValueChange = { viewModel.updateCustomMedication(it) },
                        placeholder = { Text("Add medication...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { viewModel.addCustomMedication() })
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.addCustomMedication() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = colorScheme.primary)
                    }
                }
            }
        }

        // Medical conditions
        item {
            ChipGroup(
                title = "Medical Conditions",
                icon = Icons.Default.LocalHospital,
                color = colorScheme.secondary,
                options = COMMON_CONDITIONS,
                selected = state.samData.medicalConditions,
                onToggle = { viewModel.toggleCondition(it) },
                customValue = "",
                onCustomChange = {},
                onCustomAdd = {}
            )
        }

        item {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                Text("Continue to Head-to-Toe Check →", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HeadToToeSection(
    state: SecondarySurveyState,
    viewModel: SecondarySurveyViewModel,
    onNext: () -> Unit,
    onNavigateToProtocol: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Head-to-Toe Check",
                subtitle = "Tap affected areas to select injuries",
                color = colorScheme.secondary
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.secondaryContainer)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Visually scan from head to toe. Check for bleeding, " +
                            "burns, fractures, deformity, or medical alert jewelry.",
                    fontSize = 13.sp,
                    color = colorScheme.onSecondaryContainer
                )
            }
        }

        items(BODY_AREAS) { area ->
            val isSelected = state.injuriesFound.contains(area)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) colorScheme.primaryContainer else colorScheme.surface)
                    .border(
                        width = if (isSelected) 1.5.dp else 0.dp,
                        color = if (isSelected) colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { viewModel.toggleInjury(area) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle
                    else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = area,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) colorScheme.primary else colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (isSelected) {
                    Text(
                        text = "Injury noted",
                        fontSize = 11.sp,
                        color = colorScheme.primary
                    )
                }
            }
        }

        if (state.injuriesFound.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.primaryContainer)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Injuries found — view protocols:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("BURN", "FRACTURE", "LACERATION").forEach { injury ->
                        TextButton(
                            onClick = { onNavigateToProtocol(injury) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "→ View ${injury.lowercase().replaceFirstChar { it.uppercase() }} Protocol",
                                color = colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.secondary)
            ) {
                Text("Continue to Vitals →", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun VitalsSection(
    state: SecondarySurveyState,
    viewModel: SecondarySurveyViewModel,
    onNext: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Vital Signs",
                subtitle = "Record current observations",
                color = colorScheme.secondary
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.vitalSigns.pulseRate,
                    onValueChange = { viewModel.updatePulseRate(it) },
                    label = { Text("Pulse Rate (beats/min)") },
                    placeholder = { Text("e.g. 72") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Favorite, null, tint = colorScheme.primary)
                    }
                )
                OutlinedTextField(
                    value = state.vitalSigns.breathingRate,
                    onValueChange = { viewModel.updateBreathingRate(it) },
                    label = { Text("Breathing Rate (breaths/min)") },
                    placeholder = { Text("e.g. 16") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Air, null, tint = colorScheme.secondary)
                    }
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Level of Consciousness (AVPU)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                listOf(
                    ConsciousnessLevel.ALERT to "Alert — fully conscious",
                    ConsciousnessLevel.VOICE to "Voice — responds to voice",
                    ConsciousnessLevel.PAIN to "Pain — responds to pain only",
                    ConsciousnessLevel.UNRESPONSIVE to "Unresponsive — no response"
                ).forEach { (level, label) ->
                    val isSelected = state.vitalSigns.consciousnessLevel == level
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) colorScheme.secondaryContainer
                                else Color.Transparent
                            )
                            .clickable { viewModel.updateConsciousness(level) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle
                            else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isSelected) colorScheme.secondary else colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            color = if (isSelected) colorScheme.onSecondaryContainer else colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.secondary)
            ) {
                Text("Continue to Recovery Position →", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RecoverySection(
    state: SecondarySurveyState,
    viewModel: SecondarySurveyViewModel,
    onComplete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Recovery Position",
                subtitle = "If conscious and breathing normally",
                color = colorScheme.tertiary
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Is the person unconscious but breathing normally?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.setRecoveryPosition(true) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.recoveryPositionNeeded) colorScheme.tertiary
                        else colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Yes — place in recovery position",
                        color = if (state.recoveryPositionNeeded) colorScheme.onTertiary else colorScheme.onSurface
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.setRecoveryPosition(false) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        MaterialTheme.colorScheme.secondary)
                ) {
                    Text(
                        text = "No — person is conscious",
                        color = Color.White

                    )
                }
            }
        }

        if (state.recoveryPositionNeeded) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.tertiaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Recovery Position Steps",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Kneel beside the person",
                        "Place the arm nearest you at right angle to body",
                        "Bring their far arm across chest, hold back of hand to cheek",
                        "Pull up the far knee so foot is flat on ground",
                        "Roll them towards you onto their side",
                        "Tilt head back to keep airway open",
                        "Monitor breathing until help arrives"
                    ).forEachIndexed { index, step ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text(
                                text = "${index + 1}.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.tertiary,
                                modifier = Modifier.width(24.dp)
                            )
                            Text(
                                text = step,
                                fontSize = 13.sp,
                                color = colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }
        else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.secondaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Conscious Patient: Secondary Survey (SAMPLE)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val sampleSteps = listOf(
                        "S - Signs & Symptoms (What hurts?)",
                        "A - Allergies (Any known allergies?)",
                        "M - Medications (Are they taking any?)",
                        "P - Past Medical History (Conditions?)",
                        "L - Last Oral Intake (Food/Drink?)",
                        "E - Events Leading Up (What happened?)",
                        "Perform head-to-toe check for injuries",
                        "Monitor vitals & provide reassurance"
                    )

                    sampleSteps.forEach { step ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text(
                                text = "•",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.secondary,
                                modifier = Modifier.width(20.dp)
                            )
                            Text(
                                text = step,
                                fontSize = 13.sp,
                                color = colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                Text("Complete Survey", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String, color: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Column {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colorScheme.onBackground
        )
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 2.dp)
        )
        Divider(
            modifier = Modifier.padding(top = 8.dp),
            color = color.copy(alpha = 0.3f),
            thickness = 2.dp
        )
    }
}

@Composable
private fun ChipGroup(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    options: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit,
    customValue: String,
    onCustomChange: (String) -> Unit,
    onCustomAdd: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Chips
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = selected.contains(option)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggle(option) },
                    label = { Text(option, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color.copy(alpha = 0.15f),
                        selectedLabelColor = color
                    )
                )
            }
        }

        if (customValue.isNotEmpty() || onCustomAdd != {}) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customValue,
                    onValueChange = onCustomChange,
                    placeholder = { Text("Add other...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onCustomAdd() })
                )
                IconButton(onClick = onCustomAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = color)
                }
            }
        }
    }
}