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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roleora.data.model.DirectorSpecialisation
import com.example.roleora.data.model.ProductionEntity
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.SceneStatus
import com.example.roleora.data.model.ShotStatus
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.PolishPrimaryLight
import com.example.roleora.ui.theme.TealAccent
import com.example.roleora.ui.viewmodel.DirectorSection
import com.example.roleora.ui.viewmodel.DirectorViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirectorWorkspaceScreen(
    role: RoleEntity,
    viewModel: DirectorViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(role.id) {
        viewModel.initializeForRole(role.id)
    }

    val productions by viewModel.productions.collectAsStateWithLifecycle()
    val selectedProduction by viewModel.selectedProduction.collectAsStateWithLifecycle()
    val activeSection by viewModel.activeDirectorSection.collectAsStateWithLifecycle()

    var showCreateProductionDialog by remember { mutableStateOf(false) }
    var showProductionMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // =====================================================================
        // 1. TOP CINEMATIC HEADER BAR
        // =====================================================================
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Active Production Selector Dropdown
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PolishPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MovieFilter,
                                    contentDescription = "Film Project",
                                    tint = PolishPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showProductionMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = selectedProduction?.title ?: "Select Production",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "▼",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${selectedProduction?.format ?: "Project"} • ${selectedProduction?.currentStage ?: "Idea Stage"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            DropdownMenu(
                                expanded = showProductionMenu,
                                onDismissRequest = { showProductionMenu = false }
                            ) {
                                productions.forEach { prod ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = prod.title,
                                                    fontWeight = if (prod.id == selectedProduction?.id) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(
                                                    text = "${prod.format} • ${prod.currentStage}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectProduction(prod.id)
                                            showProductionMenu = false
                                        },
                                        leadingIcon = {
                                            if (prod.id == selectedProduction?.id) {
                                                Icon(Icons.Default.Check, contentDescription = "Active", tint = PolishPrimary)
                                            }
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("+ Create New Production", color = PolishPrimary, fontWeight = FontWeight.SemiBold) },
                                    onClick = {
                                        showProductionMenu = false
                                        showCreateProductionDialog = true
                                    }
                                )
                            }
                        }
                    }

                    // Right: Quick Actions & Stage Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        selectedProduction?.let { prod ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = EmeraldGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = prod.currentStage.uppercase(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(
                            onClick = { showCreateProductionDialog = true },
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("btn_new_production"),
                            colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Project", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =============================================================
                // 2. COMPLETE 24-SECTION DIRECTOR NAVIGATION RAIL / TABS
                // =============================================================
                val sections = DirectorSection.entries
                ScrollableTabRow(
                    selectedTabIndex = activeSection.ordinal,
                    edgePadding = 0.dp,
                    divider = {},
                    containerColor = Color.Transparent
                ) {
                    sections.forEach { section ->
                        val isSelected = activeSection == section
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectSection(section) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = getIconForSection(section),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) PolishPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = section.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) PolishPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        // =====================================================================
        // 3. MAIN SECTION CONTENT SWITCHER
        // =====================================================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (activeSection) {
                DirectorSection.DASHBOARD -> DirectorDashboardView(role, viewModel)
                DirectorSection.PRODUCTIONS -> DirectorProductionsView(viewModel)
                DirectorSection.IDEAS -> DirectorIdeasView(viewModel)
                DirectorSection.SCREENPLAY -> DirectorScreenplayEditorView(viewModel)
                DirectorSection.CHARACTERS -> DirectorCharactersView(viewModel)
                DirectorSection.BREAKDOWN -> DirectorBreakdownView(viewModel)
                DirectorSection.SCENES -> DirectorScenesView(viewModel)
                DirectorSection.STORYBOARDS -> DirectorStoryboardsView(viewModel)
                DirectorSection.SHOT_LISTS -> DirectorShotsView(viewModel)
                DirectorSection.CAST_CREW -> DirectorCastCrewView(viewModel)
                DirectorSection.AUDITIONS -> DirectorAuditionsView(viewModel)
                DirectorSection.LOCATIONS -> DirectorLocationsView(viewModel)
                DirectorSection.REHEARSALS -> DirectorRehearsalsView(viewModel)
                DirectorSection.SHOOTING_SCHEDULE -> DirectorScheduleView(viewModel)
                DirectorSection.CALL_SHEETS -> DirectorCallSheetsView(viewModel)
                DirectorSection.CONTINUITY -> DirectorContinuityView(viewModel)
                DirectorSection.FOOTAGE_TAKES -> DirectorTakesView(viewModel)
                DirectorSection.PRODUCTION_DIARY -> DirectorProductionDiaryView(viewModel)
                DirectorSection.EDITING_REVIEW -> DirectorEditingReviewView(viewModel)
                DirectorSection.SOUND_MUSIC -> DirectorSoundMusicView(viewModel)
                DirectorSection.BUDGET -> DirectorBudgetView(viewModel)
                DirectorSection.DOCUMENTS -> DirectorDocumentsView(viewModel)
                DirectorSection.REPORTS -> DirectorReportsView(viewModel)
                DirectorSection.DIRECTOR_SETTINGS -> DirectorSettingsView(role, viewModel)
            }
        }
    }

    // Modal Create Production Dialog
    if (showCreateProductionDialog) {
        CreateProductionDialog(
            onDismiss = { showCreateProductionDialog = false },
            onCreate = { title, format, genre, language, logline, budget, currency ->
                viewModel.createProduction(title, format, genre, language, logline, budget, currency)
                showCreateProductionDialog = false
            }
        )
    }
}

