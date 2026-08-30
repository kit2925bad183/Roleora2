package com.example.roleora.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.ui.screens.NavigationTab
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary

/**
 * Bottom Navigation Component for Roleora.
 * Features:
 * 1. Quick Workspace Toggle Dock: Displays active specialized workspaces (Director, Student, Developer, Photographer, Farmer)
 *    with instant 1-tap switching and zero-leakage local UI state purging.
 * 2. Primary Navigation Tabs: Workspace Dashboard, Diary, and Security/Audit logs.
 * 3. Quick-Action Add / Switcher modal trigger.
 */
@Composable
fun WorkspaceBottomNavigation(
    roles: List<RoleEntity>,
    activeRole: RoleEntity?,
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onRoleSelected: (RoleEntity) -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    onOpenAddNewRole: () -> Unit,
    onClearStaleUiState: () -> Unit,
    modifier: Modifier = Modifier,
    showWorkspaceDock: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("workspace_bottom_navigation"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
        ) {
            // --- Section 1: Active Workspaces Quick Toggle Dock ---
            if (showWorkspaceDock && roles.isNotEmpty()) {
                WorkspaceQuickToggleDock(
                    roles = roles,
                    activeRole = activeRole,
                    onRoleSelected = { targetRole ->
                        if (targetRole.id != activeRole?.id) {
                            onClearStaleUiState()
                            onRoleSelected(targetRole)
                        }
                    },
                    onOpenRoleSwitcher = onOpenRoleSwitcher,
                    onOpenAddNewRole = onOpenAddNewRole,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 0.8.dp
                )
            }

            // --- Section 2: Standard Application Tabs (Workspace, Timeline, Tasks, Calendar, Security) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavTabItem(
                    selected = currentTab == NavigationTab.DASHBOARD,
                    onClick = { onTabSelected(NavigationTab.DASHBOARD) },
                    selectedIcon = Icons.Filled.Dashboard,
                    unselectedIcon = Icons.Outlined.Dashboard,
                    label = "Workspace",
                    activeColor = activeRole?.let { parseColorHex(it.colorHex) } ?: MaterialTheme.colorScheme.primary,
                    testTag = "nav_dashboard_tab"
                )

                BottomNavTabItem(
                    selected = currentTab == NavigationTab.TIMELINE,
                    onClick = { onTabSelected(NavigationTab.TIMELINE) },
                    selectedIcon = Icons.Filled.Schedule,
                    unselectedIcon = Icons.Outlined.Schedule,
                    label = "Timeline",
                    activeColor = activeRole?.let { parseColorHex(it.colorHex) } ?: MaterialTheme.colorScheme.primary,
                    testTag = "nav_timeline_tab"
                )

                BottomNavTabItem(
                    selected = currentTab == NavigationTab.TASKS,
                    onClick = { onTabSelected(NavigationTab.TASKS) },
                    selectedIcon = Icons.Filled.Check,
                    unselectedIcon = Icons.Filled.Check,
                    label = "Tasks",
                    activeColor = activeRole?.let { parseColorHex(it.colorHex) } ?: MaterialTheme.colorScheme.primary,
                    testTag = "nav_tasks_tab"
                )

                BottomNavTabItem(
                    selected = currentTab == NavigationTab.CALENDAR,
                    onClick = { onTabSelected(NavigationTab.CALENDAR) },
                    selectedIcon = Icons.Filled.AutoAwesome,
                    unselectedIcon = Icons.Filled.AutoAwesome,
                    label = "Calendar",
                    activeColor = activeRole?.let { parseColorHex(it.colorHex) } ?: MaterialTheme.colorScheme.primary,
                    testTag = "nav_calendar_tab"
                )

                BottomNavTabItem(
                    selected = currentTab == NavigationTab.AUDIT,
                    onClick = { onTabSelected(NavigationTab.AUDIT) },
                    selectedIcon = Icons.Filled.Security,
                    unselectedIcon = Icons.Outlined.Security,
                    label = "Security",
                    activeColor = activeRole?.let { parseColorHex(it.colorHex) } ?: MaterialTheme.colorScheme.primary,
                    testTag = "nav_audit_tab"
                )
            }
        }
    }
}

/**
 * Horizontal quick-switcher dock of active specialized workspaces.
 */
@Composable
fun WorkspaceQuickToggleDock(
    roles: List<RoleEntity>,
    activeRole: RoleEntity?,
    onRoleSelected: (RoleEntity) -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    onOpenAddNewRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("workspace_quick_toggle_dock"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Dock Header / Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, end = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Quick Switch Workspaces",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "ROLES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }

        // Scrollable Quick-Toggle Chips
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(scrollState)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            roles.forEach { role ->
                val isSelected = role.id == activeRole?.id
                WorkspaceQuickChip(
                    role = role,
                    isSelected = isSelected,
                    onClick = { onRoleSelected(role) }
                )
            }
        }

        // Action Buttons: More & Add
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            // Open full modal switcher
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = "Open all workspaces",
                        onClick = onOpenRoleSwitcher
                    )
                    .testTag("dock_open_switcher_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "All Roles",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Quick add new role
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = "Add new workspace",
                        onClick = onOpenAddNewRole
                    )
                    .testTag("dock_add_role_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Workspace",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Individual Workspace Pill in the Quick Switcher Dock.
 */
@Composable
fun WorkspaceQuickChip(
    role: RoleEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roleColor = parseColorHex(role.colorHex)

    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) roleColor.copy(alpha = 0.18f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "chip_bg"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) roleColor
        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        label = "chip_border"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = animatedBgColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, animatedBorderColor),
        modifier = modifier
            .heightIn(min = 36.dp)
            .testTag("workspace_chip_${role.id}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Role Adaptive Icon
            RoleAdaptiveIcon(
                roleKeyOrIconName = role.iconName.ifBlank { role.displayName },
                size = AdaptiveIconSize.COMPACT,
                isSelected = isSelected,
                aiEnabled = role.aiEnabled,
                customTint = roleColor
            )

            // Display Name (e.g., "Director", "Student", "Developer", "Photographer", "Farmer")
            Text(
                text = role.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Active Badge Checkmark
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active",
                    tint = roleColor,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

/**
 * Individual Tab Item in Bottom Navigation.
 */
@Composable
private fun BottomNavTabItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    activeColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val contentColor by animateColorAsState(
        targetValue = if (selected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tab_color"
    )

    val pillWidth by animateDpAsState(
        targetValue = if (selected) 56.dp else 36.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pill_width"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                role = Role.Tab,
                onClickLabel = label,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Active Indicator Capsule
        Box(
            modifier = Modifier
                .width(pillWidth)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (selected) activeColor.copy(alpha = 0.16f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor,
            fontSize = 11.sp
        )
    }
}
