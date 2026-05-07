package com.example.trigen.screens.drsabcd_alternatives

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnsafeSceneScreen(
    onCallEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TriGen -> Unsafe Scene Guide",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onBackground,
                    )
                },
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
                        text = "Golden rule of first aid",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan,
                        fontStyle = FontStyle.Italic

                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Do not approach the casualty if the scene is unsafe.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Your safety is the absolute priority. If you become injured, you cannot help the victim and you create an additional rescue risk.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            StepCard(
                number = 1,
                title = "Stay back and assess",
                body = "Keep a safe distance. Identify the danger: traffic, fire, smoke, deep water, falling debris, live electricity, gas, or violence.",
            )

            StepCard(
                number = 2,
                title = "Call emergency services immediately",
                body = "In Kenya, dial 999, 112, or 911. State that the scene is unsafe and specify the hazard so the right teams are sent.",
            )

            StepCard(
                number = 3,
                title = "Make the area safe only if low risk",
                body = "Only act if it does not put you in danger. Examples: switch off a light, place hazard triangles, use a flashlight, or warn others to keep away.",
            )

            StepCard(
                number = 4,
                title = "Communicate with the casualty",
                body = "If safe from a distance, tell the person help is coming, ask them to stay still, and keep them calm until rescuers arrive.",
            )

            StepCard(
                number = 5,
                title = "Reassess constantly",
                body = "Hazards can change quickly. Be ready to move further back if the danger increases.",
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedButton(
                        onClick = onCallEmergency,
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null)
                        Spacer(Modifier.padding(4.dp))
                        Text("Send For Help")
                    }
                }
            }
        }
    }
}

@Composable
private fun StepCard(
    number: Int,
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Step $number",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
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