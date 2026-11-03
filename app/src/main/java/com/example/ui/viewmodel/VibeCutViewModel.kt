package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiEditSuggestion
import com.example.data.model.AiTemplate
import com.example.data.model.CaptionCue
import com.example.data.model.PresetsData
import com.example.data.model.VideoProject
import com.example.data.repository.VibeCutRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import kotlin.math.abs

private const val TAG = "VibeCutViewModel"

sealed interface DataUiState<out T> {
    data object Loading : DataUiState<Nothing>
    data class Success<T>(val data: T) : DataUiState<T>
    data class Error(val message: String) : DataUiState<Nothing>
}

sealed interface AiMagicUiState {
    data object Idle : AiMagicUiState
    data class Loading(val statusText: String) : AiMagicUiState
    data class StreamingScript(val partialText: String) : AiMagicUiState
    data class EditGenerated(val suggestion: AiEditSuggestion) : AiMagicUiState
    data class ScriptGenerated(val scriptText: String) : AiMagicUiState
    data class Error(val message: String) : AiMagicUiState
}

data class TimelineClip(
    val id: String,
    val label: String,
    val durationSec: Int = 5,
    val volume: Float = 1.0f, // 0.0f .. 2.0f
    val speed: Float = 1.0f, // 0.2f .. 4.0f
    val curvePreset: String = "none", // "none", "montage", "hero", "bullet", "jump_cut", "flash"
    val keyframes: List<Float> = emptyList(), // Normalized positions (0.1f..0.9f) within clip
    val isReversed: Boolean = false
)

data class StudioEditorState(
    val editingProjectId: String = "",
    val title: String = "Biến Hình Cyberpunk Đêm Tokyo",
    val caption: String = "Thử ngay tính năng AI tự cắt nhịp Bass & tạo phụ đề Karaoke trên VibeCut! ⚡🔥",
    val hashtags: String = "#vibecut #capcutai #xuhuong #cyberpunk",
    val scenePreset: String = "cyberpunk",
    val aiFilter: String = "neon_glow",
    val transitionStyle: String = "glitch_cut",
    val musicTrack: String = "Cyber Phonk 2026 (Remix)",
    val bpm: Int = 138,
    val durationSeconds: Int = 15,
    val captions: List<CaptionCue> = listOf(
        CaptionCue(0, 3, "Đừng rời mắt ở giây thứ 3!", "giây thứ 3!"),
        CaptionCue(3, 7, "AI tự động bắt nhịp Bass 138 BPM ⚡", "bắt nhịp Bass"),
        CaptionCue(7, 11, "Phối màu Neon Bloom chuẩn điện ảnh", "Neon Bloom"),
        CaptionCue(11, 15, "Đăng ngay lên Xu Hướng VibeCut!", "Xu Hướng")
    ),
    val aiDirectorNotes: String = "Hook 3 giây đầu tạo sự tò mò. Nhịp cắt Glitch khớp tiếng trống Phonk giúp giữ chân người xem 95%.",
    val viralScore: Int = 95,
    val mediaUri: String = "",
    val isPlaying: Boolean = true,
    val storyboardBeats: List<String> = listOf(
        "0-3s: Mở đầu cận cảnh ánh đèn Neon",
        "3-7s: Nhịp trống Drop + chuyển cảnh Glitch Cut",
        "7-11s: Lia máy góc rộng toàn cảnh phố đêm",
        "11-15s: Kết thúc kèm câu hỏi tương tác"
    ),
    // CapCut Pro NLE Controls
    val aspectRatio: String = "9:16", // "9:16", "16:9", "1:1"
    val resolution: String = "1080P", // "720P", "1080P", "2K", "4K AI"
    val fps: Int = 60, // 24, 30, 60
    val playbackSpeed: Float = 1.0f, // Global fallback speed
    val brightness: Float = 0f, // -0.4f .. 0.4f
    val contrast: Float = 1.0f, // 0.6f .. 1.6f
    val saturation: Float = 1.0f, // 0.0f .. 2.0f
    val sharpen: Float = 0.2f, // 0.0f .. 1.0f
    val aiCutoutEnabled: Boolean = false,
    val animationStyle: String = "combo_pendulum", // "none", "in_zoom", "out_spin", "combo_pendulum"
    val clips: List<TimelineClip> = listOf(
        TimelineClip(
            id = "clip_1",
            label = "Mở đầu Hook",
            durationSec = 5,
            volume = 1.0f,
            speed = 1.0f,
            curvePreset = "none",
            keyframes = listOf(0.25f, 0.75f)
        ),
        TimelineClip(
            id = "clip_2",
            label = "Bass Drop",
            durationSec = 5,
            volume = 1.2f,
            speed = 1.2f,
            curvePreset = "montage",
            keyframes = listOf(0.5f)
        ),
        TimelineClip(
            id = "clip_3",
            label = "Cao trào Outro",
            durationSec = 5,
            volume = 1.0f,
            speed = 1.0f,
            curvePreset = "hero",
            keyframes = emptyList()
        )
    ),
    val selectedClipIndex: Int = 0,
    val pipOverlayPreset: String = "none", // "none", "reaction_cam", "cyber_hud", "timer_corner"
    val activeStickers: List<String> = listOf("⚡"), // "🔥", "⚡", "💯", "🎵", "🚀"
    val aiVoiceEffect: String = "none", // "none", "anime_girl", "deep_cinema", "cyber_robot", "fast_reviewer"
    val teleprompterActive: Boolean = false,
    val teleprompterText: String = "Xin chào các bạn! Hôm nay mình sẽ hướng dẫn cách biến hình bắt nhịp nhạc cực mượt chỉ với 1 chạm bằng công cụ AutoCut AI..."
) {
    val clipSegments: List<Int>
        get() = clips.map { it.durationSec }

    val selectedClip: TimelineClip
        get() = clips.getOrElse(selectedClipIndex.coerceIn(0, (clips.size - 1).coerceAtLeast(0))) {
            TimelineClip(id = "default", label = "Clip 1", durationSec = 5)
        }
}

