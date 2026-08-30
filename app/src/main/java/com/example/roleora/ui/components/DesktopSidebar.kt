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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.ui.screens.NavigationTab
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary

/**
 * Professional Expandable/Collapsible Left Sidebar for Laptop & Large Desktop workstations.
 */
@Composable
fun DesktopSidebar(
    activeRole: RoleEntity?,
    roles: List<RoleEntity>,
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onRoleSelected: (RoleEntity) -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    onOpenAddNewRole: () -> Unit,
    onOpenCloudAuth: () -> Unit,
    onClearStaleUiState: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }

    val sidebarWidth by animateDpAsState(
        targetValue = if (isExpanded) 250.dp else 76.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "sidebar_width"
    )

    val roleColor = activeRole?.let { parseColorHex(it.colorHex) } ?: PolishPrimary

    Surface(
        modifier = modifier
            .width(sidebarWidth)
            .fillMaxHeight()
            .testTag("desktop_sidebar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 14.dp, horizontal = if (isExpanded) 12.dp else 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Header & Collapse Toggle
            Column(modifier = Modifier.weight(1f, fill = false)) {
                // Workspace Brand Header + Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = if (isExpanded) Arrangement.SpaceBetween else Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isExpanded) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = "Roleora Logo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ROLEORA",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Workspace Engine",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Collapse / Expand Toggle Icon Button
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("sidebar_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ChevronLeft else Icons.Default.ChevronRight,
                            contentDescription = if (isExpanded) "Collapse Sidebar" else "Expand Sidebar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Role Card / Quick Switcher
                if (activeRole != null) {
                    Surface(
                        onClick = onOpenRoleSwitcher,
                        shape = RoundedCornerShape(14.dp),
                        color = roleColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, roleColor.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sidebar_active_role_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
                        ) {
                            RoleAdaptiveIcon(
                                roleKeyOrIconName = activeRole.iconName.ifBlank { activeRole.displayName },
                                size = AdaptiveIconSize.COMPACT,
                                isSelected = true,
                                aiEnabled = activeRole.aiEnabled,
                                customTint = roleColor
                            )

                            if (isExpanded) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeRole.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Active Workspace",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = roleColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Switch",
                                    tint = roleColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                // Main Nav Navigation Items
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SidebarNavItem(
                        selected = currentTab == NavigationTab.DASHBOARD,
                        onClick = { onTabSelected(NavigationTab.DASHBOARD) },
                        icon = Icons.Filled.Dashboard,
                        unselectedIcon = Icons.Outlined.Dashboard,
                        label = "Dashboard",
                        isExpanded = isExpanded,
                        activeColor = roleColor,
                        testTag = "sidebar_nav_dashboard"
                    )

                    SidebarNavItem(
                        selected = currentTab == NavigationTab.TIMELINE,
                        onClick = { onTabSelected(NavigationTab.TIMELINE) },
                        icon = Icons.Filled.Schedule,
                        unselectedIcon = Icons.Outlined.Schedule,
                        label = "Timeline & Diary",
                        isExpanded = isExpanded,
                        activeColor = roleColor,
                        testTag = "sidebar_nav_timeline"
                    )

                    SidebarNavItem(
                        selected = currentTab == NavigationTab.TASKS,
                        onClick = { onTabSelected(NavigationTab.TASKS) },
                        icon = Icons.Filled.CheckCircle,
                        unselectedIcon = Icons.Outlined.CheckCircle,
                        label = "Tasks & Reminders",
                        isExpanded = isExpanded,
                        activeColor = roleColor,
                        testTag = "sidebar_nav_tasks"
                    )

                    SidebarNavItem(
                        selected = currentTab == NavigationTab.CALENDAR,
                        onClick = { onTabSelected(NavigationTab.CALENDAR) },
                        icon = Icons.Filled.Event,
                        unselectedIcon = Icons.Outlined.Event,
                        label = "Calendar & Events",
                        isExpanded = isExpanded,
                        activeColor = roleColor,
                        testTag = "sidebar_nav_calendar"
                    )

                    SidebarNavItem(
                        selected = currentTab == NavigationTab.AUDIT,
                        onClick = { onTabSelected(NavigationTab.AUDIT) },
                        icon = Icons.Filled.Security,
                        unselectedIcon = Icons.Outlined.Security,
                        label = "Security & Audit",
                        isExpanded = isExpanded,
                        activeColor = roleColor,
                        testTag = "sidebar_nav_audit"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Workspaces List (1-Tap Switch)
                if (isExpanded && roles.isNotEmpty()) {
                    Text(
                        text = "WORKSPACES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        roles.forEach { role ->
                            val isSelected = role.id == activeRole?.id
                            val itemColor = parseColorHex(role.colorHex)
                            Surface(
                                onClick = {
                                    if (!isSelected) {
                                        onClearStaleUiState()
                                        onRoleSelected(role)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) itemColor.copy(alpha = 0.14f) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sidebar_role_item_${role.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RoleAdaptiveIcon(
                                        roleKeyOrIconName = role.iconName.ifBlank { role.displayName },
                                        size = AdaptiveIconSize.COMPACT,
                                        isSelected = isSelected,
                                        customTint = itemColor
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = role.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Section: Add Role, Cloud Sync & Shortcuts Hint
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // New Role Button
                Surface(
                    onClick = onOpenAddNewRole,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sidebar_add_workspace_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Workspace",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        if (isExpanded) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "New Workspace",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Cloud Sync & Account Status
                Surface(
                    onClick = onOpenCloudAuth,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Cloud Sync",
                            tint = PolishGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        if (isExpanded) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sync & Devices",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Keyboard Shortcut Legend (Laptop / Desktop only)
                if (isExpanded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Alt+1..5 Switch | Ctrl+N Add",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single item inside the Desktop Sidebar.
 */
@Composable
private fun SidebarNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    isExpanded: Boolean,
    activeColor: Color,
    testTag: String
) {
    val animatedBg by animateColorAsState(
        targetValue = if (selected) activeColor.copy(alpha = 0.15f) else Color.Transparent,
        label = "nav_bg"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = animatedBg,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) icon else unselectedIcon,
                contentDescription = label,
                tint = if (selected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Tablet Navigation Rail (compact side rail for 768dp–1023dp screens).
 */
@Composable
fun TabletNavigationRail(
    activeRole: RoleEntity?,
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    onOpenAddNewRole: () -> Unit,
    onOpenCloudAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roleColor = activeRole?.let { parseColorHex(it.colorHex) } ?: PolishPrimary

    NavigationRail(
        modifier = modifier
            .fillMaxHeight()
            .testTag("tablet_navigation_rail"),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                if (activeRole != null) {
                    IconButton(onClick = onOpenRoleSwitcher) {
                        RoleAdaptiveIcon(
                            roleKeyOrIconName = activeRole.iconName.ifBlank { activeRole.displayName },
                            size = AdaptiveIconSize.STANDARD,
                            isSelected = true,
                            customTint = roleColor
                        )
                    }
                }
            }
        }
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        NavigationRailItem(
            selected = currentTab == NavigationTab.DASHBOARD,
            onClick = { onTabSelected(NavigationTab.DASHBOARD) },
            icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Workspace") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = roleColor,
                indicatorColor = roleColor.copy(alpha = 0.2f),
                selectedTextColor = roleColor
            ),
            modifier = Modifier.testTag("rail_nav_dashboard")
        )

        NavigationRailItem(
            selected = currentTab == NavigationTab.TIMELINE,
            onClick = { onTabSelected(NavigationTab.TIMELINE) },
            icon = { Icon(Icons.Filled.Schedule, contentDescription = "Timeline") },
            label = { Text("Timeline") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = roleColor,
                indicatorColor = roleColor.copy(alpha = 0.2f),
                selectedTextColor = roleColor
            ),
            modifier = Modifier.testTag("rail_nav_timeline")
        )

        NavigationRailItem(
            selected = currentTab == NavigationTab.TASKS,
            onClick = { onTabSelected(NavigationTab.TASKS) },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Tasks") },
            label = { Text("Tasks") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = roleColor,
                indicatorColor = roleColor.copy(alpha = 0.2f),
                selectedTextColor = roleColor
            ),
            modifier = Modifier.testTag("rail_nav_tasks")
        )

        NavigationRailItem(
            selected = currentTab == NavigationTab.CALENDAR,
            onClick = { onTabSelected(NavigationTab.CALENDAR) },
            icon = { Icon(Icons.Filled.Event, contentDescription = "Calendar") },
            label = { Text("Calendar") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = roleColor,
                indicatorColor = roleColor.copy(alpha = 0.2f),
                selectedTextColor = roleColor
            ),
            modifier = Modifier.testTag("rail_nav_calendar")
        )

        NavigationRailItem(
            selected = currentTab == NavigationTab.AUDIT,
            onClick = { onTabSelected(NavigationTab.AUDIT) },
            icon = { Icon(Icons.Filled.Security, contentDescription = "Security") },
            label = { Text("Security") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = roleColor,
                indicatorColor = roleColor.copy(alpha = 0.2f),
                selectedTextColor = roleColor
            ),
            modifier = Modifier.testTag("rail_nav_audit")
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onOpenCloudAuth,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(Icons.Default.CloudSync, contentDescription = "Sync", tint = PolishGreen)
        }
    }
}
