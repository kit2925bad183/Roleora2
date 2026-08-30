package com.example.roleora.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.EntryType
import com.example.roleora.data.model.SubtaskItem
import com.example.roleora.data.model.TaskEntity
import com.example.roleora.data.model.TaskPriority
import com.example.roleora.data.model.TaskStatus
import com.example.roleora.ui.viewmodel.RoleoraViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasksAndRemindersScreen(
    viewModel: RoleoraViewModel,
    onStartWorkTimer: (String, String) -> Unit = { _, _ -> }
) {
    val activeRole by viewModel.activeRole.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val filterStatus by viewModel.taskFilterStatus.collectAsState()

    val roleColor = try {
        Color(android.graphics.Color.parseColor(activeRole?.colorHex ?: "#8B5CF6"))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val now = System.currentTimeMillis()

    val filteredTasks = tasks.filter { task ->
        when (filterStatus) {
            "OVERDUE" -> task.dueDate != null && task.dueDate < now && task.status != TaskStatus.COMPLETED.name
            "ALL", null -> true
            else -> task.status == filterStatus
        }
    }.sortedWith(compareBy<TaskEntity> { it.status == TaskStatus.COMPLETED.name }
        .thenBy {
            when (it.priority) {
                TaskPriority.URGENT.name -> 0
                TaskPriority.HIGH.name -> 1
                TaskPriority.MEDIUM.name -> 2
                else -> 3
            }
        }
        .thenBy { it.dueDate ?: Long.MAX_VALUE }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("tasks_and_reminders_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val filters = listOf(
                "ALL" to "All Tasks (${tasks.size})",
                "OVERDUE" to "Overdue (${tasks.count { it.dueDate != null && it.dueDate < now && it.status != TaskStatus.COMPLETED.name }})",
                TaskStatus.NOT_STARTED.name to "To Do",
                TaskStatus.IN_PROGRESS.name to "In Progress",
                TaskStatus.COMPLETED.name to "Completed"
            )

            items(filters) { (statusKey, label) ->
                val isSelected = (filterStatus == statusKey) || (statusKey == "ALL" && filterStatus == null)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setTaskFilter(statusKey) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (statusKey == "OVERDUE") Color(0xFFEF4444) else roleColor,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("task_filter_${statusKey.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = roleColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No tasks found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add tasks to organize your workflow and set reminders.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.openUniversalCreate(EntryType.TASK) },
                        colors = ButtonDefaults.buttonColors(containerColor = roleColor)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Task")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("tasks_list")
            ) {
                items(filteredTasks, key = { it.taskId }) { task ->
                    TaskItemCard(
                        task = task,
                        roleColor = roleColor,
                        onToggleComplete = { viewModel.toggleTaskComplete(task.taskId, task.status) },
                        onDuplicate = { viewModel.duplicateTask(task.taskId) },
                        onTrash = { viewModel.moveToTrashTask(task.taskId) },
                        onStartTimer = {
                            val activeId = activeRole?.id ?: task.roleId
                            viewModel.startWorkTimer(activeId, "Working on: ${task.title}", task.taskId)
                        },
                        onUpdateSubtasks = { updatedSubtasksJson ->
                            viewModel.updateTask(task.copy(subtasksJson = updatedSubtasksJson))
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    roleColor: Color,
    onToggleComplete: () -> Unit,
    onDuplicate: () -> Unit,
    onTrash: () -> Unit,
    onStartTimer: () -> Unit,
    onUpdateSubtasks: (String) -> Unit
) {
    val isCompleted = task.status == TaskStatus.COMPLETED.name
    var expandedSubtasks by remember { mutableStateOf(false) }

    // Parse subtasks from JSON
    val subtasks = remember(task.subtasksJson) {
        val list = mutableListOf<SubtaskItem>()
        try {
            val array = JSONArray(task.subtasksJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SubtaskItem(
                        id = obj.optString("id", i.toString()),
                        title = obj.optString("title", ""),
                        isCompleted = obj.optBoolean("isCompleted", false)
                    )
                )
            }
        } catch (ignored: Exception) {}
        list
    }

    val completedSubtasksCount = subtasks.count { it.isCompleted }
    val now = System.currentTimeMillis()
    val isOverdue = task.dueDate != null && task.dueDate < now && !isCompleted

    val priorityColor = when (task.priority) {
        TaskPriority.URGENT.name -> Color(0xFFEF4444)
        TaskPriority.HIGH.name -> Color(0xFFF59E0B)
        TaskPriority.MEDIUM.name -> roleColor
        else -> Color(0xFF10B981)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.taskId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Complete Checkbox
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(checkedColor = roleColor),
                    modifier = Modifier.testTag("task_checkbox_${task.taskId}")
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                // Priority Badge
                Box(
                    modifier = Modifier
                        .background(priorityColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = task.priority,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = priorityColor
                    )
                }
            }

            // Due Date & Reminders Row
            if (task.dueDate != null || isOverdue) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (task.dueDate != null) {
                        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        val dueString = formatter.format(Date(task.dueDate))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isOverdue) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Due: $dueString ${task.dueTime ?: ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    if (isOverdue) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OVERDUE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            // Subtasks Progress & Expansion
            if (subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedSubtasks = !expandedSubtasks },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Subtasks ($completedSubtasksCount/${subtasks.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = if (expandedSubtasks) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand subtasks",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { if (subtasks.isEmpty()) 0f else completedSubtasksCount.toFloat() / subtasks.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = roleColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                AnimatedVisibility(visible = expandedSubtasks) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        subtasks.forEachIndexed { idx, sub ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Checkbox(
                                    checked = sub.isCompleted,
                                    onCheckedChange = { checked ->
                                        subtasks[idx] = sub.copy(isCompleted = checked)
                                        val newArray = JSONArray()
                                        subtasks.forEach { item ->
                                            newArray.put(JSONObject().apply {
                                                put("id", item.id)
                                                put("title", item.title)
                                                put("isCompleted", item.isCompleted)
                                            })
                                        }
                                        onUpdateSubtasks(newArray.toString())
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = sub.title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = if (sub.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row: Start Timer, Duplicate, Trash
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Focus session button
                if (!isCompleted) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(roleColor.copy(alpha = 0.1f))
                            .clickable { onStartTimer() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Focus",
                            tint = roleColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Focus Timer",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = roleColor
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDuplicate,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicate task",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onTrash,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Move to trash",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
