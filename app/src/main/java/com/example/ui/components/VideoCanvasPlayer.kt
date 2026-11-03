package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CaptionCue
import com.example.data.model.PresetsData
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SolarGold
import com.example.ui.viewmodel.TimelineClip
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VideoCanvasPlayer(
    scenePreset: String,
    aiFilter: String,
    transitionStyle: String,
    bpm: Int,
    durationSeconds: Int,
    captions: List<CaptionCue>,
    mediaUri: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onProgressChanged: ((Float) -> Unit)? = null,
    externalProgress: Float? = null,
    showControlsOverlay: Boolean = true,
    playbackSpeed: Float = 1.0f,
    brightness: Float = 0f,
    contrast: Float = 1.0f,
    saturation: Float = 1.0f,
    aiCutoutEnabled: Boolean = false,
    animationStyle: String = "none",
    clips: List<TimelineClip> = emptyList(),
    pipOverlayPreset: String = "none",
    activeStickers: List<String> = emptyList(),
    aiVoiceEffect: String = "none",
    teleprompterActive: Boolean = false,
    teleprompterText: String = "",
    modifier: Modifier = Modifier
) {
    val preset = remember(scenePreset) { PresetsData.presetById(scenePreset) }
    val filter = remember(aiFilter) { PresetsData.filterById(aiFilter) }

    var internalTimeSec by remember { mutableFloatStateOf(0f) }
    val totalSec = durationSeconds.coerceAtLeast(5).toFloat()

    val currentTimeSec = if (externalProgress != null && !isPlaying) {
        externalProgress * totalSec
    } else {
        internalTimeSec
    }

    // Determine active clip and normalized progress within that clip
    val activeClipInfo = remember(clips, currentTimeSec, totalSec) {
        if (clips.isEmpty()) {
            Triple(null as TimelineClip?, 0f, 1.0f)
        } else {
            var acc = 0f
            var matched: TimelineClip = clips.last()
            var localProgress = 0.5f
            for (c in clips) {
                val dur = c.durationSec.coerceAtLeast(1).toFloat()
                if (currentTimeSec >= acc && currentTimeSec <= acc + dur) {
                    matched = c
                    localProgress = ((currentTimeSec - acc) / dur).coerceIn(0f, 1f)
                    break
                }
                acc += dur
            }
            val curveFactor = computeCurveMultiplier(matched.curvePreset, localProgress)
            val effectiveSpeed = (matched.speed * curveFactor).coerceIn(0.25f, 4.0f)
            Triple(matched, localProgress, effectiveSpeed)
        }
    }

    val activeClip = activeClipInfo.first
    val clipLocalProgress = activeClipInfo.second
    val dynamicSpeed = if (activeClip != null) activeClipInfo.third else playbackSpeed.coerceIn(0.25f, 4.0f)

    val currentDynamicSpeed by androidx.compose.runtime.rememberUpdatedState(dynamicSpeed)
    val currentOnProgressChanged by androidx.compose.runtime.rememberUpdatedState(onProgressChanged)

    LaunchedEffect(isPlaying, totalSec) {
        if (!isPlaying) return@LaunchedEffect
        withFrameNanos { }
        delay(350L)
        while (isPlaying) {
            delay(200L)
            internalTimeSec = (internalTimeSec + 0.2f * currentDynamicSpeed) % totalSec
            currentOnProgressChanged?.invoke(internalTimeSec / totalSec)
        }
    }

    LaunchedEffect(externalProgress) {
        if (externalProgress != null && !isPlaying) {
            internalTimeSec = (externalProgress * totalSec).coerceIn(0f, totalSec)
        }
    }

    val currentSecondInt by remember(externalProgress, isPlaying) {
        derivedStateOf { currentTimeSec.toInt() }
    }

    val activeCaption by remember(captions) {
        derivedStateOf {
            val secInt = currentSecondInt
            captions.find { secInt >= it.startSec && secInt < it.endSec }
                ?: captions.firstOrNull { secInt == it.endSec }
        }
    }

    val beatsPerSec = bpm.coerceIn(60, 220) / 60f
    val beatFraction = (currentTimeSec * beatsPerSec) % 1f
    val isBeatDrop = isPlaying && beatFraction < 0.22f

    // Keyframe interpolation zoom/pan effect
    val keyframeZoom = remember(activeClip, clipLocalProgress) {
        val kfList = activeClip?.keyframes.orEmpty()
        if (kfList.isEmpty()) {
            1f
        } else {
            val nearestDist = kfList.minOf { abs(it - clipLocalProgress) }
            if (nearestDist < 0.18f) {
                1f + (0.18f - nearestDist) * 0.65f
            } else {
                1f
            }
        }
    }

    val beatScale = (if (isBeatDrop) {
        when (transitionStyle) {
            "zoom_burst" -> 1.06f
            "glitch_cut" -> 1.025f
            else -> 1.01f
        }
    } else 1f) * keyframeZoom

    val glitchOffsetX = if (isBeatDrop && transitionStyle == "glitch_cut") {
        sin(currentTimeSec * 30f) * 8f
    } else if (isBeatDrop && transitionStyle == "whip_pan") {
        cos(currentTimeSec * 20f) * 14f
    } else {
        0f
    }

    val animRotationZ = when (animationStyle) {
        "combo_pendulum" -> sin(currentTimeSec * 2.5f) * 2.2f
        "out_spin" -> if (currentTimeSec > totalSec - 2f) (currentTimeSec - (totalSec - 2f)) * 12f else 0f
        else -> 0f
    }

    val wavePath = remember { Path() }

    val colorMatrixFilter = remember(brightness, contrast, saturation) {
        val cm = ColorMatrix().apply {
            setToSaturation(saturation)
        }
        ColorFilter.colorMatrix(cm)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF07080E))
            .clickable { onTogglePlay() }
    ) {
        // 1. Video Frame Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (activeClip?.isReversed == true) -beatScale else beatScale
                    scaleY = beatScale
                    translationX = glitchOffsetX
                    rotationZ = animRotationZ
                }
                .drawWithCache {
                    val w = size.width
                    val h = size.height
                    val baseAlpha = if (mediaUri.isNotBlank()) 0.45f else 1f
                    val bgBrush = Brush.verticalGradient(
                        colors = listOf(
                            preset.primaryColor.copy(alpha = 0.35f * baseAlpha),
                            Color(0xFF090A14).copy(alpha = 0.92f * baseAlpha),
                            preset.secondaryColor.copy(alpha = 0.42f * baseAlpha)
                        )
                    )
                    val orbCenter = Offset(w * 0.5f, h * 0.35f)
                    val orbRadius = size.minDimension * 0.36f
                    val orbBrush = Brush.radialGradient(
                        colors = listOf(
                            preset.primaryColor.copy(alpha = 0.80f * baseAlpha),
                            preset.secondaryColor.copy(alpha = 0.32f * baseAlpha),
                            Color.Transparent
                        ),
                        center = orbCenter,
                        radius = orbRadius
                    )
                    val waveBrush = Brush.horizontalGradient(
                        listOf(preset.primaryColor, Color.White, preset.secondaryColor)
                    )
                    val filterBrush = if (filter.id != "none") {
                        Brush.linearGradient(
                            colors = listOf(
                                filter.tintColor.copy(alpha = 0.22f),
                                Color.Transparent,
                                filter.tintColor.copy(alpha = 0.18f)
                            )
                        )
                    } else null

                    onDrawWithContent {
                        drawContent()

                        drawRect(brush = bgBrush)
                        drawCircle(brush = orbBrush, center = orbCenter, radius = orbRadius)

                        // Perspective Horizon Grid
                        val horizonY = h * 0.62f
                        for (i in 0..6) {
                            val progress = ((i / 6f) + (currentTimeSec * 0.25f) % 0.16f).coerceAtMost(1f)
                            val lineY = horizonY + (h - horizonY) * (progress * progress)
                            drawLine(
                                color = preset.primaryColor.copy(alpha = (0.15f + 0.3f * progress) * baseAlpha),
                                start = Offset(0f, lineY),
                                end = Offset(w, lineY),
                                strokeWidth = 2f
                            )
                        }
                        for (col in -4..4) {
                            val topX = w * 0.5f + col * (w * 0.05f)
                            val bottomX = w * 0.5f + col * (w * 0.24f)
                            drawLine(
                                color = preset.secondaryColor.copy(alpha = 0.25f * baseAlpha),
                                start = Offset(topX, horizonY),
                                end = Offset(bottomX, h),
                                strokeWidth = 2f
                            )
                        }

                        // AI Cutout Neon Rim Subject Frame
                        if (aiCutoutEnabled) {
                            drawRoundRect(
                                brush = Brush.linearGradient(listOf(ElectricCyan, NeonMagenta)),
                                topLeft = Offset(w * 0.18f, h * 0.12f),
                                size = Size(w * 0.64f, h * 0.68f),
                                cornerRadius = CornerRadius(32f, 32f),
                                style = Stroke(width = 6f)
                            )
                        }

                        // Audio-Reactive Waveform
                        wavePath.reset()
                        val waveY = h * 0.46f
                        val segments = 24
                        val phase = currentTimeSec * 3.2f
                        val clipVol = activeClip?.volume ?: 1f
                        for (s in 0..segments) {
                            val x = (w * 0.12f) + (w * 0.76f) * (s.toFloat() / segments)
                            val amp = sin(phase + s * 0.6f) * (if (isBeatDrop) 30f else 13f) * clipVol
                            val y = waveY + amp
                            if (s == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                        }
                        drawPath(
                            path = wavePath,
                            brush = waveBrush,
                            style = Stroke(width = 4f)
                        )

                        if (filterBrush != null) {
                            drawRect(brush = filterBrush)
                        }

                        // Brightness / Contrast adjustment overlay
                        if (brightness > 0.02f) {
                            drawRect(color = Color.White.copy(alpha = brightness.coerceAtMost(0.45f)))
                        } else if (brightness < -0.02f) {
                            drawRect(color = Color.Black.copy(alpha = (-brightness).coerceAtMost(0.55f)))
                        }
                    }
                }
        ) {
            if (mediaUri.isNotBlank()) {
                AsyncImage(
                    model = Uri.parse(mediaUri),
                    contentDescription = "User selected video or image frame",
                    contentScale = ContentScale.Crop,
                    colorFilter = colorMatrixFilter,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 2. Top Status Badge (Compact & Informative)
        if (showControlsOverlay) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.62f),
                    shape = CircleShape,
                    modifier = Modifier.border(
                        1.dp,
                        preset.primaryColor.copy(alpha = 0.5f),
                        CircleShape
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = preset.primaryColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = buildString {
                                append(activeClip?.label ?: preset.label)
                                if (activeClip != null && activeClip.curvePreset != "none") {
                                    append(" • Curve:${activeClip.curvePreset.uppercase()}")
                                } else if (dynamicSpeed != 1.0f) {
                                    append(String.format(Locale.US, " • %.1fx", dynamicSpeed))
                                }
                                if (activeClip != null && activeClip.keyframes.isNotEmpty()) {
                                    append(" • ◆${activeClip.keyframes.size}")
                                }
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                if (aiVoiceEffect != "none") {
                    Surface(
                        color = NeonMagenta.copy(alpha = 0.78f),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = voiceLabel(aiVoiceEffect),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 3. Picture-in-Picture (PIP) Overlay Window (Top-End)
        if (pipOverlayPreset != "none") {
            Surface(
                color = Color(0xFF101320).copy(alpha = 0.90f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 38.dp, end = 10.dp)
                    .size(width = 88.dp, height = 114.dp)
                    .border(1.5.dp, ElectricCyan, RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    NeonMagenta.copy(alpha = 0.35f),
                                    Color(0xFF0A0D18),
                                    ElectricCyan.copy(alpha = 0.35f)
                                )
                            )
                        )
                        .padding(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "PIP",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = when (pipOverlayPreset) {
                            "reaction_cam" -> "🎙️ Cam\nPhản Ứng"
                            "cyber_hud" -> "⚡ Khung\nCyber HUD"
                            "timer_corner" -> String.format(Locale.US, "⏱️ 00:%02d", currentSecondInt)
                            else -> "PIP"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // 4. Beat-Reactive Animated Stickers Layer (Top-Start)
        if (activeStickers.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 40.dp, start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activeStickers.forEachIndexed { idx, emoji ->
                    val bounce = if (isBeatDrop) 1.22f else 1.0f
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = bounce
                                scaleY = bounce
                                rotationZ = sin(currentTimeSec * 4f + idx) * 8f
                            }
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        // 5. CapCut Teleprompter Overlay (if enabled)
        if (teleprompterActive && teleprompterText.isNotBlank()) {
            Surface(
                color = Color.Black.copy(alpha = 0.78f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 36.dp, start = 14.dp, end = 14.dp)
                    .fillMaxWidth()
                    .border(1.dp, ElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📜 MÁY NHẮC CHỮ (TELEPROMPTER)",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = teleprompterText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }

        // 6. Synchronized Kinetic AI Auto-Captions Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedContent(
                targetState = activeCaption,
                transitionSpec = {
                    (fadeIn(tween(150)) + scaleIn(initialScale = 0.92f, animationSpec = tween(150)))
                        .togetherWith(fadeOut(tween(100)))
                },
                label = "kinetic_caption"
            ) { cue ->
                if (cue != null && cue.text.isNotBlank()) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .border(
                                width = 1.2.dp,
                                brush = Brush.horizontalGradient(listOf(ElectricCyan, NeonMagenta)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 4.dp)
                    ) {
                        val annotated = buildAnnotatedString {
                            val full = cue.text
                            val hl = cue.highlightWord
                            if (hl.isNotBlank() && full.contains(hl, ignoreCase = true)) {
                                val startIdx = full.indexOf(hl, ignoreCase = true)
                                val endIdx = startIdx + hl.length
                                append(full.substring(0, startIdx))
                                withStyle(
                                    SpanStyle(
                                        color = SolarGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        background = NeonMagenta.copy(alpha = 0.32f)
                                    )
                                ) {
                                    append(full.substring(startIdx, endIdx))
                                }
                                append(full.substring(endIdx))
                            } else {
                                append(full)
                            }
                        }
                        Text(
                            text = annotated,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 7. Play / Pause Indicator when paused
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(2.dp, ElectricCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Phát video",
                    tint = ElectricCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

private fun computeCurveMultiplier(curvePreset: String, t: Float): Float {
    return when (curvePreset) {
        "montage" -> if (t in 0.3f..0.65f) 2.4f else 0.65f
        "hero" -> if (t in 0.4f..0.75f) 0.4f else 1.8f
        "bullet" -> if (t < 0.25f) 2.8f else 0.45f
        "jump_cut" -> if ((t * 4f).toInt() % 2 == 0) 2.2f else 0.7f
        "flash" -> if (t < 0.2f || t > 0.8f) 2.6f else 0.8f
        else -> 1.0f
    }
}

private fun voiceLabel(voiceId: String): String = when (voiceId) {
    "anime_girl" -> "Giọng Nữ Anime"
    "deep_cinema" -> "Trầm Điện Ảnh"
    "cyber_robot" -> "Robot Cyber"
    "fast_reviewer" -> "Review Nhanh"
    else -> "Gốc"
}
