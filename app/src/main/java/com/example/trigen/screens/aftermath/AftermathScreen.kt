package com.example.trigen.screens.aftermath

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trigen.screens.incidents.IncidentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AftermathScreen(
    onBack: () -> Unit,
    onViewIncidents: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: IncidentViewModel
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val incident = state.currentEditIncident

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            Toast.makeText(context, "Incident report saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
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
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorScheme.onBackground)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Aftermath",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                Text(
                    text = "Reporting & Recovery",
                    fontSize = 12.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Saved banner
            if (state.saveSuccess) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.secondaryContainer)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Incident report saved",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Syncing to cloud when connected",
                                fontSize = 11.sp,
                                color = colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Header
            item {
                Column {
                    Text(
                        text = "Incident Report",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onBackground
                    )
                    Text(
                        text = SimpleDateFormat(
                            "dd MMM yyyy, HH:mm",
                            Locale.getDefault()
                        ).format(Date(incident.date)),
                        fontSize = 13.sp,
                        color = colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Handover summary
            item {
                SectionCard(
                    title = "Handover Summary",
                    icon = Icons.Default.LocalHospital,
                    color = colorScheme.primary
                ) {
                    Text(
                        text = "Key information for paramedics:",
                        fontSize = 12.sp,
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    FormField(
                        label = "Patient Condition",
                        value = incident.patientCondition,
                        onValueChange = { newValue -> 
                            viewModel.updateEditState { it.copy(patientCondition = newValue) }
                        },
                        placeholder = "e.g. Conscious, breathing normally, responsive",
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormField(
                        label = "Handover Notes",
                        value = incident.handoverNotes,
                        onValueChange = { newValue -> 
                            viewModel.updateEditState { it.copy(handoverNotes = newValue) }
                        },
                        placeholder = "What happened, timeline, treatments given...",
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Emergency services called",
                            fontSize = 13.sp,
                            color = colorScheme.onSurface
                        )
                        Switch(
                            checked = incident.calledEmergency,
                            onCheckedChange = { newValue -> 
                                viewModel.updateEditState { it.copy(calledEmergency = newValue) }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = colorScheme.secondary)
                        )
                    }
                }
            }

            // Incident details
            item {
                SectionCard(
                    title = "Incident Details",
                    icon = Icons.Default.Info,
                    color = colorScheme.secondary
                ) {
                    FormField(
                        label = "Location",
                        value = incident.location,
                        onValueChange = { newValue -> 
                            viewModel.updateEditState { it.copy(location = newValue) }
                        },
                        placeholder = "e.g. Corner of Main St and 2nd Ave"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormField(
                        label = "Injury Types",
                        value = incident.injuryTypes,
                        onValueChange = { newValue -> 
                            viewModel.updateEditState { it.copy(injuryTypes = newValue) }
                        },
                        placeholder = "e.g. Burn (left arm), suspected fracture (wrist)"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormField(
                        label = "Treatments Given",
                        value = incident.treatmentsGiven,
                        onValueChange = { newValue -> 
                            viewModel.updateEditState { it.copy(treatmentsGiven = newValue) }
                        },
                        placeholder = "e.g. Wound dressed, limb immobilized, CPR 3 cycles",
                        minLines = 2
                    )
                }
            }

            // Responder notes
            item {
                SectionCard(
                    title = "Responder Notes",
                    icon = Icons.AutoMirrored.Filled.Note,
                    color = colorScheme.tertiary
                ) {
                    FormField(
                        label = "Personal Notes",
                        value = incident.responderNotes,
                        onValueChange = { newValue -> 
                            viewModel.updateEditState { it.copy(responderNotes = newValue) }
                        },
                        placeholder = "Any additional observations or notes for yourself...",
                        minLines = 3
                    )
                }
            }

            // Action buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.saveIncident() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.secondary),
                        enabled = !state.isSaving
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = colorScheme.onSecondary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Incident Report", fontWeight = FontWeight.Bold)
                        }
                    }

//                    if (state.saveSuccess) {
//                        Button(
//                            onClick = onViewIncidents,
//                            modifier = Modifier.fillMaxWidth().height(52.dp),
//                            shape = RoundedCornerShape(14.dp),
//                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
//                        ) {
//                            Icon(Icons.Default.List, contentDescription = null)
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("View All Incidents", fontWeight = FontWeight.Bold)
//                        }
//                    }

                    OutlinedButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.tertiary
                        )


                    ) {
                        Text(if (state.saveSuccess) "Done" else "Cancel -> Home", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }
        content()
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        placeholder = { Text(placeholder, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(10.dp)
    )
}
