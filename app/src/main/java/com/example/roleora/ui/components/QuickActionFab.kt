package com.example.roleora.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.RoleEntity

/**
 * Specification defining the specialized QuickActionFab icon, label, and contextual action options
 * for a specific professional workspace.
 */
data class QuickActionFabSpec(
    val primaryLabel: String,
    val primaryIcon: ImageVector,
    val primaryActionId: String,
    val roleColor: Color,
    val secondaryActions: List<QuickSubAction> = emptyList(),
    val contentDescription: String
)

data class QuickSubAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val description: String
)

object QuickActionFabRegistry {

    fun resolve(role: RoleEntity?): QuickActionFabSpec {
        val templateId = role?.templateId?.lowercase()?.trim() ?: ""
        val roleColor = role?.let { parseColorHex(it.colorHex) } ?: Color(0xFF6366F1)

        return when {
            templateId.contains("director") || templateId.contains("movie") || (role?.displayName?.contains("Director", ignoreCase = true) == true) -> {
                QuickActionFabSpec(
                    primaryLabel = "Capture Scene",
                    primaryIcon = Icons.Filled.Movie,
                    primaryActionId = "NEW_SCENE",
                    roleColor = Color(0xFFD97706),
                    secondaryActions = listOf(
                        QuickSubAction("NEW_SCENE", "Capture Scene", Icons.Filled.Movie, "Heading, action & screenplay text"),
                        QuickSubAction("NEW_SHOT", "Plan Shot", Icons.Filled.Videocam, "Camera angle, lens, and movement"),
                        QuickSubAction("NEW_BREAKDOWN", "Breakdown", Icons.Filled.Bookmark, "Props, cast & scene elements")
                    ),
                    contentDescription = "Capture Scene and Director Actions"
                )
            }
            templateId.contains("student") || templateId.contains("academic") || (role?.displayName?.contains("Student", ignoreCase = true) == true) -> {
                QuickActionFabSpec(
                    primaryLabel = "Add Task",
                    primaryIcon = Icons.Filled.School,
                    primaryActionId = "NEW_ASSIGNMENT",
                    roleColor = Color(0xFF2563EB),
                    secondaryActions = listOf(
                        QuickSubAction("NEW_ASSIGNMENT", "Add Task / Assignment", Icons.Filled.Bookmark, "Due dates, portal & marks"),
                        QuickSubAction("NEW_ATTENDANCE", "Log Attendance", Icons.Filled.CheckCircle, "Update class attendance status"),
                        QuickSubAction("NEW_SUBJECT", "Add Course Subject", Icons.Filled.School, "Course code & credits")
                    ),
                    contentDescription = "Add Task and Student Actions"
                )
            }
            templateId.contains("developer") || templateId.contains("software") || (role?.displayName?.contains("Developer", ignoreCase = true) == true) -> {
                QuickActionFabSpec(
                    primaryLabel = "New PR / Task",
                    primaryIcon = Icons.Filled.Code,
                    primaryActionId = "NEW_TASK",
                    roleColor = Color(0xFF0D9488),
                    secondaryActions = listOf(
                        QuickSubAction("NEW_TASK", "Sprint Task", Icons.Filled.Code, "Backlog item, story points & tags"),
                        QuickSubAction("NEW_SNIPPET", "Save Snippet", Icons.Filled.Code, "Code snippet library with syntax tags"),
                        QuickSubAction("NEW_API_NOTE", "Architecture Note", Icons.Filled.Bookmark, "API endpoints & microservice spec")
                    ),
                    contentDescription = "New PR, Task and Developer Actions"
                )
            }
            templateId.contains("photographer") || templateId.contains("photo") || (role?.displayName?.contains("Photographer", ignoreCase = true) == true) -> {
                QuickActionFabSpec(
                    primaryLabel = "Book Shoot",
                    primaryIcon = Icons.Filled.CameraAlt,
                    primaryActionId = "NEW_BOOKING",
                    roleColor = Color(0xFFE11D48),
                    secondaryActions = listOf(
                        QuickSubAction("NEW_BOOKING", "New Client Booking", Icons.Filled.CameraAlt, "Event date, package & fee invoice"),
                        QuickSubAction("NEW_SHOT_LIST", "Shot List", Icons.Filled.Bookmark, "Poses, preferred lens & lighting"),
                        QuickSubAction("NEW_EQUIPMENT", "Gear Health Log", Icons.Filled.Bookmark, "Sensor cleaning & shutter counts")
                    ),
                    contentDescription = "Book Shoot and Photography Actions"
                )
            }
            templateId.contains("farmer") || templateId.contains("agriculture") || (role?.displayName?.contains("Farmer", ignoreCase = true) == true) -> {
                QuickActionFabSpec(
                    primaryLabel = "Log Crop",
                    primaryIcon = Icons.Filled.Eco,
                    primaryActionId = "NEW_CROP",
                    roleColor = Color(0xFF16A34A),
                    secondaryActions = listOf(
                        QuickSubAction("NEW_CROP", "Add Crop / Parcel", Icons.Filled.Eco, "Plot, sowing date & yield forecast"),
                        QuickSubAction("NEW_IRRIGATION", "Irrigation Cycle", Icons.Filled.CheckCircle, "Water source & duration hours"),
                        QuickSubAction("NEW_TREATMENT", "Pest & Soil Log", Icons.Filled.Bookmark, "Dosage, treatment & observations")
                    ),
                    contentDescription = "Log Crop and Farming Actions"
                )
            }
            else -> {
                QuickActionFabSpec(
                    primaryLabel = "Add Item",
                    primaryIcon = Icons.Filled.Add,
                    primaryActionId = "NEW_DIARY",
                    roleColor = roleColor,
                    secondaryActions = listOf(
                        QuickSubAction("NEW_DIARY", "Universal Work Note", Icons.Filled.History, "Capture dated log or reflection")
                    ),
                    contentDescription = "Create Item in Workspace"
                )
            }
        }
    }
}

