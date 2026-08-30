package com.example.roleora.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.roleora.data.model.RoleEntity

/**
 * Adaptive Iconography Specification for Specialized Workspaces.
 * Captures the visual DNA (Filled symbol, Outlined symbol, Accent symbol, Thematic colors, Shapes).
 */
data class RoleIconSpec(
    val key: String,
    val roleTitle: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val accentIcon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val containerShape: Shape,
    val badgeSymbolName: String,
    val contentDescription: String
)

/**
 * Registry of Adaptive Iconography for all core professional workspaces.
 */
object RoleIconographyRegistry {

    // 1. Movie Director / Filmmaker
    val DirectorSpec = RoleIconSpec(
        key = "director",
        roleTitle = "Movie Director",
        filledIcon = Icons.Filled.Movie,
        outlinedIcon = Icons.Outlined.Movie,
        accentIcon = Icons.Filled.Videocam,
        primaryColor = Color(0xFFD97706),      // Cinematic Amber Gold
        secondaryColor = Color(0xFFB45309),    // Warm Ochre
        containerShape = RoundedCornerShape(14.dp),
        badgeSymbolName = "Clapperboard",
        contentDescription = "Movie Director & Filmmaking Workspace Icon"
    )

    // 2. College Student / Academic
    val StudentSpec = RoleIconSpec(
        key = "student",
        roleTitle = "College Student",
        filledIcon = Icons.Filled.School,
        outlinedIcon = Icons.Outlined.School,
        accentIcon = Icons.Filled.MenuBook,
        primaryColor = Color(0xFF2563EB),      // Royal Scholar Blue
        secondaryColor = Color(0xFF1D4ED8),    // Deep Navy
        containerShape = RoundedCornerShape(16.dp),
        badgeSymbolName = "Graduation Cap",
        contentDescription = "College Student & Academic Workspace Icon"
    )

    // 3. Software Developer / Engineer
    val DeveloperSpec = RoleIconSpec(
        key = "developer",
        roleTitle = "Software Developer",
        filledIcon = Icons.Filled.Code,
        outlinedIcon = Icons.Outlined.Code,
        accentIcon = Icons.Filled.Terminal,
        primaryColor = Color(0xFF0D9488),      // High-Tech Teal & Matrix Cyan
        secondaryColor = Color(0xFF0F766E),    // Deep Emerald
        containerShape = RoundedCornerShape(12.dp),
        badgeSymbolName = "Code Brackets",
        contentDescription = "Software Developer & Engineering Workspace Icon"
    )

    // 4. Photographer / Visual Artist
    val PhotographerSpec = RoleIconSpec(
        key = "photographer",
        roleTitle = "Photographer",
        filledIcon = Icons.Filled.CameraAlt,
        outlinedIcon = Icons.Outlined.CameraAlt,
        accentIcon = Icons.Filled.Collections,
        primaryColor = Color(0xFFE11D48),      // Shutter Rose / Crimson
        secondaryColor = Color(0xFFBE123C),    // Deep Ruby
        containerShape = CircleShape,          // Camera Lens Aperture Shape
        badgeSymbolName = "Shutter Lens",
        contentDescription = "Photographer & Creative Studio Workspace Icon"
    )

    // 5. Farmer / Agricultural Manager
    val FarmerSpec = RoleIconSpec(
        key = "farmer",
        roleTitle = "Farmer",
        filledIcon = Icons.Filled.Eco,
        outlinedIcon = Icons.Outlined.Eco,
        accentIcon = Icons.Filled.Park,
        primaryColor = Color(0xFF16A34A),      // Lush Organic Green
        secondaryColor = Color(0xFF15803D),    // Forest Canopy
        containerShape = RoundedCornerShape(18.dp),
        badgeSymbolName = "Sprout / Leaf",
        contentDescription = "Farmer & Agricultural Management Workspace Icon"
    )

    // Fallback General Profession
    val DefaultSpec = RoleIconSpec(
        key = "general",
        roleTitle = "General Workspace",
        filledIcon = Icons.Filled.Work,
        outlinedIcon = Icons.Outlined.Work,
        accentIcon = Icons.Filled.Work,
        primaryColor = Color(0xFF6366F1),      // Indigo
        secondaryColor = Color(0xFF4F46E5),
        containerShape = RoundedCornerShape(14.dp),
        badgeSymbolName = "Briefcase",
        contentDescription = "Professional Workspace Icon"
    )

    /**
     * Resolves the matching icon specification from role name, identifier, or icon descriptor.
     */
    fun resolve(identifier: String?): RoleIconSpec {
        if (identifier == null) return DefaultSpec
        val normalized = identifier.lowercase().trim()
        return when {
            normalized.contains("director") || normalized.contains("movie") || normalized.contains("cinema") || normalized.contains("film") -> DirectorSpec
            normalized.contains("student") || normalized.contains("school") || normalized.contains("academic") || normalized.contains("college") -> StudentSpec
            normalized.contains("developer") || normalized.contains("code") || normalized.contains("software") || normalized.contains("engineer") -> DeveloperSpec
            normalized.contains("photographer") || normalized.contains("camera") || normalized.contains("photo") || normalized.contains("shoot") -> PhotographerSpec
            normalized.contains("farmer") || normalized.contains("eco") || normalized.contains("crop") || normalized.contains("agriculture") -> FarmerSpec
            else -> DefaultSpec
        }
    }
}

