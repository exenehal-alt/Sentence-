package com.example.model

enum class QuestionType(
    val id: String,
    val number: Int,
    val titleBn: String,
    val titleEn: String,
    val difficultyBn: String,
    val conceptBn: String,
    val strategyBn: String
) {
    APPOSITION_AND_STRUCTURE(
        id = "TYPE_1",
        number = 1,
        titleBn = "Apposition ও Grammar/Structure Fitting",
        titleEn = "Apposition & Structure Fitting",
        difficultyBn = "সবচেয়ে সহজ",
        conceptBn = "দুটি Noun বা Noun Phrase পাশাপাশি বসে একই ব্যক্তি বা বস্তুকে নির্দেশ করলে এবং ২য় Phrase-টি ১মটির ব্যাপারে অতিরিক্ত তথ্য দিলে ২য় Phrase-টিকে Appositive এবং তার অবস্থানকে Apposition বলে।",
        strategyBn = "২য় Word বা Phrase-টি ১মটির থেকে Comma (,), Semicolon (;), Hyphen (-) বা Parentheses ()-এর মাধ্যমে বিচ্ছিন্ন থাকে এবং বাক্যের ব্যাকরণগত গঠন (Part of Speech, Verb-এর রূপ বা Clause) দেখেই সরাসরি উত্তর বের করা যায়।"
    ),
    POSITIVE_NEGATIVE_FLOW(
        id = "TYPE_2",
        number = 2,
        titleBn = "Positive / Negative Flow",
        titleEn = "Positive / Negative Sentiment Flow",
        difficultyBn = "অত্যন্ত সহজ",
        conceptBn = "বাক্যটি ইতিবাচক (Positive) নাকি নেতিবাচক (Negative) অর্থ প্রকাশ করছে তা খেয়াল করতে হয়।",
        strategyBn = "বাক্যে কারণ হিসেবে যা বলা হয়েছে তার ফলাফল ইতিবাচক হলে Positive শব্দ এবং নেতিবাচক হলে Negative শব্দ অপশন থেকে বেছে নিতে হবে।"
    ),
    PUNCTUATION(
        id = "TYPE_3",
        number = 3,
        titleBn = "Punctuation (বিরামচিহ্ন ভিত্তিক)",
        titleEn = "Punctuation Connections",
        difficultyBn = "মাঝারি সহজ",
        conceptBn = "বিরামচিহ্ন দুটি বাক্যের মাঝে বসে সমার্থক শব্দ ব্যবহার করে সংযোগ স্থাপন করতে সাহায্য করে।",
        strategyBn = "কমা (,), কোলন (:) বা সেমিকোলন (;) থাকলে পরবর্তী অংশটি পূর্ববর্তী তথ্যকেই অনুসরণ বা ব্যাখ্যা করে, তাই বিরামচিহ্নের আগের বা পরের অংশের সমার্থক শব্দটি অপশন থেকে খুঁজে বসাতে হয়।"
    ),
    TRANSITIONAL_WORDS(
        id = "TYPE_4",
        number = 4,
        titleBn = "Transitional Words (সংযোগকারী শব্দ ভিত্তিক)",
        titleEn = "Transitional Words (Contrast, Support, Cause & Effect)",
        difficultyBn = "মাঝারি",
        conceptBn = "Transitional words বাক্যে কী হতে যাচ্ছে সে সম্পর্কে ধারণা দেয় এবং লেখক পূর্ববর্তী ধারণার সাথে বৈসাদৃশ্য (Contrast), সমর্থন (Support), নাকি কারণ ও ফলাফল (Cause and Effect) দেখাবেন তা নির্দেশ করে।",
        strategyBn = "চেনার শব্দ দেখে বাক্যের প্রকৃতি নির্ধারণ করুন: But, Yet, Despite থাকলে Contrast; And, Furthermore থাকলে Support; Because, So, Hence থাকলে Cause and Effect।"
    ),
    WORD_FITTING(
        id = "TYPE_5",
        number = 5,
        titleBn = "Word Fitting (শব্দের অর্থ ও সামঞ্জস্যতা ভিত্তিক)",
        titleEn = "Word Fitting & Advanced Vocabulary",
        difficultyBn = "তুলনামূলক কঠিন",
        conceptBn = "অপশনগুলোর দিকে তাকানোর আগেই এমন একটি শব্দের কথা চিন্তা করতে হবে যা বাক্যটির অর্থের সাথে পুরোপুরি সামঞ্জস্যপূর্ণ।",
        strategyBn = "বাক্যের মূল ভাব অনুধাবন করে প্রাক-অনুমান (Pre-think) করুন। এরপর অপশন থেকে সেই শব্দটি বা তার সমার্থক শব্দ (Synonymous word) বাছাই করে শূন্যস্থান পূরণ করুন।"
    )
}

enum class SubType(val id: String, val titleBn: String, val titleEn: String) {
    CONTRAST("CONTRAST", "Contrast Indicators (বিপরীতমুখী ধারণা)", "Contrast Indicators"),
    SUPPORT("SUPPORT", "Support Indicators (সমর্থনমূলক ধারণা)", "Support Indicators"),
    CAUSE_AND_EFFECT("CAUSE_EFFECT", "Cause and Effect Indicators (কারণ ও ফলাফল)", "Cause & Effect"),
    GENERAL_WORD_FITTING("GENERAL_WF", "সাধারণ Word Fitting ও প্রচলিত Phrase", "General Word Fitting"),
    ADVANCED_VOCABULARY("ADVANCED_VOCAB", "উন্নত Vocabulary ভিত্তিক Word Fitting", "Advanced Vocabulary")
}

data class Question(
    val id: String,
    val serialNo: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val type: QuestionType,
    val subType: SubType? = null,
    val examTag: String,
    val explanationBn: String,
    val explanationEn: String,
    val ruleHint: String
)

data class GrammarRule(
    val titleBn: String,
    val titleEn: String,
    val summaryBn: String,
    val bulletPointsBn: List<String>,
    val examples: List<Pair<String, String>>
)
