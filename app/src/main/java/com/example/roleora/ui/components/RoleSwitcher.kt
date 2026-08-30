package com.example.roleora.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * State holder managing stale UI state clearance when switching professional contexts.
 */
class RoleSwitchContextState(
    val onClearStaleUiState: () -> Unit
) {
    /**
     * Executes clean transition by clearing all local drafts, filters, active forms, and temporary state.
     */
    fun performContextSwitch(targetRole: RoleEntity, onSelectRole: (RoleEntity) -> Unit) {
        onClearStaleUiState()
        onSelectRole(targetRole)
    }
}

/**
 * Composable factory for RoleSwitchContextState.
 */
@Composable
fun rememberRoleSwitchContextState(
    onClearStaleUiState: () -> Unit
): RoleSwitchContextState {
    return remember(onClearStaleUiState) {
        RoleSwitchContextState(onClearStaleUiState)
    }
}

/**
 * Advanced RoleSwitcher component that displays active professional roles,
 * provides live Firestore cloud synchronization, and enforces complete clearance
 * of stale local UI state upon context switching.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoleSwitcher(
    roles: List<RoleEntity>,
    activeRoleId: String?,
    onSelectRole: (RoleEntity) -> Unit,
    onAddNewRole: () -> Unit,
    modifier: Modifier = Modifier,
    onClearStaleUiState: (() -> Unit)? = null,
    isFetchingFromFirestore: Boolean = false,
    onFetchFromFirestore: (() -> Unit)? = null,
    isCloudAuthenticated: Boolean = false,
    onOpenCloudAuth: (() -> Unit)? = null,
    showAddButton: Boolean = true,
    title: String = "Professional Workspaces",
    subtitle: String = "Zero-leakage isolated environments"
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterAiOnly by remember { mutableStateOf(false) }
    var filterCategory by remember { mutableStateOf<String?>(null) }
    var switchConfirmationMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Extract available categories
    val categories = remember(roles) {
        roles.map { it.category }.distinct()
    }

    // Filtered roles based on query, AI filter, and category
    val filteredRoles = remember(roles, searchQuery, filterAiOnly, filterCategory) {
        roles.filter { role ->
            val matchesSearch = searchQuery.isBlank() ||
                    role.displayName.contains(searchQuery, ignoreCase = true) ||
                    role.specialisation.contains(searchQuery, ignoreCase = true) ||
                    role.workType.contains(searchQuery, ignoreCase = true) ||
                    role.category.contains(searchQuery, ignoreCase = true)
            val matchesAi = !filterAiOnly || role.aiEnabled
            val matchesCat = filterCategory == null || role.category == filterCategory
            matchesSearch && matchesAi && matchesCat
        }
    }

    /**
     * Context switch execution logic that guarantees clearance of all local UI state
     */
    val executeContextSwitch: (RoleEntity) -> Unit = { targetRole ->
        focusManager.clearFocus()
        // 1. Clear local search/filter states in the switcher
        searchQuery = ""
        filterAiOnly = false
        filterCategory = null

        // 2. Clear parent/screen stale UI state (forms, draft text, active editing records)
        onClearStaleUiState?.invoke()

        // 3. Trigger role switch in repository/viewmodel
        onSelectRole(targetRole)

        // 4. Temporary confirmation banner
        switchConfirmationMessage = "Switched to ${targetRole.displayName} • UI cache reset"
        scope.launch {
            delay(2200)
            switchConfirmationMessage = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("role_switcher_container")
    ) {
        // --- Header Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ) {
                Text(
                    text = "${roles.size} Active",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Firestore Cloud Sync Bar ---
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isCloudAuthenticated) PolishGreen.copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(
                1.dp,
                if (isCloudAuthenticated) PolishGreen.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isCloudAuthenticated) Icons.Default.CloudDone else Icons.Default.CloudSync,
                        contentDescription = "Firestore Cloud Status",
                        tint = if (isCloudAuthenticated) PolishGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCloudAuthenticated) "Firestore Cloud Sync Online" else "Local Database Storage",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCloudAuthenticated) PolishGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isCloudAuthenticated && onFetchFromFirestore != null) {
                    TextButton(
                        onClick = onFetchFromFirestore,
                        enabled = !isFetchingFromFirestore,
                        modifier = Modifier.testTag("fetch_firestore_roles_button")
                    ) {
                        if (isFetchingFromFirestore) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fetching...", style = MaterialTheme.typography.labelSmall)
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fetch Cloud", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (!isCloudAuthenticated && onOpenCloudAuth != null) {
                    TextButton(
                        onClick = onOpenCloudAuth,
                        modifier = Modifier.testTag("connect_firestore_roles_button")
                    ) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Connect Cloud", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- Context Switch Confirmation Banner ---
        AnimatedVisibility(
            visible = switchConfirmationMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            switchConfirmationMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PolishGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PolishGreen.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PolishGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PolishGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Search Bar ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("role_switcher_search_input"),
            placeholder = {
                Text(
                    "Search by profession, skill, or discipline...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- Quick Category & AI Filters ---
        if (roles.size > 1) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = filterCategory == null && !filterAiOnly,
                    onClick = {
                        filterCategory = null
                        filterAiOnly = false
                    },
                    label = { Text("All (${roles.size})", style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                FilterChip(
                    selected = filterAiOnly,
                    onClick = { filterAiOnly = !filterAiOnly },
                    label = { Text("AI-Assisted (${roles.count { it.aiEnabled }})", style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                categories.forEach { cat ->
                    val count = roles.count { it.category == cat }
                    FilterChip(
                        selected = filterCategory == cat,
                        onClick = {
                            filterCategory = if (filterCategory == cat) null else cat
                        },
                        label = { Text("$cat ($count)", style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // --- Empty State ---
        if (filteredRoles.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No Matching Workspaces" else "No Workspaces Created",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Try adjusting your search keywords or filters."
                        else "Initialize an adaptive workspace or fetch from Firestore.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // --- List of Roles ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = filteredRoles,
                    key = { it.id }
                ) { role ->
                    val isSelected = role.id == activeRoleId

                    RoleSwitcherItem(
                        role = role,
                        isSelected = isSelected,
                        onSelect = {
                            if (!isSelected) {
                                executeContextSwitch(role)
                            }
                        }
                    )
                }
            }
        }

        // --- Add New Role Action ---
        if (showAddButton) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddNewRole,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("role_switcher_add_new_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Professional Role", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Individual Role Item in the Role Switcher.
 * Shows Icon, Name, Specialisation, Category, Privacy Status, AI Status, and Active Selection Badge.
 */
@Composable
fun RoleSwitcherItem(
    role: RoleEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roleColor = parseColorHex(role.colorHex)
    val containerBg by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        else MaterialTheme.colorScheme.surface,
        label = "role_item_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        label = "role_item_border"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("role_item_${role.id}"),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Adaptive Role Icon with themed container and accent badge
            RoleAdaptiveIcon(
                roleKeyOrIconName = role.iconName.ifBlank { role.displayName },
                size = AdaptiveIconSize.SWITCHER_CARD,
                isSelected = isSelected,
                aiEnabled = role.aiEnabled,
                showAccentBadge = true,
                customTint = roleColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Role Details
            Column(modifier = Modifier.weight(1f)) {
                // Name & Version
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = role.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "v${role.templateVersion}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                // Specialisation & Work Type
                Text(
                    text = "${role.specialisation} • ${role.workType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Privacy & AI Status Chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Privacy Status Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (role.isPrivate) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (role.isPrivate) Icons.Default.Lock else Icons.Default.Public,
                                contentDescription = null,
                                tint = if (role.isPrivate) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (role.isPrivate) "Private" else "Shared",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = if (role.isPrivate) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // AI Status Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (role.aiEnabled) PolishGreen.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (role.aiEnabled) Icons.Default.AutoAwesome else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (role.aiEnabled) PolishGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (role.aiEnabled) "AI Active" else "AI Disabled",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = if (role.aiEnabled) PolishGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Category Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = role.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Selection Indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Active Workspace",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Select Workspace",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Modal Bottom Sheet variant of the RoleSwitcher.
 * Automatically executes stale local UI state clearance on role selection before dismissing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSwitcherModalSheet(
    roles: List<RoleEntity>,
    activeRoleId: String?,
    onSelectRole: (RoleEntity) -> Unit,
    onAddNewRole: () -> Unit,
    onDismiss: () -> Unit,
    onClearStaleUiState: (() -> Unit)? = null,
    isFetchingFromFirestore: Boolean = false,
    onFetchFromFirestore: (() -> Unit)? = null,
    isCloudAuthenticated: Boolean = false,
    onOpenCloudAuth: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(bottom = 36.dp)
        ) {
            RoleSwitcher(
                roles = roles,
                activeRoleId = activeRoleId,
                onSelectRole = { selectedRole ->
                    onSelectRole(selectedRole)
                    onDismiss()
                },
                onAddNewRole = {
                    onDismiss()
                    onAddNewRole()
                },
                onClearStaleUiState = onClearStaleUiState,
                isFetchingFromFirestore = isFetchingFromFirestore,
                onFetchFromFirestore = onFetchFromFirestore,
                isCloudAuthenticated = isCloudAuthenticated,
                onOpenCloudAuth = {
                    onDismiss()
                    onOpenCloudAuth?.invoke()
                }
            )
        }
    }
}
