package com.example.trigen.screens.drsabcd_alternatives

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CprWithoutAedScreen(
    onCallEmergency: () -> Unit,
    onNavigateToCprScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TriGen -> CPR Without AED Guide",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onBackground,
                    )
                        }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "No AED available",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Focus entirely on high-quality CPR and keep emergency services updated.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "CPR keeps oxygenated blood moving to the brain until professional help takes over.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            CprStepCard(
                title = "1. Continuous chest compressions",
                body = "Push in the center of the chest, 5–6 cm deep, at a rate of 100–120 compressions per minute. Minimize interruptions and continue if ribs crack."
            )

            CprStepCard(
                title = "2. Switch rescuers",
                body = "CPR is exhausting. If another rescuer is present, switch every 2 minutes and keep the changeover under 5 seconds."
            )

            CprStepCard(
                title = "3. Hands-only CPR is acceptable",
                body = "If you are untrained or uncomfortable with rescue breaths, hands-only CPR is effective in the first few minutes after collapse."
            )

            CprStepCard(
                title = "4. Update the emergency operator",
                body = "Tell the operator: 'We are performing CPR but we do not have an AED.' Ask someone else to search nearby if possible."
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onNavigateToCprScreen,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Start CPR")
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Continue until paramedics arrive, the patient breathes normally again, you are too exhausted, or the scene becomes dangerous.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick reminder",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Compressions are the priority. Keep the flow going and avoid long pauses.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(16.dp))

            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCallEmergency,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Call Emergency Services")
            }
            Spacer(Modifier.height(16.dp))

        }
    }
}

@Composable
private fun CprStepCard(title: String, body: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}