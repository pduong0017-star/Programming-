package com.example.data.model

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject

data class ScenePresetInfo(
    val id: String,
    val label: String,
    val subtitle: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color
)

data class AiFilterInfo(
    val id: String,
    val label: String,
    val badge: String,
    val tintColor: Color
)

data class TransitionInfo(
    val id: String,
    val label: String,
    val description: String
)

data class MusicTrackInfo(
    val title: String,
    val artist: String,
    val defaultBpm: Int,
    val genre: String
)

object PresetsData {

    val scenePresets = listOf(
        ScenePresetInfo(
            id = "cyberpunk",
            label = "Cyber Tokyo 2077",
            subtitle = "Phố đêm Neon & mưa phản chiếu",
            primaryColor = Color(0xFF00F5D4),
            secondaryColor = Color(0xFFFF2A85),
            accentColor = Color(0xFF7B2CBF)
        ),
        ScenePresetInfo(
            id = "golden_hour",
            label = "Hoàng Hôn Đà Lạt",
            subtitle = "Nắng vàng 35mm & hạt phim cổ điển",
            primaryColor = Color(0xFFFF9F1C),
            secondaryColor = Color(0xFFE71D36),
            accentColor = Color(0xFFFFBF69)
        ),
        ScenePresetInfo(
            id = "synthwave",
            label = "Synthwave DJ Drop",
            subtitle = "Sóng nhạc Laser & lưới không gian 80s",
            primaryColor = Color(0xFFF72585),
            secondaryColor = Color(0xFF4CC9F0),
            accentColor = Color(0xFF7209B7)
        ),
        ScenePresetInfo(
            id = "glitch_matrix",
            label = "Digital Matrix AI",
            subtitle = "Hiệu ứng phân mảnh dữ liệu & tốc độ cao",
            primaryColor = Color(0xFF00FF87),
            secondaryColor = Color(0xFF60EFFF),
            accentColor = Color(0xFF0061FF)
        ),
        ScenePresetInfo(
            id = "y2k_dream",
            label = "Y2K Pop Aesthetic",
            subtitle = "Ánh sao lấp lánh & pastel hologram",
            primaryColor = Color(0xFFFF70A6),
            secondaryColor = Color(0xFF70D6FF),
            accentColor = Color(0xFFFFD670)
        )
    )

    val aiFilters = listOf(
        AiFilterInfo("none", "Gốc (Original)", "RAW", Color.Transparent),
        AiFilterInfo("neon_glow", "Neon Bloom AI", "HOT", Color(0xFF00F5D4)),
        AiFilterInfo("vintage_film", "Kodak 400 Film", "RETRO", Color(0xFFFF9F1C)),
        AiFilterInfo("anime_cel", "Anime Shinkai", "AI ART", Color(0xFF4CC9F0)),
        AiFilterInfo("cyber_matrix", "Cyber Glitch", "BASS", Color(0xFFFF2A85)),
        AiFilterInfo("dreamy_bloom", "Hologram Mơ Màng", "VIBE", Color(0xFFD08BFF))
    )

    val transitions = listOf(
        TransitionInfo("glitch_cut", "Glitch Bass Cut", "Giật hình RGB khớp nhịp trống"),
        TransitionInfo("whip_pan", "Whip Pan 360°", "Lia máy siêu tốc làm mờ chuyển động"),
        TransitionInfo("zoom_burst", "AI Zoom Burst", "Phóng cận tâm điểm theo nhịp Drop"),
        TransitionInfo("smooth_fade", "Cinematic Cross", "Chuyển cảnh điện ảnh mượt mà")
    )

    val musicTracks = listOf(
        MusicTrackInfo("Cyber Phonk 2026 (Remix)", "DJ Kaito x VibeAI", 138, "Phonk / Bass"),
        MusicTrackInfo("Hoàng Hôn Trên Đồi (Lo-Fi)", "Mây Chill", 92, "Lo-Fi Chill"),
        MusicTrackInfo("Sài Gòn Đêm Không Ngủ", "MCK Beat Vibe", 128, "Hip-Hop / Trap"),
        MusicTrackInfo("Hyperpop Starlight", "Luna Y2K", 150, "Speed Up / Pop"),
        MusicTrackInfo("Tương Lai Bứt Phá", "AI Synth Lab", 124, "Future House")
    )

    val quickAiPrompts = listOf(
        "Biến hình đường phố Cyberpunk ban đêm theo nhạc Phonk cực cháy",
        "Vlog một ngày săn mây và uống cà phê hoàng hôn tại Đà Lạt",
        "Đập hộp & đánh giá nhanh phụ kiện công nghệ tương lai trong 15 giây",
        "Hướng dẫn 3 góc quay điện ảnh bằng điện thoại ai cũng làm được",
        "Nhảy bắt trend Y2K biến hình trang phục khớp nhịp trống"
    )

