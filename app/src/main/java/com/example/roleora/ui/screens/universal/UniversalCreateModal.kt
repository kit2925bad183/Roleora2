package com.example.roleora.ui.screens.universal

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.EntryType
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.SecurityLevel
import com.example.roleora.data.model.SubtaskItem
import com.example.roleora.data.model.TaskPriority
import com.example.roleora.ui.components.RoleIconHelper
import com.example.roleora.ui.viewmodel.RoleoraViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UniversalCreateModal(
    viewModel: RoleoraViewModel,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val activeRoles by viewModel.activeRoles.collectAsState()
    val activeRole by viewModel.activeRole.collectAsState()
    val defaultType by viewModel.universalCreateDefaultType.collectAsState()

    var selectedRoleId by remember(activeRole) { mutableStateOf(activeRole?.id ?: activeRoles.firstOrNull()?.id ?: "") }
    var selectedType by remember(defaultType) { mutableStateOf(defaultType) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var selectedSecurityLevel by remember { mutableStateOf(SecurityLevel.ROLE_RESTRICTED) }

    // Diary specific fields
    var diaryMood by remember { mutableStateOf("Productive") }
    var diaryType by remember { mutableStateOf("Professional") }

    // Task specific fields
    var taskPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var taskDueDate by remember { mutableStateOf(System.currentTimeMillis() + 86400000L) }
    var taskDueTime by remember { mutableStateOf("17:00") }
    var taskRecurrence by remember { mutableStateOf("None") }
    val subtasks = remember { mutableStateListOf<SubtaskItem>() }
    var newSubtaskText by remember { mutableStateOf("") }

    // Event specific fields
    var eventStartMillis by remember { mutableStateOf(System.currentTimeMillis() + 3600000L) }
    var eventEndMillis by remember { mutableStateOf(System.currentTimeMillis() + 7200000L) }
    var eventLocation by remember { mutableStateOf("") }
    var eventIsAllDay by remember { mutableStateOf(false) }
    var eventReminderMinutes by remember { mutableStateOf(15) }

    // Media & Attachment specific fields
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var selectedMediaName by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
            selectedMediaName = uri.lastPathSegment ?: "Selected File"
            if (title.isBlank()) {
                title = selectedMediaName
            }
        }
    }

    val selectedRole = activeRoles.find { it.id == selectedRoleId } ?: activeRole
    val roleColor = try {
        Color(android.graphics.Color.parseColor(selectedRole?.colorHex ?: "#8B5CF6"))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("universal_create_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Create in Roleora",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Unified creation across all workspace modules",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_universal_create_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Destination Role Selector Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = roleColor.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                var roleDropdownExpanded by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { roleDropdownExpanded = true }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(roleColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = RoleIconHelper.getIconForTemplate(selectedRole?.templateId ?: ""),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "TARGET WORKSPACE",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                color = roleColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = selectedRole?.displayName ?: "Select Workspace",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Role", tint = roleColor)

                    DropdownMenu(
                        expanded = roleDropdownExpanded,
                        onDismissRequest = { roleDropdownExpanded = false }
                    ) {
                        activeRoles.forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(
                                                    try { Color(android.graphics.Color.parseColor(role.colorHex)) } catch (e: Exception) { Color.Gray },
                                                    CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(role.displayName, fontWeight = if (role.id == selectedRoleId) FontWeight.Bold else FontWeight.Normal)
                                    }
                                },
                                onClick = {
                                    selectedRoleId = role.id
                                    roleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Entry Type Chips Grid
            Text(
                text = "SELECT TYPE",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                EntryType.values().forEach { type ->
                    val isSelected = selectedType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedType = type },
                        label = { Text(type.displayName) },
                        leadingIcon = {
                            val icon = when (type) {
                                EntryType.DIARY -> Icons.Default.Mood
                                EntryType.NOTE -> Icons.Default.FormatListBulleted
                                EntryType.TASK -> Icons.Default.Schedule
                                EntryType.EVENT -> Icons.Default.Event
                                EntryType.PHOTO -> Icons.Default.PhotoCamera
                                EntryType.VOICE -> Icons.Default.Mic
                                EntryType.VIDEO -> Icons.Default.Videocam
                                EntryType.DOCUMENT -> Icons.Default.Description
                                EntryType.EXPENSE -> Icons.Default.AttachFile
                                EntryType.PROJECT_UPDATE -> Icons.Default.CalendarToday
                                EntryType.CUSTOM -> Icons.Default.Add
                            }
                            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = roleColor,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.testTag("type_chip_${type.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Common Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (selectedType == EntryType.DIARY) "Diary Title / Headline" else "Title *") },
                placeholder = { Text("e.g., Daily Reflection, Client Brief, Sprint Planning...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("universal_create_title_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Type-Specific Form Sections
            when (selectedType) {
                EntryType.DIARY -> {
                    // Mood & Diary Type Selection
                    Text(
                        text = "MOOD & REFLECTION",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Productive", "Inspired", "Reflective", "Focused", "Challenging").forEach { mood ->
                            FilterChip(
                                selected = diaryMood == mood,
                                onClick = { diaryMood = mood },
                                label = { Text(mood) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Category:", style = MaterialTheme.typography.bodySmall)
                        FilterChip(
                            selected = diaryType == "Professional",
                            onClick = { diaryType = "Professional" },
                            label = { Text("Professional") }
                        )
                        FilterChip(
                            selected = diaryType == "Personal",
                            onClick = { diaryType = "Personal" },
                            label = { Text("Personal") }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Diary Narrative / Notes") },
                        placeholder = { Text("What happened today? Decisions made, insights gained, learnings...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("universal_create_content_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                EntryType.NOTE -> {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Note Content") },
                        placeholder = { Text("Write your detailed notes, checklist items, research findings...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .testTag("universal_create_content_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                EntryType.TASK -> {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Task Description") },
                        placeholder = { Text("Details, acceptance criteria, or context...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("universal_create_content_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "PRIORITY",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TaskPriority.values().forEach { priority ->
                            FilterChip(
                                selected = taskPriority == priority,
                                onClick = { taskPriority = priority },
                                label = { Text(priority.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (priority) {
                                        TaskPriority.URGENT -> Color(0xFFEF4444)
                                        TaskPriority.HIGH -> Color(0xFFF59E0B)
                                        TaskPriority.MEDIUM -> roleColor
                                        TaskPriority.LOW -> Color(0xFF10B981)
                                    },
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Subtasks Builder
                    Text(
                        text = "SUBTASKS (${subtasks.size})",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    subtasks.forEachIndexed { index, subtask ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = subtask.isCompleted,
                                onCheckedChange = { checked ->
                                    subtasks[index] = subtask.copy(isCompleted = checked)
                                }
                            )
                            Text(
                                text = subtask.title,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(
                                onClick = { subtasks.removeAt(index) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSubtaskText,
                            onValueChange = { newSubtaskText = it },
                            placeholder = { Text("Add subtask...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newSubtaskText.isNotBlank()) {
                                    subtasks.add(SubtaskItem(UUID.randomUUID().toString(), newSubtaskText.trim(), false))
                                    newSubtaskText = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Add")
                        }
                    }
                }

                EntryType.EVENT -> {
                    OutlinedTextField(
                        value = eventLocation,
                        onValueChange = { eventLocation = it },
                        label = { Text("Location / Video Link") },
                        placeholder = { Text("e.g. Conference Room A or Meet URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("All-Day Event", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = eventIsAllDay,
                            onCheckedChange = { eventIsAllDay = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Event Agenda & Details") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                EntryType.PHOTO, EntryType.VOICE, EntryType.VIDEO, EntryType.DOCUMENT -> {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (selectedType) {
                                    EntryType.PHOTO -> Icons.Default.PhotoCamera
                                    EntryType.VOICE -> Icons.Default.Mic
                                    EntryType.VIDEO -> Icons.Default.Videocam
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = roleColor,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedMediaName.isNotBlank()) selectedMediaName else "No file attached yet",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val mime = when (selectedType) {
                                        EntryType.PHOTO -> "image/*"
                                        EntryType.VOICE -> "audio/*"
                                        EntryType.VIDEO -> "video/*"
                                        else -> "*/*"
                                    }
                                    filePickerLauncher.launch(mime)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = roleColor)
                            ) {
                                Text("Choose File / Capture")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Caption / Notes / Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                else -> {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content / Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tags & Metadata Row
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags (comma separated)") },
                placeholder = { Text("planning, sprint, client, meeting") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Security Level Chips
            Text(
                text = "PRIVACY & ACCESS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecurityLevel.values().forEach { secLevel ->
                    FilterChip(
                        selected = selectedSecurityLevel == secLevel,
                        onClick = { selectedSecurityLevel = secLevel },
                        label = { Text(secLevel.label) },
                        leadingIcon = {
                            if (secLevel == SecurityLevel.CONFIDENTIAL || secLevel == SecurityLevel.PRIVATE) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val targetRole = selectedRoleId.ifBlank { activeRole?.id ?: "" }
                        if (targetRole.isBlank()) return@Button

                        val finalTitle = title.ifBlank {
                            val formatter = SimpleDateFormat("MMM d, yyyy - HH:mm", Locale.getDefault())
                            "${selectedType.displayName} on ${formatter.format(Date())}"
                        }

                        when (selectedType) {
                            EntryType.TASK -> {
                                val subtasksArray = JSONArray()
                                subtasks.forEach {
                                    val obj = JSONObject().apply {
                                        put("id", it.id)
                                        put("title", it.title)
                                        put("isCompleted", it.isCompleted)
                                    }
                                    subtasksArray.put(obj)
                                }
                                viewModel.saveTask(
                                    roleId = targetRole,
                                    title = finalTitle,
                                    description = content,
                                    priority = taskPriority,
                                    dueDate = taskDueDate,
                                    dueTime = taskDueTime,
                                    recurrence = taskRecurrence,
                                    subtasksJson = subtasksArray.toString(),
                                    tags = tags
                                )
                            }
                            EntryType.EVENT -> {
                                viewModel.saveEvent(
                                    roleId = targetRole,
                                    title = finalTitle,
                                    description = content,
                                    startDateTime = eventStartMillis,
                                    endDateTime = eventEndMillis,
                                    isAllDay = eventIsAllDay,
                                    location = eventLocation,
                                    reminderMinutesBefore = eventReminderMinutes
                                )
                            }
                            else -> {
                                viewModel.saveUniversalEntry(
                                    roleId = targetRole,
                                    entryType = selectedType,
                                    title = finalTitle,
                                    content = content,
                                    tags = tags,
                                    securityLevel = selectedSecurityLevel,
                                    diaryMood = if (selectedType == EntryType.DIARY) diaryMood else null,
                                    diaryType = if (selectedType == EntryType.DIARY) diaryType else "Professional"
                                )
                            }
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("universal_create_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = roleColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
