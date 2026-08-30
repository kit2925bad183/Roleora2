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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roleora.data.model.BudgetItemEntity
import com.example.roleora.data.model.EditingReviewEntity
import com.example.roleora.data.model.SoundMusicEntity
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import com.example.roleora.ui.viewmodel.DirectorViewModel

// ============================================================================
// PRODUCTION DIARY VIEW
// ============================================================================
@Composable
fun DirectorProductionDiaryView(
    viewModel: DirectorViewModel
) {
    val takes by viewModel.takes.collectAsStateWithLifecycle()
    val shootingDays by viewModel.shootingDays.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Director Production Diary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Daily creative decisions, set observations and problem solving logs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Day 1 Set Log: Heritage Archives Basement Vault", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Achieved 12 planned setups. The vintage Nagra magnetic tape player worked seamlessly with the 50Hz lighting pulse simulation. Ananya's monologue in Scene 1 Take 4 had extraordinary emotional tension. Wrap called on time at 18:45.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Footage Recorded: 48 GB (ProRes 4444 XQ)", fontSize = 11.sp, color = PolishPrimary, fontWeight = FontWeight.SemiBold)
                        Text("No safety incidents", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// EDITING REVIEW & TIMECODE FEEDBACK VIEW
// ============================================================================
@Composable
fun DirectorEditingReviewView(
    viewModel: DirectorViewModel
) {
    val reviews by viewModel.editingReviews.collectAsStateWithLifecycle()
    var showAddReviewDialog by remember { mutableStateOf(false) }

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
                    Text("Editing & Post-Production Review", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${reviews.size} timecoded director cut notes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddReviewDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Cut Note", fontSize = 12.sp)
                }
            }
        }

        if (reviews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MovieCreation, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No editing notes recorded yet", fontWeight = FontWeight.Bold)
                        Text("Add timestamped feedback for editor: pacing, sound cues, and performance choice.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(reviews) { review ->
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
                                color = TealAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = review.timestampCode,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = TealAccent,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scene ${review.sceneNumber} • ${review.commentCategory}", fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = { viewModel.deleteEditingReview(review.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(review.commentText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showAddReviewDialog) {
        AddReviewDialog(
            onDismiss = { showAddReviewDialog = false },
            onSave = { tc, sc, cat, pri, text ->
                viewModel.saveEditingReview(tc, sc, cat, pri, text)
                showAddReviewDialog = false
            }
        )
    }
}