    val curatedCommunityFeed = listOf(
        VideoProject(
            id = "showcase_1",
            userId = "community_creator_1",
            authorName = "@linh.cyberdance",
            title = "Biến Hình Phố Đêm Tokyo 2077",
            caption = "Dùng tính năng AI Beat-Sync tự cắt theo nhịp Phonk 138 BPM đỉnh thật sự! ✨🔥",
            hashtags = "#vibecut #cyberpunk #xuhuong #aivideo #dance",
            scenePreset = "cyberpunk",
            aiFilter = "neon_glow",
            transitionStyle = "glitch_cut",
            musicTrack = "Cyber Phonk 2026 (Remix)",
            bpm = 138,
            durationSeconds = 15,
            autoCaptionsJson = encodeCaptions(
                listOf(
                    CaptionCue(0, 3, "Đừng chớp mắt ở giây thứ 3!", "giây thứ 3!"),
                    CaptionCue(3, 7, "Khi AI tự bắt nhịp Bass cực căng ⚡", "bắt nhịp Bass"),
                    CaptionCue(7, 11, "Hiệu ứng Neon Bloom phủ sáng góc phố", "Neon Bloom"),
                    CaptionCue(11, 15, "Thử ngay nút Remix AI bên dưới nhé!", "Remix AI")
                )
            ),
            aiDirectorNotes = "Hook thị giác mạnh trong 2.5s đầu. Nhịp cắt Glitch khớp chính xác 138 BPM giúp tăng 42% tỷ lệ xem hết video.",
            viralScore = 97,
            likesCount = 128400,
            isPublished = true
        ),
        VideoProject(
            id = "showcase_2",
            userId = "community_creator_2",
            authorName = "@khoi.wanderlust",
            title = "Hoàng Hôn Đà Lạt 35mm Film",
            caption = "Chỉ cần gõ ý tưởng, AI tự phối màu Kodak cổ điển và viết phụ đề thơ mộng 🌅☕",
            hashtags = "#dalat #chillvibes #vibecut #goldenhour #travelvlog",
            scenePreset = "golden_hour",
            aiFilter = "vintage_film",
            transitionStyle = "smooth_fade",
            musicTrack = "Hoàng Hôn Trên Đồi (Lo-Fi)",
            bpm = 92,
            durationSeconds = 15,
            autoCaptionsJson = encodeCaptions(
                listOf(
                    CaptionCue(0, 4, "Có những chiều Đà Lạt trôi thật chậm...", "Đà Lạt"),
                    CaptionCue(4, 8, "Nắng nhuộm vàng từng tán thông già 🌲", "nhuộm vàng"),
                    CaptionCue(8, 12, "Màu phim Kodak 400 do AI tinh chỉnh", "Kodak 400"),
                    CaptionCue(12, 15, "Lưu lại khoảnh khắc thanh xuân của bạn", "thanh xuân")
                )
            ),
            aiDirectorNotes = "Tông màu ấm áp gợi cảm xúc hoài niệm. Nhịp chuyển cảnh chậm 92 BPM giữ chân người xem yêu thích du lịch.",
            viralScore = 94,
            likesCount = 84200,
            isPublished = true
        ),
        VideoProject(
            id = "showcase_3",
            userId = "community_creator_3",
            authorName = "@minh.techlab",
            title = "Góc Setup Gaming Tương Lai",
            caption = "Test thử bộ lọc Anime Shinkai + Zoom Burst trên VibeCut Studio 🚀💻",
            hashtags = "#setup #techreview #vibecut #synthwave #ai",
            scenePreset = "synthwave",
            aiFilter = "anime_cel",
            transitionStyle = "zoom_burst",
            musicTrack = "Tương Lai Bứt Phá",
            bpm = 124,
            durationSeconds = 15,
            autoCaptionsJson = encodeCaptions(
                listOf(
                    CaptionCue(0, 3, "Góc làm việc năm 2026 trông như thế nào?", "năm 2026"),
                    CaptionCue(3, 7, "Ánh sáng RGB đồng bộ theo sóng nhạc AI", "đồng bộ"),
                    CaptionCue(7, 11, "Chuyển cảnh Zoom Burst không cần keyframe tay", "Zoom Burst"),
                    CaptionCue(11, 15, "Chấm điểm góc máy này từ 1 đến 10!", "1 đến 10!")
                )
            ),
            aiDirectorNotes = "Câu hỏi mở đầu kích thích bình luận. Chuyển cảnh Zoom Burst tạo nhịp điệu năng động cho video công nghệ.",
            viralScore = 91,
            likesCount = 53900,
            isPublished = true
        ),
        VideoProject(
            id = "showcase_4",
            userId = "community_creator_4",
            authorName = "@vy.y2kaesthetic",
            title = "Biến Hình OOTD Y2K Starlight",
            caption = "3 bộ outfit dạo phố cuối tuần được cắt nhịp tự động bằng AI Magic ✨💖",
            hashtags = "#ootd #y2k #fashion #vibecut #biếnhình",
            scenePreset = "y2k_dream",
            aiFilter = "dreamy_bloom",
            transitionStyle = "whip_pan",
            musicTrack = "Hyperpop Starlight",
            bpm = 150,
            durationSeconds = 15,
            autoCaptionsJson = encodeCaptions(
                listOf(
                    CaptionCue(0, 3, "Chưa biết mặc gì cuối tuần này? 👀", "mặc gì"),
                    CaptionCue(3, 7, "Look 1: Năng động với Y2K Pastel", "Look 1"),
                    CaptionCue(7, 11, "Look 2: Cá tính cùng ánh bạc Hologram ✨", "Hologram"),
                    CaptionCue(11, 15, "Bạn chọn Look 1 hay Look 2? Bình luận nhé!", "Look 1 hay Look 2?")
                )
            ),
            aiDirectorNotes = "Nhịp 150 BPM nhanh dồn dập kết hợp Whip Pan giúp tăng lượt xem lại (rewatch) lên 1.8 lần.",
            viralScore = 95,
            likesCount = 96500,
            isPublished = true
        )
    )

