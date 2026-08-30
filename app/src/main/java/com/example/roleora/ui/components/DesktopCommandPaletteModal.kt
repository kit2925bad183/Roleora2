package com.example.roleora.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.EntryType
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.ui.screens.NavigationTab
import com.example.roleora.ui.viewmodel.RoleoraViewModel

data class CommandItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val category: String,
    val shortcut: String? = null,
    val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopCommandPaletteModal(
    viewModel: RoleoraViewModel,
    roles: List<RoleEntity>,
    activeRole: RoleEntity?,
    onNavigateTab: (NavigationTab) -> Unit,
    onOpenCreateType: (EntryType) -> Unit,
    onOpenVersionManager: () -> Unit,
    onOpenTrash: () -> Unit,
    onOpenCloudAuth: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val allCommands = remember(roles, activeRole) {
        val list = mutableListOf<CommandItem>()

        // 1. Navigation Commands
        list.add(CommandItem(
            id = "nav_dash",
            title = "Go to Workspace Dashboard",
            subtitle = "View active role modules, charts and stats",
            icon = Icons.Default.Dashboard,
            category = "Navigation",
            shortcut = "Ctrl + 1",
            action = { onNavigateTab(NavigationTab.DASHBOARD); onDismiss() }
        ))
        list.add(CommandItem(
            id = "nav_timeline",
            title = "Go to Timeline & Diary",
            subtitle = "Chronological stream and session logs",
            icon = Icons.Default.Schedule,
            category = "Navigation",
            shortcut = "Ctrl + 2",
            action = { onNavigateTab(NavigationTab.TIMELINE); onDismiss() }
        ))
        list.add(CommandItem(
            id = "nav_tasks",
            title = "Go to Tasks & Reminders",
            subtitle = "Pending items, deadlines and checklists",
            icon = Icons.Default.CheckCircle,
            category = "Navigation",
            shortcut = "Ctrl + 3",
            action = { onNavigateTab(NavigationTab.TASKS); onDismiss() }
        ))
        list.add(CommandItem(
            id = "nav_calendar",
            title = "Go to Calendar & Events",
            subtitle = "Monthly and weekly event planner",
            icon = Icons.Default.Event,
            category = "Navigation",
            shortcut = "Ctrl + 4",
            action = { onNavigateTab(NavigationTab.CALENDAR); onDismiss() }
        ))
        list.add(CommandItem(
            id = "nav_audit",
            title = "Go to Security & Audit",
            subtitle = "Active sessions, device security, token status",
            icon = Icons.Default.Security,
            category = "Navigation",
            shortcut = "Ctrl + 5",
            action = { onNavigateTab(NavigationTab.AUDIT); onDismiss() }
        ))

        // 2. Creation Actions
        list.add(CommandItem(
            id = "create_task",
            title = "Create New Task",
            subtitle = "Add task with priority and due date",
            icon = Icons.Default.CheckCircle,
            category = "Quick Create",
            shortcut = "Ctrl + N",
            action = { onOpenCreateType(EntryType.TASK); onDismiss() }
        ))
        list.add(CommandItem(
            id = "create_note",
            title = "Create Diary / Work Note",
            subtitle = "Draft a rich text note in active workspace",
            icon = Icons.Default.Schedule,
            category = "Quick Create",
            shortcut = "Ctrl + D",
            action = { onOpenCreateType(EntryType.DIARY); onDismiss() }
        ))
        list.add(CommandItem(
            id = "create_event",
            title = "Schedule Calendar Event",
            subtitle = "Add deadline, meeting or shoot milestone",
            icon = Icons.Default.Event,
            category = "Quick Create",
            shortcut = "Ctrl + E",
            action = { onOpenCreateType(EntryType.EVENT); onDismiss() }
        ))
        list.add(CommandItem(
            id = "create_reminder",
            title = "Create Quick Note",
            subtitle = "Capture a markdown note in active workspace",
            icon = Icons.Default.Schedule,
            category = "Quick Create",
            shortcut = "Ctrl + R",
            action = { onOpenCreateType(EntryType.NOTE); onDismiss() }
        ))

        // 3. System & Session Tools
        list.add(CommandItem(
            id = "start_session",
            title = "Start Live Focus Timer",
            subtitle = "Begin a focused session block in active role",
            icon = Icons.Default.PlayArrow,
            category = "Work Tools",
            shortcut = "Ctrl + Space",
            action = {
                activeRole?.let {
                    viewModel.startWorkTimer(
                        roleId = it.id,
                        description = "Focused work session"
                    )
                }
                onDismiss()
            }
        ))
        list.add(CommandItem(
            id = "cloud_sync",
            title = "Trigger Cloud Synchronization",
            subtitle = "Synchronize local state with Cloud Firestore",
            icon = Icons.Default.CloudSync,
            category = "Work Tools",
            shortcut = "Ctrl + S",
            action = { onOpenCloudAuth(); onDismiss() }
        ))
        list.add(CommandItem(
            id = "view_trash",
            title = "Open Trash & Recovery Center",
            subtitle = "Restore or permanently purge soft-deleted items",
            icon = Icons.Default.DeleteOutline,
            category = "Work Tools",
            action = { onOpenTrash(); onDismiss() }
        ))
        list.add(CommandItem(
            id = "version_history",
            title = "Open Template Version Manager",
            subtitle = "Manage schema versions and rollback checkpoints",
            icon = Icons.Default.History,
            category = "Work Tools",
            action = { onOpenVersionManager(); onDismiss() }
        ))

        // 4. Role Switcher Commands
        roles.forEachIndexed { index, role ->
            list.add(CommandItem(
                id = "switch_role_${role.id}",
                title = "Switch to ${role.displayName}",
                subtitle = "${role.specialisation} (${role.templateId})",
                icon = Icons.Default.Work,
                category = "Switch Workspace",
                shortcut = "Alt + ${index + 1}",
                action = {
                    viewModel.selectRole(role.id)
                    onDismiss()
                }
            ))
        }

        list
    }

    val filteredCommands = remember(allCommands, searchQuery) {
        if (searchQuery.isBlank()) {
            allCommands
        } else {
            allCommands.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.subtitle.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("omni_command_palette_dialog")
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Color(0xFF334155)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(540.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header Search Input
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Type a command or search...", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF38BDF8)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedContainerColor = Color(0xFF1E293B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("omni_search_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(8.dp))

                // Results list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    var lastCategory = ""
                    items(filteredCommands, key = { it.id }) { cmd ->
                        if (cmd.category != lastCategory) {
                            lastCategory = cmd.category
                            Text(
                                text = cmd.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp, start = 6.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { cmd.action() }
                                .testTag("command_item_${cmd.id}"),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F172A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = cmd.icon,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = cmd.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = Color(0xFFF1F5F9)
                                        )
                                        Text(
                                            text = cmd.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                cmd.shortcut?.let { sc ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF0F172A),
                                        border = BorderStroke(1.dp, Color(0xFF334155))
                                    ) {
                                        Text(
                                            text = sc,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = Color(0xFFE2E8F0),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer helper
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROLEORA Command Dispatcher",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Press ESC or Click Outside to close",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
