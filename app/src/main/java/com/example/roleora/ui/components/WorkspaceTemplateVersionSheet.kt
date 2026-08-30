package com.example.roleora.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.TemplateDiffResult
import com.example.roleora.data.model.WorkspaceTemplateVersionEntity
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Bottom Sheet for managing Versioned Templates and Configuration Snapshots
 * within each isolated Workspace. Supports saving new version snapshots, comparing
 * side-by-side diffs, and reverting configurations with zero data loss.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkspaceTemplateVersionSheet(
    activeRole: RoleEntity?,
    versions: List<WorkspaceTemplateVersionEntity>,
    selectedDiffVersion: WorkspaceTemplateVersionEntity?,
    diffResult: TemplateDiffResult?,
    onSaveNewVersion: (label: String, summary: String, versionNumber: String, modules: List<String>, workflow: String, tags: String) -> Unit,
    onRevertToVersion: (versionId: String, createBackup: Boolean) -> Unit,
    onCompareDiff: (WorkspaceTemplateVersionEntity) -> Unit,
    onClearDiff: () -> Unit,
    onDeleteVersion: (String) -> Unit,
    onToggleLock: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDuplicateVersion: (versionId: String, newLabel: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentRoleColor = activeRole?.let { parseColorHex(it.colorHex) } ?: PolishPrimary
    val currentVersionNumber = activeRole?.templateVersion ?: "1.0.0"

    var showSaveForm by remember { mutableStateOf(false) }
    var selectedFilterTab by remember { mutableStateOf(0) } // 0: All, 1: Favorites, 2: Auto-Backups
    var versionToRevert by remember { mutableStateOf<WorkspaceTemplateVersionEntity?>(null) }
    var versionToDuplicate by remember { mutableStateOf<WorkspaceTemplateVersionEntity?>(null) }
    var duplicateLabelText by remember { mutableStateOf("") }

    // Save Form State
    var newVersionLabel by remember { mutableStateOf("") }
    var newVersionSummary by remember { mutableStateOf("") }
    var newVersionNumber by remember { mutableStateOf("") }
    var newVersionTags by remember { mutableStateOf("") }
    var newWorkflowStages by remember { mutableStateOf("Discovery, Planning, Execution, Review, Delivery") }

    val availableModuleOptions = listOf(
        "dashboard" to "Workspace Dashboard",
        "diary" to "Universal Activity Diary",
        "projects" to "Projects & Milestones",
        "tasks" to "Tasks & Action Items",
        "calendar" to "Deadlines & Calendar",
        "screenplay" to "Screenplay & Scripts",
        "breakdown" to "Shot & Scene Breakdown",
        "budget" to "Budget & Resources",
        "documents" to "Files & Documentation",
        "reports" to "Analytics & Reports"
    )
    var selectedModules by remember {
        mutableStateOf(setOf("dashboard", "diary", "projects", "tasks", "calendar", "documents", "reports"))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(currentRoleColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = currentRoleColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Workspace Template Versions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = activeRole?.displayName ?: "Workspace",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Current: v$currentVersionNumber",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_version_sheet_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Zero Data Loss Guarantee Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PolishGreen.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, PolishGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = PolishGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Zero Data Loss: Saving or reverting configurations preserves 100% of your diary notes, tasks, files, and personal records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Action: Save Snapshot Button
            if (!showSaveForm) {
                Button(
                    onClick = {
                        val base = currentVersionNumber.ifBlank { "1.0.0" }
                        val nextPatch = incrementVersion(base)
                        newVersionNumber = nextPatch
                        newVersionLabel = "${activeRole?.specialisation ?: "Workspace"} Baseline"
                        showSaveForm = true
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = currentRoleColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("open_save_snapshot_form_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Current Configuration Snapshot", fontWeight = FontWeight.Bold)
                }
            }

            // Save Snapshot Expandable Form
            AnimatedVisibility(
                visible = showSaveForm,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Save Point-in-Time Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = newVersionLabel,
                            onValueChange = { newVersionLabel = it },
                            label = { Text("Configuration Label") },
                            placeholder = { Text("e.g., Pre-Production Sprint Baseline") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("version_label_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newVersionNumber,
                                onValueChange = { newVersionNumber = it },
                                label = { Text("Version Tag") },
                                placeholder = { Text("1.1.0") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("version_number_input")
                            )

                            OutlinedButton(
                                onClick = { newVersionNumber = incrementVersion(newVersionNumber.ifBlank { currentVersionNumber }, isMinor = true) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("+Minor", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { newVersionNumber = incrementVersion(newVersionNumber.ifBlank { currentVersionNumber }, isMajor = true) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("+Major", fontSize = 12.sp)
                            }
                        }

                        OutlinedTextField(
                            value = newVersionSummary,
                            onValueChange = { newVersionSummary = it },
                            label = { Text("Change Summary / Notes") },
                            placeholder = { Text("What makes this configuration unique?") },
                            minLines = 2,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("version_summary_input")
                        )

                        Text(
                            text = "Active Workspace Modules",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            availableModuleOptions.forEach { (moduleId, moduleLabel) ->
                                val isSelected = selectedModules.contains(moduleId)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedModules = if (isSelected) {
                                            selectedModules - moduleId
                                        } else {
                                            selectedModules + moduleId
                                        }
                                    },
                                    label = { Text(moduleLabel, fontSize = 11.sp) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                    } else null
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showSaveForm = false }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onSaveNewVersion(
                                        newVersionLabel.ifBlank { "Workspace Snapshot" },
                                        newVersionSummary.ifBlank { "Saved from active configuration" },
                                        newVersionNumber.ifBlank { incrementVersion(currentVersionNumber) },
                                        selectedModules.toList(),
                                        newWorkflowStages,
                                        newVersionTags
                                    )
                                    showSaveForm = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = currentRoleColor),
                                modifier = Modifier.testTag("save_snapshot_confirm_button")
                            ) {
                                Text("Save Configuration", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Diff View Card if a version is selected for comparison
            if (selectedDiffVersion != null && diffResult != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CompareArrows,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Diff: Active (v${diffResult.baseVersionNumber}) ⇄ ${selectedDiffVersion.versionLabel} (v${diffResult.targetVersionNumber})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = onClearDiff, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close Diff", modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = diffResult.summaryText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (diffResult.addedModules.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Added modules: ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    diffResult.addedModules.forEach { mod ->
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = PolishGreen.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "+$mod",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PolishGreen,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (diffResult.removedModules.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Removed modules: ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    diffResult.removedModules.forEach { mod ->
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.errorContainer
                                        ) {
                                            Text(
                                                text = "-$mod",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { versionToRevert = selectedDiffVersion },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("revert_from_diff_button")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Revert Workspace to v${selectedDiffVersion.versionNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Filter Tabs (All, Favorites, Auto-Backups)
            TabRow(
                selectedTabIndex = selectedFilterTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedFilterTab == 0,
                    onClick = { selectedFilterTab = 0 },
                    text = { Text("All (${versions.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedFilterTab == 1,
                    onClick = { selectedFilterTab = 1 },
                    text = { Text("Favorites (${versions.count { it.isFavorite }})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedFilterTab == 2,
                    onClick = { selectedFilterTab = 2 },
                    text = { Text("Auto-Backups (${versions.count { it.tags.contains("Backup") || it.versionNumber.contains("bk") }})", fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val filteredVersions = when (selectedFilterTab) {
                1 -> versions.filter { it.isFavorite }
                2 -> versions.filter { it.tags.contains("Backup") || it.versionNumber.contains("bk") }
                else -> versions
            }

            if (filteredVersions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedFilterTab == 0) "No saved template versions yet.\nClick 'Save Current Configuration Snapshot' above to create one."
                            else "No matching template versions found.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(filteredVersions, key = { it.versionId }) { version ->
                        val isCurrentActive = version.isCurrentActive || version.versionNumber == currentVersionNumber

                        WorkspaceVersionCard(
                            version = version,
                            isActive = isCurrentActive,
                            onRevert = { versionToRevert = version },
                            onCompare = { onCompareDiff(version) },
                            onToggleLock = { onToggleLock(version.versionId) },
                            onToggleFavorite = { onToggleFavorite(version.versionId) },
                            onDuplicate = {
                                versionToDuplicate = version
                                duplicateLabelText = "${version.versionLabel} (Copy)"
                            },
                            onDelete = { onDeleteVersion(version.versionId) }
                        )
                    }
                }
            }
        }
    }

    // Revert Confirmation Dialog
    if (versionToRevert != null) {
        val target = versionToRevert!!
        AlertDialog(
            onDismissRequest = { versionToRevert = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Revert to '${target.versionLabel}'?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "This will restore workspace template configuration to v${target.versionNumber}.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PolishGreen.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PolishGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "A safety backup of your current setup will be automatically created.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRevertToVersion(target.versionId, true)
                        versionToRevert = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("confirm_revert_dialog_button")
                ) {
                    Text("Revert Workspace", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { versionToRevert = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Duplicate Dialog
    if (versionToDuplicate != null) {
        val target = versionToDuplicate!!
        AlertDialog(
            onDismissRequest = { versionToDuplicate = null },
            icon = {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
            },
            title = { Text("Duplicate Configuration") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Create a new copy of configuration v${target.versionNumber}:")
                    OutlinedTextField(
                        value = duplicateLabelText,
                        onValueChange = { duplicateLabelText = it },
                        label = { Text("New Version Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onDuplicateVersion(target.versionId, duplicateLabelText)
                    versionToDuplicate = null
                }) {
                    Text("Duplicate")
                }
            },
            dismissButton = {
                TextButton(onClick = { versionToDuplicate = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkspaceVersionCard(
    version: WorkspaceTemplateVersionEntity,
    isActive: Boolean,
    onRevert: () -> Unit,
    onCompare: () -> Unit,
    onToggleLock: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(version.createdAt) {
        SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(version.createdAt))
    }

    val modulesList = remember(version.enabledModulesJson) {
        version.enabledModulesJson
            .replace("[", "")
            .replace("]", "")
            .replace("\"", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workspace_version_card_${version.versionNumber}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isActive) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "v${version.versionNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (isActive) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (version.isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (version.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (version.isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onToggleLock, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (version.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onDuplicate, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicate",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (!isActive && !version.isLocked) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Label & Date
            Text(
                text = version.versionLabel,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "$dateStr • Author: ${version.authorId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            if (version.changeSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = version.changeSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Modules chips
            if (modulesList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    modulesList.take(6).forEach { mod ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = mod,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (modulesList.size > 6) {
                        Text(
                            text = "+${modulesList.size - 6} more",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCompare,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("compare_version_button_${version.versionNumber}")
                ) {
                    Icon(imageVector = Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compare", fontSize = 12.sp)
                }

                if (!isActive) {
                    Button(
                        onClick = onRevert,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("revert_version_button_${version.versionNumber}")
                    ) {
                        Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Revert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun parseColorHex(hex: String): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color(colorInt or 0x00000000FF000000)
        } else {
            Color(colorInt)
        }
    } catch (e: Exception) {
        PolishPrimary
    }
}

private fun incrementVersion(current: String, isMinor: Boolean = false, isMajor: Boolean = false): String {
    val clean = current.replace(Regex("[^0-9.]"), "")
    val parts = clean.split(".").mapNotNull { it.toIntOrNull() }.toMutableList()
    while (parts.size < 3) parts.add(0)

    return when {
        isMajor -> "${parts[0] + 1}.0.0"
        isMinor -> "${parts[0]}.${parts[1] + 1}.0"
        else -> "${parts[0]}.${parts[1]}.${parts[2] + 1}"
    }
}
