# Sentence Completion - Admission English Prep

A comprehensive, interactive Android learning and exam prep application for university admission aspirants (DU, JU, CU, GST, etc.) based on the 5 core Sentence Completion categories: **Apposition & Structure Fitting**, **Positive/Negative Flow**, **Punctuation Markers**, **Transitional Words (Contrast, Support, Cause & Effect)**, and **Word Fitting (General & Advanced Vocabulary)**.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Based on your selections, the application will provide:
> 1. **Interactive Quiz & Practice Experience with Immediate Explanations**: Instant feedback on answer selection, comprehensive rule breakdown in Bengali & English, and detailed grammar clues.
> 2. **Bilingual Explanations**: Concepts explained in lucid Bengali with exact grammatical terms, sentence structures, and vocabulary definitions in English.
> 3. **Structured around the 5 Core Prompt Types & Sub-types**: Clear navigation by type, sub-types, university tag filters (DU, JU, CU, GST, Probable), bookmarking difficult items, and a full Mock Exam simulator.

- **Confirmed Decision 1**: Interactive Quiz and Practice App with Instant Bilingual Explanation.
- **Confirmed Decision 2**: 5-Type categorization directly mapping to admission exam question patterns.
- **Confirmed Decision 3**: Local persistence for user progress, quiz scores, question bookmarks, and custom notes.

---

### 1. Overview & Core Concept

- **What It Does**: 
  - Delivers complete study notes and categorized interactive quizzes covering all 5 Sentence Completion types and their subcategories.
  - Houses the complete dataset of 60+ real admission questions provided in the brief (DU, JU, CU, GST, CVASU, PUST, HSTU, JUST) along with curated high-yield probable questions.
  - Provides two distinct modes: **Learn & Practice Mode** (instant explanation, grammar hints, rule breakdown) and **Timed Exam Mode** (simulated admission test with score analytics and review).
  - Includes a **Rule Reference Handbook** summarizing core grammar concepts (Sentence Completion vs. Completing Sentence, Apposition rules, Transitional Words matrix, Context clues).
  - Personal progress tracking (overall accuracy, mastery by type, bookmarked tricky questions, review incorrect attempts).

- **Target Audience**: University admission candidates (DU A/B/C/D units, GST Clusters, Medical/Engineering English sections, and BCS/Bank job test takers) aiming to master English Sentence Completion.

- **Key Value**: Replaces dry PDF notes with an engaging, structured practice system that trains students to recognize the structural clues (commas, contrast words, positive/negative polarity, and collocations) in seconds.

---

### 2. User Experience & Visual Design

#### Key User Flows:
1. **Home / Dashboard**:
   - Welcome banner with motivating exam quote and progress overview (Total Answered, Overall Accuracy, Mastery Badges).
   - Quick Actions: **Start Practice by Type**, **Take Random Mock Test**, **Rule Book / Grammar Guide**, **Bookmarked Questions**.
   - Type Cards with progress bars for each of the 5 types (Type 1 to Type 5 with subcategories).
2. **Type Practice Screen**:
   - Displays the core concept & strategy banner in Bengali (e.g., Apposition definition, Transitional words table).
   - Question cards with options, immediate visual feedback (green for correct, red with shake animation for incorrect).
   - Deep-dive explanation sheet showing: Correct Answer, Grammatical Rule in Bengali & English, Why other options are wrong, and Vocabulary meanings.
   - Action controls: Bookmark, Next question, View Rule hint before answering.
3. **Timed Mock Test Simulator**:
   - Configurable questions (10, 20, or all) with countdown timer.
   - Smooth question swipe/navigation, progress tracker.
   - Comprehensive result scorecard at the end: Score, Accuracy, Time taken, Breakdown by Type, and Full Answer Review.
4. **Grammar & Strategy Guide**:
   - Tabbed reference guide for Sentence Completion concepts:
     - *Concept Intro*: Sentence Completion vs. Completing Sentence.
     - *Type 1*: Apposition, Punctuation isolation, Verb/Clause agreement.
     - *Type 2*: Flow analysis, Cause-Effect polarity.
     - *Type 3*: Punctuation connections (Semicolon, Colon, Comma).
     - *Type 4*: Transitional words matrix (Contrast, Support, Cause & Effect).
     - *Type 5*: Word Fitting strategy (Pre-thinking answers, Collocations, Advanced GRE/SAT words).
5. **Saved & Review Hub**:
   - Bookmarked questions categorized by type.
   - "Mistakes Notebook" to review and re-test questions previously answered incorrectly.

