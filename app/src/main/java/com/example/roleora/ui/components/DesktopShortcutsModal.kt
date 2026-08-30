package com.example.roleora.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShortcutEntry(val keys: String, val description: String, val group: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopShortcutsModal(
    onDismiss: () -> Unit
) {
    val shortcuts = listOf(
        // Navigation
        ShortcutEntry("Ctrl + K / ⌘K", "Open Omni Command Palette & Fast Search", "Navigation & Control"),
        ShortcutEntry("Ctrl + 1", "Jump to Workspace Dashboard", "Navigation & Control"),
        ShortcutEntry("Ctrl + 2", "Jump to Timeline & Work Diary", "Navigation & Control"),
        ShortcutEntry("Ctrl + 3", "Jump to Tasks & Reminders", "Navigation & Control"),
        ShortcutEntry("Ctrl + 4", "Jump to Calendar & Milestones", "Navigation & Control"),
        ShortcutEntry("Ctrl + 5", "Jump to Security & Audit Center", "Navigation & Control"),
        ShortcutEntry("Alt + 1..9", "Instant switch between active Roles / Personas", "Navigation & Control"),

        // Creation & Work
        ShortcutEntry("Ctrl + N", "Open Universal Create Modal (All Types)", "Creation & Logging"),
        ShortcutEntry("Ctrl + T", "Quick Create Task & Reminder", "Creation & Logging"),
        ShortcutEntry("Ctrl + D", "Create Diary / Work Log Entry", "Creation & Logging"),
        ShortcutEntry("Ctrl + E", "Schedule Event on Workspace Calendar", "Creation & Logging"),
        ShortcutEntry("Ctrl + Space", "Start / Toggle Focus Pomodoro Timer", "Creation & Logging"),

        // Desktop OS & Panels
        ShortcutEntry("Ctrl + P", "Toggle PC 3-Pane Workstation View", "Desktop OS Features"),
        ShortcutEntry("Ctrl + B", "Collapse / Expand Left Navigation Sidebar", "Desktop OS Features"),
        ShortcutEntry("Ctrl + I", "Toggle Contextual Right Inspector Panel", "Desktop OS Features"),
        ShortcutEntry("Ctrl + S", "Trigger Instant Cloud Synchronization", "Desktop OS Features"),
        ShortcutEntry("ESC", "Close active modal / overlay / sheet", "Desktop OS Features")
    )

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("desktop_shortcuts_dialog")
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Color(0xFF334155)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(520.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Desktop Keyboard Shortcuts",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Roleora PC Workstation Pro Productivity Hotkeys",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    var lastGroup = ""
                    items(shortcuts) { sc ->
                        if (sc.group != lastGroup) {
                            lastGroup = sc.group
                            Text(
                                text = sc.group.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp, start = 4.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sc.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE2E8F0),
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    shape = RoundedCornerShape(5.dp),
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Text(
                                        text = sc.keys,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tip: You can press Ctrl + K anywhere in the app to search and run any action instantly.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
