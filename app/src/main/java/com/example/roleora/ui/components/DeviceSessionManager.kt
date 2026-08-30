package com.example.roleora.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary

data class SyncedDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val osAndBrowser: String,
    val lastActive: String,
    val lastIp: String,
    val isCurrentDevice: Boolean
)

enum class DeviceType {
    LAPTOP,
    DESKTOP,
    TABLET,
    MOBILE
}

/**
 * Cross-Device Security, Sessions and Conflict Resolution Manager.
 */
@Composable
fun DeviceSessionManagerView(
    userEmail: String?,
    onRevokeAllOtherSessions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val devices = remember {
        mutableStateListOf(
            SyncedDevice(
                id = "dev-curr",
                name = "MacBook Pro / Chrome Workstation",
                type = DeviceType.LAPTOP,
                osAndBrowser = "Chrome 128 / macOS Sequoia",
                lastActive = "Active now (Current Session)",
                lastIp = "192.168.1.42 (Primary)",
                isCurrentDevice = true
            ),
            SyncedDevice(
                id = "dev-ipad",
                name = "iPad Pro 12.9 (Field Setup)",
                type = DeviceType.TABLET,
                osAndBrowser = "Safari 18 / iPadOS",
                lastActive = "14 minutes ago",
                lastIp = "192.168.1.88",
                isCurrentDevice = false
            ),
            SyncedDevice(
                id = "dev-pixel",
                name = "Pixel 9 Pro Mobile",
                type = DeviceType.MOBILE,
                osAndBrowser = "ROLEORA PWA / Android 15",
                lastActive = "2 hours ago",
                lastIp = "10.0.0.12",
                isCurrentDevice = false
            ),
            SyncedDevice(
                id = "dev-desktop",
                name = "Linux Studio Desktop",
                type = DeviceType.DESKTOP,
                osAndBrowser = "Firefox 130 / Ubuntu 24.04",
                lastActive = "Yesterday at 6:45 PM",
                lastIp = "192.168.1.15",
                isCurrentDevice = false
            )
        )
    }

    var showConflictResolverModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("device_session_manager_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Security overview card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PolishGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = PolishGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Cross-Device Zero-Trust Sync",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Authorized sessions: ${devices.size} devices (${userEmail ?: "Verified User"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showConflictResolverModal = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PolishPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Conflict Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Devices List
        Text(
            text = "CONNECTED DEVICES (${devices.size})",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )

        devices.forEach { device ->
            val icon = when (device.type) {
                DeviceType.LAPTOP -> Icons.Default.Laptop
                DeviceType.DESKTOP -> Icons.Default.Computer
                DeviceType.TABLET -> Icons.Default.Tablet
                DeviceType.MOBILE -> Icons.Default.PhoneAndroid
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (device.isCurrentDevice) PolishGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.dp,
                    if (device.isCurrentDevice) PolishGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("device_session_card_${device.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (device.isCurrentDevice) PolishGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (device.isCurrentDevice) PolishGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = device.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (device.isCurrentDevice) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = PolishGreen,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "THIS DEVICE",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${device.osAndBrowser} • ${device.lastActive}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (!device.isCurrentDevice) {
                        IconButton(
                            onClick = { devices.remove(device) },
                            modifier = Modifier.testTag("revoke_device_${device.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Revoke Session",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Global sign out other sessions action
        Button(
            onClick = {
                devices.removeAll { !it.isCurrentDevice }
                onRevokeAllOtherSessions()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sign_out_all_other_devices_btn")
        ) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out All Other Devices", fontWeight = FontWeight.Bold)
        }

        // Realtime sync & Conflict Resolution Details
        if (showConflictResolverModal) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, PolishPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PolishGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Conflict Engine: Clean", fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { showConflictResolverModal = false }) {
                            Text("Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Realtime synchronization uses optimistic concurrency with vector clocks and server timestamps. If two devices edit simultaneously, you can choose: Keep Local, Keep Cloud, Save Both as Variants, or Auto-Merge fields.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
