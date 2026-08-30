package com.example.roleora.ui.screens.modules

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import java.util.UUID

/**
 * Professional, refined data entry modal with inline validation,
 * draft saving, sectionalization for lengthy records, and clear required field indicators.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecordDetailSheet(
    record: ProfessionRecordEntity?,
    newCategoryAction: String?,
    roleId: String,
    professionType: String,
    onSave: (ProfessionRecordEntity) -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEditing = record != null

    val effectiveCategory = record?.recordCategory ?: when (newCategoryAction) {
        "NEW_SCENE" -> "SCREENPLAY"
        "NEW_BREAKDOWN" -> "BREAKDOWN"
        "NEW_SHOT" -> "SHOT"
        "NEW_SUBJECT" -> "SUBJECT"
        "NEW_ATTENDANCE" -> "ATTENDANCE"
        "NEW_ASSIGNMENT" -> "ASSIGNMENT"
        "NEW_EXAM" -> "EXAM"
        "NEW_TASK" -> "KANBAN"
        "NEW_SNIPPET" -> "SNIPPET"
        "NEW_BUG" -> "BUG"
        "NEW_API_NOTE" -> "API_SPEC"
        "NEW_BOOKING" -> "BOOKING"
        "NEW_SHOT_LIST" -> "SHOT_LIST"
        "NEW_EQUIPMENT" -> "EQUIPMENT"
        "NEW_CROP" -> "CROP"
        "NEW_IRRIGATION" -> "IRRIGATION"
        "NEW_TREATMENT" -> "TREATMENT"
        else -> "GENERAL"
    }

    // Core fields
    var title by remember { mutableStateOf(record?.title ?: "") }
    var subtitle by remember { mutableStateOf(record?.subtitle ?: "") }
    var stage by remember { mutableStateOf(record?.stage ?: getDefaultStageForCategory(effectiveCategory)) }
    var status by remember { mutableStateOf(record?.status ?: "Active") }
    var numericVal1 by remember { mutableStateOf(if (record != null && record.numericValue1 > 0) record.numericValue1.toString() else getDefaultNum1(effectiveCategory)) }
    var numericVal2 by remember { mutableStateOf(if (record != null && record.numericValue2 > 0) record.numericValue2.toString() else getDefaultNum2(effectiveCategory)) }
    var tags by remember { mutableStateOf(record?.tags ?: "") }

    // Specialized extra fields
    var extraFieldA by remember { mutableStateOf(extractFromJson(record?.detailsJson, "fieldA") ?: "") }
    var extraFieldB by remember { mutableStateOf(extractFromJson(record?.detailsJson, "fieldB") ?: "") }
    var extraFieldC by remember { mutableStateOf(extractFromJson(record?.detailsJson, "fieldC") ?: "") }
    var extraNotes by remember { mutableStateOf(extractFromJson(record?.detailsJson, "notes") ?: "") }

    // Validation state
    var titleTouched by remember { mutableStateOf(false) }
    var numeric1Touched by remember { mutableStateOf(false) }
    var numeric2Touched by remember { mutableStateOf(false) }
    var formErrorMessage by remember { mutableStateOf<String?>(null) }
    var draftSavedNotice by remember { mutableStateOf(false) }

    // Sectionalization for lengthy forms
    var selectedSectionIndex by remember { mutableIntStateOf(0) }
    val isLengthyForm = effectiveCategory in listOf("SCREENPLAY", "SUBJECT", "KANBAN", "BOOKING", "CROP", "BUG")
    val sectionTabs = if (isLengthyForm) listOf("Core Details", "Specifications", "Notes & Logistics") else listOf("Details")

    // Validation logic
    val isTitleValid = title.trim().isNotBlank()
    val isNumeric1Valid = numericVal1.isBlank() || numericVal1.toDoubleOrNull() != null
    val isNumeric2Valid = numericVal2.isBlank() || numericVal2.toDoubleOrNull() != null
    val isFormValid = isTitleValid && isNumeric1Valid && isNumeric2Valid

    // Progress computation
    val filledFields = listOf(
        title.isNotBlank(),
        subtitle.isNotBlank(),
        extraFieldA.isNotBlank(),
        extraFieldB.isNotBlank(),
        extraNotes.isNotBlank()
    ).count { it }
    val formProgress = (filledFields / 5f).coerceIn(0.2f, 1f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Category Badge and Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = effectiveCategory.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Edit Record" else "New Entry",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Fields marked with (*) are required for workspace calculations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Form Completion Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Form Completeness: ${(formProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                if (draftSavedNotice) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = PolishGreen.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PolishGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Draft Saved", style = MaterialTheme.typography.labelSmall, color = PolishGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { formProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )

            // Section Tabs for Lengthy Forms
            if (isLengthyForm) {
                Spacer(modifier = Modifier.height(14.dp))
                TabRow(
                    selectedTabIndex = selectedSectionIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedSectionIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    sectionTabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedSectionIndex == index,
                            onClick = { selectedSectionIndex = index },
                            text = {
                                Text(
                                    text = tabTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selectedSectionIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error summary banner if submitted with invalid inputs
            if (formErrorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = formErrorMessage ?: "Please correct form errors before saving.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Render Form Fields according to section & category
            if (!isLengthyForm || selectedSectionIndex == 0) {
                // Section 1: Core Details
                renderCategoryCoreInputs(
                    category = effectiveCategory,
                    title = title,
                    onTitleChange = {
                        title = it
                        titleTouched = true
                        formErrorMessage = null
                    },
                    titleTouched = titleTouched,
                    subtitle = subtitle,
                    onSubtitleChange = { subtitle = it },
                    stage = stage,
                    onStageChange = { stage = it },
                    numericVal1 = numericVal1,
                    onNum1Change = {
                        numericVal1 = it
                        numeric1Touched = true
                        formErrorMessage = null
                    },
                    numeric1Touched = numeric1Touched,
                    numericVal2 = numericVal2,
                    onNum2Change = {
                        numericVal2 = it
                        numeric2Touched = true
                        formErrorMessage = null
                    },
                    numeric2Touched = numeric2Touched
                )
            }

            if (isLengthyForm && selectedSectionIndex == 1) {
                // Section 2: Specifications & Specialized Parameters
                renderCategorySpecifications(
                    category = effectiveCategory,
                    extraFieldA = extraFieldA,
                    onExtraAChange = { extraFieldA = it },
                    extraFieldB = extraFieldB,
                    onExtraBChange = { extraFieldB = it },
                    extraFieldC = extraFieldC,
                    onExtraCChange = { extraFieldC = it },
                    tags = tags,
                    onTagsChange = { tags = it }
                )
            }

            if (isLengthyForm && selectedSectionIndex == 2) {
                // Section 3: Notes & Logistics
                Column {
                    Text(
                        text = "Notes, Logistics & Observations",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = extraNotes,
                        onValueChange = { extraNotes = it },
                        label = { Text("Detailed Operational Notes") },
                        placeholder = { Text("Enter detailed observations, safety instructions, cast directions or prerequisites...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("record_notes_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Index Tags (comma-separated)") },
                        placeholder = { Text("e.g. Priority, Q3, Production, Organic") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons: Draft Save / Full Save / Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing) {
                    OutlinedButton(
                        onClick = {
                            record?.let { onDelete(it.id) }
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("delete_record_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            draftSavedNotice = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("draft_record_button"),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Drafts, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Quick Draft", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Button(
                    onClick = {
                        titleTouched = true
                        numeric1Touched = true
                        numeric2Touched = true

                        if (!isTitleValid) {
                            formErrorMessage = "Please provide a valid title/identifier for this record."
                            return@Button
                        }
                        if (!isNumeric1Valid || !isNumeric2Valid) {
                            formErrorMessage = "Numeric values must contain valid numbers."
                            return@Button
                        }

                        val recordId = record?.id ?: UUID.randomUUID().toString()
                        val packedDetails = buildDetailsJson(extraFieldA, extraFieldB, extraFieldC, extraNotes)

                        val newRecord = ProfessionRecordEntity(
                            id = recordId,
                            roleId = roleId,
                            professionType = professionType,
                            recordCategory = effectiveCategory,
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            stage = stage,
                            status = status,
                            numericValue1 = numericVal1.toDoubleOrNull() ?: 0.0,
                            numericValue2 = numericVal2.toDoubleOrNull() ?: 0.0,
                            detailsJson = packedDetails,
                            tags = tags.trim(),
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(newRecord)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                        .testTag("save_record_button"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isEditing) "Update Record" else "Save Record", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// --- Render Helper Composables for Core Inputs ---

@Composable
private fun renderCategoryCoreInputs(
    category: String,
    title: String,
    onTitleChange: (String) -> Unit,
    titleTouched: Boolean,
    subtitle: String,
    onSubtitleChange: (String) -> Unit,
    stage: String,
    onStageChange: (String) -> Unit,
    numericVal1: String,
    onNum1Change: (String) -> Unit,
    numeric1Touched: Boolean,
    numericVal2: String,
    onNum2Change: (String) -> Unit,
    numeric2Touched: Boolean
) {
    val titleError = if (titleTouched && title.isBlank()) "Title is required *" else null
    val num1Error = if (numeric1Touched && numericVal1.isNotBlank() && numericVal1.toDoubleOrNull() == null) "Invalid number" else null
    val num2Error = if (numeric2Touched && numericVal2.isNotBlank() && numericVal2.toDoubleOrNull() == null) "Invalid number" else null

    when (category) {
        "SCREENPLAY" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Scene Heading (SLUG LINE) *") },
                placeholder = { Text("e.g. EXT. TEMPLE COURTYARD - DAWN") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Scene Synopsis / Action Summary") },
                placeholder = { Text("e.g. Maran discovers the hidden palm leaf manuscript.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = numericVal1,
                    onValueChange = onNum1Change,
                    label = { Text("Scene Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = numericVal2,
                    onValueChange = onNum2Change,
                    label = { Text("Est. Minutes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        "SHOT" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Shot Title / Code *") },
                placeholder = { Text("e.g. Shot 1A: Wide Establishing Crane") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Camera & Lens Configuration") },
                placeholder = { Text("e.g. 50mm Anamorphic • High Angle Crane") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = numericVal1,
                    onValueChange = onNum1Change,
                    label = { Text("Scene Ref #") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = numericVal2,
                    onValueChange = onNum2Change,
                    label = { Text("Recorded Takes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        "SUBJECT" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Subject / Course Name *") },
                placeholder = { Text("e.g. Artificial Intelligence & Neural Networks") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Course Code & Faculty") },
                placeholder = { Text("e.g. CS8601 • Dr. Ramanathan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = numericVal1,
                    onValueChange = onNum1Change,
                    label = { Text("Classes Attended *") },
                    isError = num1Error != null,
                    supportingText = { num1Error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = numericVal2,
                    onValueChange = onNum2Change,
                    label = { Text("Total Classes *") },
                    isError = num2Error != null,
                    supportingText = { num2Error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        "ASSIGNMENT" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Assignment Title *") },
                placeholder = { Text("e.g. Deep Learning Model Implementation") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Subject Code & Submission Portal") },
                placeholder = { Text("e.g. CS8601 • Due Friday on College Portal") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = numericVal2,
                    onValueChange = onNum2Change,
                    label = { Text("Maximum Marks") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = stage,
                    onValueChange = onStageChange,
                    label = { Text("Status Stage") },
                    placeholder = { Text("Pending / Submitted / Graded") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        "KANBAN" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Sprint Task / Story Name *") },
                placeholder = { Text("e.g. Implement Secure Token Rotation Engine") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Sprint Scope & Component") },
                placeholder = { Text("e.g. Sprint 14 • Auth & Security Service") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            StageSelector(
                currentStage = stage,
                options = listOf("Backlog", "In Progress", "Code Review", "Done"),
                onStageSelected = onStageChange
            )
        }
        "SNIPPET" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Snippet Title *") },
                placeholder = { Text("e.g. Kotlin Flow Mutex Safe Execution") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Language / Framework") },
                placeholder = { Text("e.g. Kotlin • Coroutines & Flow") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
        "BUG" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Issue / Bug Summary *") },
                placeholder = { Text("e.g. WebSocket reconnection debounce glitch") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Component & Severity") },
                placeholder = { Text("e.g. Issue #104 • Medium Severity • Network Layer") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
        "BOOKING" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Client & Event Name *") },
                placeholder = { Text("e.g. Karthik & Divya Sunset Wedding") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Venue & Package") },
                placeholder = { Text("e.g. Grand Palace Resort, ECR • Cinematic Package") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = numericVal1,
                    onValueChange = onNum1Change,
                    label = { Text("Total Quote (₹) *") },
                    isError = num1Error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = numericVal2,
                    onValueChange = onNum2Change,
                    label = { Text("Advance Received (₹)") },
                    isError = num2Error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        "CROP" -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Crop Variety & Field Plot *") },
                placeholder = { Text("e.g. Ponni Samba Paddy (Plot North-3)") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Soil Type & Location") },
                placeholder = { Text("e.g. Clay Loam • Plot North-3") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = numericVal1,
                    onValueChange = onNum1Change,
                    label = { Text("Acres Cultivated *") },
                    isError = num1Error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = numericVal2,
                    onValueChange = onNum2Change,
                    label = { Text("Est. Yield (kg/ac)") },
                    isError = num2Error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        else -> {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Record Title *") },
                placeholder = { Text("Enter descriptive entry title") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_title_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = subtitle,
                onValueChange = onSubtitleChange,
                label = { Text("Subtitle / Details") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

// --- Render Helper Composables for Specifications ---

@Composable
private fun renderCategorySpecifications(
    category: String,
    extraFieldA: String,
    onExtraAChange: (String) -> Unit,
    extraFieldB: String,
    onExtraBChange: (String) -> Unit,
    extraFieldC: String,
    onExtraCChange: (String) -> Unit,
    tags: String,
    onTagsChange: (String) -> Unit
) {
    when (category) {
        "SCREENPLAY" -> {
            OutlinedTextField(
                value = extraFieldA,
                onValueChange = onExtraAChange,
                label = { Text("Cast / Characters Involved") },
                placeholder = { Text("e.g. Maran, Gurukkal, Extra Monks (4)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = extraFieldB,
                onValueChange = onExtraBChange,
                label = { Text("Key Props & Set Elements") },
                placeholder = { Text("e.g. Brass bell, Palm leaf manuscript, Oil lamp") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = extraFieldC,
                onValueChange = onExtraCChange,
                label = { Text("Lighting & Sound Atmosphere") },
                placeholder = { Text("e.g. Natural golden morning backlight, Soft temple bell") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
        "SUBJECT" -> {
            OutlinedTextField(
                value = extraFieldA,
                onValueChange = onExtraAChange,
                label = { Text("Course Credits") },
                placeholder = { Text("e.g. 4 Credits") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = extraFieldB,
                onValueChange = onExtraBChange,
                label = { Text("Target Grade / Objective") },
                placeholder = { Text("e.g. O Grade (>90 marks)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
        "KANBAN" -> {
            OutlinedTextField(
                value = extraFieldA,
                onValueChange = onExtraAChange,
                label = { Text("Assigned Engineer / Owner") },
                placeholder = { Text("e.g. Lead Mobile Architect") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = extraFieldB,
                onValueChange = onExtraBChange,
                label = { Text("Technical Tags & Service") },
                placeholder = { Text("e.g. Jetpack Compose, Room, Coroutines") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
        "BOOKING" -> {
            OutlinedTextField(
                value = extraFieldA,
                onValueChange = onExtraAChange,
                label = { Text("Client Phone / Contact") },
                placeholder = { Text("e.g. +91 98401 23456") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = extraFieldB,
                onValueChange = onExtraBChange,
                label = { Text("Crew Size & Deliverables") },
                placeholder = { Text("e.g. 3 Crew • 40-Page Layflat Album + 4K Teaser") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
        "CROP" -> {
            OutlinedTextField(
                value = extraFieldA,
                onValueChange = onExtraAChange,
                label = { Text("Sowing & Planting Date") },
                placeholder = { Text("e.g. 2026-06-10") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = extraFieldB,
                onValueChange = onExtraBChange,
                label = { Text("Irrigation Method & Source") },
                placeholder = { Text("e.g. Drip & Alternate Wetting • Borewell #2") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
        "BUG" -> {
            OutlinedTextField(
                value = extraFieldA,
                onValueChange = onExtraAChange,
                label = { Text("Reproduction Steps") },
                placeholder = { Text("1. Go to screen...\n2. Tap action...\n3. Observe crash") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = extraFieldB,
                onValueChange = onExtraBChange,
                label = { Text("Expected vs Actual Behavior") },
                placeholder = { Text("Expected smooth transition but socket throttled.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
        else -> {
            OutlinedTextField(
                value = extraFieldA,
                onValueChange = onExtraAChange,
                label = { Text("Specification Parameter") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StageSelector(
    currentStage: String,
    options: List<String>,
    onStageSelected: (String) -> Unit
) {
    Column {
        Text("Stage / Pipeline Status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { opt ->
                val isSelected = currentStage == opt
                FilterChip(
                    selected = isSelected,
                    onClick = { onStageSelected(opt) },
                    label = { Text(opt) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

// Utility Helpers
private fun getDefaultStageForCategory(category: String): String {
    return when (category) {
        "SCREENPLAY" -> "Ready"
        "SHOT" -> "Planned"
        "SUBJECT" -> "Active"
        "ASSIGNMENT" -> "Pending"
        "KANBAN" -> "In Progress"
        "BUG" -> "Open"
        "BOOKING" -> "Booked"
        "CROP" -> "Growth"
        "IRRIGATION" -> "Scheduled"
        "TREATMENT" -> "Logged"
        else -> "Active"
    }
}

private fun getDefaultNum1(category: String): String {
    return when (category) {
        "SCREENPLAY" -> "1.0"
        "SUBJECT" -> "24.0"
        "BOOKING" -> "50000.0"
        "CROP" -> "3.5"
        else -> "0.0"
    }
}

private fun getDefaultNum2(category: String): String {
    return when (category) {
        "SCREENPLAY" -> "3.0"
        "SUBJECT" -> "28.0"
        "BOOKING" -> "15000.0"
        "CROP" -> "2800.0"
        else -> "0.0"
    }
}

private fun extractFromJson(json: String?, key: String): String? {
    if (json.isNullOrBlank()) return null
    return try {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\"".toRegex()
        pattern.find(json)?.groupValues?.getOrNull(1)
    } catch (e: Exception) {
        null
    }
}

private fun buildDetailsJson(fieldA: String, fieldB: String, fieldC: String, notes: String): String {
    val escapedA = fieldA.replace("\"", "\\\"").replace("\n", "\\n")
    val escapedB = fieldB.replace("\"", "\\\"").replace("\n", "\\n")
    val escapedC = fieldC.replace("\"", "\\\"").replace("\n", "\\n")
    val escapedNotes = notes.replace("\"", "\\\"").replace("\n", "\\n")
    return "{\"fieldA\":\"$escapedA\",\"fieldB\":\"$escapedB\",\"fieldC\":\"$escapedC\",\"notes\":\"$escapedNotes\"}"
}
