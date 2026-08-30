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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.roleora.data.model.AuditionEntity
import com.example.roleora.data.model.AuditionStatus
import com.example.roleora.data.model.CastCrewMemberEntity
import com.example.roleora.data.model.CharacterEntity
import com.example.roleora.data.model.LocationEntity
import com.example.roleora.data.model.RehearsalEntity
import com.example.roleora.ui.theme.AmberWarning
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import com.example.roleora.ui.viewmodel.DirectorViewModel

// ============================================================================
// CAST & CREW DIRECTORY VIEW
// ============================================================================
@Composable
fun DirectorCastCrewView(
    viewModel: DirectorViewModel
) {
    val members by viewModel.castCrewMembers.collectAsStateWithLifecycle()
    var showAddMemberDialog by remember { mutableStateOf(false) }

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
                    Text("Cast & Crew Directory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${members.size} personnel registered across departments", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddMemberDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Member", fontSize = 12.sp)
                }
            }
        }

        items(members) { member ->
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
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (member.memberType == "Cast") Icons.Default.Person else Icons.Default.Groups,
                                contentDescription = null,
                                tint = PolishPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (member.memberType == "Cast") EmeraldGreen.copy(alpha = 0.15f) else TealAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = member.positionTitle,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (member.memberType == "Cast") EmeraldGreen else TealAccent
                                )
                            }
                        }

                        Text(
                            text = "${member.characterNameOrDepartment} • ${member.phone} • ${member.email}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (member.availabilityStatus.isNotBlank()) {
                            Text(
                                text = "Availability: ${member.availabilityStatus}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (member.availabilityStatus.contains("Conflict", ignoreCase = true)) AmberWarning else EmeraldGreen
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.deleteMember(member.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onSave = { name, type, title, dept, phone, email, avail, restricted ->
                viewModel.saveCastCrewMember(name, type, title, dept, phone, email, avail, restricted)
                showAddMemberDialog = false
            }
        )
    }
}

