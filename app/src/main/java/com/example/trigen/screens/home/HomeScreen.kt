package com.example.trigen.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onStartScan: () -> Unit,
    onOpenAcademy: () -> Unit,
    onOpenCpr: () -> Unit,
    onOpenProtocol: (String) -> Unit,
    onStartEmergency: () -> Unit,
    onOpenAftermath: () -> Unit,
    onOpenSecondary: () -> Unit,
    onOpenIncidents: () -> Unit,
    onLogout: () -> Unit,
    onOpenProfile: () -> Unit,
    authViewModel: com.example.trigen.screens.auth.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
){
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme
    val authState by authViewModel.uiState.collectAsState()
    val displayName = authState.displayName ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)

    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, $displayName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                Text(
                    text = "TriGen Emergency Aid",
                    fontSize = 12.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenProfile) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = colorScheme.onBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                RightTagBadge("Profile")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Emergency Mode banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF2C2C2A))
                .clickable { onStartEmergency() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Emergency Mode",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Full DRSABCD guided response",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))


        // Emergency scan card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorScheme.primary, Color(0xFFC13333))
                    )
                )
                .clickable { onStartScan() }
                .padding(28.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Scan Injury",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Point camera at affected area to identify injury and get instant guidance",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 4.dp),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Start Scanning",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick actions column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Favorite,
                    label = "CPR Guide",
                    subtitle = "Metronome + steps",
                    iconBg = colorScheme.primary.copy(alpha = 0.1f),
                    iconTint = colorScheme.primary,
                    onClick = onOpenCpr
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Assessment,
                    label = "Secondary Survey",
                    subtitle = "Assessment & Care",
                    iconBg = colorScheme.secondary.copy(alpha = 0.1f),
                    iconTint = colorScheme.secondary,
                    onClick = onOpenSecondary
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.RateReview,
                    label = "Aftermath",
                    subtitle = "Reporting & Recovery",
                    iconBg = colorScheme.onSurface.copy(alpha = 0.1f),
                    iconTint = colorScheme.onSurface,
                    onClick = onOpenAftermath
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.School,
                    label = "Academy",
                    subtitle = "Learn first aid",
                    iconBg = colorScheme.secondary.copy(alpha = 0.1f),
                    iconTint = colorScheme.secondary,
                    onClick = onOpenAcademy
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.HistoryEdu,
                    label = "Incidents History",
                    subtitle = "View All Incidents",
                    iconBg = colorScheme.tertiary.copy(alpha = 0.1f),
                    iconTint = colorScheme.tertiary,
                    onClick = onOpenIncidents
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = "Sign Out",
                    subtitle = "Securely logout",
                    iconBg = colorScheme.error.copy(alpha = 0.1f),
                    iconTint = colorScheme.error,
                    onClick = onLogout
                )

            }


        }


        Spacer(modifier = Modifier.height(16.dp))

        // Info cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "FIRST AID PROTOCOLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            ProtocolRow(
                icon = Icons.Default.LocalFireDepartment,
                label = "Burns",
                color = colorScheme.tertiary,
                onClick = { onOpenProtocol("BURN") }
            )
            ProtocolRow(
                icon = Icons.Default.MedicalServices,
                label = "Fractures",
                color = Color(0xFF378ADD),
                onClick = { onOpenProtocol("FRACTURE") }
            )
            ProtocolRow(
                icon = Icons.Default.Healing,
                label = "Lacerations",
                color = colorScheme.primary,
                onClick = { onOpenProtocol("LACERATION") }
            )
            ProtocolRow(
                icon = Icons.Default.BugReport,
                label = "Bites & Stings",
                color = colorScheme.secondary,
                onClick = { onOpenProtocol("BITE") }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            text = "Protocols sourced from Red Cross & WHO",
            fontSize = 11.sp,
            color = colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
fun RightTagBadge(title: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(colorScheme.secondary.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(colorScheme.secondary)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.secondary
        )
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    subtitle: String,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surface)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun ProtocolRow(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onStartScan = {},
        onOpenAcademy = {},
        onOpenCpr = {},
        onOpenProtocol = {},
        onStartEmergency = {},
        onOpenAftermath = {},
        onOpenSecondary = {},
        onOpenIncidents = {},
        onLogout = {},
        onOpenProfile = {}
    )
}
