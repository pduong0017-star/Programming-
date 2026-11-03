package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AiTemplate
import com.example.data.model.VideoProject
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class VibeCutRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProjects(): Flow<List<VideoProject>> = flow {
        val uid = requireUserId()
        val path = COLLECTION_PROJECTS
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        VideoProject::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun getUserProjects(): Result<List<VideoProject>> = runCatching {
        val uid = requireUserId()
        val path = COLLECTION_PROJECTS
        try {
            val snapshot = db.collection(path)
                .whereEqualTo("userId", uid)
                .get()
                .await()
            snapshot.toObjects(
                VideoProject::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            throw e
        }
    }

    suspend fun getProjectById(projectId: String): Result<VideoProject?> = runCatching {
        requireUserId()
        val docRef = db.collection(COLLECTION_PROJECTS).document(projectId)
        try {
            val doc = docRef.get().await()
            doc.toObject(VideoProject::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun createProject(project: VideoProject): Result<String> = runCatching {
        val uid = requireUserId()
        val collectionRef = db.collection(COLLECTION_PROJECTS)
        val docRef = if (project.id.isNotBlank()) {
            collectionRef.document(project.id)
        } else {
            collectionRef.document()
        }
        val payload = mapOf(
            "userId" to uid,
            "authorName" to project.authorName.ifBlank { "Creator" }.take(80),
            "title" to project.title.ifBlank { "Untitled VibeCut" }.take(120),
            "caption" to project.caption.ifBlank { "Được tạo bởi VibeCut AI Studio" }.take(500),
            "hashtags" to project.hashtags.ifBlank { "#vibecut #aivideo #xuhuong" }.take(200),
            "scenePreset" to project.scenePreset,
            "aiFilter" to project.aiFilter,
            "transitionStyle" to project.transitionStyle,
            "musicTrack" to project.musicTrack.ifBlank { "Cyber Phonk 2026" }.take(100),
            "bpm" to project.bpm.coerceIn(60, 220),
            "durationSeconds" to project.durationSeconds.coerceIn(5, 180),
            "autoCaptionsJson" to project.autoCaptionsJson.ifBlank { "[]" }.take(3000),
            "aiDirectorNotes" to project.aiDirectorNotes.ifBlank { "AI Beat-Sync Ready" }.take(2000),
            "viralScore" to project.viralScore.coerceIn(0, 100),
            "likesCount" to project.likesCount.coerceIn(0, 10000000),
            "isPublished" to project.isPublished,
            "mediaUri" to project.mediaUri.take(1000),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateProject(project: VideoProject): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(project.id.isNotBlank()) { "Project ID must not be blank for update" }
        requireNotNull(project.createdAt) { "Project createdAt must not be null for update" }
        val docRef = db.collection(COLLECTION_PROJECTS).document(project.id)
        try {
            val sanitized = project.copy(
                userId = uid,
                authorName = project.authorName.ifBlank { "Creator" }.take(80),
                title = project.title.ifBlank { "Untitled VibeCut" }.take(120),
                caption = project.caption.ifBlank { "Được tạo bởi VibeCut AI Studio" }.take(500),
                hashtags = project.hashtags.ifBlank { "#vibecut #aivideo" }.take(200),
                musicTrack = project.musicTrack.ifBlank { "Cyber Phonk 2026" }.take(100),
                bpm = project.bpm.coerceIn(60, 220),
                durationSeconds = project.durationSeconds.coerceIn(5, 180),
                autoCaptionsJson = project.autoCaptionsJson.ifBlank { "[]" }.take(3000),
                aiDirectorNotes = project.aiDirectorNotes.ifBlank { "AI Beat-Sync Ready" }.take(2000),
                viralScore = project.viralScore.coerceIn(0, 100),
                likesCount = project.likesCount.coerceIn(0, 10000000),
                mediaUri = project.mediaUri.take(1000),
                updatedAt = null
            )
            docRef.set(sanitized).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteProject(projectId: String): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection(COLLECTION_PROJECTS).document(projectId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    fun observeUserTemplates(): Flow<List<AiTemplate>> = flow {
        val uid = requireUserId()
        val path = COLLECTION_TEMPLATES
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        AiTemplate::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createTemplate(template: AiTemplate): Result<String> = runCatching {
        val uid = requireUserId()
        val collectionRef = db.collection(COLLECTION_TEMPLATES)
        val docRef = if (template.id.isNotBlank()) {
            collectionRef.document(template.id)
        } else {
            collectionRef.document()
        }
        val beats = template.storyboardBeats.take(10).map { it.take(300) }
        val payload = mapOf(
            "userId" to uid,
            "name" to template.name.ifBlank { "Mẫu AI Xu Hướng" }.take(100),
            "category" to template.category,
            "hookLine" to template.hookLine.ifBlank { "Xem đến giây cuối để thấy điều bất ngờ!" }.take(300),
            "storyboardBeats" to beats,
            "recommendedPreset" to template.recommendedPreset,
            "recommendedFilter" to template.recommendedFilter,
            "recommendedBpm" to template.recommendedBpm.coerceIn(60, 220),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteTemplate(templateId: String): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection(COLLECTION_TEMPLATES).document(templateId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    companion object {
        const val COLLECTION_PROJECTS = "video_projects"
        const val COLLECTION_TEMPLATES = "ai_templates"
    }
}
