package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.GrammarGuideScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          SentenceCompletionApp(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun SentenceCompletionApp(viewModel: MainViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val stats by viewModel.userStats.collectAsState()

  // Handle system back navigation
  BackHandler(enabled = currentScreen !is Screen.Dashboard) {
    viewModel.goBack()
  }

  when (val screen = currentScreen) {
    is Screen.Dashboard -> {
      DashboardScreen(
        stats = stats,
        onNavigate = { dest -> viewModel.navigateTo(dest) }
      )
    }

    is Screen.Practice -> {
      PracticeScreen(
        viewModel = viewModel,
        title = screen.filterTitle,
        onBack = { viewModel.goBack() }
      )
    }

    is Screen.Exam -> {
      ExamScreen(
        viewModel = viewModel,
        onBack = { viewModel.goBack() }
      )
    }

    is Screen.GrammarGuide -> {
      GrammarGuideScreen(
        viewModel = viewModel,
        onBack = { viewModel.goBack() }
      )
    }

    is Screen.Bookmarks -> {
      PracticeScreen(
        viewModel = viewModel,
        title = "Bookmarked Questions",
        onBack = { viewModel.goBack() }
      )
    }

    is Screen.MistakesNotebook -> {
      PracticeScreen(
        viewModel = viewModel,
        title = "Review Mistakes",
        onBack = { viewModel.goBack() }
      )
    }
  }
}
