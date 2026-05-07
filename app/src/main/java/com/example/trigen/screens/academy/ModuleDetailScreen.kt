package com.example.trigen.screens.academy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.trigen.data.local.entity.LessonEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailScreen(
    moduleId: String,
    onBack: () -> Unit,
    onLessonClick: (String) -> Unit,
    onQuizClick: (String) -> Unit,
    viewModel: ModuleDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(moduleId) {
        viewModel.loadModuleDetails(moduleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.module?.title ?: "Module Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground,
                    navigationIconContentColor = colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ModuleHeader(uiState)
                }

                item {
                    Text(
                        "Lessons",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(uiState.lessons) { lesson ->
                    val lessonsCompleted = uiState.progress?.lessonsCompleted ?: 0
                    val isCompleted = lessonsCompleted >= lesson.orderIndex
                    val isLocked = lessonsCompleted < lesson.orderIndex - 1
                    
                    LessonItem(
                        lesson = lesson,
                        isCompleted = isCompleted,
                        isLocked = isLocked,
                        onClick = { onLessonClick(lesson.id) }
                    )
                }

                item {
                    val allLessonsCompleted = (uiState.progress?.lessonsCompleted ?: 0) >= uiState.lessons.size && uiState.lessons.isNotEmpty()
                    val quizPassed = uiState.progress?.quizPassed == true

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onQuizClick(moduleId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = allLessonsCompleted,
                        colors = if (quizPassed) ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) else ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Icon(if (quizPassed) Icons.Default.CheckCircle else Icons.Default.Quiz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (quizPassed) "Quiz Passed - View Badge" else "Take Module Quiz",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (!allLessonsCompleted && uiState.lessons.isNotEmpty()) {
                        Text(
                            "Complete all lessons to unlock the quiz",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleHeader(state: ModuleDetailUiState) {
    val module = state.module ?: return
    val color = try { Color(android.graphics.Color.parseColor(module.color)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = module.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    icon = Icons.Default.Book,
                    text = "${module.totalLessons} Lessons",
                    color = color
                )
                InfoChip(
                    icon = Icons.Default.Timer,
                    text = "${module.totalLessons * 5} min",
                    color = color
                )
                InfoChip(
                    icon = Icons.Default.EmojiEvents,
                    text = "Badge",
                    color = color
                )
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = color)
    }
}

@Composable
fun LessonItem(
    lesson: LessonEntity,
    isCompleted: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        isLocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (isLocked) 0.5f else 1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> Color(0xFF4CAF50)
                            isLocked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    isLocked -> Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), modifier = Modifier.size(16.dp))
                    else -> Text(
                        text = lesson.orderIndex.toString(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${lesson.durationMinutes} min reading",
                    fontSize = 12.sp, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!isLocked) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = if (isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
