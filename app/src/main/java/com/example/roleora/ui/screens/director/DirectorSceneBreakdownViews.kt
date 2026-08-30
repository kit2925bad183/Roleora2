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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Theaters
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
import com.example.roleora.data.model.BreakdownCategory
import com.example.roleora.data.model.BreakdownItemEntity
import com.example.roleora.data.model.SceneEntity
import com.example.roleora.data.model.SceneStatus
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import com.example.roleora.ui.viewmodel.DirectorViewModel

// ============================================================================
// SCENES MANAGEMENT VIEW
// ============================================================================
@Composable
fun DirectorScenesView(
    viewModel: DirectorViewModel
) {
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()
    var showAddSceneDialog by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf<String?>("ALL") }

    val filteredScenes = if (filterStatus == "ALL") scenes else scenes.filter { it.status == filterStatus }

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
                    Text("Scene Master Board", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${scenes.size} scenes indexed for production", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddSceneDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Scene", fontSize = 12.sp)
                }
            }
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = filterStatus == "ALL",
                    onClick = { filterStatus = "ALL" },
                    label = { Text("All (${scenes.size})") }
                )
                SceneStatus.entries.forEach { status ->
                    val count = scenes.count { it.status == status.name }
                    FilterChip(
                        selected = filterStatus == status.name,
                        onClick = { filterStatus = status.name },
                        label = { Text("${status.label} ($count)", fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filteredScenes) { scene ->
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
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${scene.sceneNumber}",
                                        fontWeight = FontWeight.Bold,
                                        color = PolishPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(scene.heading, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                                Text("${scene.intExt} • ${scene.locationName} • ${scene.timeOfDay}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        IconButton(onClick = { viewModel.deleteScene(scene.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (scene.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(scene.description, style = MaterialTheme.typography.bodyMedium)
                    }

                    if (scene.characterNames.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Characters: ${scene.characterNames}", style = MaterialTheme.typography.bodySmall, color = PolishPrimary, fontWeight = FontWeight.SemiBold)
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
                        SceneStatus.entries.forEach { status ->
                            val isSelected = scene.status == status.name
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) EmeraldGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, if (isSelected) EmeraldGreen else Color.Transparent),
                                modifier = Modifier.clickable { viewModel.updateSceneStatus(scene.id, status) }
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

    if (showAddSceneDialog) {
        AddSceneDialog(
            nextSceneNumber = (scenes.maxOfOrNull { it.sceneNumber } ?: 0) + 1,
            onDismiss = { showAddSceneDialog = false },
            onSave = { num, head, intExt, loc, time, desc, chars, dur, status ->
                viewModel.saveScene(num, head, intExt, loc, time, desc, chars, dur, status)
                showAddSceneDialog = false
            }
        )
    }
}

// ============================================================================
// SCRIPT BREAKDOWN BY DEPARTMENT VIEW
// ============================================================================
@Composable
fun DirectorBreakdownView(
    viewModel: DirectorViewModel
) {
    val breakdowns by viewModel.breakdownItems.collectAsStateWithLifecycle()
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()
    var showAddItemDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>("ALL") }

    val filteredItems = if (selectedCategory == "ALL") breakdowns else breakdowns.filter { it.category == selectedCategory }
    val totalCost = breakdowns.sumOf { it.costEstimate }

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
                    Text("Script Breakdown Sheet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${breakdowns.size} items across departments • Est. ₹${String.format("%,.0f", totalCost)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddItemDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item", fontSize = 12.sp)
                }
            }
        }

        // Category Filter Row
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == "ALL",
                    onClick = { selectedCategory = "ALL" },
                    label = { Text("All (${breakdowns.size})") }
                )
                BreakdownCategory.entries.forEach { cat ->
                    val count = breakdowns.count { it.category == cat.name }
                    if (count > 0 || cat == BreakdownCategory.PROPS || cat == BreakdownCategory.LIGHTING) {
                        FilterChip(
                            selected = selectedCategory == cat.name,
                            onClick = { selectedCategory = cat.name },
                            label = { Text("${cat.label} ($count)", fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        items(filteredItems) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PolishPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TealAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = item.category,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealAccent
                                )
                            }
                        }
                        Text(
                            text = "Dept: ${item.responsibleDepartment} • Qty: ${item.quantity} • Cost: ₹${String.format("%,.0f", item.costEstimate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { viewModel.deleteBreakdownItem(item.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        AddBreakdownDialog(
            scenes = scenes,
            onDismiss = { showAddItemDialog = false },
            onSave = { sceneId, cat, desc, qty, dept, cost ->
                viewModel.saveBreakdownItem(sceneId, cat, desc, qty, dept, cost)
                showAddItemDialog = false
            }
        )
    }
}

