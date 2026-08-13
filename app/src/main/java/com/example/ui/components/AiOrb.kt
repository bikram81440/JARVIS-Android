package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisBlue
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisGold

enum class OrbState {
    READY, LISTENING, THINKING, SPEAKING
}

@Composable
fun AiOrb(
    state: OrbState,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbTransition")

    // Scale animation based on state
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = when (state) {
            OrbState.READY -> 1.05f
            OrbState.LISTENING -> 1.25f
            OrbState.THINKING -> 1.15f
            OrbState.SPEAKING -> 1.20f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    OrbState.READY -> 2000
                    OrbState.LISTENING -> 600
                    OrbState.THINKING -> 800
                    OrbState.SPEAKING -> 400
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbScale"
    )

    // Rotation animation for Thinking
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbRotation"
    )

    val primaryColor = when (state) {
        OrbState.READY -> JarvisCyan
        OrbState.LISTENING -> Color(0xFF00E676) // Green for listening
        OrbState.THINKING -> JarvisGold
        OrbState.SPEAKING -> JarvisBlue
    }

    Box(
        modifier = modifier
            .size(160.dp)
            .scale(scaleAnim)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor,
                        primaryColor.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = primaryColor.copy(alpha = 0.6f),
                radius = size.minDimension / 2.2f,
                style = Stroke(width = 4.dp.toPx())
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.3f),
                radius = size.minDimension / 1.8f,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Core inner sphere
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            primaryColor,
                            JarvisBlue
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (state) {
                    OrbState.READY -> "J.A.R.V.I.S."
                    OrbState.LISTENING -> "LISTENING"
                    OrbState.THINKING -> "THINKING"
                    OrbState.SPEAKING -> "SPEAKING"
                },
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}