/**
 * Size presets for Adaptive Role Icons.
 */
enum class AdaptiveIconSize(
    val containerSize: Dp,
    val iconSize: Dp,
    val cornerRadius: Dp,
    val badgeSize: Dp
) {
    COMPACT(containerSize = 24.dp, iconSize = 14.dp, cornerRadius = 8.dp, badgeSize = 8.dp),
    DOCK_CHIP(containerSize = 28.dp, iconSize = 16.dp, cornerRadius = 9.dp, badgeSize = 9.dp),
    STANDARD(containerSize = 40.dp, iconSize = 22.dp, cornerRadius = 12.dp, badgeSize = 12.dp),
    SWITCHER_CARD(containerSize = 48.dp, iconSize = 26.dp, cornerRadius = 15.dp, badgeSize = 14.dp),
    HERO(containerSize = 64.dp, iconSize = 34.dp, cornerRadius = 20.dp, badgeSize = 18.dp)
}

/**
 * High-craft Adaptive Role Icon Composable.
 * Features:
 * - Dynamic Filled vs Outlined state transitions based on selection
 * - Thematic gradient containers with profession-specific shapes (Lens circle, Terminal squircle, etc.)
 * - Animated active glow pulse when selected
 * - Secondary accent indicator & AI capability spark badge
 */
@Composable
fun RoleAdaptiveIcon(
    roleKeyOrIconName: String,
    modifier: Modifier = Modifier,
    size: AdaptiveIconSize = AdaptiveIconSize.STANDARD,
    isSelected: Boolean = false,
    aiEnabled: Boolean = false,
    showAccentBadge: Boolean = false,
    customTint: Color? = null
) {
    val spec = RoleIconographyRegistry.resolve(roleKeyOrIconName)
    val baseColor = customTint ?: spec.primaryColor

    // Animated colors and dimensions
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) baseColor.copy(alpha = 0.22f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "adaptive_icon_bg"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) baseColor
        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        label = "adaptive_icon_border"
    )

    val animatedIconColor by animateColorAsState(
        targetValue = if (isSelected) baseColor else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "adaptive_icon_tint"
    )

    val containerScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "adaptive_icon_scale"
    )

    // Infinite breathing glow transition for the active workspace
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val pulseGlowAlpha by if (isSelected) {
        infiniteTransition.animateFloat(
            initialValue = 0.12f,
            targetValue = 0.32f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_alpha"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    Box(
        modifier = modifier
            .size(size.containerSize)
            .scale(containerScale),
        contentAlignment = Alignment.Center
    ) {
        // Active Pulsing Halo
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(size.containerSize + 6.dp)
                    .clip(spec.containerShape)
                    .background(baseColor.copy(alpha = pulseGlowAlpha))
            )
        }

        // Main Adaptive Thematic Container
        Surface(
            modifier = Modifier.size(size.containerSize),
            shape = spec.containerShape,
            color = animatedBgColor,
            border = BorderStroke(
                width = if (isSelected) 1.8.dp else 1.dp,
                color = animatedBorderColor
            ),
            shadowElevation = if (isSelected) 3.dp else 0.dp
        ) {
            Box(
                modifier = Modifier.size(size.containerSize),
                contentAlignment = Alignment.Center
            ) {
                // Adaptive Icon: Filled when selected/active, Outlined when inactive
                Icon(
                    imageVector = if (isSelected) spec.filledIcon else spec.outlinedIcon,
                    contentDescription = spec.contentDescription,
                    tint = animatedIconColor,
                    modifier = Modifier.size(size.iconSize)
                )
            }
        }

        // Secondary Accent Symbol Badge (e.g. clapperboard, book, terminal, aperture, sprout)
        if (showAccentBadge && isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(size.badgeSize + 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, baseColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = spec.accentIcon,
                    contentDescription = spec.badgeSymbolName,
                    tint = baseColor,
                    modifier = Modifier.size(size.badgeSize)
                )
            }
        }

        // AI Capability Spark Aura Badge
        if (aiEnabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 3.dp, y = (-3).dp)
                    .size(size.badgeSize + 2.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Enhanced",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(size.badgeSize - 2.dp)
                )
            }
        }
    }
}

/**
 * Helper utility for convenient access to role iconography and colors
 */
object RoleIconHelper {
    fun getIconForTemplate(templateId: String): ImageVector {
        return RoleIconographyRegistry.resolve(templateId).filledIcon
    }

    fun getRoleColor(role: RoleEntity?): Color {
        if (role == null) return Color(0xFF6366F1)
        return try {
            Color(android.graphics.Color.parseColor(role.colorHex))
        } catch (e: Exception) {
            RoleIconographyRegistry.resolve(role.templateId).primaryColor
        }
    }
}

