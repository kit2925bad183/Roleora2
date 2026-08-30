package com.example.roleora.ui.screens.director

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roleora.data.model.SceneEntity
import com.example.roleora.data.model.ShotEntity
import com.example.roleora.data.model.ShotSize
import com.example.roleora.data.model.ShotStatus
import com.example.roleora.data.model.StoryboardEntity
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import com.example.roleora.ui.viewmodel.DirectorViewModel

// ============================================================================
// SHOTS MASTER LIST VIEW
// ============================================================================
@Composable
fun DirectorShotsView(
    viewModel: DirectorViewModel
) {
    val shots by viewModel.shots.collectAsStateWithLifecycle()
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()
    var showAddShotDialog by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf<String?>("ALL") }

    val filteredShots = if (filterStatus == "ALL") shots else shots.filter { it.status == filterStatus }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Cinematography Shot Planner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${shots.size} setups • ${shots.count { it.status == ShotStatus.COMPLETED.name }} completed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddShotDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Plan Shot", fontSize = 12.sp)
                }
            }
        }

        // Filter status
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = filterStatus == "ALL",
                    onClick = { filterStatus = "ALL" },
                    label = { Text("All (${shots.size})") }
                )
                ShotStatus.entries.forEach { status ->
                    val count = shots.count { it.status == status.name }
                    FilterChip(
                        selected = filterStatus == status.name,
                        onClick = { filterStatus = status.name },
                        label = { Text("${status.label} ($count)", fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filteredShots) { shot ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PolishPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = shot.shotNumber,
                                        fontWeight = FontWeight.Bold,
                                        color = PolishPrimary,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${shot.shotSize} • ${shot.lens} • ${shot.cameraAngle}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Movement: ${shot.cameraMovement} • Priority: ${shot.priority}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.deleteShot(shot.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (shot.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(shot.description, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status:", style = MaterialTheme.typography.labelSmall)
                        ShotStatus.entries.forEach { status ->
                            val isSelected = shot.status == status.name
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) EmeraldGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, if (isSelected) EmeraldGreen else Color.Transparent),
                                modifier = Modifier.clickable { viewModel.updateShotStatus(shot.id, status) }
                            ) {
                                Text(
                                    text = status.label,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddShotDialog) {
        AddShotDialog(
            scenes = scenes,
            onDismiss = { showAddShotDialog = false },
            onSave = { sceneId, shotNum, desc, size, angle, lens, movement, priority ->
                viewModel.saveShot(sceneId, shotNum, desc, size, angle, lens, movement, priority)
                showAddShotDialog = false
            }
        )
    }
}

// ============================================================================
// STORYBOARDS GRID VIEW
// ============================================================================
@Composable
fun DirectorStoryboardsView(
    viewModel: DirectorViewModel
) {
    val storyboards by viewModel.storyboards.collectAsStateWithLifecycle()
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()
    var showAddFrameDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Visual Storyboard Frames", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${storyboards.size} visual composition frames", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddFrameDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Frame", fontSize = 12.sp)
                }
            }
        }

        if (storyboards.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Brush, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No storyboard frames created yet", fontWeight = FontWeight.Bold)
                        Text("Add visual camera compositions, blocking, and action descriptions.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(storyboards) { frame ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PolishPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "FRAME #${frame.frameNumber}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolishPrimary
                                )
                            }
                            Text(frame.caption, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = { viewModel.deleteStoryboard(frame.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (frame.actionDescription.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(frame.actionDescription, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Angle: ${frame.cameraAngle}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Movement: ${frame.cameraMovement}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (showAddFrameDialog) {
        AddStoryboardDialog(
            scenes = scenes,
            nextFrameNumber = (storyboards.maxOfOrNull { it.frameNumber } ?: 0) + 1,
            onDismiss = { showAddFrameDialog = false },
            onSave = { sceneId, frameNum, caption, action, angle, movement ->
                viewModel.saveStoryboardFrame(sceneId, frameNum, caption, action, angle, movement)
                showAddFrameDialog = false
            }
        )
    }
}

// Dialogs
@Composable
fun AddShotDialog(
    scenes: List<SceneEntity>,
    onDismiss: () -> Unit,
    onSave: (sceneId: String, shotNumber: String, desc: String, size: String, angle: String, lens: String, movement: String, priority: String) -> Unit
) {
    var selectedSceneId by remember { mutableStateOf(scenes.firstOrNull()?.id ?: "") }
    var shotNumber by remember { mutableStateOf("1A") }
    var description by remember { mutableStateOf("") }
    var shotSize by remember { mutableStateOf(ShotSize.MEDIUM.code) }
    var cameraAngle by remember { mutableStateOf("Eye Level") }
    var lens by remember { mutableStateOf("50mm Master Prime") }
    var cameraMovement by remember { mutableStateOf("Static") }
    var priority by remember { mutableStateOf("MUST_HAVE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Plan New Shot", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = shotNumber, onValueChange = { shotNumber = it }, label = { Text("Shot # (e.g. 1A, 2B)") }, modifier = Modifier.fillMaxWidth())

                Text("Shot Size", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ShotSize.entries.forEach { size ->
                        FilterChip(
                            selected = shotSize == size.code,
                            onClick = { shotSize = size.code },
                            label = { Text("${size.code} (${size.label})", fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(value = lens, onValueChange = { lens = it }, label = { Text("Lens Package (e.g. 35mm Prime)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cameraAngle, onValueChange = { cameraAngle = it }, label = { Text("Camera Angle (e.g. Low Angle)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cameraMovement, onValueChange = { cameraMovement = it }, label = { Text("Camera Movement (e.g. Dana Dolly / Steadicam)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Shot Framing & Action Description *") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                Text("Coverage Priority", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("LOW", "MEDIUM", "HIGH", "MUST_HAVE").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        onSave(selectedSceneId, shotNumber, description, shotSize, cameraAngle, lens, cameraMovement, priority)
                    }
                },
                enabled = description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Shot")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddStoryboardDialog(
    scenes: List<SceneEntity>,
    nextFrameNumber: Int,
    onDismiss: () -> Unit,
    onSave: (sceneId: String, frameNumber: Int, caption: String, action: String, angle: String, movement: String) -> Unit
) {
    var selectedSceneId by remember { mutableStateOf(scenes.firstOrNull()?.id ?: "") }
    var frameNumber by remember { mutableStateOf(nextFrameNumber.toString()) }
    var caption by remember { mutableStateOf("") }
    var actionDescription by remember { mutableStateOf("") }
    var cameraAngle by remember { mutableStateOf("Wide establishing angle") }
    var cameraMovement by remember { mutableStateOf("Slow push-in") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Storyboard Frame", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = frameNumber, onValueChange = { frameNumber = it }, label = { Text("Frame #") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = caption, onValueChange = { caption = it }, label = { Text("Frame Title / Caption *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = actionDescription, onValueChange = { actionDescription = it }, label = { Text("Action & Blocking Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(value = cameraAngle, onValueChange = { cameraAngle = it }, label = { Text("Camera Framing") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cameraMovement, onValueChange = { cameraMovement = it }, label = { Text("Camera Movement") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (caption.isNotBlank()) {
                        onSave(selectedSceneId, frameNumber.toIntOrNull() ?: nextFrameNumber, caption, actionDescription, cameraAngle, cameraMovement)
                    }
                },
                enabled = caption.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Frame")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
