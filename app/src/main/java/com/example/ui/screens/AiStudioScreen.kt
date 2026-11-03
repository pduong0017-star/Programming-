package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.CaptionCue
import com.example.data.model.PresetsData
import com.example.ui.components.VideoCanvasPlayer
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SolarGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AiMagicUiState
import com.example.ui.viewmodel.StudioEditorState
import com.example.ui.viewmodel.TimelineClip
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun AiStudioScreen(
    editorState: StudioEditorState,
    aiState: AiMagicUiState,
    initialBottomTab: Int? = null,
    onCloseStudio: () -> Unit = {},
    onUpdateEditor: ((StudioEditorState) -> StudioEditorState) -> Unit,
    onSelectClip: (Int) -> Unit = {},
    onSplitClip: () -> Unit,
    onTrimClip: (Int) -> Unit = {},
    onDuplicateClip: () -> Unit = {},
    onDeleteClip: () -> Unit = {},
    onUpdateSelectedClip: ((TimelineClip) -> TimelineClip) -> Unit = {},
    onToggleKeyframe: (Float) -> Unit = {},
    onToggleSticker: (String) -> Unit = {},
    onEnhance4k: () -> Unit,
    onUpdateCaptionCue: (Int, CaptionCue) -> Unit,
    onAddCaptionCue: () -> Unit,
    onGenerateAiEdit: (String) -> Unit,
    onPublishProject: () -> Unit,
    onSaveAsTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var aiPromptInput by rememberSaveable {
        mutableStateOf("Biến hình đường phố Cyberpunk ban đêm theo nhạc Phonk cực cháy")
    }
    // Contextual Slide-Up Drawer State: null = collapsed (clean full viewport), 0..5 = open tool sheet
    var activeDrawerTab by rememberSaveable(initialBottomTab) { mutableStateOf(initialBottomTab) }
    var scrubProgress by remember { mutableFloatStateOf(0.25f) }
    var showResolutionPicker by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableIntStateOf(0) }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onUpdateEditor { it.copy(mediaUri = uri.toString()) }
        }
    }

    LaunchedEffect(showExportDialog) {
        if (showExportDialog) {
            exportProgress = 0
            while (exportProgress < 100) {
                delay(45L)
                exportProgress = (exportProgress + 5).coerceAtMost(100)
            }
            onPublishProject()
        }
    }

    // Fixed Single-Screen Non-Scrolling CapCut NLE Layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. Sleek Top Studio Bar: [←] | [9:16] | [1080P•60FPS] | [Lưu Mẫu] | [Xuất]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCloseStudio,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(StudioSurface)
                        .testTag("studio_close_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Đóng bàn dựng",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Aspect Ratio Pill
                Surface(
                    color = StudioSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable {
                        val nextRatio = when (editorState.aspectRatio) {
                            "9:16" -> "16:9"
                            "16:9" -> "1:1"
                            else -> "9:16"
                        }
                        onUpdateEditor { it.copy(aspectRatio = nextRatio) }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = null,
                            tint = SolarGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = editorState.aspectRatio,
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarGold
                        )
                    }
                }

                // Resolution & FPS Badge
                Surface(
                    color = StudioSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { showResolutionPicker = !showResolutionPicker }
                        .testTag("resolution_picker_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hd,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${editorState.resolution}•${editorState.fps}FPS ▾",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSaveAsTemplate,
                    shape = RoundedCornerShape(9.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("save_template_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Lưu mẫu",
                        tint = ElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Lưu Mẫu", color = ElectricCyan, style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = { showExportDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = Color(0xFF03201B)
                    ),
                    shape = RoundedCornerShape(9.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("publish_video_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Xuất", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Compact Resolution Dropdown Bar
        AnimatedVisibility(visible = showResolutionPicker) {
            Surface(
                color = StudioSurface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("720P", "1080P", "2K", "4K AI").forEach { res ->
                        FilterChip(
                            selected = editorState.resolution == res,
                            onClick = { onUpdateEditor { it.copy(resolution = res) } },
                            label = { Text(res, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = Color.Black,
                                labelColor = Color.White
                            )
                        )
                    }
                    listOf(24, 30, 60).forEach { fpsVal ->
                        FilterChip(
                            selected = editorState.fps == fpsVal,
                            onClick = { onUpdateEditor { it.copy(fps = fpsVal) } },
                            label = { Text("${fpsVal}FPS", style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonMagenta,
                                selectedLabelColor = Color.White,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 2. Central Interactive Preview Monitor (Flexibly expands to fill available height)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val widthFraction = when (editorState.aspectRatio) {
                "9:16" -> 0.68f
                "1:1" -> 0.82f
                else -> 1.0f
            }
            VideoCanvasPlayer(
                scenePreset = editorState.scenePreset,
                aiFilter = editorState.aiFilter,
                transitionStyle = editorState.transitionStyle,
                bpm = editorState.bpm,
                durationSeconds = editorState.durationSeconds,
                captions = editorState.captions,
                mediaUri = editorState.mediaUri,
                isPlaying = editorState.isPlaying,
                onTogglePlay = { onUpdateEditor { s -> s.copy(isPlaying = !s.isPlaying) } },
                onProgressChanged = { p -> scrubProgress = p },
                externalProgress = scrubProgress,
                playbackSpeed = editorState.playbackSpeed,
                brightness = editorState.brightness,
                contrast = editorState.contrast,
                saturation = editorState.saturation,
                aiCutoutEnabled = editorState.aiCutoutEnabled,
                animationStyle = editorState.animationStyle,
                clips = editorState.clips,
                pipOverlayPreset = editorState.pipOverlayPreset,
                activeStickers = editorState.activeStickers,
                aiVoiceEffect = editorState.aiVoiceEffect,
                teleprompterActive = editorState.teleprompterActive,
                teleprompterText = editorState.teleprompterText,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(widthFraction)
                    .testTag("studio_video_preview")
            )
        }

        // 3. CapCut Transport & Keyframe Quick Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(StudioSurface)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = { onUpdateEditor { it.copy(isPlaying = !it.isPlaying) } },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (editorState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = String.format(
                    Locale.US,
                    "%02d.%ds / %02ds",
                    (scrubProgress * editorState.durationSeconds).toInt(),
                    ((scrubProgress * editorState.durationSeconds * 10).toInt() % 10),
                    editorState.durationSeconds
                ),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )

            Slider(
                value = scrubProgress,
                onValueChange = { newProgress ->
                    scrubProgress = newProgress
                    if (editorState.isPlaying) {
                        onUpdateEditor { it.copy(isPlaying = false) }
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = ElectricCyan,
                    inactiveTrackColor = StudioSurfaceElevated
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
            )

            // Keyframe (◆+) Quick Button
            Surface(
                color = Color(0xFFFF3366).copy(alpha = 0.22f),
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier
                    .border(1.dp, Color(0xFFFF3366), RoundedCornerShape(7.dp))
                    .clickable { onToggleKeyframe(scrubProgress) }
                    .testTag("capcut_keyframe_button")
            ) {
                Text(
                    text = "◆+",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFF3366),
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }

            // Quick Split Button
            Surface(
                color = StudioSurfaceElevated,
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier
                    .clickable(onClick = onSplitClip)
                    .testTag("capcut_split_clip_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Tách clip",
                        tint = ElectricCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Tách", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }

        // 4. CapCut Multi-Track Interactive Timeline (Fixed compact height, zero page scroll)
        CapCutMultiTrackTimeline(
            editorState = editorState,
            scrubProgress = scrubProgress,
            onSelectClip = { idx ->
                onSelectClip(idx)
                activeDrawerTab = 0
            },
            onPickMedia = {
                mediaPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            },
            onOpenDrawer = { tab -> activeDrawerTab = tab }
        )

        // 5. Contextual Slide-Up Tool Drawer (Appears smoothly only when a tool tab is active)
        AnimatedVisibility(
            visible = activeDrawerTab != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            val currentTab = activeDrawerTab ?: 0
            Surface(
                color = Color(0xFF161925),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 215.dp)
                    .border(
                        1.dp,
                        ElectricCyan.copy(alpha = 0.45f),
                        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Drawer Header with Title & Close [✕]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentTab) {
                                0 -> "✂️ Chỉnh Sửa Clip Đang Chọn: ${editorState.selectedClip.label}"
                                1 -> "◆ Khung Hình Chính (Keyframe) & Đồ Thị Tốc Độ (Curve)"
                                2 -> "🖼️ Lớp Phủ PIP, Nhãn Dán Động & Tư Liệu"
                                3 -> "🎙️ Phụ Đề Karaoke, Giọng Nói AI & Máy Nhắc Chữ"
                                4 -> "🎨 Hiệu Ứng, Bộ Lọc Màu & Nhạc Bắt Beat"
                                else -> "✨ AutoCut AI Director — Dựng Tự Động 1 Chạm"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = StudioSurfaceElevated,
                            shape = CircleShape,
                            modifier = Modifier
                                .clickable { activeDrawerTab = null }
                                .testTag("close_drawer_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Thu gọn", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Thu gọn ngăn công cụ",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Scrollable inner drawer body
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (currentTab) {
                            0 -> PerClipInspectorDrawer(
                                editorState = editorState,
                                onSplitClip = onSplitClip,
                                onTrimClip = onTrimClip,
                                onDuplicateClip = onDuplicateClip,
                                onDeleteClip = onDeleteClip,
                                onUpdateSelectedClip = onUpdateSelectedClip,
                                onUpdateEditor = onUpdateEditor
                            )

                            1 -> KeyframeAndCurveSpeedDrawer(
                                selectedClip = editorState.selectedClip,
                                scrubProgress = scrubProgress,
                                onToggleKeyframe = onToggleKeyframe,
                                onUpdateSelectedClip = onUpdateSelectedClip
                            )

                            2 -> PipAndStickerOverlayDrawer(
                                editorState = editorState,
                                onToggleSticker = onToggleSticker,
                                onPickMedia = {
                                    mediaPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                },
                                onUpdateEditor = onUpdateEditor
                            )

                            3 -> CaptionsAndAiVoiceDrawer(
                                editorState = editorState,
                                onUpdateEditor = onUpdateEditor,
                                onUpdateCue = onUpdateCaptionCue,
                                onAddCue = onAddCaptionCue
                            )

                            4 -> EffectsColorAndAudioDrawer(
                                editorState = editorState,
                                onEnhance4k = onEnhance4k,
                                onUpdateEditor = onUpdateEditor
                            )

                            5 -> AiMagicCopilotDrawer(
                                promptInput = aiPromptInput,
                                onPromptChange = { aiPromptInput = it },
                                aiState = aiState,
                                onGenerateAiEdit = { onGenerateAiEdit(aiPromptInput) }
                            )
                        }
                    }
                }
            }
        }

        // 6. CapCut 6-Icon Bottom Dock (Tap to open/toggle drawer)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(StudioSurface)
                .padding(vertical = 6.dp, horizontal = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val toggleTab: (Int) -> Unit = { idx ->
                activeDrawerTab = if (activeDrawerTab == idx) null else idx
            }
            CapCutDockItem(
                selected = activeDrawerTab == 0,
                icon = Icons.Default.ContentCut,
                label = "Từng Clip",
                tag = "studio_tab_edit",
                onClick = { toggleTab(0) }
            )
            CapCutDockItem(
                selected = activeDrawerTab == 1,
                icon = Icons.Default.Speed,
                label = "Tốc độ & ◆",
                tag = "studio_tab_speed",
                onClick = { toggleTab(1) }
            )
            CapCutDockItem(
                selected = activeDrawerTab == 2,
                icon = Icons.Default.Layers,
                label = "Lớp phủ/Nhãn",
                tag = "studio_tab_pip",
                onClick = { toggleTab(2) }
            )
            CapCutDockItem(
                selected = activeDrawerTab == 3,
                icon = Icons.Default.Subtitles,
                label = "Văn bản/Giọng",
                tag = "studio_tab_captions",
                onClick = { toggleTab(3) }
            )
            CapCutDockItem(
                selected = activeDrawerTab == 4,
                icon = Icons.Default.MovieFilter,
                label = "Màu & Nhạc",
                tag = "studio_tab_filters",
                onClick = { toggleTab(4) }
            )
            CapCutDockItem(
                selected = activeDrawerTab == 5,
                icon = Icons.Default.AutoAwesome,
                label = "AutoCut AI",
                tag = "studio_tab_ai",
                onClick = { toggleTab(5) }
            )
        }
    }

    // CapCut Export Progress Modal
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { if (exportProgress >= 100) showExportDialog = false },
            containerColor = StudioSurface,
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (exportProgress >= 100) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (exportProgress < 100) "Đang xuất video ${editorState.resolution} ${editorState.fps}FPS..."
                        else "Đã xuất video thành công!"
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LinearProgressIndicator(
                        progress = { exportProgress / 100f },
                        color = ElectricCyan,
                        trackColor = StudioSurfaceElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = if (exportProgress < 100)
                            "Đang kết xuất ${editorState.clips.size} Clip, Keyframe ◆, Curve Speed & đồng bộ đám mây ($exportProgress%)..."
                        else
                            "Dự án \"${editorState.title}\" đã được lưu vào Đám mây & đăng lên mục Xu Hướng VibeCut!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                if (exportProgress >= 100) {
                    Button(
                        onClick = { showExportDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Hoàn tất", fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@Composable
private fun CapCutMultiTrackTimeline(
    editorState: StudioEditorState,
    scrubProgress: Float,
    onSelectClip: (Int) -> Unit,
    onPickMedia: () -> Unit,
    onOpenDrawer: (Int) -> Unit
) {
    Surface(
        color = Color(0xFF0E101A),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StudioSurfaceElevated, RoundedCornerShape(14.dp))
    ) {
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Time Ruler
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val total = editorState.durationSeconds.coerceAtLeast(5)
                    listOf(0, total / 4, total / 2, (total * 3) / 4, total).forEach { sec ->
                        Text(
                            text = String.format(Locale.US, "00:%02d", sec),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Track 1: Interactive Video Clips with Keyframe Diamonds (◆) + Transition Boxes + [+]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    editorState.clips.forEachIndexed { idx, clip ->
                        val isSelected = idx == editorState.selectedClipIndex
                        Box(
                            modifier = Modifier
                                .weight(clip.durationSec.toFloat().coerceAtLeast(1f))
                                .height(34.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        if (isSelected) {
                                            listOf(
                                                ElectricCyan.copy(alpha = 0.42f),
                                                NeonMagenta.copy(alpha = 0.30f)
                                            )
                                        } else {
                                            listOf(
                                                StudioSurfaceElevated,
                                                Color(0xFF1A1E2E)
                                            )
                                        }
                                    )
                                )
                                .border(
                                    width = if (isSelected) 1.8.dp else 1.dp,
                                    color = if (isSelected) Color.White else ElectricCyan.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(7.dp)
                                )
                                .clickable { onSelectClip(idx) }
                                .padding(horizontal = 5.dp)
                                .testTag("timeline_clip_$idx")
                        ) {
                            Text(
                                text = buildString {
                                    append("${idx + 1}.${clip.label} (${clip.durationSec}s)")
                                    if (clip.curvePreset != "none") append(" • 📈")
                                    else if (clip.speed != 1.0f) append(" • ${clip.speed}x")
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )

                            // Draw Red Keyframe Diamonds (◆) along the bottom of the clip
                            if (clip.keyframes.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(bottom = 1.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    clip.keyframes.forEach { _ ->
                                        Text(
                                            text = "◆",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFF3366),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        if (idx < editorState.clips.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                                    .clickable { onOpenDrawer(4) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "|",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    // CapCut White [+] Add Media Button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color.White)
                            .clickable(onClick = onPickMedia)
                            .testTag("pick_media_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm ảnh hoặc video",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Track 2: PIP Overlay & Animated Stickers Track (Violet/Cyan)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(21.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(CyberViolet.copy(alpha = 0.24f))
                        .border(1.dp, CyberViolet.copy(alpha = 0.65f), RoundedCornerShape(5.dp))
                        .clickable { onOpenDrawer(2) }
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🖼️ Lớp phủ PIP: ${if (editorState.pipOverlayPreset == "none") "Tắt" else editorState.pipOverlayPreset} • Nhãn dán: ${editorState.activeStickers.joinToString(" ").ifBlank { "Trống" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE0AAFF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Track 3: Text / Captions & AI Voice Track (CapCut Gold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(21.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFFF9F1C).copy(alpha = 0.20f))
                        .border(1.dp, Color(0xFFFF9F1C).copy(alpha = 0.65f), RoundedCornerShape(5.dp))
                        .clickable { onOpenDrawer(3) }
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "T Phụ đề (${editorState.captions.size}) • Giọng AI: ${editorState.aiVoiceEffect} • " +
                            editorState.captions.joinToString(" | ") { it.text },
                        style = MaterialTheme.typography.labelSmall,
                        color = SolarGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Track 4: Audio Track with Yellow Beat Dots (CapCut Emerald)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.22f))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.7f), RoundedCornerShape(5.dp))
                        .clickable { onOpenDrawer(4) }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val dotCount = 12
                        for (i in 1 until dotCount) {
                            val x = size.width * (i.toFloat() / dotCount)
                            drawCircle(
                                color = SolarGold,
                                radius = 3.5f,
                                center = Offset(x, size.height - 4.5f)
                            )
                        }
                    }
                    Text(
                        text = "🎵 ${editorState.musicTrack} • ${editorState.bpm} BPM (Hạt nhịp Beat AI)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 6.dp)
                    )
                }
            }

            // White Playhead Line Overlay
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val playheadX = (size.width * scrubProgress).coerceIn(6f, size.width - 6f)
                drawLine(
                    color = Color.White,
                    start = Offset(playheadX, 0f),
                    end = Offset(playheadX, size.height),
                    strokeWidth = 3f
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(playheadX, 5f)
                )
            }
        }
    }
}

@Composable
private fun CapCutDockItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) ElectricCyan.copy(alpha = 0.18f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) ElectricCyan else Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) ElectricCyan else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun PerClipInspectorDrawer(
    editorState: StudioEditorState,
    onSplitClip: () -> Unit,
    onTrimClip: (Int) -> Unit,
    onDuplicateClip: () -> Unit,
    onDeleteClip: () -> Unit,
    onUpdateSelectedClip: ((TimelineClip) -> TimelineClip) -> Unit,
    onUpdateEditor: ((StudioEditorState) -> StudioEditorState) -> Unit
) {
    val clip = editorState.selectedClip

    // Row 1: Per-clip operations (Tách, Cắt -1s, Kéo dài +1s, Nhân đôi, Đảo ngược, Xóa, Xóa nền AI)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ActionChipButton("✂️ Tách", ElectricCyan, onClick = onSplitClip)
        ActionChipButton("⏪ Cắt -1s", SolarGold, onClick = { onTrimClip(-1) })
        ActionChipButton("⏩ Thêm +1s", SolarGold, onClick = { onTrimClip(1) })
        ActionChipButton(
            label = "⧉ Nhân đôi",
            tint = Color.White,
            onClick = onDuplicateClip,
            testTag = "capcut_duplicate_clip_button"
        )
        ActionChipButton(
            label = if (clip.isReversed) "⇄ Đã đảo ngược" else "⇄ Đảo chiều",
            tint = if (clip.isReversed) NeonMagenta else Color.White,
            onClick = { onUpdateSelectedClip { it.copy(isReversed = !it.isReversed) } }
        )
        ActionChipButton(
            label = if (editorState.aiCutoutEnabled) "🧍 Đã xóa nền" else "🧍 Xóa nền AI",
            tint = if (editorState.aiCutoutEnabled) ElectricCyan else Color.White,
            onClick = { onUpdateEditor { it.copy(aiCutoutEnabled = !it.aiCutoutEnabled) } },
            testTag = "capcut_cutout_button"
        )
        ActionChipButton(
            label = "🗑️ Xóa clip",
            tint = NeonMagenta,
            onClick = onDeleteClip,
            testTag = "capcut_delete_clip_button"
        )
    }

    // Row 2: Clip Volume Slider (0% - 200%)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "🔊 Âm lượng Clip: ${(clip.volume * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.width(125.dp)
        )
        Slider(
            value = clip.volume,
            onValueChange = { v -> onUpdateSelectedClip { it.copy(volume = v) } },
            valueRange = 0f..2f,
            colors = SliderDefaults.colors(
                thumbColor = ElectricCyan,
                activeTrackColor = ElectricCyan,
                inactiveTrackColor = StudioSurfaceElevated
            ),
            modifier = Modifier.weight(1f)
        )
    }

    // Row 3: Clip Animation (Hoạt ảnh Vào / Ra / Kết hợp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Hoạt ảnh:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        listOf(
            "none" to "Không",
            "in_zoom" to "Vào: Phóng cận",
            "out_spin" to "Ra: Xoay 360°",
            "combo_pendulum" to "Kết hợp: Con lắc 3D"
        ).forEach { (id, label) ->
            FilterChip(
                selected = editorState.animationStyle == id,
                onClick = { onUpdateEditor { it.copy(animationStyle = id) } },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonMagenta,
                    selectedLabelColor = Color.White,
                    labelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun KeyframeAndCurveSpeedDrawer(
    selectedClip: TimelineClip,
    scrubProgress: Float,
    onToggleKeyframe: (Float) -> Unit,
    onUpdateSelectedClip: ((TimelineClip) -> TimelineClip) -> Unit
) {
    // Keyframe Header Bar
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "◆ Mốc Keyframe trên Clip (${selectedClip.keyframes.size} mốc)",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Button(
            onClick = { onToggleKeyframe(scrubProgress) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF3366),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(30.dp)
        ) {
            Text("◆ Đánh dấu / Gỡ tại Playhead", style = MaterialTheme.typography.labelSmall)
        }
    }

    // Normal Speed Chips
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Tốc độ đều:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f).forEach { spd ->
            FilterChip(
                selected = selectedClip.curvePreset == "none" && selectedClip.speed == spd,
                onClick = { onUpdateSelectedClip { it.copy(speed = spd, curvePreset = "none") } },
                label = { Text("${spd}x", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricCyan,
                    selectedLabelColor = Color.Black,
                    labelColor = Color.White
                )
            )
        }
    }

    // CapCut Curve Speed Presets + Live Bezier Graph
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Đồ thị (Curve):", style = MaterialTheme.typography.labelSmall, color = SolarGold)
        listOf(
            "none" to "Tắt Curve",
            "montage" to "📈 Montage",
            "hero" to "🦸 Anh hùng",
            "bullet" to "🔫 Viên đạn",
            "jump_cut" to "⚡ Nhảy vọt",
            "flash" to "✨ Chớp nhoáng"
        ).forEach { (id, label) ->
            FilterChip(
                selected = selectedClip.curvePreset == id,
                onClick = { onUpdateSelectedClip { it.copy(curvePreset = id) } },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SolarGold,
                    selectedLabelColor = Color.Black,
                    labelColor = Color.White
                ),
                modifier = Modifier.testTag("curve_preset_$id")
            )
        }
    }

    // Interactive Bezier Curve Speed Visualization
    if (selectedClip.curvePreset != "none") {
        val curvePath = remember { Path() }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0B0D15))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            val w = size.width
            val h = size.height
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(0f, h * 0.5f),
                end = Offset(w, h * 0.5f),
                strokeWidth = 1.5f
            )
            curvePath.reset()
            curvePath.moveTo(0f, h * 0.65f)
            when (selectedClip.curvePreset) {
                "montage" -> curvePath.cubicTo(w * 0.25f, h * 0.8f, w * 0.45f, h * 0.05f, w, h * 0.7f)
                "hero" -> curvePath.cubicTo(w * 0.2f, h * 0.1f, w * 0.5f, h * 0.95f, w, h * 0.2f)
                "bullet" -> curvePath.cubicTo(w * 0.15f, h * 0.05f, w * 0.4f, h * 0.85f, w, h * 0.85f)
                "jump_cut" -> curvePath.cubicTo(w * 0.3f, h * 0.05f, w * 0.6f, h * 0.9f, w, h * 0.15f)
                else -> curvePath.cubicTo(w * 0.1f, h * 0.05f, w * 0.9f, h * 0.05f, w, h * 0.75f)
            }
            drawPath(
                path = curvePath,
                color = SolarGold,
                style = Stroke(width = 3f)
            )
        }
    }
}

@Composable
private fun PipAndStickerOverlayDrawer(
    editorState: StudioEditorState,
    onToggleSticker: (String) -> Unit,
    onPickMedia: () -> Unit,
    onUpdateEditor: ((StudioEditorState) -> StudioEditorState) -> Unit
) {
    // 1. PIP Overlay Presets
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Lớp phủ PIP:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        listOf(
            "none" to "Tắt PIP",
            "reaction_cam" to "🎙️ Cam Phản Ứng",
            "cyber_hud" to "⚡ Khung Cyber HUD",
            "timer_corner" to "⏱️ Đếm Ngược Góc"
        ).forEach { (id, label) ->
            FilterChip(
                selected = editorState.pipOverlayPreset == id,
                onClick = { onUpdateEditor { it.copy(pipOverlayPreset = id) } },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricCyan,
                    selectedLabelColor = Color.Black,
                    labelColor = Color.White
                ),
                modifier = Modifier.testTag("pip_preset_$id")
            )
        }
    }

    // 2. Beat-Reactive Animated Stickers
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Nhãn dán (Sticker):", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        listOf(
            "🔥" to "🔥 Lửa Cháy",
            "⚡" to "⚡ Tia Chớp",
            "💯" to "💯 Điểm 10",
            "🎵" to "🎵 Nốt Nhạc",
            "🚀" to "🚀 Xu Hướng"
        ).forEach { (emoji, label) ->
            FilterChip(
                selected = editorState.activeStickers.contains(emoji),
                onClick = { onToggleSticker(emoji) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonMagenta,
                    selectedLabelColor = Color.White,
                    labelColor = Color.White
                )
            )
        }
    }

    // 3. Import custom photo/video button
    OutlinedButton(
        onClick = onPickMedia,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = SolarGold, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Chèn Ảnh / Video Từ Thư Viện Máy", color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun CaptionsAndAiVoiceDrawer(
    editorState: StudioEditorState,
    onUpdateEditor: ((StudioEditorState) -> StudioEditorState) -> Unit,
    onUpdateCue: (Int, CaptionCue) -> Unit,
    onAddCue: () -> Unit
) {
    // 1. AI Voice Changer Selector
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(16.dp))
        Text("Giọng AI:", style = MaterialTheme.typography.labelSmall, color = Color.White)
        listOf(
            "none" to "Mặc định",
            "anime_girl" to "🎀 Nữ Anime",
            "deep_cinema" to "🎬 Trầm Điện Ảnh",
            "cyber_robot" to "🤖 Robot Cyber",
            "fast_reviewer" to "⚡ Review Nhanh"
        ).forEach { (id, label) ->
            FilterChip(
                selected = editorState.aiVoiceEffect == id,
                onClick = { onUpdateEditor { it.copy(aiVoiceEffect = id) } },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonMagenta,
                    selectedLabelColor = Color.White,
                    labelColor = Color.White
                ),
                modifier = Modifier.testTag("voice_effect_$id")
            )
        }
    }

    // 2. Teleprompter & Add Caption Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📜 Máy nhắc chữ:", style = MaterialTheme.typography.labelSmall, color = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Switch(
                checked = editorState.teleprompterActive,
                onCheckedChange = { active -> onUpdateEditor { it.copy(teleprompterActive = active) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = ElectricCyan)
            )
        }
        OutlinedButton(
            onClick = onAddCue,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(30.dp)
        ) {
            Text("+ Thêm câu phụ đề", color = ElectricCyan, style = MaterialTheme.typography.labelSmall)
        }
    }

    // 3. Compact First Caption Quick Editor
    editorState.captions.take(2).forEachIndexed { idx, cue ->
        OutlinedTextField(
            value = cue.text,
            onValueChange = { newText -> onUpdateCue(idx, cue.copy(text = newText)) },
            label = { Text("Phụ đề ${cue.startSec}s-${cue.endSec}s (Highlight: ${cue.highlightWord})") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = StudioSurfaceElevated,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EffectsColorAndAudioDrawer(
    editorState: StudioEditorState,
    onEnhance4k: () -> Unit,
    onUpdateEditor: ((StudioEditorState) -> StudioEditorState) -> Unit
) {
    // 1. Scene & AI Filter Chips
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionChipButton("✨ Làm nét 4K AI", ElectricCyan, onClick = onEnhance4k)
        PresetsData.scenePresets.forEach { preset ->
            FilterChip(
                selected = editorState.scenePreset == preset.id,
                onClick = { onUpdateEditor { it.copy(scenePreset = preset.id) } },
                label = { Text(preset.label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = preset.primaryColor,
                    selectedLabelColor = Color.Black,
                    labelColor = Color.White
                )
            )
        }
    }

    // 2. Music Track & BPM Selector
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🎵 Nhạc:", style = MaterialTheme.typography.labelSmall, color = SolarGold)
        PresetsData.musicTracks.forEach { track ->
            FilterChip(
                selected = editorState.musicTrack == track.title,
                onClick = {
                    onUpdateEditor {
                        it.copy(musicTrack = track.title, bpm = track.defaultBpm)
                    }
                },
                label = { Text("${track.title} (${track.defaultBpm} BPM)", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SolarGold,
                    selectedLabelColor = Color.Black,
                    labelColor = Color.White
                )
            )
        }
    }

    // 3. Brightness & Saturation Sliders
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Độ sáng:", style = MaterialTheme.typography.labelSmall, color = Color.White)
        Slider(
            value = editorState.brightness,
            onValueChange = { v -> onUpdateEditor { it.copy(brightness = v) } },
            valueRange = -0.4f..0.4f,
            colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan),
            modifier = Modifier.weight(1f)
        )
        Text("Bão hòa:", style = MaterialTheme.typography.labelSmall, color = Color.White)
        Slider(
            value = editorState.saturation,
            onValueChange = { v -> onUpdateEditor { it.copy(saturation = v) } },
            valueRange = 0f..2f,
            colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonMagenta),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AiMagicCopilotDrawer(
    promptInput: String,
    onPromptChange: (String) -> Unit,
    aiState: AiMagicUiState,
    onGenerateAiEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PresetsData.quickAiPrompts.forEach { suggestion ->
            FilterChip(
                selected = promptInput == suggestion,
                onClick = { onPromptChange(suggestion) },
                label = {
                    Text(
                        text = suggestion,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricCyan.copy(alpha = 0.25f),
                    selectedLabelColor = ElectricCyan,
                    labelColor = TextSecondary
                )
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = promptInput,
            onValueChange = onPromptChange,
            label = { Text("Ý tưởng video để AutoCut AI tự dựng") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = StudioSurfaceElevated,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .weight(1f)
                .testTag("ai_prompt_input")
        )

        val isGenerating = aiState is AiMagicUiState.Loading
        Button(
            onClick = onGenerateAiEdit,
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricCyan,
                contentColor = Color(0xFF04201B)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(48.dp)
                .testTag("generate_ai_edit_button")
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color(0xFF04201B),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dựng AI", fontWeight = FontWeight.ExtraBold)
            }
        }
    }

    if (aiState is AiMagicUiState.Error) {
        Text(
            text = aiState.message,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ActionChipButton(
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Surface(
        color = StudioSurfaceElevated,
        shape = RoundedCornerShape(9.dp),
        modifier = Modifier
            .border(1.dp, tint.copy(alpha = 0.45f), RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