class VibeCutViewModel(
    private val repository: VibeCutRepository,
    private val currentUserId: String,
    private val currentUserName: String
) : ViewModel() {

    val userProjectsState: StateFlow<DataUiState<List<VideoProject>>> = repository.observeUserProjects()
        .map<List<VideoProject>, DataUiState<List<VideoProject>>> { DataUiState.Success(it) }
        .catch { error ->
            Log.w(TAG, "Error observing user projects", error)
            emit(DataUiState.Error(error.message ?: "Không thể tải danh sách dự án"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DataUiState.Loading
        )

    val userTemplatesState: StateFlow<DataUiState<List<AiTemplate>>> = repository.observeUserTemplates()
        .map<List<AiTemplate>, DataUiState<List<AiTemplate>>> { DataUiState.Success(it) }
        .catch { error ->
            Log.w(TAG, "Error observing user templates", error)
            emit(DataUiState.Error(error.message ?: "Không thể tải kho mẫu AI"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DataUiState.Loading
        )

    private val _editorState = MutableStateFlow(StudioEditorState())
    val editorState: StateFlow<StudioEditorState> = _editorState.asStateFlow()

    private val _aiState = MutableStateFlow<AiMagicUiState>(AiMagicUiState.Idle)
    val aiState: StateFlow<AiMagicUiState> = _aiState.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds: StateFlow<Set<String>> = _likedPostIds.asStateFlow()

    private val aiEditSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "title" to Schema.string(description = "Tiêu đề ngắn gọn, thu hút cho video ngắn (tối đa 80 ký tự)"),
                "caption" to Schema.string(description = "Mô tả bài đăng mạng xã hội hấp dẫn bằng tiếng Việt"),
                "hashtags" to Schema.string(description = "Các hashtag xu hướng cách nhau bởi dấu cách"),
                "scenePreset" to Schema.enumeration(
                    values = listOf("cyberpunk", "golden_hour", "synthwave", "glitch_matrix", "y2k_dream"),
                    description = "Bối cảnh hình ảnh phù hợp nhất"
                ),
                "aiFilter" to Schema.enumeration(
                    values = listOf("none", "neon_glow", "vintage_film", "anime_cel", "cyber_matrix", "dreamy_bloom"),
                    description = "Bộ lọc màu AI phù hợp nhất"
                ),
                "transitionStyle" to Schema.enumeration(
                    values = listOf("whip_pan", "glitch_cut", "zoom_burst", "smooth_fade"),
                    description = "Kiểu chuyển cảnh khớp nhịp nhạc"
                ),
                "musicTrack" to Schema.string(description = "Tên bản nhạc nền gợi ý"),
                "bpm" to Schema.integer(description = "Nhịp độ BPM từ 80 đến 170"),
                "viralScore" to Schema.integer(description = "Điểm tiềm năng xu hướng từ 80 đến 99"),
                "aiDirectorNotes" to Schema.string(description = "Nhận xét của đạo diễn AI về Hook 3 giây đầu và nhịp cắt"),
                "captions" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "startSec" to Schema.integer(description = "Giây bắt đầu"),
                            "endSec" to Schema.integer(description = "Giây kết thúc"),
                            "text" to Schema.string(description = "Lời phụ đề tiếng Việt ngắn gọn"),
                            "highlightWord" to Schema.string(description = "1-2 từ khóa nổi bật nằm trong câu phụ đề")
                        )
                    ),
                    description = "Danh sách 4 câu phụ đề động theo thời gian (0-3s, 3-7s, 7-11s, 11-15s)"
                ),
                "storyboardBeats" to Schema.array(
                    items = Schema.string(),
                    description = "Danh sách 4 phân cảnh hướng dẫn quay/dựng chi tiết"
                )
            )
        )
    }

    private val structuredEditModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = aiEditSchema
            }
        )
    }

    private val streamingScriptModel by lazy {
        Firebase.ai.generativeModel(modelName = "gemini-3.8-flash")
    }

    fun dismissBanner() {
        _statusBanner.value = null
    }

    fun showToast(message: String) {
        _statusBanner.value = message
    }

    fun updateEditorField(transform: (StudioEditorState) -> StudioEditorState) {
        _editorState.value = transform(_editorState.value)
    }

    fun selectClip(index: Int) {
        val current = _editorState.value
        if (index in current.clips.indices) {
            val chosen = current.clips[index]
            _editorState.value = current.copy(
                selectedClipIndex = index,
                playbackSpeed = chosen.speed
            )
        }
    }

    fun startFreshProject() {
        _editorState.value = StudioEditorState(
            editingProjectId = "",
            title = "Dự Án Mới #${(100..999).random()}",
            caption = "Chỉnh sửa trên VibeCut CapCut Pro Studio ✨",
            hashtags = "#vibecut #capcut #xuhuong"
        )
        _statusBanner.value = "Đã mở bàn dựng Dự Án Mới!"
    }

    fun splitClipAtPlayhead() {
        val state = _editorState.value
        val currentClips = state.clips
        if (currentClips.size >= 6) {
            _statusBanner.value = "Đã đạt tối đa 6 phân đoạn Clip trên Timeline."
            return
        }
        val idx = state.selectedClipIndex.coerceIn(0, currentClips.lastIndex)
        val target = currentClips[idx]
        if (target.durationSec <= 2) {
            _statusBanner.value = "Clip \"${target.label}\" quá ngắn (2s) để tách thêm."
            return
        }
        val firstHalf = (target.durationSec / 2).coerceAtLeast(1)
        val secondHalf = (target.durationSec - firstHalf).coerceAtLeast(1)
        val mutable = currentClips.toMutableList()
        mutable[idx] = target.copy(
            label = "${target.label} (A)",
            durationSec = firstHalf
        )
        mutable.add(
            idx + 1,
            target.copy(
                id = "clip_${System.currentTimeMillis()}",
                label = "${target.label} (B)",
                durationSec = secondHalf,
                keyframes = listOf(0.5f)
            )
        )
        val totalDur = mutable.sumOf { it.durationSec }.coerceIn(5, 60)
        _editorState.value = state.copy(
            clips = mutable,
            selectedClipIndex = idx + 1,
            durationSeconds = totalDur
        )
        _statusBanner.value = "✂️ Đã tách thành ${mutable.size} phân đoạn Clip độc lập!"
    }

    fun trimSelectedClip(deltaSec: Int) {
        val state = _editorState.value
        val idx = state.selectedClipIndex.coerceIn(0, state.clips.lastIndex)
        val clip = state.clips[idx]
        val newDur = (clip.durationSec + deltaSec).coerceIn(2, 12)
        if (newDur == clip.durationSec) return
        val mutable = state.clips.toMutableList()
        mutable[idx] = clip.copy(durationSec = newDur)
        val totalDur = mutable.sumOf { it.durationSec }.coerceIn(5, 60)
        _editorState.value = state.copy(
            clips = mutable,
            durationSeconds = totalDur
        )
        _statusBanner.value = "⏱️ Thời lượng \"${clip.label}\": ${newDur}s (Tổng: ${totalDur}s)"
    }

    fun duplicateSelectedClip() {
        val state = _editorState.value
        if (state.clips.size >= 6) {
            _statusBanner.value = "Timeline tối đa 6 phân đoạn Clip."
            return
        }
        val idx = state.selectedClipIndex.coerceIn(0, state.clips.lastIndex)
        val source = state.clips[idx]
        val copyClip = source.copy(
            id = "clip_dup_${System.currentTimeMillis()}",
            label = "${source.label} (Copy)"
        )
        val mutable = state.clips.toMutableList()
        mutable.add(idx + 1, copyClip)
        val totalDur = mutable.sumOf { it.durationSec }.coerceIn(5, 60)
        _editorState.value = state.copy(
            clips = mutable,
            selectedClipIndex = idx + 1,
            durationSeconds = totalDur
        )
        _statusBanner.value = "⧉ Đã nhân đôi \"${source.label}\"!"
    }

    fun deleteSelectedClip() {
        val state = _editorState.value
        if (state.clips.size <= 1) {
            _statusBanner.value = "Cần giữ lại ít nhất 1 Clip chính trên Timeline."
            return
        }
        val idx = state.selectedClipIndex.coerceIn(0, state.clips.lastIndex)
        val removed = state.clips[idx]
        val mutable = state.clips.toMutableList()
        mutable.removeAt(idx)
        val newSelected = (idx - 1).coerceAtLeast(0)
        val totalDur = mutable.sumOf { it.durationSec }.coerceIn(5, 60)
        _editorState.value = state.copy(
            clips = mutable,
            selectedClipIndex = newSelected,
            durationSeconds = totalDur
        )
        _statusBanner.value = "🗑️ Đã xóa \"${removed.label}\" khỏi Timeline."
    }

    fun updateSelectedClip(transform: (TimelineClip) -> TimelineClip) {
        val state = _editorState.value
        val idx = state.selectedClipIndex.coerceIn(0, state.clips.lastIndex)
        val updatedClip = transform(state.clips[idx])
        val mutable = state.clips.toMutableList()
        mutable[idx] = updatedClip
        _editorState.value = state.copy(
            clips = mutable,
            playbackSpeed = updatedClip.speed
        )
    }

    fun toggleKeyframeAtPlayhead(normalizedProgress: Float) {
        val state = _editorState.value
        val idx = state.selectedClipIndex.coerceIn(0, state.clips.lastIndex)
        val clip = state.clips[idx]
        val clampedPos = normalizedProgress.coerceIn(0.12f, 0.88f)
        val existingIdx = clip.keyframes.indexOfFirst { abs(it - clampedPos) < 0.12f }
        val updatedKeyframes = if (existingIdx >= 0) {
            clip.keyframes.toMutableList().also { it.removeAt(existingIdx) }
        } else {
            (clip.keyframes + clampedPos).sorted().take(5)
        }
        val mutable = state.clips.toMutableList()
        mutable[idx] = clip.copy(keyframes = updatedKeyframes)
        _editorState.value = state.copy(clips = mutable)
        _statusBanner.value = if (existingIdx >= 0) {
            "◆ Đã gỡ mốc Keyframe khỏi \"${clip.label}\""
        } else {
            "◆ Đã gắn mốc Keyframe chuyển động vào \"${clip.label}\" (${updatedKeyframes.size} mốc)"
        }
    }

    fun toggleSticker(stickerEmoji: String) {
        val current = _editorState.value.activeStickers
        val updated = if (current.contains(stickerEmoji)) {
            current - stickerEmoji
        } else {
            (current + stickerEmoji).take(4)
        }
        _editorState.value = _editorState.value.copy(activeStickers = updated)
    }

    fun applyAi4kEnhance() {
        _editorState.value = _editorState.value.copy(
            resolution = "4K AI",
            fps = 60,
            sharpen = 0.85f,
            contrast = 1.18f,
            saturation = 1.15f
        )
        _statusBanner.value = "✨ Đã kích hoạt Làm Nét 4K 60FPS & Khử Nhiễu HDR AI!"
    }

    fun updateCaptionCue(index: Int, updatedCue: CaptionCue) {
        val current = _editorState.value.captions.toMutableList()
        if (index in current.indices) {
            current[index] = updatedCue
            _editorState.value = _editorState.value.copy(captions = current)
        }
    }

    fun addCaptionCue() {
        val current = _editorState.value.captions
        val lastEnd = current.maxOfOrNull { it.endSec } ?: 0
        val newStart = lastEnd.coerceAtMost(12)
        val newEnd = (newStart + 3).coerceAtMost(15)
        val updated = current + CaptionCue(
            startSec = newStart,
            endSec = newEnd,
            text = "Khoảnh khắc bùng nổ mới! ✨",
            highlightWord = "bùng nổ"
        )
        _editorState.value = _editorState.value.copy(captions = updated)
    }

    fun loadProjectIntoStudio(project: VideoProject) {
        val decodedCaptions = PresetsData.decodeCaptions(project.autoCaptionsJson).ifEmpty {
            _editorState.value.captions
        }
        _editorState.value = StudioEditorState(
            editingProjectId = if (project.userId == currentUserId) project.id else "",
            title = project.title,
            caption = project.caption,
            hashtags = project.hashtags,
            scenePreset = project.scenePreset,
            aiFilter = project.aiFilter,
            transitionStyle = project.transitionStyle,
            musicTrack = project.musicTrack,
            bpm = project.bpm,
            durationSeconds = project.durationSeconds,
            captions = decodedCaptions,
            aiDirectorNotes = project.aiDirectorNotes,
            viralScore = project.viralScore,
            mediaUri = project.mediaUri,
            isPlaying = true
        )
        _statusBanner.value = "Đã mở dự án \"${project.title}\" trong Bàn dựng!"
    }

    fun applyTemplateToStudio(template: AiTemplate) {
        val autoCaptions = template.storyboardBeats.take(4).mapIndexed { idx, beat ->
            val cleanText = beat.substringAfter(":").trim().ifBlank { beat }
            val words = cleanText.split(" ")
            val highlight = words.takeLast(2).joinToString(" ")
            CaptionCue(
                startSec = idx * 4,
                endSec = ((idx + 1) * 4).coerceAtMost(15),
                text = cleanText,
                highlightWord = highlight
            )
        }.ifEmpty {
            listOf(CaptionCue(0, 5, template.hookLine, ""))
        }

        _editorState.value = _editorState.value.copy(
            editingProjectId = "",
            title = template.name,
            caption = "${template.hookLine} • Dựng tự động từ mẫu AI VibeCut",
            scenePreset = template.recommendedPreset,
            aiFilter = template.recommendedFilter,
            bpm = template.recommendedBpm,
            captions = autoCaptions,
            storyboardBeats = template.storyboardBeats,
            aiDirectorNotes = "Áp dụng mẫu kịch bản: ${template.hookLine}",
            viralScore = 94,
            isPlaying = true
        )
        _statusBanner.value = "Đã áp dụng mẫu \"${template.name}\" vào Bàn dựng!"
    }

    fun generateAiVideoEdit(userPrompt: String) {
        if (userPrompt.isBlank()) {
            _aiState.value = AiMagicUiState.Error("Vui lòng nhập ý tưởng hoặc chủ đề video ngắn.")
            return
        }
        viewModelScope.launch {
            _aiState.value = AiMagicUiState.Loading("AutoCut AI đang cắt nhịp, gắn Keyframe & tạo phụ đề...")
            try {
                val prompt = """
                    Bạn là Đạo diễn Video Ngắn AI hàng đầu trên mạng xã hội (CapCut, TikTok, Reels).
                    Hãy thiết kế một bản dựng hoàn chỉnh cho video ngắn 15 giây dựa trên ý tưởng sau:
                    "$userPrompt"
                    
                    Yêu cầu:
                    1. Tạo tiêu đề, caption bắt trend và hashtags tiếng Việt.
                    2. Chọn scenePreset, aiFilter, transitionStyle và bpm (80-170) phù hợp nhất với tâm trạng video.
                    3. Tạo đúng 4 câu phụ đề động (captions) phủ từ giây 0 đến giây 15 (0-3, 3-7, 7-11, 11-15), mỗi câu có highlightWord là cụm từ đắt giá nằm trong câu.
                    4. Chấm điểm viralScore (80-99) và đưa ra nhận xét đạo diễn (aiDirectorNotes) sắc sảo.
                    5. Liệt kê 4 bước storyboardBeats ngắn gọn.
                """.trimIndent()

                val response = withTimeout(60_000L) {
                    structuredEditModel.generateContent(prompt)
                }
                val jsonString = response.text ?: throw IllegalStateException("Mô hình AI trả về phản hồi rỗng.")

                val json = JSONObject(jsonString)
                val captionsArr = json.getJSONArray("captions")
                val parsedCaptions = (0 until captionsArr.length()).map { i ->
                    val c = captionsArr.getJSONObject(i)
                    CaptionCue(
                        startSec = c.optInt("startSec", i * 4),
                        endSec = c.optInt("endSec", (i + 1) * 4),
                        text = c.getString("text"),
                        highlightWord = c.optString("highlightWord", "")
                    )
                }
                val beatsArr = json.getJSONArray("storyboardBeats")
                val parsedBeats = (0 until beatsArr.length()).map { i -> beatsArr.getString(i).take(300) }.take(10)

                val suggestion = AiEditSuggestion(
                    title = json.getString("title").take(120),
                    caption = json.getString("caption").take(500),
                    hashtags = json.getString("hashtags").take(200),
                    scenePreset = json.getString("scenePreset"),
                    aiFilter = json.getString("aiFilter"),
                    transitionStyle = json.getString("transitionStyle"),
                    musicTrack = json.getString("musicTrack").take(100),
                    bpm = json.getInt("bpm").coerceIn(60, 220),
                    viralScore = json.getInt("viralScore").coerceIn(0, 100),
                    aiDirectorNotes = json.getString("aiDirectorNotes").take(2000),
                    captions = parsedCaptions,
                    storyboardBeats = parsedBeats
                )

                _editorState.value = _editorState.value.copy(
                    title = suggestion.title,
                    caption = suggestion.caption,
                    hashtags = suggestion.hashtags,
                    scenePreset = suggestion.scenePreset,
                    aiFilter = suggestion.aiFilter,
                    transitionStyle = suggestion.transitionStyle,
                    musicTrack = suggestion.musicTrack,
                    bpm = suggestion.bpm,
                    viralScore = suggestion.viralScore,
                    aiDirectorNotes = suggestion.aiDirectorNotes,
                    captions = suggestion.captions,
                    storyboardBeats = suggestion.storyboardBeats,
                    teleprompterText = parsedCaptions.joinToString(" • ") { it.text },
                    isPlaying = true
                )
                _aiState.value = AiMagicUiState.EditGenerated(suggestion)
                _statusBanner.value = "AutoCut AI đã dựng xong! Điểm Viral: ${suggestion.viralScore}/100 🔥"
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI video edit timed out", e)
                _aiState.value = AiMagicUiState.Error("Quá thời gian xử lý AI (60 giây). Vui lòng thử lại.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI video edit generation failed", e)
                _aiState.value = AiMagicUiState.Error(e.localizedMessage ?: "Không thể kết nối AI Director.")
            }
        }
    }

    fun streamViralScriptIdea(topic: String) {
        if (topic.isBlank()) {
            _aiState.value = AiMagicUiState.Error("Vui lòng nhập chủ đề kịch bản.")
            return
        }
        viewModelScope.launch {
            _aiState.value = AiMagicUiState.Loading("Đang khởi tạo luồng ý tưởng từ Gemini AI...")
            val accumulated = StringBuilder()
            try {
                val prompt = """
                    Bạn là chuyên gia sáng tạo nội dung video ngắn CapCut triệu view.
                    Hãy viết kịch bản quay dựng 15 giây cực cuốn cho chủ đề: "$topic".
                    Trình bày ngắn gọn, súc tích gồm:
                    - 🎯 Câu Hook 3 giây đầu
                    - 🎬 3 góc máy & chuyển cảnh theo nhịp nhạc
                    - 💡 Lời thoại gợi ý để đọc trên Máy nhắc chữ (Teleprompter)
                """.trimIndent()

                withTimeout(60_000L) {
                    streamingScriptModel.generateContentStream(prompt).collect { chunk ->
                        val text = chunk.text.orEmpty()
                        accumulated.append(text)
                        _aiState.value = AiMagicUiState.StreamingScript(accumulated.toString())
                    }
                }
                if (accumulated.isNotBlank()) {
                    val finalScript = accumulated.toString()
                    _aiState.value = AiMagicUiState.ScriptGenerated(finalScript)
                    _editorState.value = _editorState.value.copy(teleprompterText = finalScript.take(500))
                } else {
                    _aiState.value = AiMagicUiState.Error("Mô hình trả về kết quả trống.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Streaming script timed out", e)
                _aiState.value = AiMagicUiState.Error("Quá thời gian tạo kịch bản trực tiếp.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Streaming script failed", e)
                _aiState.value = AiMagicUiState.Error(e.localizedMessage ?: "Tạo kịch bản thất bại.")
            }
        }
    }

    fun clearAiState() {
        _aiState.value = AiMagicUiState.Idle
    }

    fun saveCurrentProjectToCloud(publishToFeed: Boolean = true) {
        val state = _editorState.value
        viewModelScope.launch {
            val encodedCaptions = PresetsData.encodeCaptions(state.captions)
            val existingProjects = (userProjectsState.value as? DataUiState.Success)?.data.orEmpty()
            val existingMatch = existingProjects.find { it.id == state.editingProjectId }

            if (existingMatch != null && existingMatch.createdAt != null) {
                val updated = existingMatch.copy(
                    authorName = currentUserName.ifBlank { "Creator" },
                    title = state.title,
                    caption = state.caption,
                    hashtags = state.hashtags,
                    scenePreset = state.scenePreset,
                    aiFilter = state.aiFilter,
                    transitionStyle = state.transitionStyle,
                    musicTrack = state.musicTrack,
                    bpm = state.bpm,
                    durationSeconds = state.durationSeconds,
                    autoCaptionsJson = encodedCaptions,
                    aiDirectorNotes = state.aiDirectorNotes,
                    viralScore = state.viralScore,
                    isPublished = publishToFeed,
                    mediaUri = state.mediaUri
                )
                val result = repository.updateProject(updated)
                result.onSuccess {
                    _statusBanner.value = "Đã xuất & cập nhật video \"${state.title}\" (${state.resolution} ${state.fps}FPS)!"
                }.onFailure { err ->
                    _statusBanner.value = "Lỗi cập nhật dự án: ${err.localizedMessage}"
                }
            } else {
                val newProject = VideoProject(
                    userId = currentUserId,
                    authorName = currentUserName.ifBlank { "Creator" },
                    title = state.title,
                    caption = state.caption,
                    hashtags = state.hashtags,
                    scenePreset = state.scenePreset,
                    aiFilter = state.aiFilter,
                    transitionStyle = state.transitionStyle,
                    musicTrack = state.musicTrack,
                    bpm = state.bpm,
                    durationSeconds = state.durationSeconds,
                    autoCaptionsJson = encodedCaptions,
                    aiDirectorNotes = state.aiDirectorNotes,
                    viralScore = state.viralScore,
                    likesCount = 1,
                    isPublished = publishToFeed,
                    mediaUri = state.mediaUri
                )
                val result = repository.createProject(newProject)
                result.onSuccess { newId ->
                    _editorState.value = _editorState.value.copy(editingProjectId = newId)
                    _statusBanner.value = "Đã xuất video \"${state.title}\" (${state.resolution} ${state.fps}FPS) lên đám mây!"
                }.onFailure { err ->
                    _statusBanner.value = "Lỗi lưu dự án: ${err.localizedMessage}"
                }
            }
        }
    }

    fun saveCurrentAsAiTemplate() {
        val state = _editorState.value
        viewModelScope.launch {
            val hook = state.captions.firstOrNull()?.text ?: state.title
            val template = AiTemplate(
                userId = currentUserId,
                name = state.title.take(100),
                category = "dance_trend",
                hookLine = hook.take(300),
                storyboardBeats = state.storyboardBeats.ifEmpty {
                    state.captions.map { "${it.startSec}-${it.endSec}s: ${it.text}" }
                }.take(10),
                recommendedPreset = state.scenePreset,
                recommendedFilter = state.aiFilter,
                recommendedBpm = state.bpm
            )
            repository.createTemplate(template)
                .onSuccess {
                    _statusBanner.value = "Đã lưu công thức vào Kho Mẫu CapCut AI!"
                }
                .onFailure { err ->
                    _statusBanner.value = "Không thể lưu mẫu: ${err.localizedMessage}"
                }
        }
    }

    fun deleteUserProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
                .onSuccess {
                    if (_editorState.value.editingProjectId == projectId) {
                        _editorState.value = _editorState.value.copy(editingProjectId = "")
                    }
                    _statusBanner.value = "Đã xóa dự án khỏi đám mây."
                }
                .onFailure { err ->
                    _statusBanner.value = "Lỗi xóa dự án: ${err.localizedMessage}"
                }
        }
    }

    fun deleteUserTemplate(templateId: String) {
        viewModelScope.launch {
            repository.deleteTemplate(templateId)
                .onSuccess {
                    _statusBanner.value = "Đã xóa mẫu AI."
                }
                .onFailure { err ->
                    _statusBanner.value = "Lỗi xóa mẫu: ${err.localizedMessage}"
                }
        }
    }

    fun toggleLikeOnProject(project: VideoProject) {
        val currentLikes = _likedPostIds.value
        val isAlreadyLiked = currentLikes.contains(project.id)
        _likedPostIds.value = if (isAlreadyLiked) currentLikes - project.id else currentLikes + project.id

        if (project.userId == currentUserId && project.createdAt != null) {
            viewModelScope.launch {
                val delta = if (isAlreadyLiked) -1 else 1
                val updated = project.copy(likesCount = (project.likesCount + delta).coerceAtLeast(0))
                repository.updateProject(updated)
            }
        }
    }
}
