package com.example.trigen.screens.academy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    moduleId: String,
    onBack: () -> Unit,
    onViewBadge: () -> Unit,
    onQuizFinished: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(moduleId) {
        viewModel.loadQuiz(moduleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Module Quiz") },
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
        } else if (uiState.isFinished) {
            QuizResultScreen(
                score = uiState.score,
                total = uiState.questions.size,
                onViewBadge = onViewBadge,
                onFinish = onQuizFinished
            )
        } else if (uiState.questions.isNotEmpty()) {
            val currentQuestion = uiState.questions[uiState.currentQuestionIndex]
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Progress
                LinearProgressIndicator(
                    progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = currentQuestion.question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                val options = listOf(
                    "A" to currentQuestion.optionA,
                    "B" to currentQuestion.optionB,
                    "C" to currentQuestion.optionC,
                    "D" to currentQuestion.optionD
                )
                
                options.forEach { (key, text) ->
                    QuizOption(
                        optionKey = key,
                        text = text,
                        isSelected = uiState.selectedOption == key,
                        isCorrect = if (uiState.showExplanation) key == currentQuestion.correctAnswer else null,
                        onClick = { viewModel.selectOption(key) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (uiState.showExplanation) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Explanation",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(currentQuestion.explanation)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { viewModel.nextQuestion(moduleId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            if (uiState.currentQuestionIndex + 1 == uiState.questions.size) "Finish Quiz" else "Next Question",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.submitAnswer() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = uiState.selectedOption != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text("Submit Answer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuizOption(
    optionKey: String,
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit
) {
    val borderColor = when {
        isCorrect == true -> Color(0xFF4CAF50)
        isCorrect == false && isSelected -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    
    val containerColor = when {
        isCorrect == true -> Color(0xFF4CAF50).copy(alpha = 0.1f)
        isCorrect == false && isSelected -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$optionKey.",
                fontWeight = FontWeight.Bold,
                color = if (isSelected || isCorrect != null) borderColor else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (isCorrect == true) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50))
            } else if (isCorrect == false && isSelected) {
                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    score: Int,
    total: Int,
    onViewBadge: () -> Unit,
    onFinish: () -> Unit
) {
    val passed = score >= (total * 0.7)
    val colorScheme = MaterialTheme.colorScheme
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (passed) Icons.Default.EmojiEvents else Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = if (passed) Color(0xFFFFD700) else colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (passed) "Congratulations!" else "Keep Learning",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (passed) "You passed the quiz and earned a badge!" else "You didn't reach the 70% passing score. Try reviewing the lessons again.",
            textAlign = TextAlign.Center,
            color = colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Your Score", style = MaterialTheme.typography.labelLarge, color = colorScheme.onPrimaryContainer)
                Text(
                    text = "$score / $total",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.primary
                )
                Text(
                    text = "${(score.toFloat() / total * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.secondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (passed) {
            Button(
                onClick = onViewBadge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("View Badge", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, colorScheme.outline)
        ) {
            Text("Back to Academy", fontWeight = FontWeight.Bold, color = colorScheme.primary)
        }
    }
}

