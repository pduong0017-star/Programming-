package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SpeakerNotes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SolarGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.TextSecondary

@Composable
fun CapCutHomeScreen(
    userProjects: List<VideoProject>,
    onStartNewProject: () -> Unit,
    onOpenAutoCutAi: () -> Unit,
    onOpenAutoCaptions: () -> Unit,
    onOpenTeleprompter: () -> Unit,
    onOpen4kEnhancer: () -> Unit,
    onOpenAiCutout: () -> Unit,
    onOpenScriptGenerator: () -> Unit,
    onOpenExistingProject: (VideoProject) -> Unit,
    onDeleteProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showInstallPhoneDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Minimalist CapCut Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(ElectricCyan, NeonMagenta))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "VibeCut Pro",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Đồng bộ Cloud AI",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Surface(
                    color = StudioSurface,
                    shape = CircleShape,
                    modifier = Modifier
                        .border(1.dp, ElectricCyan.copy(alpha = 0.4f), CircleShape)
                        .clickable { showInstallPhoneDialog = true }
                        .testTag("install_on_phone_guide_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cài APK",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                    }
                }
            }
        }

        // 2. Streamlined CapCut "Dự Án Mới +" Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00F5D4),
                                Color(0xFF00BBF9),
                                Color(0xFF0096C7)
                            )
                        )
                    )
                    .clickable(onClick = onStartNewProject)
                    .padding(horizontal = 20.dp)
                    .testTag("capcut_new_project_button"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dự án mới",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF03191E),
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mở bàn dựng toàn màn hình • Keyframe ◆ • Curve Speed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF062C33)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.88f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddBox,
                            contentDescription = "Dự án mới",
                            tint = ElectricCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // 3. Single-Row Horizontal AI Quick Tools Strip (Uncluttered & Fast)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactToolPill(
                    icon = Icons.Default.AutoAwesome,
                    tint = NeonMagenta,
                    title = "AutoCut AI",
                    onClick = onOpenAutoCutAi,
                    testTag = "quick_tool_autocut"
                )
                CompactToolPill(
                    icon = Icons.Default.ClosedCaption,
                    tint = ElectricCyan,
                    title = "Phụ đề & Giọng AI",
                    onClick = onOpenAutoCaptions,
                    testTag = "quick_tool_captions"
                )
                CompactToolPill(
                    icon = Icons.Default.SpeakerNotes,
                    tint = SolarGold,
                    title = "Máy nhắc chữ",
                    onClick = onOpenTeleprompter,
                    testTag = "quick_tool_teleprompter"
                )
                CompactToolPill(
                    icon = Icons.Default.Hd,
                    tint = Color(0xFF4CC9F0),
                    title = "Làm nét 4K",
                    onClick = onOpen4kEnhancer,
                    testTag = "quick_tool_4k"
                )
                CompactToolPill(
                    icon = Icons.Default.PersonRemove,
                    tint = CyberViolet,
                    title = "Xóa nền AI",
                    onClick = onOpenAiCutout,
                    testTag = "quick_tool_cutout"
                )
                CompactToolPill(
                    icon = Icons.Default.ContentCut,
                    tint = Color(0xFF10B981),
                    title = "Kịch bản AI",
                    onClick = onOpenScriptGenerator,
                    testTag = "quick_tool_script"
                )
            }
        }

        // 4. Recent Projects Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dự án gần đây (${userProjects.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Chạm để mở Bàn dựng",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        if (userProjects.isEmpty()) {
            items(PresetsData.curatedCommunityFeed.take(3), key = { "demo_${it.id}" }) { sample ->
                CapCutProjectRowItem(
                    project = sample,
                    isSample = true,
                    onClick = { onOpenExistingProject(sample) },
                    onDelete = null
                )
            }
        } else {
            items(userProjects, key = { it.id }) { project ->
                CapCutProjectRowItem(
                    project = project,
                    isSample = false,
                    onClick = { onOpenExistingProject(project) },
                    onDelete = { onDeleteProject(project.id) }
                )
            }
        }
    }

    if (showInstallPhoneDialog) {
        AlertDialog(
            onDismissRequest = { showInstallPhoneDialog = false },
            containerColor = StudioSurface,
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đẩy Lên GitHub & Tải APK Về Máy")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Đã tích hợp sẵn bộ tự động đóng gói APK trên GitHub (.github/workflows/android-apk.yml) trong dự án của bạn!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElectricCyan
                    )
                    Text(
                        text = "Bước 1 — Nhấn nút GitHub ở thanh trên cùng của trang web AI Studio:\nNhìn lên góc trên bên phải của TRANG WEB Google AI Studio (nằm ở thanh công cụ bên ngoài màn hình điện thoại ảo, cạnh nút Share / Download ZIP), nhấn vào biểu tượng GitHub (hình con mèo Octocat) để kết nối tài khoản GitHub của bạn và đẩy code lên.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Bước 2 — Lấy file APK tự động trên GitHub:\nSau khi đẩy lên GitHub, mở trang Repository của bạn trên GitHub -> vào mục \"Actions\" -> bấm vào tiến trình \"Build VibeCut Android APK\" -> kéo xuống mục \"Artifacts\" và tải file \"VibeCut-Pro-Installable-APK\" về điện thoại để cài đặt ngay mà không cần máy tính!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInstallPhoneDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = Color(0xFF03201B)
                    )
                ) {
                    Text("Đã hiểu", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun CompactToolPill(
    icon: ImageVector,
    tint: Color,
    title: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = StudioSurface,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .border(1.dp, StudioSurfaceElevated, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(17.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CapCutProjectRowItem(
    project: VideoProject,
    isSample: Boolean,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val preset = PresetsData.presetById(project.scenePreset)
    val filter = PresetsData.filterById(project.aiFilter)

    Surface(
        color = StudioSurface,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StudioSurfaceElevated, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 58.dp, height = 72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(preset.primaryColor, Color(0xFF101322), preset.secondaryColor)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(3.dp)
                ) {
                    Text(
                        text = "00:${project.durationSeconds}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${preset.label} • ${filter.label} • ${project.bpm} BPM",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = ElectricCyan.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isSample) "Dự án mẫu" else "1080P • 60FPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = SolarGold.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Viral ${project.viralScore}đ",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Xóa dự án",
                        tint = TextSecondary
                    )
                }
            }
        }
    }
}
