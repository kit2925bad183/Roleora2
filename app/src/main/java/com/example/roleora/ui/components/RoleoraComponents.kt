package com.example.roleora.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.TemplateVersionEntity
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.PolishPrimaryLight
import com.example.roleora.ui.theme.PolishSurfaceVariant
import com.example.roleora.ui.theme.TealAccent

fun getProfessionIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "movie", "director" -> Icons.Default.Movie
        "school", "student" -> Icons.Default.School
        "code", "developer" -> Icons.Default.Code
        "camera", "photographer" -> Icons.Default.CameraAlt
        "eco", "farmer" -> Icons.Default.Eco
        else -> Icons.Default.Work
    }
}

fun parseColorHex(colorHex: String, fallback: Color = PolishPrimary): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        fallback
    }
}

/**
 * Top App Bar with Role Switcher Trigger, Version Manager, and Cloud / Google Auth status.
 */
@Composable
fun RoleoraHeader(
    activeRole: RoleEntity?,
    onOpenRoleSwitcher: () -> Unit,
    onOpenVersionManager: () -> Unit,
    onOpenCloudAuth: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    isAuthenticated: Boolean = false,
    userDisplayName: String? = null,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    showSearchAndActions: Boolean = false,
    onUniversalCreate: () -> Unit = {},
    showContextPanelToggle: Boolean = false,
    isContextPanelOpen: Boolean = false,
    onToggleContextPanel: () -> Unit = {},
    isPcMode: Boolean = false,
    onTogglePcMode: () -> Unit = {},
    onOpenCommandPalette: () -> Unit = {},
    onOpenShortcutsModal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand & Active Role Selector with Drawer Trigger
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(if (showSearchAndActions) 0.35f else 1f)
            ) {
                if (onOpenDrawer != null) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("main_drawer_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation Drawer",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenRoleSwitcher() }
                        .padding(4.dp)
                        .testTag("role_switcher_button")
                ) {
                    // Adaptive Role Icon Badge
                    val roleColor = activeRole?.let { parseColorHex(it.colorHex) } ?: PolishPrimary
                    RoleAdaptiveIcon(
                        roleKeyOrIconName = activeRole?.iconName?.ifBlank { activeRole.displayName } ?: "general",
                        size = AdaptiveIconSize.STANDARD,
                        isSelected = true,
                        aiEnabled = activeRole?.aiEnabled ?: false,
                        showAccentBadge = true,
                        customTint = roleColor
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeRole?.displayName ?: "ROLEORA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Switch Role",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = activeRole?.specialisation ?: "Select or create role",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Laptop & Desktop Global Search Field
            if (showSearchAndActions) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search records, diary, items...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PolishPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(0.4f)
                        .padding(horizontal = 12.dp)
                        .height(44.dp)
                        .testTag("global_search_input")
                )
            }

            // Action Chips, Universal Create & Cloud Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Desktop Universal Create Button
                if (showSearchAndActions && activeRole != null) {
                    Button(
                        onClick = onUniversalCreate,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activeRole.let { parseColorHex(it.colorHex) },
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("header_universal_create_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (activeRole != null) {
                    // Version Chip (Clickable to open Version Management)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clickable { onOpenVersionManager() }
                            .testTag("version_chip_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "Version",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "v${activeRole.templateVersion}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // AI Status Chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeRole.aiEnabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = if (activeRole.aiEnabled) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                contentDescription = "AI Status",
                                tint = if (activeRole.aiEnabled) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (activeRole.aiEnabled) "AI" else "Private",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (activeRole.aiEnabled) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Cloud / Google Account Chip Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAuthenticated) PolishGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (isAuthenticated) PolishGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .clickable { onOpenCloudAuth() }
                        .testTag("open_cloud_auth_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = if (isAuthenticated) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                            contentDescription = "Cloud Identity",
                            tint = if (isAuthenticated) PolishGreen else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAuthenticated) (userDisplayName?.split(" ")?.firstOrNull() ?: "Cloud") else "Sync",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isAuthenticated) PolishGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // PC Desktop Mode Toggle Chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPcMode) Color(0xFF0284C7) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (isPcMode) Color(0xFF38BDF8) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .clickable { onTogglePcMode() }
                        .testTag("header_pc_toggle_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = "PC Mode",
                            tint = if (isPcMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPcMode) "PC" else "Desktop",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isPcMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Omni Command Palette Trigger (Ctrl + K)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .clickable { onOpenCommandPalette() }
                        .testTag("header_omni_palette_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Command Palette",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "⌘K",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Toggle Right Context Panel (Laptop / Desktop)
                if (showContextPanelToggle) {
                    IconButton(
                        onClick = onToggleContextPanel,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_context_panel_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewSidebar,
                            contentDescription = "Toggle Context Panel",
                            tint = if (isContextPanelOpen) PolishPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom Sheet for Switching between isolated user roles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSwitcherSheet(
    roles: List<RoleEntity>,
    activeRoleId: String?,
    onSelectRole: (String) -> Unit,
    onAddNewRole: () -> Unit,
    onDismiss: () -> Unit,
    onClearStaleUiState: (() -> Unit)? = null,
    isFetchingFromFirestore: Boolean = false,
    onFetchFromFirestore: (() -> Unit)? = null,
    isCloudAuthenticated: Boolean = false,
    onOpenCloudAuth: (() -> Unit)? = null
) {
    RoleSwitcherModalSheet(
        roles = roles,
        activeRoleId = activeRoleId,
        onSelectRole = { role ->
            onClearStaleUiState?.invoke()
            onSelectRole(role.id)
        },
        onAddNewRole = onAddNewRole,
        onDismiss = onDismiss,
        onClearStaleUiState = onClearStaleUiState,
        isFetchingFromFirestore = isFetchingFromFirestore,
        onFetchFromFirestore = onFetchFromFirestore,
        isCloudAuthenticated = isCloudAuthenticated,
        onOpenCloudAuth = onOpenCloudAuth
    )
}


/**
 * Universal & Profession Specific Create Bottom Sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalCreateSheet(
    activeRole: RoleEntity?,
    onActionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Create in ${activeRole?.displayName ?: "ROLEORA"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Target: ${activeRole?.displayName ?: "Active Role"} (${activeRole?.specialisation ?: "General"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "SPECIALIZED ACTIONS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic Profession Actions
            when (activeRole?.templateId) {
                "movie_director" -> {
                    CreateActionRow("New Screenplay Scene", "Heading, action, dialogue & Tamil/English text", Icons.Default.Movie) {
                        onActionSelected("NEW_SCENE")
                    }
                    CreateActionRow("Script Breakdown Element", "Props, cast, costume, location, sound", Icons.Default.Bookmark) {
                        onActionSelected("NEW_BREAKDOWN")
                    }
                    CreateActionRow("Plan Camera Shot", "Shot size, angle, lens, camera movement, takes", Icons.Default.CameraAlt) {
                        onActionSelected("NEW_SHOT")
                    }
                    CreateActionRow("Production Diary Log", "Shooting day, scenes done, best takes, expenses", Icons.Default.History) {
                        onActionSelected("NEW_DIARY")
                    }
                }
                "college_student" -> {
                    CreateActionRow("Add Academic Subject", "Course code, professor, credits, target CGPA", Icons.Default.School) {
                        onActionSelected("NEW_SUBJECT")
                    }
                    CreateActionRow("Record Attendance Entry", "Update class attendance and check 75% warnings", Icons.Default.CheckCircle) {
                        onActionSelected("NEW_ATTENDANCE")
                    }
                    CreateActionRow("New Assignment / Deadline", "Track due date, submission portal, and marks", Icons.Default.Bookmark) {
                        onActionSelected("NEW_ASSIGNMENT")
                    }
                    CreateActionRow("Upcoming Examination", "Hall ticket details, syllabus units, date & venue", Icons.Default.Info) {
                        onActionSelected("NEW_EXAM")
                    }
                }
                "software_developer" -> {
                    CreateActionRow("Add Sprint Task / Story", "Kanban backlog item, estimation, tags", Icons.Default.Code) {
                        onActionSelected("NEW_TASK")
                    }
                    CreateActionRow("Save Code Snippet", "Syntax highlighted snippet with tag library", Icons.Default.Code) {
                        onActionSelected("NEW_SNIPPET")
                    }
                    CreateActionRow("Report Bug / Issue", "Severity, reproduction steps, expected behavior", Icons.Default.Info) {
                        onActionSelected("NEW_BUG")
                    }
                    CreateActionRow("API / Architecture Note", "Endpoints, payload schema, and microservice spec", Icons.Default.Bookmark) {
                        onActionSelected("NEW_API_NOTE")
                    }
                }
                "photographer" -> {
                    CreateActionRow("New Client Booking", "Event date, package type, advance & total invoice", Icons.Default.CameraAlt) {
                        onActionSelected("NEW_BOOKING")
                    }
                    CreateActionRow("Shot List & Moodboard", "Essential poses, preferred lens, lighting guide", Icons.Default.Bookmark) {
                        onActionSelected("NEW_SHOT_LIST")
                    }
                    CreateActionRow("Equipment Health Log", "Sensor cleaning, shutter count, battery check", Icons.Default.Security) {
                        onActionSelected("NEW_EQUIPMENT")
                    }
                }
                "farmer" -> {
                    CreateActionRow("Add Crop / Field Plot", "Soil type, acreage, sowing date & harvest forecast", Icons.Default.Eco) {
                        onActionSelected("NEW_CROP")
                    }
                    CreateActionRow("Record Irrigation Cycle", "Water source, duration hours, drip method", Icons.Default.Info) {
                        onActionSelected("NEW_IRRIGATION")
                    }
                    CreateActionRow("Treatment & Pest Observation", "Dosage, product, observation notes", Icons.Default.Security) {
                        onActionSelected("NEW_TREATMENT")
                    }
                }
                else -> {
                    CreateActionRow("General Work Note", "Capture dated reflection or milestone", Icons.Default.Work) {
                        onActionSelected("NEW_DIARY")
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "UNIVERSAL TOOLS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            CreateActionRow("Universal Dated Diary Entry", "Activity date, tags, and security level", Icons.Default.History) {
                onActionSelected("NEW_DIARY")
            }
        }
    }
}

@Composable
fun CreateActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Profession Pack Version Manager Bottom Sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionManagerSheet(
    activeRole: RoleEntity?,
    versions: List<TemplateVersionEntity>,
    onUpgrade: (TemplateVersionEntity) -> Unit,
    onRollback: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentVersion = activeRole?.templateVersion ?: "1.0.0"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Profession Pack Versioning",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${activeRole?.displayName} • Installed v$currentVersion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety Guarantee Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = PolishGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Zero Data Loss Guarantee: Updates preserve all personal records, screenplays, attendance, and customized modules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "AVAILABLE PACK VERSIONS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(versions) { version ->
                    val isInstalled = version.versionNumber == currentVersion
                    val isUpgradeAvailable = version.versionNumber > currentVersion

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isInstalled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = if (isInstalled) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                        else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Version ${version.versionNumber}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isInstalled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (isInstalled) "INSTALLED" else version.status.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isInstalled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = version.releaseDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = version.changeSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (version.addedModules.isNotBlank() && version.addedModules != "[]") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✨ Added modules: ${version.addedModules.replace("[", "").replace("]", "").replace("\"", "")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (isUpgradeAvailable) {
                                Button(
                                    onClick = {
                                        onUpgrade(version)
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(46.dp),
                                    shape = RoundedCornerShape(23.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(imageVector = Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Update Now to v${version.versionNumber}", fontWeight = FontWeight.Bold)
                                }
                            } else if (!isInstalled && version.versionNumber < currentVersion) {
                                OutlinedButton(
                                    onClick = {
                                        onRollback(version.versionNumber)
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(46.dp),
                                    shape = RoundedCornerShape(23.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Rollback to v${version.versionNumber}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