    val builtInTemplates = listOf(
        AiTemplate(
            id = "builtin_tpl_1",
            userId = "system",
            name = "Biến Hình Bass Drop 3 Nhịp",
            category = "dance_trend",
            hookLine = "Đừng rời mắt ở giây thứ 3 khi nhịp Bass nổi lên!",
            storyboardBeats = listOf(
                "0-3s: Quay cận cảnh gương mặt/trang phục thường ngày dưới ánh đèn nhẹ",
                "3-7s: Đập tay vào ống kính khớp tiếng trống, chuyển sang bộ lọc Neon Bloom AI",
                "7-12s: Góc máy toàn cảnh chuyển động nhanh với hiệu ứng Glitch Bass Cut",
                "12-15s: Tạo dáng kết thúc + Kêu gọi người xem thử mẫu Remix"
            ),
            recommendedPreset = "cyberpunk",
            recommendedFilter = "neon_glow",
            recommendedBpm = 138
        ),
        AiTemplate(
            id = "builtin_tpl_2",
            userId = "system",
            name = "Nhật Ký Du Lịch Điện Ảnh 35mm",
            category = "story_vlog",
            hookLine = "Nơi trốn phố thị bình yên nhất mà mình từng đặt chân tới...",
            storyboardBeats = listOf(
                "0-4s: Toàn cảnh thiên nhiên/quán cà phê ngập nắng với âm thanh tự nhiên",
                "4-8s: Cận cảnh chi tiết (tách cà phê, bước chân, gió thổi) màu Kodak 400",
                "8-12s: Góc quay lưng người đi về phía hoàng hôn chuyển cảnh Smooth Fade",
                "12-15s: Dòng trích dẫn truyền cảm hứng + địa điểm check-in"
            ),
            recommendedPreset = "golden_hour",
            recommendedFilter = "vintage_film",
            recommendedBpm = 92
        ),
        AiTemplate(
            id = "builtin_tpl_3",
            userId = "system",
            name = "Review Công Nghệ Tốc Độ Cao",
            category = "tech_review",
            hookLine = "3 lý do món đồ nhỏ gọn này đang làm mưa làm gió!",
            storyboardBeats = listOf(
                "0-3s: Cầm sản phẩm đưa sát camera với hiệu ứng AI Zoom Burst",
                "3-7s: Trình diễn tính năng số 1 kèm phụ đề Karaoke nổi bật thông số",
                "7-11s: So sánh nhanh trước & sau khi sử dụng (Whip Pan 360°)",
                "11-15s: Tổng kết điểm số + câu hỏi tương tác phần bình luận"
            ),
            recommendedPreset = "synthwave",
            recommendedFilter = "cyber_matrix",
            recommendedBpm = 128
        )
    )

    fun presetById(id: String): ScenePresetInfo =
        scenePresets.find { it.id == id } ?: scenePresets.first()

    fun filterById(id: String): AiFilterInfo =
        aiFilters.find { it.id == id } ?: aiFilters.first()

    fun transitionById(id: String): TransitionInfo =
        transitions.find { it.id == id } ?: transitions.first()

    fun encodeCaptions(captions: List<CaptionCue>): String {
        val arr = JSONArray()
        captions.forEach { cue ->
            arr.put(
                JSONObject().apply {
                    put("startSec", cue.startSec)
                    put("endSec", cue.endSec)
                    put("text", cue.text)
                    put("highlightWord", cue.highlightWord)
                }
            )
        }
        return arr.toString()
    }

    fun decodeCaptions(json: String): List<CaptionCue> {
        if (json.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).mapNotNull { idx ->
                val obj = arr.optJSONObject(idx) ?: return@mapNotNull null
                CaptionCue(
                    startSec = obj.optInt("startSec", idx * 3),
                    endSec = obj.optInt("endSec", (idx + 1) * 3),
                    text = obj.optString("text", ""),
                    highlightWord = obj.optString("highlightWord", "")
                )
            }.filter { it.text.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
