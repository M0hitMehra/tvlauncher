package com.tvlauncher.app.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.tvlauncher.app.ui.theme.FocusBorder
import com.tvlauncher.app.ui.theme.FocusGlow

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppCard(
    label: String,
    icon: Drawable,
    isFocused: Boolean,
    isSelected: Boolean = false,
    isEditMode: Boolean = false,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onFocusChanged: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(200),
        label = "glow"
    )

    val borderColor = when {
        isSelected && isEditMode -> Color(0xFFFFD700)
        isFocused -> FocusBorder
        else -> Color.Transparent
    }

    val iconBitmap = remember(icon) {
        try { icon.toBitmap(96, 96).asImageBitmap() } catch (e: Exception) { null }
    }

    // Long-press detection via key hold
    var keyDownTime by remember { mutableLongStateOf(0L) }
    var longPressHandled by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isFocused) 16.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = FocusGlow.copy(alpha = glowAlpha * 0.6f),
                spotColor = FocusGlow.copy(alpha = glowAlpha * 0.8f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = if (isFocused) {
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E3A5F), Color(0xFF0D1B2A))
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(Color(0xFF1A1A2E), Color(0xFF12121F))
                    )
                }
            )
            .border(
                width = if (isFocused || (isSelected && isEditMode)) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .focusable()
            .onKeyEvent { event ->
                when {
                    // OK / Enter key
                    (event.key == Key.DirectionCenter || event.key == Key.Enter) -> {
                        when (event.type) {
                            KeyEventType.KeyDown -> {
                                if (event.nativeKeyEvent.repeatCount == 0) {
                                    keyDownTime = System.currentTimeMillis()
                                    longPressHandled = false
                                } else if (!longPressHandled &&
                                    System.currentTimeMillis() - keyDownTime >= 600L
                                ) {
                                    longPressHandled = true
                                    onLongClick()
                                }
                                true
                            }
                            KeyEventType.KeyUp -> {
                                if (!longPressHandled) {
                                    onClick()
                                }
                                longPressHandled = false
                                true
                            }
                            else -> false
                        }
                    }
                    else -> false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = label,
                        modifier = Modifier.size(56.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.Gray, RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isFocused) Color.White else Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )

            if (isEditMode && isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✦ MOVING",
                    fontSize = 9.sp,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
