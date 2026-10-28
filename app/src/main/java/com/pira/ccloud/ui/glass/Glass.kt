package com.pira.ccloud.ui.glass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Liquid Glass design system for CCloud.
 *
 * Principles (no new dependencies, minSdk 24 safe):
 *  - Every surface sits on a deep, colourful aurora backdrop, so translucency
 *    actually reads as glass instead of a flat grey card.
 *  - Glass = translucent fill + a thin bright rim + a top sheen gradient.
 *    On API 31+ the aurora blobs and detail backdrops are additionally blurred
 *    with [androidx.compose.ui.draw.blur]; on older devices the same layout
 *    still renders, just without the blur pass.
 *  - Selected / primary actions use a violet -> cyan gradient, never a flat
 *    opaque block, so they glow against the glass instead of punching a hole
 *    through it.
 */

@Composable
fun isDarkGlass(): Boolean =
    MaterialTheme.colorScheme.background.luminance() < 0.5f

@Composable
fun glassFill(): Color =
    if (isDarkGlass()) Color.White.copy(alpha = 0.09f)
    else Color.White.copy(alpha = 0.60f)

@Composable
fun glassStroke(): Color =
    if (isDarkGlass()) Color.White.copy(alpha = 0.20f)
    else Color.White.copy(alpha = 0.65f)

@Composable
fun glassGradient(): Brush {
    val scheme = MaterialTheme.colorScheme
    return Brush.horizontalGradient(
        listOf(scheme.primary, scheme.primary.copy(alpha = 0.75f))
    )
}

/** Translucent fill + bright rim for any shape. Use [GlassSurface] when a top sheen is wanted too. */
@Composable
fun Modifier.glass(
    shape: Shape = RoundedCornerShape(24.dp)
): Modifier {
    val dark = isDarkGlass()
    val fill = if (dark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.62f)
    val fillBottom = if (dark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.42f)
    return this
        .clip(shape)
        .background(Brush.verticalGradient(listOf(fill, fillBottom)), shape)
        .border(1.dp, glassStroke(), shape)
}

/**
 * The core glass container: translucent fill, bright rim, a soft top sheen
 * (light falling on the top edge, like a real glass pane) and an optional
 * press-scale so every card feels tactile.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    contentPadding: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(150),
        label = "glass_press"
    )
    val clickable = if (onClick != null) {
        Modifier.clickable(interactionSource = interactionSource, indication = null) { onClick() }
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .scale(scale)
            .then(clickable)
            .glass(shape)
    ) {
        // Top sheen: bright at the very top edge, fading out by ~40% height.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    ),
                    shape
                )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(contentPadding),
            content = content
        )
    }
}

/** Deep cinematic backdrop: near-black violet in dark mode, soft lilac in light mode, with aurora blobs. */
@Composable
fun AuroraBackground(modifier: Modifier = Modifier) {
    val dark = isDarkGlass()
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (dark) {
                        listOf(Color(0xFF0B0714), Color(0xFF150E2E), Color(0xFF0B0714))
                    } else {
                        listOf(Color(0xFFF6F3FF), Color(0xFFE7E9FF), Color(0xFFF6F3FF))
                    }
                )
            )
    ) {
        // Aurora blobs: blurred colour fields behind the glass. On pre-31
        // devices blur() is a no-op visually, the radial colour still shows.
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset((-90).dp, (-70).dp)
                .blur(90.dp)
                .background(Color(0xFF7C5CFF).copy(alpha = if (dark) 0.42f else 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(220.dp, 180.dp)
                .blur(90.dp)
                .background(Color(0xFF22D3EE).copy(alpha = if (dark) 0.28f else 0.18f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset((-70).dp, 520.dp)
                .blur(90.dp)
                .background(Color(0xFFFF5C8A).copy(alpha = if (dark) 0.24f else 0.15f), CircleShape)
        )
        // Keep the scheme's primary faintly present so a custom theme colour
        // chosen in Settings still tints the whole app.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            scheme.primary.copy(alpha = if (dark) 0.06f else 0.04f),
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/** Section title with a small gradient underline bar. */
@Composable
fun GlassSectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(32.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            Color(0xFF22D3EE)
                        )
                    )
                )
        )
    }
}

/** Selectable pill: gradient when selected, glass when not. Used for genres / categories. */
@Composable
fun GlassChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .then(
                if (selected) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            listOf(scheme.primary, Color(0xFF7C5CFF), Color(0xFF22D3EE))
                        )
                    )
                } else {
                    Modifier.glass(RoundedCornerShape(50.dp))
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else scheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else scheme.onSurface,
                maxLines = 1
            )
        }
    }
}

/** Non-clickable glass tag (e.g. a movie's genres on the detail screen). */
@Composable
fun GlassTag(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .glass(RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

/** Circular glass button for back / favourite / close actions floating over images. */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
            .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

/** Small frosted rating pill that floats on posters. */
@Composable
fun GlassRatingPill(
    rating: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = Color(0xFFFFC93C),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Primary call-to-action: full-width gradient button with white bold text. */
@Composable
fun GlassPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        Color(0xFF7C5CFF)
                    )
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/** One row inside a download / play sheet: icon in a tinted glass circle, title, subtitle, chevron. */
@Composable
fun GlassActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        onClick = onClick,
        contentPadding = 14.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(scheme.primary.copy(alpha = 0.16f))
                    .border(1.dp, scheme.primary.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = scheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = scheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Bottom sheet rendered as a floating glass panel (not a full-width opaque
 * dialog): drag handle, title, subtitle, then scrollable content.
 */
@Composable
fun GlassSheet(
    onDismiss: () -> Unit,
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                contentPadding = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(44.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color.White.copy(alpha = 0.35f))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }
            }
        }
    }
}
