package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.PresetsData
import com.example.data.model.VideoProject
import com.example.ui.components.VideoCanvasPlayer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SolarGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedScreen(
    userProjects: List<VideoProject>,
    likedPostIds: Set<String>,
    onToggleLike: (VideoProject) -> Unit,
    onRemixInStudio: (VideoProject) -> Unit,
    onNavigateToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFeedFilter by rememberSaveable { mutableIntStateOf(0) } // 0: Xu Hướng, 1: Của Tôi
    val feedItems = remember(userProjects, selectedFeedFilter) {
        val publishedUserProjects = userProjects.filter { it.isPublished }
        if (selectedFeedFilter == 1) {
            publishedUserProjects
        } else {
            publishedUserProjects + PresetsData.curatedCommunityFeed
        }
    }

    var currentIndex by rememberSaveable(feedItems.size, selectedFeedFilter) { mutableIntStateOf(0) }
    val safeIndex = if (feedItems.isEmpty()) 0 else currentIndex.coerceIn(0, feedItems.lastIndex)
    val activePost = feedItems.getOrNull(safeIndex)

    var isPlaying by rememberSaveable { mutableStateOf(true) }
    var showAiBreakdownSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (activePost == null) {
            // Empty state for "Video Của Tôi"
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bạn chưa đăng video ngắn nào",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hãy mở AI Studio để tự động dựng video ngắn khớp nhịp nhạc và tạo phụ đề Karaoke chỉ trong vài giây!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToStudio,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = Color(0xFF03201B)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("empty_feed_open_studio_button")
                ) {
                    Icon(Icons.Default.MovieFilter, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mở AI Studio Ngay", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            val captions = remember(activePost.autoCaptionsJson) {
                PresetsData.decodeCaptions(activePost.autoCaptionsJson)
            }
            val isLiked = likedPostIds.contains(activePost.id)
            val displayedLikes = activePost.likesCount + (if (isLiked && !activePost.id.startsWith("proj")) 1 else 0)

            // Main Full-Bleed Short Video Card
            VideoCanvasPlayer(
                scenePreset = activePost.scenePreset,
                aiFilter = activePost.aiFilter,
                transitionStyle = activePost.transitionStyle,
                bpm = activePost.bpm,
                durationSeconds = activePost.durationSeconds,
                captions = captions,
                mediaUri = activePost.mediaUri,
                isPlaying = isPlaying,
                onTogglePlay = { isPlaying = !isPlaying },
                showControlsOverlay = false,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("feed_video_player")
            )

            // Bottom Gradient Scrim for readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.65f),
                                Color.Black.copy(alpha = 0.92f)
                            )
                        )
                    )
            )

            // Right Floating Social & AI Action Rail
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp, bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Up / Down Reel Navigation Buttons
                Surface(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = CircleShape,
                    modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                if (safeIndex > 0) currentIndex = safeIndex - 1
                                else currentIndex = feedItems.lastIndex
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("feed_prev_reel_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Video trước",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "${safeIndex + 1}/${feedItems.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                        IconButton(
                            onClick = {
                                if (safeIndex < feedItems.lastIndex) currentIndex = safeIndex + 1
                                else currentIndex = 0
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("feed_next_reel_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Video tiếp theo",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Like Action
                FeedActionItem(
                    icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    tint = if (isLiked) NeonMagenta else Color.White,
                    label = formatCompactCount(displayedLikes),
                    testTag = "feed_like_button",
                    onClick = { onToggleLike(activePost) }
                )

                // AI Viral Score Breakdown Action
                FeedActionItem(
                    icon = Icons.Default.Insights,
                    tint = SolarGold,
                    label = "AI ${activePost.viralScore}đ",
                    testTag = "feed_ai_score_button",
                    onClick = { showAiBreakdownSheet = true }
                )

                // 1-Tap Remix in AI Studio
                FeedActionItem(
                    icon = Icons.Default.AutoAwesome,
                    tint = ElectricCyan,
                    label = "Remix AI",
                    testTag = "feed_remix_button",
                    onClick = { onRemixInStudio(activePost) }
                )
            }

            // Bottom Creator Info & Caption Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.80f)
                    .padding(start = 18.dp, bottom = 18.dp)
            ) {
                // AI Spec Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = ElectricCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Viral ${activePost.viralScore}/100",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    Surface(
                        color = NeonMagenta.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${activePost.bpm} BPM Beat-Sync",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = activePost.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = activePost.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = activePost.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = activePost.hashtags,
                    style = MaterialTheme.typography.labelLarge,
                    color = SolarGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrolling / Music Track bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${activePost.musicTrack} • Chuyển cảnh: ${PresetsData.transitionById(activePost.transitionStyle).label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Top Filter Bar ("Xu Hướng Đang Hot" vs "Video Đã Đăng")
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                FilterChip(
                    selected = selectedFeedFilter == 0,
                    onClick = { selectedFeedFilter = 0 },
                    label = { Text("🔥 Xu Hướng Hot") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan,
                        selectedLabelColor = Color(0xFF04201B),
                        labelColor = Color.White
                    ),
                    border = null,
                    modifier = Modifier.testTag("feed_tab_trending")
                )
                FilterChip(
                    selected = selectedFeedFilter == 1,
                    onClick = { selectedFeedFilter = 1 },
                    label = { Text("✨ Của Tôi (${userProjects.count { it.isPublished }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonMagenta,
                        selectedLabelColor = Color.White,
                        labelColor = Color.White
                    ),
                    border = null,
                    modifier = Modifier.testTag("feed_tab_mine")
                )
            }
        }

        // AI Director Analysis Bottom Sheet
        if (showAiBreakdownSheet && activePost != null) {
            ModalBottomSheet(
                onDismissRequest = { showAiBreakdownSheet = false },
                containerColor = StudioSurface,
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SolarGold,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Phân Tích Đạo Diễn AI • ${activePost.viralScore}/100 Điểm",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }

                    Surface(
                        color = StudioSurfaceElevated,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Nhận xét từ Gemini AI Director:",
                                style = MaterialTheme.typography.labelLarge,
                                color = ElectricCyan
                            )
                            Text(
                                text = activePost.aiDirectorNotes,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecBadge("Bối cảnh: ${PresetsData.presetById(activePost.scenePreset).label}")
                        SpecBadge("Bộ lọc AI: ${PresetsData.filterById(activePost.aiFilter).label}")
                        SpecBadge("Chuyển cảnh: ${PresetsData.transitionById(activePost.transitionStyle).label}")
                        SpecBadge("Nhịp độ: ${activePost.bpm} BPM")
                    }

                    Button(
                        onClick = {
                            showAiBreakdownSheet = false
                            onRemixInStudio(activePost)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = Color(0xFF04201B)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sheet_remix_in_studio_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Remix Công Thức AI Này Trong Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FeedActionItem(
    icon: ImageVector,
    tint: Color,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .border(1.dp, tint.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SpecBadge(text: String) {
    Surface(
        color = StudioSurfaceElevated,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private fun formatCompactCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000f)
        count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1_000f)
        else -> count.toString()
    }
}