/**
 * Adaptive QuickActionFab component.
 * Dynamic Morphing:
 * - Adapts its icon, label, and theme color dynamically per workspace (e.g. "Capture Scene" for Director, "Add Task" for Student).
 * - Tap triggers immediate creation or expands quick sub-action speed dial.
 * - Long-press or secondary tap opens universal create modal.
 */
@Composable
fun QuickActionFab(
    activeRole: RoleEntity?,
    onActionTriggered: (actionId: String) -> Unit,
    onOpenFullCreateModal: () -> Unit,
    modifier: Modifier = Modifier,
    expandedSpeedDial: Boolean = false,
    onToggleSpeedDial: (Boolean) -> Unit = {}
) {
    val spec = remember(activeRole?.id, activeRole?.templateId) {
        QuickActionFabRegistry.resolve(activeRole)
    }

    var isSpeedDialOpen by remember(activeRole?.id) { mutableStateOf(false) }

    val fabColor by animateColorAsState(
        targetValue = spec.roleColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "fab_color"
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (isSpeedDialOpen) 45f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fab_rotation"
    )

    Column(
        modifier = modifier.testTag("quick_action_fab_container"),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // --- Expanded Speed Dial Sub-Actions ---
        AnimatedVisibility(
            visible = isSpeedDialOpen,
            enter = fadeIn(tween(150)) + slideInVertically { it / 2 } + scaleIn(initialScale = 0.8f),
            exit = fadeOut(tween(100)) + slideOutVertically { it / 2 } + scaleOut(targetScale = 0.8f)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                // More / Universal sheet action
                Surface(
                    onClick = {
                        isSpeedDialOpen = false
                        onToggleSpeedDial(false)
                        onOpenFullCreateModal()
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    shadowElevation = 4.dp,
                    modifier = Modifier.testTag("speed_dial_more_options")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "More Options...",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "All Creation Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Sub-Actions tailored to the profession
                spec.secondaryActions.forEach { subAction ->
                    Surface(
                        onClick = {
                            isSpeedDialOpen = false
                            onToggleSpeedDial(false)
                            onActionTriggered(subAction.id)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, spec.roleColor.copy(alpha = 0.4f)),
                        shadowElevation = 6.dp,
                        modifier = Modifier.testTag("speed_dial_action_${subAction.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = subAction.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = subAction.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(spec.roleColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = subAction.icon,
                                    contentDescription = subAction.label,
                                    tint = spec.roleColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Main Morphing FAB Button ---
        ExtendedFloatingActionButton(
            onClick = {
                if (spec.secondaryActions.size > 1) {
                    isSpeedDialOpen = !isSpeedDialOpen
                    onToggleSpeedDial(isSpeedDialOpen)
                } else {
                    onActionTriggered(spec.primaryActionId)
                }
            },
            containerColor = fabColor,
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp, pressedElevation = 10.dp),
            modifier = Modifier.testTag("quick_action_fab"),
            icon = {
                Box(
                    modifier = Modifier.rotate(if (spec.secondaryActions.size > 1) rotationAngle else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = if (isSpeedDialOpen) Icons.Filled.Close else spec.primaryIcon,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220, delayMillis = 50)) +
                                scaleIn(initialScale = 0.8f)) togetherWith
                                (fadeOut(animationSpec = tween(120)) +
                                    scaleOut(targetScale = 0.8f))
                        },
                        label = "fab_icon_transition"
                    ) { targetIcon ->
                        Icon(
                            imageVector = targetIcon,
                            contentDescription = spec.contentDescription,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            },
            text = {
                AnimatedContent(
                    targetState = if (isSpeedDialOpen) "Close" else spec.primaryLabel,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(200)) + slideInVertically { it / 2 }) togetherWith
                            (fadeOut(animationSpec = tween(100)) + slideOutVertically { -it / 2 })
                    },
                    label = "fab_text_transition"
                ) { targetLabel ->
                    Text(
                        text = targetLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }
            }
        )
    }
}
