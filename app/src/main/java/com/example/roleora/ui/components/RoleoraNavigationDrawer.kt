package com.example.roleora.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.sp
import com.example.roleora.data.auth.AuthState
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.ui.screens.NavigationTab
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.PolishPrimaryLight
import com.example.roleora.ui.theme.PolishSurfaceVariant
import com.example.roleora.ui.theme.TealAccent

/**
 * Persistent & Modal Navigation Drawer for Roleora.
 * Allows instant jumping between active profession workspaces, managing roles,
 * exploring navigation tabs, and system utilities.
 */
@Composable
fun RoleoraDrawerContent(
    roles: List<RoleEntity>,
    activeRole: RoleEntity?,
    currentTab: NavigationTab,
    authState: AuthState,
    onTabSelected: (NavigationTab) -> Unit,
    onRoleSelected: (RoleEntity) -> Unit,
    onOpenAddNewRole: () -> Unit,
    onOpenCloudAuth: () -> Unit,
    onOpenVersionManager: () -> Unit,
    onOpenTrash: () -> Unit,
    onOpenShortcuts: () -> Unit,
    onOpenCommandPalette: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeRoleColor = activeRole?.let { parseColorHex(it.colorHex) } ?: PolishPrimary

    ModalDrawerSheet(
        modifier = modifier
            .width(340.dp)
            .fillMaxHeight()
            .testTag("roleora_nav_drawer"),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. BRAND HEADER & CLOSE ACTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(PolishPrimary, activeRoleColor, TealAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Roleora Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ROLEORA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = activeRoleColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "PRO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeRoleColor,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Multi-Role Workspace Engine",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier.testTag("drawer_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Drawer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. ACTIVE WORKSPACE HERO CARD
            if (activeRole != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_workspace_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = activeRoleColor.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.5.dp, activeRoleColor.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = activeRoleColor.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(activeRoleColor)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "CURRENT WORKSPACE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = activeRoleColor
                                    )
                                }
                            }

                            if (activeRole.aiEnabled) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldGreen.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = EmeraldGreen,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "AI ACTIVE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldGreen
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RoleAdaptiveIcon(
                                roleKeyOrIconName = activeRole.iconName.ifBlank { activeRole.displayName },
                                size = AdaptiveIconSize.STANDARD,
                                isSelected = true,
                                aiEnabled = activeRole.aiEnabled,
                                showAccentBadge = true,
                                customTint = activeRoleColor
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeRole.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = activeRole.specialisation.ifBlank { activeRole.templateId.replace('_', ' ').uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // 3. ACTIVE PROFESSION WORKSPACES LIST (QUICK SWITCHER)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROFESSION WORKSPACES (${roles.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.8.sp
                    )

                    TextButton(
                        onClick = {
                            onCloseDrawer()
                            onOpenAddNewRole()
                        },
                        modifier = Modifier.testTag("drawer_add_role_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Workspace",
                            modifier = Modifier.size(16.dp),
                            tint = PolishPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolishPrimary
                        )
                    }
                }

                if (roles.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Active Workspaces",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onCloseDrawer()
                                    onOpenAddNewRole()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Create First Workspace")
                            }
                        }
                    }
                } else {
                    roles.forEach { role ->
                        val isSelected = activeRole?.id == role.id
                        val rColor = parseColorHex(role.colorHex)

                        val itemBg by animateColorAsState(
                            targetValue = if (isSelected) rColor.copy(alpha = 0.14f) else Color.Transparent,
                            label = "role_item_bg"
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (!isSelected) {
                                        onRoleSelected(role)
                                    }
                                    onCloseDrawer()
                                }
                                .testTag("drawer_role_item_${role.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = itemBg,
                            border = if (isSelected) BorderStroke(1.5.dp, rColor) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RoleAdaptiveIcon(
                                    roleKeyOrIconName = role.iconName.ifBlank { role.displayName },
                                    size = AdaptiveIconSize.COMPACT,
                                    isSelected = isSelected,
                                    aiEnabled = role.aiEnabled,
                                    showAccentBadge = false,
                                    customTint = rColor
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = role.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Active",
                                                tint = rColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = role.specialisation.ifBlank { role.templateId.replace('_', ' ') },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (!isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Switch",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // 4. WORKSPACE NAVIGATION TABS
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "WORKSPACE NAVIGATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                val navItems = listOf(
                    DrawerNavItem(
                        tab = NavigationTab.DASHBOARD,
                        label = "Command Dashboard",
                        icon = Icons.Filled.Dashboard,
                        unselectedIcon = Icons.Outlined.Dashboard
                    ),
                    DrawerNavItem(
                        tab = NavigationTab.TIMELINE,
                        label = "Timeline & Diary",
                        icon = Icons.Filled.MenuBook,
                        unselectedIcon = Icons.Outlined.MenuBook
                    ),
                    DrawerNavItem(
                        tab = NavigationTab.TASKS,
                        label = "Tasks & Action Items",
                        icon = Icons.Filled.CheckCircle,
                        unselectedIcon = Icons.Outlined.CheckCircle
                    ),
                    DrawerNavItem(
                        tab = NavigationTab.CALENDAR,
                        label = "Schedule & Deadlines",
                        icon = Icons.Filled.CalendarMonth,
                        unselectedIcon = Icons.Outlined.CalendarMonth
                    ),
                    DrawerNavItem(
                        tab = NavigationTab.AUDIT,
                        label = "Audit Trail & History",
                        icon = Icons.Filled.History,
                        unselectedIcon = Icons.Outlined.History
                    )
                )

                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.icon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) activeRoleColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            onTabSelected(item.tab)
                            onCloseDrawer()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("drawer_nav_${item.tab.name.lowercase()}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = activeRoleColor.copy(alpha = 0.12f),
                            unselectedContainerColor = Color.Transparent
                        )
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // 5. UTILITIES & SYSTEM MANAGEMENT
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "UTILITIES & SYSTEM",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                // Cloud Sync Item
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onCloseDrawer()
                            onOpenCloudAuth()
                        }
                        .testTag("drawer_cloud_sync_button"),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (authState is AuthState.Authenticated) Icons.Default.CloudDone else Icons.Default.CloudSync,
                            contentDescription = "Cloud Sync",
                            tint = if (authState is AuthState.Authenticated) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cloud Synchronization",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (authState is AuthState.Authenticated) {
                                    authState.displayName ?: "Connected to Cloud"
                                } else {
                                    "Local Only • Tap to Sign In"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = if (authState is AuthState.Authenticated) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Template Version Manager Item
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onCloseDrawer()
                            onOpenVersionManager()
                        }
                        .testTag("drawer_version_manager_button"),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Template Versions",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Template Upgrades",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Manage schemas & features",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Recycle Bin & Trash Item
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onCloseDrawer()
                            onOpenTrash()
                        }
                        .testTag("drawer_trash_button"),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Recycle Bin",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Trash & Recycle Bin",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Restore deleted records & logs",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Command Palette & Keyboard Shortcuts
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onCloseDrawer()
                            onOpenCommandPalette()
                        }
                        .testTag("drawer_shortcuts_button"),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Command Palette",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Command Palette & Shortcuts",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Press ⌘K or Ctrl+K anytime",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private data class DrawerNavItem(
    val tab: NavigationTab,
    val label: String,
    val icon: ImageVector,
    val unselectedIcon: ImageVector
)
