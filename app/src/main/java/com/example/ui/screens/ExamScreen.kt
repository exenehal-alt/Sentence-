package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.viewmodel.ExamResult
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.examQuestions.collectAsState()
    val currentIndex by viewModel.currentExamIndex.collectAsState()
    val answers by viewModel.examAnswers.collectAsState()
    val secondsRemaining by viewModel.examSecondsRemaining.collectAsState()
    val examResult by viewModel.examResult.collectAsState()

    var showConfirmSubmitDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("exam_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (examResult == null) "Admission Mock Exam" else "Exam Results",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        if (examResult == null) {
                            val mins = secondsRemaining / 60
                            val secs = secondsRemaining % 60
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (secondsRemaining < 60) RoseError.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = if (secondsRemaining < 60) RoseError else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format("%02d:%02d", mins, secs),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (secondsRemaining < 60) RoseError else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("exam_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (examResult == null && questions.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentIndex > 0) viewModel.setExamIndex(currentIndex - 1) },
                            enabled = currentIndex > 0
                        ) {
                            Text("পূর্ববর্তী")
                        }

                        Button(
                            onClick = { showConfirmSubmitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            modifier = Modifier.testTag("submit_exam_btn")
                        ) {
                            Text("Submit Exam")
                        }

                        Button(
                            onClick = { if (currentIndex < questions.size - 1) viewModel.setExamIndex(currentIndex + 1) },
                            enabled = currentIndex < questions.size - 1
                        ) {
                            Text("পরবর্তী")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (examResult != null) {
            ExamScorecardView(
                result = examResult!!,
                questions = questions,
                onRestart = { viewModel.navigateTo(com.example.viewmodel.Screen.Exam(15)) },
                onBack = onBack,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            val currentQ = questions.getOrNull(currentIndex)
            if (currentQ != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    // Question Number Palette (Quick Jump)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(questions) { idx, _ ->
                            val isAnswered = answers.containsKey(idx)
                            val isCurrent = idx == currentIndex
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            isAnswered -> EmeraldSuccess.copy(alpha = 0.2f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .border(
                                        width = if (isCurrent) 2.dp else 1.dp,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setExamIndex(idx) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Question Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "Question ${currentIndex + 1}/${questions.size}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer
                                    ) {
                                        Text(
                                            text = currentQ.examTag,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = currentQ.text,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 17.sp,
                                        lineHeight = 24.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                Spacer(modifier = Modifier.height(18.dp))
                            }

                            itemsIndexed(currentQ.options) { optIdx, optText ->
                                val isSelected = answers[currentIndex] == optIdx
                                val letter = ('a' + optIdx).toString()

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.selectExamOption(optIdx) }
                                        .testTag("exam_opt_${currentIndex}_$optIdx"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    border = BorderStroke(
                                        1.2.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "($letter)",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = optText,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showConfirmSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmSubmitDialog = false },
            title = { Text("Submit Exam?") },
            text = {
                val answeredCount = answers.size
                Text("You have answered $answeredCount of ${questions.size} questions. Are you sure you want to finish?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmSubmitDialog = false
                        viewModel.submitExam()
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmSubmitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExamScorecardView(
    result: ExamResult,
    questions: List<com.example.model.Question>,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accuracy = if (result.totalQuestions > 0) {
        ((result.correctCount.toFloat() / result.totalQuestions) * 100).toInt()
    } else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Exam Completed!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your Score: ${result.correctCount} / ${result.totalQuestions}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Accuracy: $accuracy%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (accuracy >= 70) EmeraldSuccess else RoseError,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ScoreStat("Correct", "${result.correctCount}", EmeraldSuccess)
                        ScoreStat("Incorrect", "${result.incorrectCount}", RoseError)
                        ScoreStat("Skipped", "${result.skippedCount}", MaterialTheme.colorScheme.onSurfaceVariant)
                        ScoreStat("Time", "${result.timeSpentSeconds}s", MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dashboard")
                        }
                        Button(
                            onClick = onRestart,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Retake Exam")
                        }
                    }
                }
            }
        }

        // Section Title: Answer Key & Explanations Review
        item {
            Text(
                text = "Detailed Answer Review",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        itemsIndexed(questions) { idx, q ->
            val userSelected = result.userAnswers[idx]
            val isCorrect = userSelected == q.correctIndex

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.dp,
                    if (userSelected == null) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    else if (isCorrect) EmeraldSuccess.copy(alpha = 0.5f)
                    else RoseError.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Q${idx + 1}. [${q.examTag}]",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            shape = CircleShape,
                            color = when {
                                userSelected == null -> MaterialTheme.colorScheme.surfaceVariant
                                isCorrect -> EmeraldSuccess
                                else -> RoseError
                            }
                        ) {
                            Text(
                                text = when {
                                    userSelected == null -> "Skipped"
                                    isCorrect -> "✓ Correct"
                                    else -> "✗ Incorrect"
                                },
                                color = if (userSelected == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = q.text,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Correct Answer: (${('a' + q.correctIndex)}) ${q.options[q.correctIndex]}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldSuccess
                    )

                    if (userSelected != null && !isCorrect) {
                        Text(
                            text = "Your Answer: (${('a' + userSelected)}) ${q.options[userSelected]}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = RoseError
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "ব্যাখ্যা: ${q.explanationBn}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
