package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class VideoProject(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val title: String = "",
    val caption: String = "",
    val hashtags: String = "",
    val scenePreset: String = "cyberpunk",
    val aiFilter: String = "neon_glow",
    val transitionStyle: String = "glitch_cut",
    val musicTrack: String = "Cyber Phonk 2026",
    val bpm: Int = 130,
    val durationSeconds: Int = 15,
    val autoCaptionsJson: String = "[]",
    val aiDirectorNotes: String = "Hook ấn tượng trong 3 giây đầu, nhịp cắt khớp bass.",
    val viralScore: Int = 92,
    val likesCount: Int = 0,
    val isPublished: Boolean = true,
    val mediaUri: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AiTemplate(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "dance_trend",
    val hookLine: String = "",
    val storyboardBeats: List<String> = emptyList(),
    val recommendedPreset: String = "cyberpunk",
    val recommendedFilter: String = "neon_glow",
    val recommendedBpm: Int = 135,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CaptionCue(
    val startSec: Int,
    val endSec: Int,
    val text: String,
    val highlightWord: String = ""
)

data class AiEditSuggestion(
    val title: String,
    val caption: String,
    val hashtags: String,
    val scenePreset: String,
    val aiFilter: String,
    val transitionStyle: String,
    val musicTrack: String,
    val bpm: Int,
    val viralScore: Int,
    val aiDirectorNotes: String,
    val captions: List<CaptionCue>,
    val storyboardBeats: List<String>
)