// ============================================================================
// AUDITIONS EVALUATION PIPELINE
// ============================================================================
@Composable
fun DirectorAuditionsView(
    viewModel: DirectorViewModel
) {
    val auditions by viewModel.auditions.collectAsStateWithLifecycle()
    val characters by viewModel.characters.collectAsStateWithLifecycle()
    var showAddAuditionDialog by remember { mutableStateOf(false) }

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
                    Text("Casting & Auditions Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${auditions.size} audition recordings & callback ratings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddAuditionDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Audition", fontSize = 12.sp)
                }
            }
        }

        items(auditions) { aud ->
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
                        Column {
                            Text(aud.candidateName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("Contact: ${aud.contact}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { starIndex ->
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (starIndex < aud.rating) AmberWarning else MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { viewModel.deleteAudition(aud.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    if (aud.directorNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Director Notes: ${aud.directorNotes}", style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Audition Status Pipeline Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AuditionStatus.entries.forEach { status ->
                            val isSelected = aud.status == status.name
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateAuditionStatus(aud.id, status) },
                                label = { Text(status.label, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddAuditionDialog) {
        AddAuditionDialog(
            characters = characters,
            onDismiss = { showAddAuditionDialog = false },
            onSave = { charId, name, contact, rating, status, notes ->
                viewModel.saveAudition(charId, name, contact, rating, status, notes)
                showAddAuditionDialog = false
            }
        )
    }
}

// ============================================================================
// LOCATION MANAGEMENT & PERMITS
// ============================================================================
@Composable
fun DirectorLocationsView(
    viewModel: DirectorViewModel
) {
    val locations by viewModel.locations.collectAsStateWithLifecycle()
    var showAddLocDialog by remember { mutableStateOf(false) }

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
                    Text("Filming Locations & Tech Recce", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${locations.size} verified locations • Permits & Logistics", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddLocDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Location", fontSize = 12.sp)
                }
            }
        }

        items(locations) { loc ->
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
                                color = PolishPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = PolishPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(loc.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(loc.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (loc.permissionStatus == "Granted") EmeraldGreen.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Permit: ${loc.permissionStatus}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (loc.permissionStatus == "Granted") EmeraldGreen else AmberWarning
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Daily Cost: ₹${String.format("%,.0f", loc.dailyCost)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("Acoustics: ${loc.noiseLevel}", fontSize = 11.sp)
                            Text("Power: ${loc.powerAvailable}", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddLocDialog) {
        AddLocationDialog(
            onDismiss = { showAddLocDialog = false },
            onSave = { name, addr, contact, phone, cost, perm, noise, power ->
                viewModel.saveLocation(name, addr, contact, phone, cost, perm, noise, power)
                showAddLocDialog = false
            }
        )
    }
}

// ============================================================================
// REHEARSALS VIEW
// ============================================================================
@Composable
fun DirectorRehearsalsView(
    viewModel: DirectorViewModel
) {
    val rehearsals by viewModel.rehearsals.collectAsStateWithLifecycle()
    var showAddRehearsalDialog by remember { mutableStateOf(false) }

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
                    Text("Rehearsal & Table Reads", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${rehearsals.size} blocking & character dialogue sessions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showAddRehearsalDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Schedule Rehearsal", fontSize = 12.sp)
                }
            }
        }

        items(rehearsals) { reh ->
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
                        Text(reh.locationName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = { viewModel.deleteRehearsal(reh.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                    if (reh.objectives.isNotBlank()) {
                        Text("Objectives: ${reh.objectives}", style = MaterialTheme.typography.bodySmall)
                    }
                    if (reh.blockingNotes.isNotBlank()) {
                        Text("Blocking & Camera Movement: ${reh.blockingNotes}", style = MaterialTheme.typography.bodySmall, color = PolishPrimary)
                    }
                }
            }
        }
    }

    if (showAddRehearsalDialog) {
        AddRehearsalDialog(
            onDismiss = { showAddRehearsalDialog = false },
            onSave = { loc, obj, block, dial ->
                viewModel.saveRehearsal(loc, obj, block, dial)
                showAddRehearsalDialog = false
            }
        )
    }
}

// Dialogs
@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, memberType: String, title: String, dept: String, phone: String, email: String, avail: String, isRestricted: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var memberType by remember { mutableStateOf("Cast") }
    var title by remember { mutableStateOf("Lead Actor") }
    var department by remember { mutableStateOf("Principal Cast") }
    var phone by remember { mutableStateOf("+91 98400 12345") }
    var email by remember { mutableStateOf("actor@cinelab.in") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Cast / Crew Member", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name *") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Cast", "Crew", "HOD", "Vendor").forEach { t ->
                        FilterChip(
                            selected = memberType == t,
                            onClick = { memberType = t },
                            label = { Text(t) }
                        )
                    }
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Position Title (e.g. Cinematographer, Maya)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Department") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, memberType, title, department, phone, email, "Available", false)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Add Member")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddAuditionDialog(
    characters: List<CharacterEntity>,
    onDismiss: () -> Unit,
    onSave: (charId: String, name: String, contact: String, rating: Int, status: AuditionStatus, notes: String) -> Unit
) {
    var selectedCharId by remember { mutableStateOf(characters.firstOrNull()?.id ?: "") }
    var candidateName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(4) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Audition Candidate", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = candidateName, onValueChange = { candidateName = it }, label = { Text("Candidate Name *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Info") }, modifier = Modifier.fillMaxWidth())

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

                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Director Notes & Performance Feedback") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (candidateName.isNotBlank()) {
                        onSave(selectedCharId, candidateName, contact, rating, AuditionStatus.SHORTLISTED, notes)
                    }
                },
                enabled = candidateName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Log Candidate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddLocationDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, addr: String, contact: String, phone: String, cost: Double, perm: String, noise: String, power: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var costStr by remember { mutableStateOf("15000") }
    var permitStatus by remember { mutableStateOf("Granted") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Tech Recce Location", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Location Name *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address / GPS Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Person") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = costStr, onValueChange = { costStr = it }, label = { Text("Daily Rent (₹)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, address, contact, phone, costStr.toDoubleOrNull() ?: 15000.0, permitStatus, "Low Ambient Noise", "3-Phase 32A Generator")
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Save Location")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddRehearsalDialog(
    onDismiss: () -> Unit,
    onSave: (loc: String, obj: String, block: String, dial: String) -> Unit
) {
    var location by remember { mutableStateOf("Studio Rehearsal Hall A") }
    var objectives by remember { mutableStateOf("Scene 1 & Scene 2 Blocking and timing") }
    var blocking by remember { mutableStateOf("Actor moves from door to filing cabinet on cue") }
    var dialogue by remember { mutableStateOf("Pacing should be rapid and tense") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Rehearsal Session", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Rehearsal Location *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = objectives, onValueChange = { objectives = it }, label = { Text("Objectives") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = blocking, onValueChange = { blocking = it }, label = { Text("Blocking Notes") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dialogue, onValueChange = { dialogue = it }, label = { Text("Dialogue Guidance") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (location.isNotBlank()) {
                        onSave(location, objectives, blocking, dialogue)
                    }
                },
                enabled = location.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