// ============================================================================
// SOUND & MUSIC VIEW
// ============================================================================
@Composable
fun DirectorSoundMusicView(
    viewModel: DirectorViewModel
) {
    val sounds by viewModel.soundItems.collectAsStateWithLifecycle()
    var showAddSoundDialog by remember { mutableStateOf(false) }

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
                    Text("Sound Design & Music Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${sounds.size} audio cues & licensing tracks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddSoundDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Audio Cue", fontSize = 12.sp)
                }
            }
        }

        items(sounds) { sound ->
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
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(sound.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("${sound.category} • ${sound.composerOrDesigner} • Status: ${sound.licensingStatus}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (sound.description.isNotBlank()) {
                            Text(sound.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    IconButton(onClick = { viewModel.deleteSoundItem(sound.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    if (showAddSoundDialog) {
        AddSoundDialog(
            onDismiss = { showAddSoundDialog = false },
            onSave = { title, cat, desc, comp, lic ->
                viewModel.saveSoundItem(title, cat, desc, comp, lic)
                showAddSoundDialog = false
            }
        )
    }
}

// ============================================================================
// BUDGET & EXPENSES VIEW
// ============================================================================
@Composable
fun DirectorBudgetView(
    viewModel: DirectorViewModel
) {
    val budgetItems by viewModel.budgetItems.collectAsStateWithLifecycle()
    var showAddBudgetDialog by remember { mutableStateOf(false) }

    val totalPlanned = budgetItems.sumOf { it.plannedAmount }
    val totalActual = budgetItems.sumOf { it.actualExpense }
    val variance = totalPlanned - totalActual

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
                    Text("Production Budget & Expense Ledger", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Planned: ₹${String.format("%,.0f", totalPlanned)} | Spent: ₹${String.format("%,.0f", totalActual)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddBudgetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Expense", fontSize = 12.sp)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (variance >= 0) EmeraldGreen.copy(alpha = 0.1f) else AmberWarning.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, if (variance >= 0) EmeraldGreen.copy(alpha = 0.3f) else AmberWarning.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (variance >= 0) "Budget Surplus Remaining" else "Budget Overrun",
                            fontWeight = FontWeight.Bold,
                            color = if (variance >= 0) EmeraldGreen else AmberWarning
                        )
                        Text("Variance: ₹${String.format("%,.0f", kotlin.math.abs(variance))}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = if (variance >= 0) EmeraldGreen else AmberWarning,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        items(budgetItems) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.itemTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = PolishPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = item.category,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolishPrimary
                                )
                            }
                        }
                        Text(
                            text = "Vendor: ${item.vendor} • Planned: ₹${String.format("%,.0f", item.plannedAmount)} • Actual: ₹${String.format("%,.0f", item.actualExpense)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (item.paymentStatus == "Paid") EmeraldGreen.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = item.paymentStatus,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.paymentStatus == "Paid") EmeraldGreen else AmberWarning
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { viewModel.deleteBudgetItem(item.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    if (showAddBudgetDialog) {
        AddBudgetItemDialog(
            onDismiss = { showAddBudgetDialog = false },
            onSave = { title, cat, planned, actual, vendor, status ->
                viewModel.saveBudgetItem(title, cat, planned, actual, vendor, status)
                showAddBudgetDialog = false
            }
        )
    }
}

// ============================================================================
// DOCUMENTS & CONTRACTS VIEW
// ============================================================================
@Composable
fun DirectorDocumentsView(
    viewModel: DirectorViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Production Documents & Clearances", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Legal contracts, location release agreements and guild paperwork", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("1. Location Release Form - Heritage Archives Vault", fontWeight = FontWeight.Bold)
                    Text("Executed on 2026-08-28 • Indemnity and security bond active", fontSize = 12.sp, color = EmeraldGreen)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("2. Principal Actor Agreement - Ananya Ramachandran (Maya)", fontWeight = FontWeight.Bold)
                    Text("Executed on 2026-08-20 • Guild terms agreed • 100% exclusivity", fontSize = 12.sp, color = EmeraldGreen)
                }
            }
        }
    }
}

// ============================================================================
// REPORTS & EXPORTS VIEW
// ============================================================================
@Composable
fun DirectorReportsView(
    viewModel: DirectorViewModel
) {
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()
    val shots by viewModel.shots.collectAsStateWithLifecycle()
    val budgetItems by viewModel.budgetItems.collectAsStateWithLifecycle()
    val members by viewModel.castCrewMembers.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    var copiedReportName by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Production Reports & Export Center", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Generate comprehensive production documents for producers and HODs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            ReportCard(
                title = "1. Full Script Breakdown & Requirements Report",
                description = "Complete department breakdown with props, costumes, VFX and lighting requirements for all ${scenes.size} scenes.",
                onCopy = {
                    val report = "PRODUCTION BREAKDOWN REPORT\nTotal Scenes: ${scenes.size}\n" + scenes.joinToString("\n") { "Scene ${it.sceneNumber}: ${it.heading} (${it.locationName})" }
                    clipboardManager.setText(AnnotatedString(report))
                    copiedReportName = "Script Breakdown"
                }
            )
        }

        item {
            ReportCard(
                title = "2. Master Cinematography Shot List Report",
                description = "Complete list of ${shots.size} setups grouped by camera lens, angle, and priority.",
                onCopy = {
                    val report = "MASTER SHOT LIST\n" + shots.joinToString("\n") { "Shot ${it.shotNumber} (${it.shotSize}, ${it.lens}): ${it.description}" }
                    clipboardManager.setText(AnnotatedString(report))
                    copiedReportName = "Master Shot List"
                }
            )
        }

        item {
            ReportCard(
                title = "3. Cast & Crew Call List Summary",
                description = "Emergency contact sheet and department roster for all ${members.size} personnel.",
                onCopy = {
                    val report = "CAST & CREW ROSTER\n" + members.joinToString("\n") { "${it.name} (${it.positionTitle}) - ${it.phone}" }
                    clipboardManager.setText(AnnotatedString(report))
                    copiedReportName = "Cast & Crew Roster"
                }
            )
        }

        item {
            ReportCard(
                title = "4. Production Budget Variance & Expense Ledger",
                description = "Financial summary comparing planned vs actual costs across departments.",
                onCopy = {
                    val report = "BUDGET VARIANCE REPORT\nTotal Items: ${budgetItems.size}\n" + budgetItems.joinToString("\n") { "${it.itemTitle} - Planned: ₹${it.plannedAmount}, Actual: ₹${it.actualExpense} (${it.paymentStatus})" }
                    clipboardManager.setText(AnnotatedString(report))
                    copiedReportName = "Budget Variance"
                }
            )
        }

        if (copiedReportName != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldGreen.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ $copiedReportName report copied to clipboard!",
                        modifier = Modifier.padding(12.dp),
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    title: String,
    description: String,
    onCopy: () -> Unit
) {
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
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Report", fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// Dialogs
@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSave: (tc: String, sceneNum: Int, cat: String, pri: String, text: String) -> Unit
) {
    var timestamp by remember { mutableStateOf("00:01:24") }
    var sceneNumStr by remember { mutableStateOf("1") }
    var category by remember { mutableStateOf("Pacing & Rhythm") }
    var noteText by remember { mutableStateOf("Cut 2 frames earlier when Maya turns toward the door.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Editing Cut Note", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = timestamp, onValueChange = { timestamp = it }, label = { Text("Timecode (e.g. 00:01:24)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sceneNumStr, onValueChange = { sceneNumStr = it }, label = { Text("Scene #") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Pacing, Performance, Sound, Color)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = noteText, onValueChange = { noteText = it }, label = { Text("Director Cut Note *") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        onSave(timestamp, sceneNumStr.toIntOrNull() ?: 1, category, "High", noteText)
                    }
                },
                enabled = noteText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Note")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddSoundDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, cat: String, desc: String, comp: String, lic: String) -> Unit
) {
    var title by remember { mutableStateOf("Base Theme (Tape Loop Reprise)") }
    var category by remember { mutableStateOf("Background Score") }
    var desc by remember { mutableStateOf("Atmospheric synth strings with analogue hiss") }
    var composer by remember { mutableStateOf("Music Director") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Sound / Music Cue", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Track Title *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Score, SFX, Ambience, Foley)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description & Mood") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = composer, onValueChange = { composer = it }, label = { Text("Composer / Sound Designer") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, category, desc, composer, "Original Composition - Secured")
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Cue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddBudgetItemDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, cat: String, planned: Double, actual: Double, vendor: String, status: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("CAMERA") }
    var plannedStr by remember { mutableStateOf("50000") }
    var actualStr by remember { mutableStateOf("45000") }
    var vendor by remember { mutableStateOf("Cinewave Rentals") }
    var status by remember { mutableStateOf("Paid") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Budget Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Item Title *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Budget Category") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = plannedStr, onValueChange = { plannedStr = it }, label = { Text("Planned (₹)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = actualStr, onValueChange = { actualStr = it }, label = { Text("Actual (₹)") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = vendor, onValueChange = { vendor = it }, label = { Text("Vendor / Payee") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Payment Status (Paid, Pending, Overdue)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            title,
                            category,
                            plannedStr.toDoubleOrNull() ?: 50000.0,
                            actualStr.toDoubleOrNull() ?: 45000.0,
                            vendor,
                            status
                        )
                    }
                },
                enabled = title.isNotBlank(),
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
