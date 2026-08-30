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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roleora.data.model.CallSheetEntity
import com.example.roleora.data.model.ContinuityEntity
import com.example.roleora.data.model.ShootingDayEntity
import com.example.roleora.data.model.TakeEntity
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import com.example.roleora.ui.viewmodel.DirectorViewModel

// ============================================================================
// SHOOTING SCHEDULE VIEW
// ============================================================================
@Composable
fun DirectorScheduleView(
    viewModel: DirectorViewModel
) {
    val shootingDays by viewModel.shootingDays.collectAsStateWithLifecycle()
    var showAddDayDialog by remember { mutableStateOf(false) }

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
                    Text("Shooting Schedule & Day Planner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${shootingDays.size} shooting days scheduled", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddDayDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Shoot Day", fontSize = 12.sp)
                }
            }
        }

        items(shootingDays) { day ->
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
                                    Text("D${day.dayNumber}", fontWeight = FontWeight.Bold, color = PolishPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Day ${day.dayNumber} • Call: ${day.generalCallTime} | Wrap: ${day.wrapTime}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Location: ${day.primaryLocation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.deleteShootingDay(day.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (day.equipmentNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Gear Required: ${day.equipmentNotes}", style = MaterialTheme.typography.bodySmall, color = TealAccent)
                    }

                    if (day.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notes: ${day.notes}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (showAddDayDialog) {
        AddShootingDayDialog(
            nextDay = (shootingDays.maxOfOrNull { it.dayNumber } ?: 0) + 1,
            onDismiss = { showAddDayDialog = false },
            onSave = { dayNum, call, wrap, loc, equip, notes ->
                viewModel.saveShootingDay(dayNum, call, wrap, loc, equip, notes)
                showAddDayDialog = false
            }
        )
    }
}

// ============================================================================
// CALL SHEETS VIEW
// ============================================================================
@Composable
fun DirectorCallSheetsView(
    viewModel: DirectorViewModel
) {
    val callSheets by viewModel.callSheets.collectAsStateWithLifecycle()
    var showAddCallSheetDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

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
                    Text("Daily Call Sheets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${callSheets.size} generated call sheets", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddCallSheetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Call Sheet", fontSize = 12.sp)
                }
            }
        }

        items(callSheets) { sheet ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PolishPrimary.copy(alpha = 0.3f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                color = PolishPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "DAY ${sheet.dayNumber} CALL SHEET",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = PolishPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Call: ${sheet.generalCallTime}", fontWeight = FontWeight.Bold)
                        }

                        Row {
                            IconButton(onClick = {
                                val text = "CALL SHEET - DAY ${sheet.dayNumber}\nCall Time: ${sheet.generalCallTime}\nLocation: ${sheet.locationAddress}\nWeather: ${sheet.weatherNote}\nInstructions: ${sheet.specialInstructions}"
                                clipboardManager.setText(AnnotatedString(text))
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { viewModel.deleteCallSheet(sheet.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Location: ${sheet.locationAddress}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text("Weather: ${sheet.weatherNote}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (sheet.specialInstructions.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AmberWarning.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Notice: ${sheet.specialInstructions}",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 11.sp,
                                color = AmberWarning,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddCallSheetDialog) {
        AddCallSheetDialog(
            onDismiss = { showAddCallSheetDialog = false },
            onSave = { day, call, loc, weather, inst ->
                viewModel.saveCallSheet(day, call, loc, weather, inst)
                showAddCallSheetDialog = false
            }
        )
    }
}

// ============================================================================
// CONTINUITY LOG VIEW
// ============================================================================
@Composable
fun DirectorContinuityView(
    viewModel: DirectorViewModel
) {
    val continuities by viewModel.continuities.collectAsStateWithLifecycle()
    var showAddContinuityDialog by remember { mutableStateOf(false) }

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
                    Text("Script Supervisor Continuity Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${continuities.size} continuity tracking entries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddContinuityDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Continuity", fontSize = 12.sp)
                }
            }
        }

        items(continuities) { cont ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Take #${cont.takeNumber} Continuity", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { viewModel.deleteContinuity(cont.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                    if (cont.costumeNotes.isNotBlank()) Text("Costume: ${cont.costumeNotes}", style = MaterialTheme.typography.bodySmall)
                    if (cont.propsNotes.isNotBlank()) Text("Props: ${cont.propsNotes}", style = MaterialTheme.typography.bodySmall)
                    if (cont.continuityWarnings.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Warning: ${cont.continuityWarnings}", style = MaterialTheme.typography.bodySmall, color = AmberWarning, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showAddContinuityDialog) {
        AddContinuityDialog(
            onDismiss = { showAddContinuityDialog = false },
            onSave = { sceneId, shotId, takeNum, cost, mu, props, warn ->
                viewModel.saveContinuity(sceneId, shotId, takeNum, cost, mu, props, warn)
                showAddContinuityDialog = false
            }
        )
    }
}

// ============================================================================
// TAKES & FOOTAGE LOG VIEW
// ============================================================================
@Composable
fun DirectorTakesView(
    viewModel: DirectorViewModel
) {
    val takes by viewModel.takes.collectAsStateWithLifecycle()
    var showAddTakeDialog by remember { mutableStateOf(false) }

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
                    Text("Production Takes & Footage Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${takes.size} recorded takes • ${takes.count { it.isSelectedBestTake }} circled best takes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddTakeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Take", fontSize = 12.sp)
                }
            }
        }

        items(takes) { take ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (take.isSelectedBestTake) EmeraldGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(
                    containerColor = if (take.isSelectedBestTake) EmeraldGreen.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                )
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
                                color = if (take.isSelectedBestTake) EmeraldGreen.copy(alpha = 0.15f) else PolishPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "TAKE #${take.takeNumber}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (take.isSelectedBestTake) EmeraldGreen else PolishPrimary
                                )
                            }
                            if (take.isSelectedBestTake) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("★ CIRCLED BEST TAKE", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { s ->
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (s < take.directorRating) AmberWarning else MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { viewModel.deleteTake(take.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    if (take.fileUriOrReference.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("File / Clip: ${take.fileUriOrReference}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TealAccent)
                    }

                    if (take.problemNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notes: ${take.problemNotes}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (showAddTakeDialog) {
        AddTakeDialog(
            nextTake = (takes.maxOfOrNull { it.takeNumber } ?: 0) + 1,
            onDismiss = { showAddTakeDialog = false },
            onSave = { sceneId, shotId, takeNum, fileRef, rating, isBest, notes ->
                viewModel.logTake(sceneId, shotId, takeNum, fileRef, rating, isBest, notes)
                showAddTakeDialog = false
            }
        )
    }
}

// Dialogs
@Composable
fun AddShootingDayDialog(
    nextDay: Int,
    onDismiss: () -> Unit,
    onSave: (dayNum: Int, call: String, wrap: String, loc: String, equip: String, notes: String) -> Unit
) {
    var dayNumber by remember { mutableStateOf(nextDay.toString()) }
    var callTime by remember { mutableStateOf("06:30 AM") }
    var wrapTime by remember { mutableStateOf("06:30 PM") }
    var location by remember { mutableStateOf("State Archives Vault") }
    var equip by remember { mutableStateOf("Alexa Mini LF + 35mm Prime") }
    var notes by remember { mutableStateOf("Scene 1 & Scene 2 Shoot") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Shooting Day", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = dayNumber, onValueChange = { dayNumber = it }, label = { Text("Day #") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = callTime, onValueChange = { callTime = it }, label = { Text("Call Time") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = wrapTime, onValueChange = { wrapTime = it }, label = { Text("Wrap Time") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = equip, onValueChange = { equip = it }, label = { Text("Equipment Notes") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Day Objectives") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(dayNumber.toIntOrNull() ?: nextDay, callTime, wrapTime, location, equip, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Schedule Day")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddCallSheetDialog(
    onDismiss: () -> Unit,
    onSave: (day: Int, call: String, loc: String, weather: String, inst: String) -> Unit
) {
    var dayNumber by remember { mutableStateOf("1") }
    var callTime by remember { mutableStateOf("06:30 AM") }
    var location by remember { mutableStateOf("Heritage Archives Vault, Old Port Road, Chennai") }
    var weather by remember { mutableStateOf("29°C Night, Clear Sky, Humid") }
    var instructions by remember { mutableStateOf("Silent footwear mandatory inside soundstage vault") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Daily Call Sheet", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = dayNumber, onValueChange = { dayNumber = it }, label = { Text("Day #") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = callTime, onValueChange = { callTime = it }, label = { Text("General Crew Call") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location Address") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = weather, onValueChange = { weather = it }, label = { Text("Weather Forecast") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("Special Notice / Safety Guidelines") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(dayNumber.toIntOrNull() ?: 1, callTime, location, weather, instructions)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Publish Call Sheet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddContinuityDialog(
    onDismiss: () -> Unit,
    onSave: (sceneId: String, shotId: String, takeNum: Int, costume: String, mu: String, props: String, warn: String) -> Unit
) {
    var takeNumStr by remember { mutableStateOf("1") }
    var costume by remember { mutableStateOf("Maya: Brown wool trench coat, collar turned up") }
    var props by remember { mutableStateOf("Reel tape in right hand, flashlight in left") }
    var warning by remember { mutableStateOf("Watch watch strap position on Raghav") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Continuity Record", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = takeNumStr, onValueChange = { takeNumStr = it }, label = { Text("Take #") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = costume, onValueChange = { costume = it }, label = { Text("Costume Details") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = props, onValueChange = { props = it }, label = { Text("Props & Placement") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = warning, onValueChange = { warning = it }, label = { Text("Continuity Mismatch Warnings") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave("scene_1", "shot_1A", takeNumStr.toIntOrNull() ?: 1, costume, "", props, warning)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddTakeDialog(
    nextTake: Int,
    onDismiss: () -> Unit,
    onSave: (sceneId: String, shotId: String, takeNum: Int, fileRef: String, rating: Int, isBest: Boolean, notes: String) -> Unit
) {
    var takeNumStr by remember { mutableStateOf(nextTake.toString()) }
    var clipRef by remember { mutableStateOf("A001_C004_0829.MOV") }
    var rating by remember { mutableStateOf(5) }
    var isBest by remember { mutableStateOf(true) }
    var problemNotes by remember { mutableStateOf("Clean audio, excellent performance") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Production Take", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = takeNumStr, onValueChange = { takeNumStr = it }, label = { Text("Take #") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = clipRef, onValueChange = { clipRef = it }, label = { Text("File Name / Clip ID") }, modifier = Modifier.fillMaxWidth())

                Text("Director Rating", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..5).forEach { r ->
                        IconButton(onClick = { rating = r }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (r <= rating) AmberWarning else MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isBest, onCheckedChange = { isBest = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Circle as Best Take for Post-Production", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }

                OutlinedTextField(value = problemNotes, onValueChange = { problemNotes = it }, label = { Text("Director / Sound Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave("scene_1", "shot_1A", takeNumStr.toIntOrNull() ?: nextTake, clipRef, rating, isBest, problemNotes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Log Take")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