// ============================================================================
// SECTION 1: DIRECTOR DASHBOARD (REAL STORED METRICS & WORKFLOW METRICS)
// ============================================================================
@Composable
fun DirectorDashboardView(
    role: RoleEntity,
    viewModel: DirectorViewModel
) {
    val selectedProduction by viewModel.selectedProduction.collectAsStateWithLifecycle()
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()
    val shots by viewModel.shots.collectAsStateWithLifecycle()
    val budgetItems by viewModel.budgetItems.collectAsStateWithLifecycle()
    val shootingDays by viewModel.shootingDays.collectAsStateWithLifecycle()
    val rehearsals by viewModel.rehearsals.collectAsStateWithLifecycle()
    val castCrew by viewModel.castCrewMembers.collectAsStateWithLifecycle()
    val takes by viewModel.takes.collectAsStateWithLifecycle()
    val reviews by viewModel.editingReviews.collectAsStateWithLifecycle()

    val totalScenes = scenes.size
    val readyScenes = scenes.count { it.status == SceneStatus.READY_FOR_SHOOTING.name }
    val completedScenes = scenes.count { it.status == SceneStatus.COMPLETED.name }

    val totalShots = shots.size
    val completedShots = shots.count { it.status == ShotStatus.COMPLETED.name }
    val pendingShots = shots.count { it.status == ShotStatus.PLANNED.name || it.status == ShotStatus.READY.name }

    val totalPlannedBudget = selectedProduction?.budget ?: budgetItems.sumOf { it.plannedAmount }
    val totalActualExpense = budgetItems.sumOf { it.actualExpense }
    val budgetProgress = if (totalPlannedBudget > 0) (totalActualExpense / totalPlannedBudget).toFloat().coerceIn(0f, 1f) else 0f

    val conflicts = castCrew.filter { it.availabilityStatus.equals("Conflict", ignoreCase = true) }
    val fiveStarTakes = takes.count { it.directorRating == 5 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedProduction?.title ?: "No Active Production",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${selectedProduction?.genre ?: "Film Project"} • ${selectedProduction?.language ?: "Multilingual"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PolishPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = selectedProduction?.currentStage ?: "In Development",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = PolishPrimary
                            )
                        }
                    }

                    if (!selectedProduction?.logline.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"${selectedProduction?.logline}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Budget Progress Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Budget Utilised: ₹${String.format("%,.0f", totalActualExpense)} / ₹${String.format("%,.0f", totalPlannedBudget)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(budgetProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (budgetProgress > 0.9f) AmberWarning else PolishPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { budgetProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (budgetProgress > 0.9f) AmberWarning else PolishPrimary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        // Live Production Metric Counters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Scenes Ready",
                    value = "$readyScenes / $totalScenes",
                    subtitle = "$completedScenes shot",
                    icon = Icons.Default.Theaters,
                    tint = EmeraldGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectSection(DirectorSection.SCENES) }
                )
                MetricCard(
                    title = "Shots Pending",
                    value = "$pendingShots",
                    subtitle = "$completedShots completed",
                    icon = Icons.Default.Videocam,
                    tint = TealAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectSection(DirectorSection.SHOT_LISTS) }
                )
                MetricCard(
                    title = "Takes Logged",
                    value = "${takes.size}",
                    subtitle = "$fiveStarTakes ★★★★★",
                    icon = Icons.Default.VideoLibrary,
                    tint = AmberWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectSection(DirectorSection.FOOTAGE_TAKES) }
                )
            }
        }

        // Cast Conflict Alerts
        if (conflicts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Alert", tint = AmberWarning)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cast Availability Conflict Detected",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                            Text(
                                text = "${conflicts.first().name} has marked a schedule conflict.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TextButton(onClick = { viewModel.selectSection(DirectorSection.CAST_CREW) }) {
                            Text("Resolve", color = AmberWarning, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Production Jumps
        item {
            Text(
                text = "Professional Workstation Modules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Screenplay Editor",
                        subtitle = "Structured draft & dialogue",
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectSection(DirectorSection.SCREENPLAY) }
                    )
                    QuickActionCard(
                        title = "Script Breakdown",
                        subtitle = "Cast, props, VFX & gear",
                        icon = Icons.Default.AutoFixHigh,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectSection(DirectorSection.BREAKDOWN) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Shooting Schedule",
                        subtitle = "${shootingDays.size} shooting days planned",
                        icon = Icons.Default.Event,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectSection(DirectorSection.SHOOTING_SCHEDULE) }
                    )
                    QuickActionCard(
                        title = "Call Sheets",
                        subtitle = "Generate & approve daily calls",
                        icon = Icons.Default.Assignment,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectSection(DirectorSection.CALL_SHEETS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Storyboards",
                        subtitle = "Shot sketches & blocking",
                        icon = Icons.Default.Brush,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectSection(DirectorSection.STORYBOARDS) }
                    )
                    QuickActionCard(
                        title = "Editing Review",
                        subtitle = "${reviews.count { it.status == "Open" }} open cut notes",
                        icon = Icons.Default.MovieCreation,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectSection(DirectorSection.EDITING_REVIEW) }
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = tint
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = PolishPrimary.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
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
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun CreateProductionDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, format: String, genre: String, language: String, logline: String, budget: Double, currency: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf(DirectorSpecialisation.FEATURE_FILM.displayName) }
    var genre by remember { mutableStateOf("Drama / Thriller") }
    var language by remember { mutableStateOf("English / Tamil") }
    var logline by remember { mutableStateOf("") }
    var budgetStr by remember { mutableStateOf("5000000") }
    var currency by remember { mutableStateOf("INR") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Production", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Production Title *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Format", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DirectorSpecialisation.entries.forEach { format ->
                        FilterChip(
                            selected = selectedFormat == format.displayName,
                            onClick = { selectedFormat = format.displayName },
                            label = { Text(format.displayName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = language,
                    onValueChange = { language = it },
                    label = { Text("Language") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = logline,
                    onValueChange = { logline = it },
                    label = { Text("Logline (1-2 sentences)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = budgetStr,
                        onValueChange = { budgetStr = it },
                        label = { Text("Budget") },
                        modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Currency") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(
                            title,
                            selectedFormat,
                            genre,
                            language,
                            logline,
                            budgetStr.toDoubleOrNull() ?: 5000000.0,
                            currency
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Create Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun getIconForSection(section: DirectorSection): ImageVector = when (section) {
    DirectorSection.DASHBOARD -> Icons.Default.Dashboard
    DirectorSection.PRODUCTIONS -> Icons.Default.MovieFilter
    DirectorSection.IDEAS -> Icons.Default.Lightbulb
    DirectorSection.SCREENPLAY -> Icons.Default.Description
    DirectorSection.CHARACTERS -> Icons.Default.Person
    DirectorSection.BREAKDOWN -> Icons.Default.AutoFixHigh
    DirectorSection.SCENES -> Icons.Default.Theaters
    DirectorSection.STORYBOARDS -> Icons.Default.Brush
    DirectorSection.SHOT_LISTS -> Icons.Default.Videocam
    DirectorSection.CAST_CREW -> Icons.Default.Groups
    DirectorSection.AUDITIONS -> Icons.Default.Badge
    DirectorSection.LOCATIONS -> Icons.Default.Place
    DirectorSection.REHEARSALS -> Icons.Default.RecordVoiceOver
    DirectorSection.SHOOTING_SCHEDULE -> Icons.Default.Event
    DirectorSection.CALL_SHEETS -> Icons.Default.Assignment
    DirectorSection.CONTINUITY -> Icons.Default.CheckCircle
    DirectorSection.FOOTAGE_TAKES -> Icons.Default.VideoLibrary
    DirectorSection.PRODUCTION_DIARY -> Icons.Default.Book
    DirectorSection.EDITING_REVIEW -> Icons.Default.MovieCreation
    DirectorSection.SOUND_MUSIC -> Icons.Default.MusicNote
    DirectorSection.BUDGET -> Icons.Default.AccountBalanceWallet
    DirectorSection.DOCUMENTS -> Icons.Default.Folder
    DirectorSection.REPORTS -> Icons.Default.Assessment
    DirectorSection.DIRECTOR_SETTINGS -> Icons.Default.Tune
}
