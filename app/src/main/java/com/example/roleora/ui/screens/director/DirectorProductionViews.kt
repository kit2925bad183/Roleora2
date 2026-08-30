package com.example.roleora.ui.screens.director

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roleora.data.model.CharacterEntity
import com.example.roleora.data.model.DirectorSpecialisation
import com.example.roleora.data.model.IdeaEntity
import com.example.roleora.data.model.ProductionEntity
import com.example.roleora.data.model.ProductionStage
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import com.example.roleora.ui.viewmodel.DirectorViewModel

// ============================================================================
// PRODUCTIONS MANAGEMENT VIEW
// ============================================================================
@Composable
fun DirectorProductionsView(
    viewModel: DirectorViewModel
) {
    val productions by viewModel.productions.collectAsStateWithLifecycle()
    val selectedProd by viewModel.selectedProduction.collectAsStateWithLifecycle()
    var editingProd by remember { mutableStateOf<ProductionEntity?>(null) }

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
                    Text("Film & Media Productions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Manage active, archived and developing titles", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(productions) { prod ->
            val isSelected = prod.id == selectedProd?.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectProduction(prod.id) },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) PolishPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) PolishPrimary.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(prod.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = PolishPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text("ACTIVE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PolishPrimary)
                                    }
                                }
                            }
                            Text("${prod.format} • ${prod.genre} • ${prod.language}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = prod.currentStage,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                    }

                    if (prod.logline.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${prod.logline}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Budget: ₹${String.format("%,.0f", prod.budget)} ${prod.currency}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row {
                            IconButton(onClick = { editingProd = prod }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { viewModel.moveProductionToTrash(prod.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    editingProd?.let { prod ->
        EditProductionDialog(
            production = prod,
            onDismiss = { editingProd = null },
            onSave = { updated ->
                viewModel.updateProduction(updated)
                editingProd = null
            }
        )
    }
}

// ============================================================================
// IDEAS & STORY DEVELOPMENT VIEW
// ============================================================================
@Composable
fun DirectorIdeasView(
    viewModel: DirectorViewModel
) {
    val ideas by viewModel.ideas.collectAsStateWithLifecycle()
    var showAddIdeaDialog by remember { mutableStateOf(false) }

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
                    Text("Story & Premise Development", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Brainstorm concepts, 3-act story beats and creative loglines", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddIdeaDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Idea", fontSize = 12.sp)
                }
            }
        }

        if (ideas.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No story ideas recorded for this production", fontWeight = FontWeight.Bold)
                        Text("Capture your initial spark, conflict beats and theme notes.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(ideas) { idea ->
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
                        Text(idea.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { viewModel.deleteStoryIdea(idea.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (idea.premise.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Premise:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = PolishPrimary)
                        Text(idea.premise, style = MaterialTheme.typography.bodyMedium)
                    }

                    if (idea.logline.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Logline:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = PolishPrimary)
                        Text(idea.logline, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Serif)
                    }

                    if (idea.beginningBeat.isNotBlank() || idea.middleBeat.isNotBlank() || idea.endingBeat.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("3-Act Structure Beats", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (idea.beginningBeat.isNotBlank()) Text("Act 1 (Beginning): ${idea.beginningBeat}", fontSize = 12.sp)
                                if (idea.middleBeat.isNotBlank()) Text("Act 2 (Middle): ${idea.middleBeat}", fontSize = 12.sp)
                                if (idea.endingBeat.isNotBlank()) Text("Act 3 (Resolution): ${idea.endingBeat}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddIdeaDialog) {
        AddIdeaDialog(
            onDismiss = { showAddIdeaDialog = false },
            onSave = { title, premise, logline, genre, tone, theme, b, m, e ->
                viewModel.saveStoryIdea(title, premise, logline, genre, tone, theme, b, m, e)
                showAddIdeaDialog = false
            }
        )
    }
}

// ============================================================================
// CHARACTERS & CASTING BINDING VIEW
// ============================================================================
@Composable
fun DirectorCharactersView(
    viewModel: DirectorViewModel
) {
    val characters by viewModel.characters.collectAsStateWithLifecycle()
    var showAddCharacterDialog by remember { mutableStateOf(false) }

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
                    Text("Characters & Cast Roles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${characters.size} dramatic roles configured", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddCharacterDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Character", fontSize = 12.sp)
                }
            }
        }

        items(characters) { char ->
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
                                shape = CircleShape,
                                color = PolishPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(char.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${char.roleType} • ${char.ageRange}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        IconButton(onClick = { viewModel.deleteCharacter(char.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (char.goal.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dramatic Goal: ${char.goal}", style = MaterialTheme.typography.bodySmall)
                    }

                    if (char.conflict.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Internal / External Conflict: ${char.conflict}", style = MaterialTheme.typography.bodySmall, color = AmberWarning)
                    }

                    if (char.dialogueStyle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Dialogue Style / Voice: ${char.dialogueStyle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (char.assignedActorName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TealAccent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Cast Actor: ${char.assignedActorName}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TealAccent
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddCharacterDialog) {
        AddCharacterDialog(
            onDismiss = { showAddCharacterDialog = false },
            onSave = { name, roleType, age, goal, conflict, dialogue, actor ->
                viewModel.saveCharacter(name, roleType, age, goal, conflict, dialogue, actor)
                showAddCharacterDialog = false
            }
        )
    }
}

// ============================================================================
// DIRECTOR SETTINGS & ROLE ISOLATION INFO
// ============================================================================
@Composable
fun DirectorSettingsView(
    role: RoleEntity,
    viewModel: DirectorViewModel
) {
    val selectedProd by viewModel.selectedProduction.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Director Studio Workspace Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Role Isolation & Local Encryption", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All production scripts, shot lists, actor audition notes and financial ledgers are strictly isolated to Role ID: ${role.id}. No data leaks to other installed roles.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = PolishPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Active Production Stage", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProductionStage.entries.forEach { stage ->
                        FilterChip(
                            selected = selectedProd?.currentStage == stage.displayName,
                            onClick = {
                                selectedProd?.let { p ->
                                    viewModel.updateProduction(p.copy(currentStage = stage.displayName))
                                }
                            },
                            label = { Text(stage.displayName, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }
    }
}

// Dialogs
@Composable
fun EditProductionDialog(
    production: ProductionEntity,
    onDismiss: () -> Unit,
    onSave: (ProductionEntity) -> Unit
) {
    var title by remember { mutableStateOf(production.title) }
    var genre by remember { mutableStateOf(production.genre) }
    var language by remember { mutableStateOf(production.language) }
    var logline by remember { mutableStateOf(production.logline) }
    var budgetStr by remember { mutableStateOf(production.budget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Production Details", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = genre, onValueChange = { genre = it }, label = { Text("Genre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = language, onValueChange = { language = it }, label = { Text("Language") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = logline, onValueChange = { logline = it }, label = { Text("Logline") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                OutlinedTextField(value = budgetStr, onValueChange = { budgetStr = it }, label = { Text("Budget") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        production.copy(
                            title = title,
                            genre = genre,
                            language = language,
                            logline = logline,
                            budget = budgetStr.toDoubleOrNull() ?: production.budget
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddIdeaDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, premise: String, logline: String, genre: String, tone: String, theme: String, b: String, m: String, e: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var premise by remember { mutableStateOf("") }
    var logline by remember { mutableStateOf("") }
    var act1 by remember { mutableStateOf("") }
    var act2 by remember { mutableStateOf("") }
    var act3 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Story Idea & Beats", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Idea Title *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = premise, onValueChange = { premise = it }, label = { Text("Premise Concept") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(value = logline, onValueChange = { logline = it }, label = { Text("Logline") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = act1, onValueChange = { act1 = it }, label = { Text("Act 1 (Beginning Beat)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = act2, onValueChange = { act2 = it }, label = { Text("Act 2 (Middle Beat)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = act3, onValueChange = { act3 = it }, label = { Text("Act 3 (Resolution Beat)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, premise, logline, "", "", "", act1, act2, act3)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Idea")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddCharacterDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, roleType: String, age: String, goal: String, conflict: String, dialogue: String, actor: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var roleType by remember { mutableStateOf("Protagonist") }
    var age by remember { mutableStateOf("25-35") }
    var goal by remember { mutableStateOf("") }
    var conflict by remember { mutableStateOf("") }
    var dialogue by remember { mutableStateOf("") }
    var actor by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Character Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Character Name *") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Protagonist", "Antagonist", "Supporting", "Cameo").forEach { type ->
                        FilterChip(
                            selected = roleType == type,
                            onClick = { roleType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age Range") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = goal, onValueChange = { goal = it }, label = { Text("Dramatic Goal") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = conflict, onValueChange = { conflict = it }, label = { Text("Internal / External Conflict") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dialogue, onValueChange = { dialogue = it }, label = { Text("Dialogue Voice / Dialect") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = actor, onValueChange = { actor = it }, label = { Text("Assigned Actor (Optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, roleType, age, goal, conflict, dialogue, actor)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Character")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
