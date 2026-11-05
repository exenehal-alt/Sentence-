package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuestionBank
import com.example.data.UserPreferencesRepository
import com.example.data.UserStats
import com.example.model.GrammarRule
import com.example.model.Question
import com.example.model.QuestionType
import com.example.model.SubType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class Screen {
    data object Dashboard : Screen()
    data class Practice(
        val type: QuestionType? = null,
        val subType: SubType? = null,
        val filterTitle: String = "All Questions"
    ) : Screen()
    data class Exam(val questionCount: Int = 15) : Screen()
    data object GrammarGuide : Screen()
    data object Bookmarks : Screen()
    data object MistakesNotebook : Screen()
}

data class ExamResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val skippedCount: Int,
    val timeSpentSeconds: Int,
    val userAnswers: Map<Int, Int?> // questionIndex -> selectedOptionIndex
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferencesRepository(application.applicationContext)
    val userStats: StateFlow<UserStats> = userPrefs.stats

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Practice Mode State
    private val _practiceQuestions = MutableStateFlow<List<Question>>(emptyList())
    val practiceQuestions: StateFlow<List<Question>> = _practiceQuestions.asStateFlow()

    private val _currentPracticeIndex = MutableStateFlow(0)
    val currentPracticeIndex: StateFlow<Int> = _currentPracticeIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _isExplanationRevealed = MutableStateFlow(false)
    val isExplanationRevealed: StateFlow<Boolean> = _isExplanationRevealed.asStateFlow()

    private val _showHint = MutableStateFlow(false)
    val showHint: StateFlow<Boolean> = _showHint.asStateFlow()

    // Exam Mode State
    private val _examQuestions = MutableStateFlow<List<Question>>(emptyList())
    val examQuestions: StateFlow<List<Question>> = _examQuestions.asStateFlow()

    private val _currentExamIndex = MutableStateFlow(0)
    val currentExamIndex: StateFlow<Int> = _currentExamIndex.asStateFlow()

    private val _examAnswers = MutableStateFlow<MutableMap<Int, Int?>>(mutableMapOf())
    val examAnswers: StateFlow<Map<Int, Int?>> = _examAnswers.asStateFlow()

    private val _examSecondsRemaining = MutableStateFlow(600) // 10 minutes default
    val examSecondsRemaining: StateFlow<Int> = _examSecondsRemaining.asStateFlow()

    private val _examResult = MutableStateFlow<ExamResult?>(null)
    val examResult: StateFlow<ExamResult?> = _examResult.asStateFlow()

    private var timerJob: Job? = null

    val allQuestions: List<Question> = QuestionBank.questions
    val allGrammarRules: List<GrammarRule> = QuestionBank.grammarRules

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        when (screen) {
            is Screen.Practice -> setupPractice(screen.type, screen.subType)
            is Screen.Exam -> setupExam(screen.questionCount)
            is Screen.Bookmarks -> setupBookmarks()
            is Screen.MistakesNotebook -> setupMistakes()
            else -> {}
        }
    }

    fun goBack(): Boolean {
        if (_currentScreen.value !is Screen.Dashboard) {
            timerJob?.cancel()
            _currentScreen.value = Screen.Dashboard
            return true
        }
        return false
    }

    private fun setupPractice(type: QuestionType?, subType: SubType?) {
        val filtered = when {
            subType != null -> allQuestions.filter { it.subType == subType }
            type != null -> allQuestions.filter { it.type == type }
            else -> allQuestions
        }
        _practiceQuestions.value = filtered.ifEmpty { allQuestions }
        _currentPracticeIndex.value = 0
        _selectedOptionIndex.value = null
        _isExplanationRevealed.value = false
        _showHint.value = false
    }

    private fun setupBookmarks() {
        val bookmarked = allQuestions.filter { userStats.value.bookmarkedIds.contains(it.id) }
        _practiceQuestions.value = bookmarked
        _currentPracticeIndex.value = 0
        _selectedOptionIndex.value = null
        _isExplanationRevealed.value = false
        _showHint.value = false
    }

    private fun setupMistakes() {
        val mistakes = allQuestions.filter { userStats.value.wrongQuestionIds.contains(it.id) }
        _practiceQuestions.value = mistakes
        _currentPracticeIndex.value = 0
        _selectedOptionIndex.value = null
        _isExplanationRevealed.value = false
        _showHint.value = false
    }

    fun selectPracticeOption(optionIndex: Int) {
        if (_isExplanationRevealed.value) return
        _selectedOptionIndex.value = optionIndex
        _isExplanationRevealed.value = true

        val currentQ = _practiceQuestions.value.getOrNull(_currentPracticeIndex.value) ?: return
        val isCorrect = (optionIndex == currentQ.correctIndex)
        userPrefs.recordAnswer(currentQ.id, isCorrect)
    }

    fun toggleHint() {
        _showHint.value = !_showHint.value
    }

    fun nextPracticeQuestion() {
        if (_currentPracticeIndex.value < _practiceQuestions.value.size - 1) {
            _currentPracticeIndex.value++
            _selectedOptionIndex.value = null
            _isExplanationRevealed.value = false
            _showHint.value = false
        }
    }

    fun prevPracticeQuestion() {
        if (_currentPracticeIndex.value > 0) {
            _currentPracticeIndex.value--
            _selectedOptionIndex.value = null
            _isExplanationRevealed.value = false
            _showHint.value = false
        }
    }

    fun toggleBookmark(questionId: String) {
        userPrefs.toggleBookmark(questionId)
    }

    // Exam handling
    private fun setupExam(count: Int) {
        val shuffled = allQuestions.shuffled().take(count)
        _examQuestions.value = shuffled
        _currentExamIndex.value = 0
        _examAnswers.value = mutableMapOf()
        _examSecondsRemaining.value = count * 45 // 45s per question
        _examResult.value = null

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_examSecondsRemaining.value > 0) {
                delay(1000)
                _examSecondsRemaining.value--
            }
            submitExam()
        }
    }

    fun selectExamOption(optionIndex: Int) {
        val map = _examAnswers.value.toMutableMap()
        map[_currentExamIndex.value] = optionIndex
        _examAnswers.value = map
    }

    fun setExamIndex(index: Int) {
        if (index in _examQuestions.value.indices) {
            _currentExamIndex.value = index
        }
    }

    fun submitExam() {
        timerJob?.cancel()
        val questions = _examQuestions.value
        val answers = _examAnswers.value

        var correct = 0
        var incorrect = 0
        var skipped = 0

        questions.forEachIndexed { index, question ->
            val userSelected = answers[index]
            if (userSelected == null) {
                skipped++
            } else if (userSelected == question.correctIndex) {
                correct++
                userPrefs.recordAnswer(question.id, true)
            } else {
                incorrect++
                userPrefs.recordAnswer(question.id, false)
            }
        }

        val totalTime = (questions.size * 45) - _examSecondsRemaining.value

        _examResult.value = ExamResult(
            totalQuestions = questions.size,
            correctCount = correct,
            incorrectCount = incorrect,
            skippedCount = skipped,
            timeSpentSeconds = totalTime.coerceAtLeast(0),
            userAnswers = answers
        )
    }

    fun resetStats() {
        userPrefs.resetStats()
    }
}
