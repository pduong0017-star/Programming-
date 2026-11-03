package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.AiTemplate
import com.example.data.model.VideoProject
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class VibeCutRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createAndObserveProject_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = VibeCutRepository(firestore)
        val projectId = "proj_${UUID.randomUUID().toString().replace("-", "").take(12)}"

        val project = VideoProject(
            id = projectId,
            userId = uid,
            authorName = "Alice Creator",
            title = "Cyberpunk Neon Beat Cut",
            caption = "Chỉnh sửa tự động bằng AI trên VibeCut!",
            hashtags = "#vibecut #aivideo #xuhuong",
            scenePreset = "cyberpunk",
            aiFilter = "neon_glow",
            transitionStyle = "glitch_cut",
            musicTrack = "Cyber Phonk 2026",
            bpm = 138,
            durationSeconds = 15,
            autoCaptionsJson = "[{\"startSec\":0,\"endSec\":3,\"text\":\"Biến hình Cyberpunk!\"}]",
            aiDirectorNotes = "Mở đầu cuốn hút, nhịp cắt chuẩn.",
            viralScore = 95,
            likesCount = 10,
            isPublished = true
        )

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.createProject(project) }
        assertTrue("Expected project creation to succeed", createResult.isSuccess)
        assertEquals(projectId, createResult.getOrThrow())

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserProjects().first { list -> list.any { it.id == projectId } }
        }
        assertTrue(emitted.any { it.id == projectId && it.title == "Cyberpunk Neon Beat Cut" })
    }

    @Test
    fun getProjectById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = VibeCutRepository(firestore)
        val projectId = "proj_${UUID.randomUUID().toString().replace("-", "").take(12)}"

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createProject(
                VideoProject(
                    id = projectId,
                    userId = aliceUid,
                    authorName = "Alice",
                    title = "Alice Private Draft",
                    caption = "Draft video",
                    hashtags = "#draft"
                )
            )
        }
        assertTrue(createResult.isSuccess)

        // Sign in as Bob and attempt to read Alice's project
        signInTestUser(BOB_EMAIL)
        val bobRepo = VibeCutRepository(firestore)
        val bobReadResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getProjectById(projectId)
        }
        assertTrue("Expected cross-user read to fail", bobReadResult.isFailure)
        val fsException = bobReadResult.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException", fsException)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsException?.code)
    }

    @Test
    fun observeProjects_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = VibeCutRepository(firestore)
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeUserProjects().first()
            }
            fail("Expected unauthenticated observeUserProjects to throw")
        } catch (e: Exception) {
            // Expected failure (IllegalStateException from requireUserId or PERMISSION_DENIED)
            assertTrue(
                e is IllegalStateException ||
                    e.unwrapFirestoreException()?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
            )
        }
    }

    @Test
    fun createAndObserveAiTemplate_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = VibeCutRepository(firestore)
        val templateId = "tpl_${UUID.randomUUID().toString().replace("-", "").take(12)}"

        val template = AiTemplate(
            id = templateId,
            userId = uid,
            name = "Biến Hình Cyberpunk 3 Nhịp",
            category = "dance_trend",
            hookLine = "Đừng rời mắt ở giây thứ 3!",
            storyboardBeats = listOf(
                "0-2s: Góc máy nghiêng dưới ánh đèn đường",
                "2-5s: Nhịp trống dồn + chuyển cảnh Glitch",
                "5-10s: Hiệu ứng Neon Glow bùng nổ"
            ),
            recommendedPreset = "cyberpunk",
            recommendedFilter = "neon_glow",
            recommendedBpm = 140
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) { repository.createTemplate(template) }
        assertTrue(result.isSuccess)

        val templates = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserTemplates().first { list -> list.any { it.id == templateId } }
        }
        assertTrue(templates.any { it.id == templateId && it.name == "Biến Hình Cyberpunk 3 Nhịp" })
    }

    private companion object {
        const val ALICE_EMAIL = "alice@vibecut.test"
        const val BOB_EMAIL = "bob@vibecut.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
