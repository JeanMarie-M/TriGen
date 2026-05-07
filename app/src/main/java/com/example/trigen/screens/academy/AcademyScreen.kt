package com.example.trigen.screens.academy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trigen.data.local.entity.ModuleEntity
import com.example.trigen.data.local.entity.UserProgressEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyScreen(
    onBack: () -> Unit,
    onModuleClick: (String) -> Unit,
    viewModel: AcademyViewModel = hiltViewModel()
) {
    val modulesWithProgress by viewModel.modulesWithProgress.collectAsState()
    val earnedBadges by viewModel.earnedBadgesCount.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TriGen Academy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Learn & Master First Aid",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onBackground
                )
                Text(
                    "Interactive courses to prepare you for real-world emergencies.",
                    fontSize = 14.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
                
                if (earnedBadges > 0) {
                    BadgeStatusCard(earnedBadges)
                }
            }

            items(modulesWithProgress) { item ->
                ModuleCard(
                    module = item.module,
                    progress = item.progress,
                    isLocked = item.isLocked,
                    onClick = { onModuleClick(item.module.id) }
                )
            }
        }
    }
}

@Composable
fun BadgeStatusCard(count: Int) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Stars,
                contentDescription = null,
                tint = colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Certification Progress",
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimaryContainer
                )
                Text(
                    "You have earned $count badges so far!",
                    fontSize = 12.sp,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ModuleCard(
    module: ModuleEntity,
    progress: UserProgressEntity?,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val moduleColor = try {
        Color(android.graphics.Color.parseColor(module.color))
    } catch (e: Exception) {
        colorScheme.primary
    }
    
    val completionPercent = if (module.totalLessons > 0) {
        (progress?.lessonsCompleted ?: 0).toFloat() / module.totalLessons
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) colorScheme.surface.copy(alpha = 0.6f) else colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (isLocked) 0.6f else 1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isLocked) colorScheme.onSurface.copy(alpha = 0.1f) else moduleColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = if (isLocked) {
                        Icons.Default.Lock
                    } else {
                        when (module.icon) {
                            "medical_services" -> Icons.Default.MedicalServices
                            "favorite" -> Icons.Default.Favorite
                            "healing" -> Icons.Default.Healing
                            "warning" -> Icons.Default.Warning
                            "sports_score" -> Icons.Default.SportsScore
                            "psychology" -> Icons.Default.Psychology
                            else -> Icons.Default.Book
                        }
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isLocked) colorScheme.onSurface.copy(alpha = 0.4f) else moduleColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) colorScheme.onSurface.copy(alpha = 0.6f) else colorScheme.onSurface
                    )
                    Text(
                        text = if (isLocked) "Complete previous modules to unlock" else "${module.totalLessons} Lessons • ${module.totalQuestions} Quiz Questions",
                        fontSize = 12.sp,
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                if (progress?.badgeEarned == true) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                } else if (!isLocked) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            if (completionPercent > 0 && progress?.badgeEarned != true && !isLocked) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { completionPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = moduleColor,
                    trackColor = moduleColor.copy(alpha = 0.1f)
                )
                Text(
                    text = "${(completionPercent * 100).toInt()}% Complete",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = moduleColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
