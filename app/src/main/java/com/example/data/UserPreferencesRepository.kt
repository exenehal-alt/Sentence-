package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserStats(
    val totalAnswered: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val bookmarkedIds: Set<String> = emptySet(),
    val completedQuestionIds: Set<String> = emptySet(),
    val wrongQuestionIds: Set<String> = emptySet()
)

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sentence_comp_prefs", Context.MODE_PRIVATE)

    private val _stats = MutableStateFlow(loadStats())
    val stats: StateFlow<UserStats> = _stats.asStateFlow()

    private fun loadStats(): UserStats {
        val total = prefs.getInt("total_answered", 0)
        val correct = prefs.getInt("correct_count", 0)
        val incorrect = prefs.getInt("incorrect_count", 0)
        val bookmarks = prefs.getStringSet("bookmarked_ids", emptySet()) ?: emptySet()
        val completed = prefs.getStringSet("completed_ids", emptySet()) ?: emptySet()
        val wrong = prefs.getStringSet("wrong_ids", emptySet()) ?: emptySet()

        return UserStats(
            totalAnswered = total,
            correctCount = correct,
            incorrectCount = incorrect,
            bookmarkedIds = bookmarks,
            completedQuestionIds = completed,
            wrongQuestionIds = wrong
        )
    }

    fun toggleBookmark(questionId: String) {
        val current = _stats.value.bookmarkedIds.toMutableSet()
        if (current.contains(questionId)) {
            current.remove(questionId)
        } else {
            current.add(questionId)
        }
        prefs.edit().putStringSet("bookmarked_ids", current).apply()
        _stats.value = _stats.value.copy(bookmarkedIds = current)
    }

    fun recordAnswer(questionId: String, isCorrect: Boolean) {
        val completed = _stats.value.completedQuestionIds.toMutableSet().apply { add(questionId) }
        val wrong = _stats.value.wrongQuestionIds.toMutableSet()
        var correct = _stats.value.correctCount
        var incorrect = _stats.value.incorrectCount

        if (isCorrect) {
            correct++
            wrong.remove(questionId)
        } else {
            incorrect++
            wrong.add(questionId)
        }
        val total = correct + incorrect

        prefs.edit()
            .putInt("total_answered", total)
            .putInt("correct_count", correct)
            .putInt("incorrect_count", incorrect)
            .putStringSet("completed_ids", completed)
            .putStringSet("wrong_ids", wrong)
            .apply()

        _stats.value = _stats.value.copy(
            totalAnswered = total,
            correctCount = correct,
            incorrectCount = incorrect,
            completedQuestionIds = completed,
            wrongQuestionIds = wrong
        )
    }

    fun resetStats() {
        prefs.edit().clear().apply()
        _stats.value = UserStats()
    }
}
