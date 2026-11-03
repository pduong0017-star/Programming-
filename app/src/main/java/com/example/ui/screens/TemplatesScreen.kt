package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AiTemplate
import com.example.data.model.PresetsData
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SolarGold
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AiMagicUiState

@Composable
fun TemplatesScreen(
    userTemplates: List<AiTemplate>,
    aiState: AiMagicUiState,
    onStreamScript: (String) -> Unit,
    onApplyTemplate: (AiTemplate) -> Unit,
    onDeleteTemplate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var scriptTopic by rememberSaveable {
        mutableStateOf("Cách quay video biến hình quần áo bắt nhịp nhạc cực mượt tại nhà")
    }

    val allTemplates = userTemplates + PresetsData.builtInTemplates

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Streaming AI Viral Script Generator Header Card
        item {
            Surface(
                color = StudioSurface,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(listOf(ElectricCyan, NeonMagenta)),
                        shape = RoundedCornerShape(22.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricCyan.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Trợ Lý Kịch Bản Viral AI (Live Stream)",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "Viết Hook 3 giây đầu & phân cảnh quay theo thời gian thực",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    OutlinedTextField(
                        value = scriptTopic,
                        onValueChange = { scriptTopic = it },
                        label = { Text("Chủ đề bạn muốn quay hôm nay") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = StudioSurfaceElevated,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("template_script_topic_input")
                    )

                    val isStreaming = aiState is AiMagicUiState.Loading
                    Button(
                        onClick = { onStreamScript(scriptTopic) },
                        enabled = !isStreaming,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = Color(0xFF04201B)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("stream_script_button")
                    ) {
                        if (isStreaming) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF04201B),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI đang viết kịch bản...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tạo Kịch Bản Quay 15s Bằng AI", fontWeight = FontWeight.Bold)
                        }
                    }

                    val streamedText = when (aiState) {
                        is AiMagicUiState.StreamingScript -> aiState.partialText
                        is AiMagicUiState.ScriptGenerated -> aiState.scriptText
                        else -> null
                    }

                    if (!streamedText.isNullOrBlank()) {
                        Surface(
                            color = StudioSurfaceElevated,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🎬 Kịch Bản Đề Xuất Từ Gemini AI:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SolarGold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = streamedText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    if (aiState is AiMagicUiState.Error) {
                        Text(
                            text = aiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // 2. Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 Kho Mẫu Dựng AI 1 Chạm (${allTemplates.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = "Cá nhân: ${userTemplates.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan
                )
            }
        }

        // 3. Template Cards
        items(allTemplates, key = { it.id.ifBlank { it.name } }) { template ->
            val preset = PresetsData.presetById(template.recommendedPreset)
            val filter = PresetsData.filterById(template.recommendedFilter)
            val isUserOwned = !template.id.startsWith("builtin_")

            Surface(
                color = StudioSurface,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        preset.primaryColor.copy(alpha = 0.35f),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(preset.primaryColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = template.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = preset.primaryColor.copy(alpha = 0.16f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "${template.recommendedBpm} BPM",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = preset.primaryColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            if (isUserOwned && template.id.isNotBlank()) {
                                IconButton(
                                    onClick = { onDeleteTemplate(template.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Xóa mẫu",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "🎯 Hook: \"${template.hookLine}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SolarGold
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioSurfaceElevated)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        template.storyboardBeats.forEach { beat ->
                            Text(
                                text = "• $beat",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${preset.label} • ${filter.label}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )

                        Button(
                            onClick = { onApplyTemplate(template) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonMagenta,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("apply_template_${template.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dùng Mẫu Này", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
