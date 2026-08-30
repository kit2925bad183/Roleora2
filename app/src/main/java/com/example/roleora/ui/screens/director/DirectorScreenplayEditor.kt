package com.example.roleora.ui.screens.director

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roleora.data.model.ScreenplayElementEntity
import com.example.roleora.data.model.ScreenplayElementType
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import com.example.roleora.ui.viewmodel.DirectorViewModel

@Composable
fun DirectorScreenplayEditorView(
    viewModel: DirectorViewModel
) {
    val screenplays by viewModel.screenplays.collectAsStateWithLifecycle()
    val elements by viewModel.screenplayElements.collectAsStateWithLifecycle()
    val versions by viewModel.screenplayVersions.collectAsStateWithLifecycle()
    val characters by viewModel.characters.collectAsStateWithLifecycle()

    var showAddElementDialog by remember { mutableStateOf(false) }
    var selectedElementType by remember { mutableStateOf(ScreenplayElementType.ACTION) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showNavigatorDrawer by remember { mutableStateOf(false) }
    var showExportPreview by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    var editingElement by remember { mutableStateOf<ScreenplayElementEntity?>(null) }

    val sceneCount = elements.count { it.elementType == ScreenplayElementType.SCENE_HEADING.name }
    val characterCount = characters.size
    val estimatedPages = (elements.size / 8.0).coerceAtLeast(1.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Screenplay Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = screenplays.firstOrNull()?.title ?: "Shooting Screenplay Draft",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$sceneCount Scenes • Est. ${String.format("%.1f", estimatedPages)} Pages • ${versions.size} Saved Drafts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showVersionDialog = true },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Snapshots (${versions.size})", fontSize = 12.sp)
                }

                Button(
                    onClick = { showExportPreview = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export Fountain", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Screenplay Element Selector Action Bar
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScreenplayElementType.entries.forEach { type ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            selectedElementType = type
                            showAddElementDialog = true
                        },
                        label = {
                            Text("+ ${type.label}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Screenplay Page Layout Container (Standard Industry Screenplay Canvas)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            if (elements.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = PolishPrimary.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Begin Writing Your Screenplay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Add a Scene Heading, Action, or Character Dialogue to start.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                selectedElementType = ScreenplayElementType.SCENE_HEADING
                                showAddElementDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
                        ) {
                            Text("+ Add Scene Heading")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(elements) { element ->
                        ScreenplayElementItem(
                            element = element,
                            onEdit = { editingElement = element },
                            onDelete = { viewModel.deleteScreenplayElement(element.id) }
                        )
                    }
                }
            }
        }
    }

    // Modal: Add Screenplay Element
    if (showAddElementDialog) {
        AddScreenplayElementDialog(
            initialType = selectedElementType,
            characters = characters.map { it.name },
            onDismiss = { showAddElementDialog = false },
            onSave = { type, text, charName, loc, dayNight, intExt ->
                viewModel.addScreenplayElement(type, text, charName, loc, dayNight, intExt)
                showAddElementDialog = false
            }
        )
    }

    // Modal: Edit Screenplay Element
    editingElement?.let { element ->
        EditScreenplayElementDialog(
            element = element,
            onDismiss = { editingElement = null },
            onSave = { updated ->
                viewModel.updateScreenplayElement(updated)
                editingElement = null
            }
        )
    }

    // Modal: Version History Snapshot
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("Screenplay Snapshots & Versions", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    var draftName by remember { mutableStateOf("Revision Draft ${versions.size + 1}") }
                    var summary by remember { mutableStateOf("Scene polish and dialogue tightening") }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PolishPrimary.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Create New Version Snapshot", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = draftName,
                                onValueChange = { draftName = it },
                                label = { Text("Draft Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = summary,
                                onValueChange = { summary = it },
                                label = { Text("Change Summary") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.createScreenplayVersion(draftName, summary)
                                    draftName = ""
                                    summary = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Snapshot Snapshot")
                            }
                        }
                    }

                    Text("Previous Version History", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    if (versions.isEmpty()) {
                        Text("No snapshots recorded yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        versions.forEach { ver ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(ver.draftName, fontWeight = FontWeight.Bold)
                                        Text("v${ver.versionNumber}", color = PolishPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Text(ver.changeSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVersionDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal: Fountain Exporter Preview
    if (showExportPreview) {
        val fountainText = generateFountainScript(elements)
        AlertDialog(
            onDismissRequest = { showExportPreview = false },
            title = { Text("Fountain Standard Script Export", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    Text(
                        "Industry-standard plain-text formatting compatible with Final Draft, Highland, WriterDuet, and Fade In.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = fountainText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(fountainText))
                        showExportPreview = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportPreview = false }) {
                    Text("Close")
                }
            }
        )
    }
}

// ============================================================================
// SCREENPLAY ELEMENT RENDERER (COURIER / SCREENPLAY STANDARD FORMATTING)
// ============================================================================
@Composable
fun ScreenplayElementItem(
    element: ScreenplayElementEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val type = try {
        ScreenplayElementType.valueOf(element.elementType)
    } catch (_: Exception) {
        ScreenplayElementType.ACTION
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onEdit() }
            .padding(vertical = 4.dp)
    ) {
        when (type) {
            ScreenplayElementType.SCENE_HEADING -> {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, PolishPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = element.text.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PolishPrimary
                        )
                        Row {
                            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                            }
                            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            ScreenplayElementType.CHARACTER -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = element.text.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ScreenplayElementType.PARENTHETICAL -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (element.text.startsWith("(") && element.text.endsWith(")")) element.text else "(${element.text})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ScreenplayElementType.DIALOGUE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = element.text,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ScreenplayElementType.ACTION -> {
                Text(
                    text = element.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }

            ScreenplayElementType.TRANSITION -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = element.text.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ScreenplayElementType.SHOT -> {
                Text(
                    text = element.text.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TealAccent
                )
            }

            ScreenplayElementType.GENERAL_TEXT -> {
                Text(
                    text = element.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ScreenplayElementType.ACT_MARKER -> {
                Surface(
                    color = AmberWarning.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "— ${element.text.uppercase()} —",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = AmberWarning,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            ScreenplayElementType.PAGE_BREAK -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

// ============================================================================
// DIALOGS FOR ADDING & EDITING SCREENPLAY BLOCKS
// ============================================================================
@Composable
fun AddScreenplayElementDialog(
    initialType: ScreenplayElementType,
    characters: List<String>,
    onDismiss: () -> Unit,
    onSave: (type: ScreenplayElementType, text: String, charName: String, loc: String, dayNight: String, intExt: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var text by remember { mutableStateOf("") }
    var charName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var intExt by remember { mutableStateOf("INT") }
    var dayNight by remember { mutableStateOf("DAY") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add ${selectedType.label}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Element Type Switcher
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ScreenplayElementType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.label, fontSize = 11.sp) }
                        )
                    }
                }

                when (selectedType) {
                    ScreenplayElementType.SCENE_HEADING -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = intExt == "INT",
                                onClick = { intExt = "INT" },
                                label = { Text("INT.") }
                            )
                            FilterChip(
                                selected = intExt == "EXT",
                                onClick = { intExt = "EXT" },
                                label = { Text("EXT.") }
                            )
                            FilterChip(
                                selected = intExt == "INT/EXT",
                                onClick = { intExt = "INT/EXT" },
                                label = { Text("INT/EXT.") }
                            )
                        }

                        OutlinedTextField(
                            value = location,
                            onValueChange = {
                                location = it
                                text = "$intExt. ${it.uppercase()} - $dayNight"
                            },
                            label = { Text("Location (e.g. COFFEE SHOP)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("DAY", "NIGHT", "DUSK", "DAWN", "CONTINUOUS").forEach { time ->
                                FilterChip(
                                    selected = dayNight == time,
                                    onClick = {
                                        dayNight = time
                                        text = "$intExt. ${location.uppercase()} - $time"
                                    },
                                    label = { Text(time, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    ScreenplayElementType.CHARACTER -> {
                        if (characters.isNotEmpty()) {
                            Text("Quick Select Character:", style = MaterialTheme.typography.labelSmall)
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                characters.forEach { cName ->
                                    FilterChip(
                                        selected = charName == cName,
                                        onClick = {
                                            charName = cName
                                            text = cName.uppercase()
                                        },
                                        label = { Text(cName) }
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = text,
                            onValueChange = {
                                text = it
                                charName = it
                            },
                            label = { Text("Character Name (e.g. MAYA)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ScreenplayElementType.PARENTHETICAL -> {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Parenthetical / Stage Direction (e.g. whispers)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ScreenplayElementType.DIALOGUE -> {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Dialogue (Tamil/English/Mixed text supported)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    ScreenplayElementType.ACTION -> {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Action Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    ScreenplayElementType.TRANSITION -> {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("CUT TO:", "FADE IN:", "FADE OUT.", "DISSOLVE TO:", "MATCH CUT TO:").forEach { trans ->
                                FilterChip(
                                    selected = text == trans,
                                    onClick = { text = trans },
                                    label = { Text(trans) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Transition") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    else -> {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Content") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onSave(selectedType, text, charName, location, dayNight, intExt)
                    }
                },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Insert Element")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditScreenplayElementDialog(
    element: ScreenplayElementEntity,
    onDismiss: () -> Unit,
    onSave: (ScreenplayElementEntity) -> Unit
) {
    var text by remember { mutableStateOf(element.text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit ${element.elementType}", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onSave(element.copy(text = text))
                    }
                },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun generateFountainScript(elements: List<ScreenplayElementEntity>): String {
    val builder = StringBuilder()
    builder.append("Title: THE WHISPERING SHADOWS\n")
    builder.append("Credit: Written by\n")
    builder.append("Author: Movie Director\n")
    builder.append("Draft date: ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())}\n\n")
    builder.append("===\n\n")

    elements.forEach { el ->
        when (el.elementType) {
            ScreenplayElementType.SCENE_HEADING.name -> builder.append("\n${el.text.uppercase()}\n\n")
            ScreenplayElementType.ACTION.name -> builder.append("${el.text}\n\n")
            ScreenplayElementType.CHARACTER.name -> builder.append("\n${el.text.uppercase()}\n")
            ScreenplayElementType.PARENTHETICAL.name -> {
                val pText = if (el.text.startsWith("(") && el.text.endsWith(")")) el.text else "(${el.text})"
                builder.append("$pText\n")
            }
            ScreenplayElementType.DIALOGUE.name -> builder.append("${el.text}\n\n")
            ScreenplayElementType.TRANSITION.name -> builder.append("\n> ${el.text.uppercase()}\n\n")
            ScreenplayElementType.SHOT.name -> builder.append("\n${el.text.uppercase()}\n\n")
            ScreenplayElementType.ACT_MARKER.name -> builder.append("\n# ${el.text.uppercase()}\n\n")
            ScreenplayElementType.PAGE_BREAK.name -> builder.append("\n===\n\n")
            else -> builder.append("${el.text}\n\n")
        }
    }
    return builder.toString()
}
