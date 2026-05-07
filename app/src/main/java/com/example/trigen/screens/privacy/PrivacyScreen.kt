package com.example.trigen.screens.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Last updated: October 2023",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            PrivacySection(
                title = "1. Information We Collect",
                content = "TriGen collects minimal personal information required for account creation, such as your email and display name. In-app incident data is stored locally or securely in the cloud to help you track your first aid history."
            )
            
            PrivacySection(
                title = "2. How We Use Information",
                content = "Your information is used to personalize your experience, track your academy progress, and allow you to access your incident records across devices."
            )
            
            PrivacySection(
                title = "3. Data Security",
                content = "We use Firebase for secure authentication and data storage. We do not sell your personal data to third parties."
            )
            
            PrivacySection(
                title = "4. Emergency Use",
                content = "TriGen is an assistive tool for emergency first aid. It does not replace professional medical advice or emergency services."
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Contact us at privacy@trigen.example.com for any questions regarding your data.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun PrivacySection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp
        )
    }
}
