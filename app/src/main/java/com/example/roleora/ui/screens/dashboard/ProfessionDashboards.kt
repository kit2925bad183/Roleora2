package com.example.roleora.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.PolishPrimaryLight
import com.example.roleora.ui.theme.TealAccent

// ============================================================================
// 1. MOVIE DIRECTOR WORKSPACE DASHBOARD
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirectorDashboard(
    role: RoleEntity,
    records: List<ProfessionRecordEntity>,
    onOpenCreate: (String) -> Unit,
    onRecordClick: (ProfessionRecordEntity) -> Unit,
    onQuickUpdateRecord: ((ProfessionRecordEntity) -> Unit)? = null
) {
    val scenes = records.filter { it.recordCategory == "SCREENPLAY" }
    val shots = records.filter { it.recordCategory == "SHOT" }
    val breakdowns = records.filter { it.recordCategory == "BREAKDOWN" }

    val totalEstMinutes = scenes.sumOf { it.numericValue2 }.toInt()
    val totalRecordedTakes = shots.sumOf { it.numericValue2 }.toInt()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Hero Production Command Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "ACTIVE PRODUCTION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Principal Photography",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PolishGreen.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PolishGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ON SCHEDULE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolishGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${role.displayName} Feature Film",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Specialization: ${role.specialisation} • Script Breakdown & Lens Registry",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cinematic Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(title = "Scenes", value = "${scenes.size}", color = PolishPrimary, modifier = Modifier.weight(1f))
                    MetricBox(title = "Est. Runtime", value = "${totalEstMinutes}m", color = TealAccent, modifier = Modifier.weight(1f))
                    MetricBox(title = "Shots Logged", value = "${shots.size}", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                    MetricBox(title = "Takes Done", value = "$totalRecordedTakes", color = PolishGreen, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Director Actions Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionShortcutButton(
                title = "+ Scene",
                icon = Icons.Default.Movie,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_SCENE") }

            ActionShortcutButton(
                title = "+ Camera Shot",
                icon = Icons.Default.CameraAlt,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_SHOT") }

            ActionShortcutButton(
                title = "+ Breakdown",
                icon = Icons.Default.Bookmark,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_BREAKDOWN") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Screenplay Breakdown Section
        SectionHeader(title = "Screenplay Scenes (${scenes.size})", actionText = "+ New Scene") {
            onOpenCreate("NEW_SCENE")
        }

        if (scenes.isEmpty()) {
            EmptyCard(message = "No scenes registered in script. Add scene headings, synopsis, and duration.")
        } else {
            scenes.forEach { scene ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(scene) }
                        .testTag("scene_card_${scene.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = scene.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "${scene.numericValue2.toInt()} MINS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = scene.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // If details json contains extra fields
                        val cast = extractValue(scene.detailsJson, "fieldA")
                        val props = extractValue(scene.detailsJson, "fieldB")
                        if (!cast.isNullOrBlank() || !props.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!cast.isNullOrBlank()) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                        Text(text = "🎭 Cast: $cast", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                if (!props.isNullOrBlank()) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                        Text(text = "📦 Props: $props", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Camera Shots & Takes Section
        SectionHeader(title = "Camera Shot Planner & Takes (${shots.size})", actionText = "+ Plan Shot") {
            onOpenCreate("NEW_SHOT")
        }

        if (shots.isEmpty()) {
            EmptyCard(message = "No camera shots configured. Define focal length, shot size, and track takes.")
        } else {
            shots.forEach { shot ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(shot) }
                        .testTag("shot_card_${shot.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = shot.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = shot.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🎬 ${shot.numericValue2.toInt()} Takes Logged",
                                style = MaterialTheme.typography.labelSmall,
                                color = PolishGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Quick "+1 Take" logger
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clickable {
                                onQuickUpdateRecord?.invoke(shot.copy(numericValue2 = shot.numericValue2 + 1))
                            }
                        ) {
                            Text(
                                text = "+ Take",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 2. COLLEGE STUDENT WORKSPACE DASHBOARD
// ============================================================================
@Composable
fun StudentDashboard(
    role: RoleEntity,
    records: List<ProfessionRecordEntity>,
    onOpenCreate: (String) -> Unit,
    onRecordClick: (ProfessionRecordEntity) -> Unit,
    onQuickUpdateRecord: ((ProfessionRecordEntity) -> Unit)? = null
) {
    val subjects = records.filter { it.recordCategory == "SUBJECT" }
    val assignments = records.filter { it.recordCategory == "ASSIGNMENT" }
    val exams = records.filter { it.recordCategory == "EXAM" }

    val totalAttended = subjects.sumOf { it.numericValue1 }
    val totalClasses = subjects.sumOf { it.numericValue2 }
    val overallAttendancePct = if (totalClasses > 0) (totalAttended / totalClasses) * 100 else 85.0
    val isLowAttendance = overallAttendancePct < 75.0

    Column(modifier = Modifier.fillMaxWidth()) {
        // Attendance & CGPA Master Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACADEMIC STANDING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isLowAttendance) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            if (isLowAttendance) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = if (isLowAttendance) "Critical: <75% Threshold" else "Attendance Healthy (≥75%)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isLowAttendance) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${overallAttendancePct.toInt()}% Overall",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isLowAttendance) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${totalAttended.toInt()} of ${totalClasses.toInt()} total classes attended",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Target CGPA", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "8.9 / 10", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PolishPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (overallAttendancePct / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isLowAttendance) MaterialTheme.colorScheme.error else PolishPrimary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Academic Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionShortcutButton(
                title = "+ Course / Subject",
                icon = Icons.Default.School,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_SUBJECT") }

            ActionShortcutButton(
                title = "+ Assignment",
                icon = Icons.Default.Bookmark,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_ASSIGNMENT") }

            ActionShortcutButton(
                title = "+ Exam",
                icon = Icons.Default.Event,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_EXAM") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Course List with Interactive Attendance Increment Logger!
        SectionHeader(title = "Enrolled Courses & Quick Attendance", actionText = "+ Course") {
            onOpenCreate("NEW_SUBJECT")
        }

        if (subjects.isEmpty()) {
            EmptyCard(message = "No subjects configured. Add semester subjects to track attendance limits.")
        } else {
            subjects.forEach { sub ->
                val subPct = if (sub.numericValue2 > 0) (sub.numericValue1 / sub.numericValue2) * 100 else 0.0
                val isSubLow = subPct < 75.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(sub) }
                        .testTag("subject_card_${sub.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isSubLow) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sub.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = sub.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSubLow) MaterialTheme.colorScheme.errorContainer else PolishGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${subPct.toInt()}% (${sub.numericValue1.toInt()}/${sub.numericValue2.toInt()})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSubLow) MaterialTheme.colorScheme.onErrorContainer else PolishGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Log Attended Shortcut
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quick Log Today: ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.clickable {
                                    onQuickUpdateRecord?.invoke(
                                        sub.copy(
                                            numericValue1 = sub.numericValue1 + 1,
                                            numericValue2 = sub.numericValue2 + 1
                                        )
                                    )
                                }
                            ) {
                                Text(
                                    text = "Present (+1)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    onQuickUpdateRecord?.invoke(
                                        sub.copy(
                                            numericValue2 = sub.numericValue2 + 1
                                        )
                                    )
                                }
                            ) {
                                Text(
                                    text = "Absent (0)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Assignments & Deadlines Section
        SectionHeader(title = "Assignments & Deadlines (${assignments.size})", actionText = "+ Assignment") {
            onOpenCreate("NEW_ASSIGNMENT")
        }

        if (assignments.isEmpty()) {
            EmptyCard(message = "No upcoming assignments.")
        } else {
            assignments.forEach { assign ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(assign) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = assign.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = assign.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = assign.stage,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 3. SOFTWARE DEVELOPER WORKSPACE DASHBOARD
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeveloperDashboard(
    role: RoleEntity,
    records: List<ProfessionRecordEntity>,
    onOpenCreate: (String) -> Unit,
    onRecordClick: (ProfessionRecordEntity) -> Unit,
    onQuickUpdateRecord: ((ProfessionRecordEntity) -> Unit)? = null
) {
    val tasks = records.filter { it.recordCategory == "KANBAN" }
    val snippets = records.filter { it.recordCategory == "SNIPPET" }
    val bugs = records.filter { it.recordCategory == "BUG" }

    val clipboardManager = LocalClipboardManager.current
    var copiedSnippetId by remember { mutableStateOf<String?>(null) }
    var selectedKanbanFilter by remember { mutableStateOf("All") }

    val displayedTasks = if (selectedKanbanFilter == "All") tasks else tasks.filter { it.stage.equals(selectedKanbanFilter, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Sprint Master Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPRINT VELOCITY & METRICS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Sprint #14 Active",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${role.displayName} Architecture",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Specialization: ${role.specialisation} • Local-First Room Repository",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(title = "Backlog", value = "${tasks.count { it.stage == "Backlog" }}", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    MetricBox(title = "In Progress", value = "${tasks.count { it.stage == "In Progress" }}", color = PolishPrimary, modifier = Modifier.weight(1f))
                    MetricBox(title = "Review", value = "${tasks.count { it.stage == "Code Review" }}", color = TealAccent, modifier = Modifier.weight(1f))
                    MetricBox(title = "Completed", value = "${tasks.count { it.stage == "Done" }}", color = PolishGreen, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Developer Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionShortcutButton(
                title = "+ Sprint Story",
                icon = Icons.Default.Code,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_TASK") }

            ActionShortcutButton(
                title = "+ Code Snippet",
                icon = Icons.Default.ContentCopy,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_SNIPPET") }

            ActionShortcutButton(
                title = "+ Bug Issue",
                icon = Icons.Default.BugReport,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_BUG") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Interactive Kanban Board Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sprint Kanban Board",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.clickable { onOpenCreate("NEW_TASK") }
            ) {
                Text(
                    text = "+ Story",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Kanban Filter Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Backlog", "In Progress", "Code Review", "Done").forEach { filter ->
                val isSelected = selectedKanbanFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedKanbanFilter = filter },
                    label = { Text(filter) },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (displayedTasks.isEmpty()) {
            EmptyCard(message = "No tasks found in stage '$selectedKanbanFilter'.")
        } else {
            displayedTasks.forEach { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(task) }
                        .testTag("task_card_${task.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (task.stage) {
                                    "Done" -> PolishGreen.copy(alpha = 0.2f)
                                    "In Progress" -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = task.stage,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = when (task.stage) {
                                        "Done" -> PolishGreen
                                        "In Progress" -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Quick Stage Progression Action
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            val nextStage = when (task.stage) {
                                "Backlog" -> "In Progress"
                                "In Progress" -> "Code Review"
                                "Code Review" -> "Done"
                                else -> null
                            }
                            if (nextStage != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable {
                                        onQuickUpdateRecord?.invoke(task.copy(stage = nextStage))
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Move to $nextStage", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Code Snippets Vault
        SectionHeader(title = "Code Snippet Vault (${snippets.size})", actionText = "+ Snippet") {
            onOpenCreate("NEW_SNIPPET")
        }

        if (snippets.isEmpty()) {
            EmptyCard(message = "No code snippets saved.")
        } else {
            snippets.forEach { snip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(snip) }
                        .testTag("snippet_card_${snip.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = snip.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(text = snip.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(snip.title + "\n" + snip.subtitle))
                                    copiedSnippetId = snip.id
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (copiedSnippetId == snip.id) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = if (copiedSnippetId == snip.id) PolishGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 4. PHOTOGRAPHER WORKSPACE DASHBOARD
// ============================================================================
@Composable
fun PhotographerDashboard(
    role: RoleEntity,
    records: List<ProfessionRecordEntity>,
    onOpenCreate: (String) -> Unit,
    onRecordClick: (ProfessionRecordEntity) -> Unit,
    onQuickUpdateRecord: ((ProfessionRecordEntity) -> Unit)? = null
) {
    val bookings = records.filter { it.recordCategory == "BOOKING" }
    val shotLists = records.filter { it.recordCategory == "SHOT_LIST" }
    val equipment = records.filter { it.recordCategory == "EQUIPMENT" }

    val totalBookingsRevenue = bookings.sumOf { it.numericValue1 }
    val totalAdvanceCollected = bookings.sumOf { it.numericValue2 }
    val pendingBalance = totalBookingsRevenue - totalAdvanceCollected

    Column(modifier = Modifier.fillMaxWidth()) {
        // Studio Revenue & Shoot Command Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STUDIO REVENUE & BOOKINGS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PolishGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Peak Season",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PolishGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${role.displayName} Visuals",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Specialization: ${role.specialisation} • Shoot Logistics & Equipment Health",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(title = "Total Invoiced", value = "₹${totalBookingsRevenue.toInt()}", color = PolishPrimary, modifier = Modifier.weight(1f))
                    MetricBox(title = "Collected", value = "₹${totalAdvanceCollected.toInt()}", color = PolishGreen, modifier = Modifier.weight(1f))
                    MetricBox(title = "Pending", value = "₹${pendingBalance.toInt()}", color = AmberWarning, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Photography Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionShortcutButton(
                title = "+ Client Shoot",
                icon = Icons.Default.CameraAlt,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_BOOKING") }

            ActionShortcutButton(
                title = "+ Shot List",
                icon = Icons.Default.Bookmark,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_SHOT_LIST") }

            ActionShortcutButton(
                title = "+ Gear Log",
                icon = Icons.Default.Security,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_EQUIPMENT") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Client Bookings Section
        SectionHeader(title = "Client Bookings & Shoots (${bookings.size})", actionText = "+ Booking") {
            onOpenCreate("NEW_BOOKING")
        }

        if (bookings.isEmpty()) {
            EmptyCard(message = "No client bookings scheduled.")
        } else {
            bookings.forEach { book ->
                val advancePct = if (book.numericValue1 > 0) (book.numericValue2 / book.numericValue1).toFloat() else 0f
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(book) }
                        .testTag("booking_card_${book.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = "₹${book.numericValue1.toInt()}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolishGreen
                            )
                        }

                        Text(text = book.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Advance: ₹${book.numericValue2.toInt()} (${(advancePct * 100).toInt()}%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                Text(
                                    text = book.stage,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { advancePct },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = PolishGreen,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Gear & Equipment Vault
        SectionHeader(title = "Equipment & Camera Vault (${equipment.size})", actionText = "+ Gear") {
            onOpenCreate("NEW_EQUIPMENT")
        }

        if (equipment.isEmpty()) {
            EmptyCard(message = "No equipment logged.")
        } else {
            equipment.forEach { gear ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(gear) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = gear.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = gear.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 5. FARMER WORKSPACE DASHBOARD
// ============================================================================
@Composable
fun FarmerDashboard(
    role: RoleEntity,
    records: List<ProfessionRecordEntity>,
    onOpenCreate: (String) -> Unit,
    onRecordClick: (ProfessionRecordEntity) -> Unit,
    onQuickUpdateRecord: ((ProfessionRecordEntity) -> Unit)? = null
) {
    val crops = records.filter { it.recordCategory == "CROP" }
    val treatments = records.filter { it.recordCategory == "TREATMENT" }
    val irrigations = records.filter { it.recordCategory == "IRRIGATION" }

    val totalAcres = crops.sumOf { it.numericValue1 }
    val totalEstYield = crops.sumOf { it.numericValue1 * it.numericValue2 }.toInt()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Farm Operations Master Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AGRICULTURE & LAND OPERATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PolishGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Kharif Season",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PolishGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${role.displayName} Farm Estate",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Specialization: ${role.specialisation} • Soil Organic Carbon & Irrigation Cycles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(title = "Cultivated", value = "$totalAcres Ac", color = PolishPrimary, modifier = Modifier.weight(1f))
                    MetricBox(title = "Plots Active", value = "${crops.size}", color = TealAccent, modifier = Modifier.weight(1f))
                    MetricBox(title = "Est. Yield", value = "${totalEstYield}kg", color = PolishGreen, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Agriculture Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionShortcutButton(
                title = "+ Crop Plot",
                icon = Icons.Default.Eco,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_CROP") }

            ActionShortcutButton(
                title = "+ Irrigation",
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_IRRIGATION") }

            ActionShortcutButton(
                title = "+ Treatment",
                icon = Icons.Default.Security,
                modifier = Modifier.weight(1f)
            ) { onOpenCreate("NEW_TREATMENT") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Crop Parcels Section
        SectionHeader(title = "Crops & Land Parcels (${crops.size})", actionText = "+ Add Crop") {
            onOpenCreate("NEW_CROP")
        }

        if (crops.isEmpty()) {
            EmptyCard(message = "No crop parcels recorded. Add land plot with sowing date and acreage.")
        } else {
            crops.forEach { crop ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(crop) }
                        .testTag("crop_card_${crop.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = crop.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${crop.numericValue1} Acres",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = crop.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(shape = RoundedCornerShape(4.dp), color = PolishGreen.copy(alpha = 0.15f)) {
                                Text(
                                    text = "Stage: ${crop.stage}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PolishGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Est: ${crop.numericValue2.toInt()} kg/ac",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Treatments & Soil Health Section
        SectionHeader(title = "Treatments & Bio-Enrichers (${treatments.size})", actionText = "+ Log Treatment") {
            onOpenCreate("NEW_TREATMENT")
        }

        if (treatments.isEmpty()) {
            EmptyCard(message = "No soil treatments or bio-fertilizers logged.")
        } else {
            treatments.forEach { trt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRecordClick(trt) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = trt.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = trt.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// COMMON DASHBOARD WIDGETS
// ============================================================================

@Composable
fun ActionShortcutButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.clickable { onAction() }
        ) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
fun EmptyCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun extractValue(json: String?, key: String): String? {
    if (json.isNullOrBlank()) return null
    return try {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\"".toRegex()
        pattern.find(json)?.groupValues?.getOrNull(1)
    } catch (e: Exception) {
        null
    }
}