// Dialogs
@Composable
fun AddSceneDialog(
    nextSceneNumber: Int,
    onDismiss: () -> Unit,
    onSave: (num: Int, head: String, intExt: String, loc: String, time: String, desc: String, chars: String, dur: Int, status: SceneStatus) -> Unit
) {
    var sceneNumber by remember { mutableStateOf(nextSceneNumber.toString()) }
    var heading by remember { mutableStateOf("") }
    var intExt by remember { mutableStateOf("INT") }
    var location by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("DAY") }
    var description by remember { mutableStateOf("") }
    var characters by remember { mutableStateOf("") }
    var durationSeconds by remember { mutableStateOf("120") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Scene Entry", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = sceneNumber, onValueChange = { sceneNumber = it }, label = { Text("Scene #") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("INT", "EXT", "INT/EXT").forEach { ie ->
                        FilterChip(
                            selected = intExt == ie,
                            onClick = {
                                intExt = ie
                                heading = "$ie. ${location.uppercase()} - $timeOfDay"
                            },
                            label = { Text(ie) }
                        )
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        heading = "$intExt. ${it.uppercase()} - $timeOfDay"
                    },
                    label = { Text("Location Name (e.g. Police Station)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("DAY", "NIGHT", "DUSK", "CONTINUOUS").forEach { time ->
                        FilterChip(
                            selected = timeOfDay == time,
                            onClick = {
                                timeOfDay = time
                                heading = "$intExt. ${location.uppercase()} - $time"
                            },
                            label = { Text(time, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Scene Action Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(value = characters, onValueChange = { characters = it }, label = { Text("Featured Characters (comma-separated)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = durationSeconds, onValueChange = { durationSeconds = it }, label = { Text("Est. Duration (Seconds)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val num = sceneNumber.toIntOrNull() ?: nextSceneNumber
                    val dur = durationSeconds.toIntOrNull() ?: 120
                    val finalHeading = if (heading.isNotBlank()) heading else "$intExt. ${location.uppercase()} - $timeOfDay"
                    onSave(num, finalHeading, intExt, location, timeOfDay, description, characters, dur, SceneStatus.DRAFT)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Create Scene")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddBreakdownDialog(
    scenes: List<SceneEntity>,
    onDismiss: () -> Unit,
    onSave: (sceneId: String, category: BreakdownCategory, desc: String, qty: Int, dept: String, cost: Double) -> Unit
) {
    var selectedSceneId by remember { mutableStateOf(scenes.firstOrNull()?.id ?: "") }
    var selectedCategory by remember { mutableStateOf(BreakdownCategory.PROPS) }
    var description by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("1") }
    var department by remember { mutableStateOf("Art Department") }
    var costStr by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Breakdown Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Department Category", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BreakdownCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = cat
                                department = when (cat) {
                                    BreakdownCategory.CAST, BreakdownCategory.EXTRAS -> "Casting / AD"
                                    BreakdownCategory.PROPS, BreakdownCategory.SET_DRESSING -> "Art Department"
                                    BreakdownCategory.COSTUME -> "Wardrobe"
                                    BreakdownCategory.MAKEUP, BreakdownCategory.HAIR -> "Hair & Makeup"
                                    BreakdownCategory.LIGHTING -> "Grip & Electric"
                                    BreakdownCategory.SOUND -> "Sound Department"
                                    BreakdownCategory.CAMERA_EQUIPMENT -> "Camera Department"
                                    BreakdownCategory.VFX -> "Visual Effects"
                                    BreakdownCategory.SFX -> "Special Effects"
                                    BreakdownCategory.STUNTS -> "Stunt Coordination"
                                    BreakdownCategory.VEHICLES -> "Transport"
                                    else -> "Production"
                                }
                            },
                            label = { Text(cat.label, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Item Description *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Responsible Department") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = quantityStr, onValueChange = { quantityStr = it }, label = { Text("Qty") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = costStr, onValueChange = { costStr = it }, label = { Text("Est. Cost (₹)") }, modifier = Modifier.weight(2f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        onSave(
                            selectedSceneId,
                            selectedCategory,
                            description,
                            quantityStr.toIntOrNull() ?: 1,
                            department,
                            costStr.toDoubleOrNull() ?: 0.0
                        )
                    }
                },
                enabled = description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