#### Visual Identity & Theme:
- **Aesthetic Direction**: Academic elegance with modern Material 3 styling. Deep indigo / navy background with vibrant teal/emerald accents for success and warm coral for alerts.
- **Color Palette**:
  - `Primary`: Deep Royal Indigo (`#1A237E` / `#3949AB`)
  - `Secondary / Accent`: Emerald Teal (`#00897B` / `#26A69A`)
  - `Surface`: Clean Soft Tinted Card surfaces (`#F8F9FA` Light / `#121826` Dark)
  - `Correct / Success`: Crisp Green (`#2E7D32` / `#66BB6A`)
  - `Incorrect / Alert`: Crimson Coral (`#C62828` / `#EF5350`)
- **Typography**:
  - Clear, legible sans-serif for UI, English questions, and Bengali unicode text with optimal line heights (`sp`).
  - High-contrast typography hierarchy ensuring admission question text is crisp and readable.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Full In-App Embedded Question Bank**:
  - *Chosen Approach*: Embed all questions with pre-compiled Bengali & English explanations, grammar hints, correct keys, and university tags directly into structured Kotlin data objects.
  - *Why*: Instant launch, 100% offline access for students studying in dorms/traveling, zero network latency.
- **Decision 2: Local User Progress & Bookmarks**:
  - *Chosen Approach*: Lightweight Room or DataStore/SharedPreferences repository tracking user answers, bookmarks, test history, and category progress.
  - *Why*: Fast, reliable, offline-first experience without forcing user registration/login.
- **Decision 3: Dedicated Type Breakdown with Sub-types**:
  - *Chosen Approach*: Allow students to practice Type 4 specifically by its sub-types (Contrast, Support, Cause & Effect) and Type 5 by (General Word Fitting, Advanced Vocabulary) as presented in the prompt.
  - *Why*: Directly reinforces the pedagogical classification taught in top admission coaching programs.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│         (Theme, EdgeToEdge, Dynamic Navigation / BackHandler)          │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           MainViewModel                                │
│   - currentScreen (Dashboard, Practice, Exam, Guide, Bookmarks)        │
│   - selectedType / selectedFilter                                      │
│   - currentQuizState (currentIndex, selectedOption, isSubmitted, etc.) │
│   - userStats & bookmarksState                                         │
└───────────────────┬────────────────────────────────┬───────────────────┘
                    │                                │
                    ▼                                ▼
┌───────────────────────────────────┐┌───────────────────────────────────┐
│         QuestionRepository        ││        UserProgressManager        │
│  - 60+ Embedded Question Bank     ││  - SharedPreferences / StateFlow  │
│  - 5 Types + Subtypes Metadata    ││  - Bookmarks, Solved Status       │
│  - Grammar Rules & Explanations   ││  - Category-wise Accuracy         │
└───────────────────────────────────┘└───────────────────────────────────┘
                    │                                │
                    ▼                                ▼
┌────────────────────────────────────────────────────────────────────────┐
│                            UI Screen Layer                             │
│  ┌──────────────────┐ ┌──────────────────┐ ┌─────────────────────────┐ │
│  │ DashboardScreen  │ │  PracticeScreen  │ │      ExamScreen         │ │
│  └──────────────────┘ └──────────────────┘ └─────────────────────────┘ │
│  ┌──────────────────┐ ┌──────────────────┐ ┌─────────────────────────┐ │
│  │ GrammarGuideView │ │ BookmarksScreen  │ │   QuestionDetailCard    │ │
│  └──────────────────┘ └──────────────────┘ └─────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

#### Data Entities:
```kotlin
enum class QuestionType(val titleBn: String, val titleEn: String, val subtitle: String)
enum class SubType(val titleBn: String, val titleEn: String)

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val type: QuestionType,
    val subType: SubType? = null,
    val examTag: String, // e.g. "JU(D)'24-25", "DU(A)'23-24", "Probable"
    val explanationBn: String,
    val explanationEn: String,
    val ruleHint: String
)

data class UserQuizState(
    val selectedOptionIndex: Int? = null,
    val isRevealed: Boolean = false,
    val isBookmarked: Boolean = false
)
```

#### Verification & Build Plan:
1. Update `metadata.json` and `strings.xml` with the app identity ("Sentence Completion Master").
2. Implement repository with full prompt data (all 60+ questions mapped across Type 1 to Type 5 with Bengali explanations).
3. Implement MVVM ViewModel with practice, exam, rulebook, and bookmark state.
4. Build polished Jetpack Compose UI adhering to M3 and safe insets.
5. Verify compile and clean build via `compile_applet`.
