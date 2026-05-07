package com.example.trigen.screens.incidents

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun IncidentsScreen(
    viewModel: IncidentViewModel,
    onNavigateToView: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var incidentToDeleteId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            Toast.makeText(context, "Incident deleted successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetDeleteSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    if (showDeleteDialog && incidentToDeleteId != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                incidentToDeleteId = null
            },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this incident record? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        incidentToDeleteId?.let { viewModel.deleteIncident(it) }
                        showDeleteDialog = false
                        incidentToDeleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    incidentToDeleteId = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorScheme.onBackground)
            }
            Text(
                text = "Incident History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }

        // Search Bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search location or injury...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = if (state.searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            } else null,
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface
            )
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.filteredIncidents.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (state.searchQuery.isEmpty()) "No incidents recorded" else "No results found",
                    color = colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.filteredIncidents) { incident ->
                    IncidentCard(
                        incident = incident,
                        onClick = {
                            viewModel.selectIncident(incident.id)
                            onNavigateToView(incident.id)
                        },
                        onDelete = {
                            incidentToDeleteId = incident.id
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ViewIncidentScreen(
    viewModel: IncidentViewModel,
    onEdit: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val incident = state.selectedIncident

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            Toast.makeText(context, "Incident deleted successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetDeleteSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    if (showDeleteDialog && incident != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this incident record? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteIncident(incident.id)
                        showDeleteDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorScheme.onBackground)
            }
            Text(
                text = "Incident Detail",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (incident != null) {
                IconButton(onClick = { 
                    viewModel.editExistingIncident(incident)
                    onEdit(incident.id) 
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = colorScheme.onBackground)
                }
            }
        }

        if (incident == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Incident not found")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SectionCard(
                        title = "Overview",
                        icon = Icons.Default.Info,
                        color = colorScheme.primary
                    ) {
                        ReadField("Date & Time", SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(incident.date)))
                        Spacer(modifier = Modifier.height(8.dp))
                        ReadField("Location", incident.location.ifEmpty { "Not specified" })
                        Spacer(modifier = Modifier.height(8.dp))
                        ReadField("Duration", "${incident.elapsedTimeSeconds} seconds")
                        Spacer(modifier = Modifier.height(8.dp))
                        ReadField("Emergency Services Called", if (incident.calledEmergency) "Yes" else "No")
                    }
                }

                item {
                    SectionCard(
                        title = "Injury & Treatment",
                        icon = Icons.Default.LocalHospital,
                        color = colorScheme.secondary
                    ) {
                        ReadField("Injury Types", incident.injuryTypes.ifEmpty { "None recorded" })
                        Spacer(modifier = Modifier.height(8.dp))
                        ReadField("Treatments Given", incident.treatmentsGiven.ifEmpty { "None recorded" })
                    }
                }

                item {
                    SectionCard(
                        title = "Handover Notes",
                        icon = Icons.Default.Description,
                        color = colorScheme.tertiary
                    ) {
                        ReadField("Patient Condition", incident.patientCondition.ifEmpty { "Not recorded" })
                        Spacer(modifier = Modifier.height(8.dp))
                        ReadField("Notes", incident.handoverNotes.ifEmpty { "None recorded" })
                    }
                }

                item {
                    SectionCard(
                        title = "Responder Notes",
                        icon = Icons.AutoMirrored.Filled.Note,
                        color = colorScheme.secondary
                    ) {
                        ReadField("Personal Notes", incident.responderNotes.ifEmpty { "No personal notes" })
                    }
                }

                item {
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.errorContainer, contentColor = colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Incident Record")
                    }
                }
            }
        }
    }
}

@Composable
private fun IncidentCard(
    incident: IncidentEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = incident.location.ifEmpty { "Unknown Location" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(incident.date)),
                        fontSize = 12.sp,
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (incident.isSynced) colorScheme.secondaryContainer else colorScheme.tertiaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = if (incident.isSynced) "Synced" else "Pending",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (incident.isSynced) colorScheme.onSecondaryContainer else colorScheme.onTertiaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = incident.injuryTypes.ifEmpty { "No injuries recorded" },
                fontSize = 13.sp,
                color = colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Timer, 
                        contentDescription = null, 
                        modifier = Modifier.size(14.dp),
                        tint = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${incident.elapsedTimeSeconds}s elapsed",
                        fontSize = 12.sp,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Delete", 
                        tint = colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
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
private fun ReadField(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
