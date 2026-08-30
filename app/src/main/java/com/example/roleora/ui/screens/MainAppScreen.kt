package com.example.roleora.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.roleora.data.auth.AuthState
import com.example.roleora.data.model.EntryType
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.ui.components.AuthAndCloudSyncModalSheet
import com.example.roleora.ui.components.ContextualRightPanel
import com.example.roleora.ui.components.DesktopCommandPaletteModal
import com.example.roleora.ui.components.DesktopShortcutsModal
import com.example.roleora.ui.components.DesktopSidebar
import com.example.roleora.ui.components.DesktopTitleBar
import com.example.roleora.ui.components.DeviceSessionManagerView
import com.example.roleora.ui.components.QuickActionFab
import com.example.roleora.ui.components.RoleSwitcherSheet
import com.example.roleora.ui.components.RoleoraDrawerContent
import com.example.roleora.ui.components.RoleoraHeader
import com.example.roleora.ui.components.TabletNavigationRail
import com.example.roleora.ui.components.UniversalCreateSheet
import com.example.roleora.ui.components.VersionManagerSheet
import com.example.roleora.ui.components.WorkspaceBottomNavigation
import com.example.roleora.ui.components.WorkspaceTemplateVersionSheet
import com.example.roleora.ui.responsive.DeviceScreenClass
import com.example.roleora.ui.responsive.rememberResponsiveLayoutSpec
import com.example.roleora.ui.screens.dashboard.DeveloperDashboard
import com.example.roleora.ui.screens.dashboard.DirectorDashboard
import com.example.roleora.ui.screens.dashboard.FarmerDashboard
import com.example.roleora.ui.screens.dashboard.PhotographerDashboard
import com.example.roleora.ui.screens.dashboard.StudentDashboard
import com.example.roleora.ui.screens.diary.RoleDiaryScreen
import com.example.roleora.ui.screens.diary.UniversalTimelineScreen
import com.example.roleora.ui.screens.tasks.TasksAndRemindersScreen
import com.example.roleora.ui.screens.calendar.CalendarEventsScreen
import com.example.roleora.ui.components.WorkSessionTimerWidget
import com.example.roleora.ui.screens.universal.UniversalCreateModal
import com.example.roleora.ui.screens.universal.VersionHistoryModal
import com.example.roleora.ui.screens.universal.TrashManagementModal
import com.example.roleora.ui.screens.modules.RecordDetailSheet
import com.example.roleora.ui.screens.director.DirectorWorkspaceScreen
import com.example.roleora.ui.viewmodel.DirectorViewModel
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.viewmodel.RoleoraViewModel

enum class NavigationTab {
    DASHBOARD,
    TIMELINE,
    TASKS,
    CALENDAR,
    AUDIT
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: RoleoraViewModel,
    directorViewModel: DirectorViewModel? = null,
    onOpenSetup: () -> Unit
) {
    val activeRoles by viewModel.activeRoles.collectAsStateWithLifecycle()
    val activeRole by viewModel.activeRole.collectAsStateWithLifecycle()
    val diaryEntries by viewModel.diaryEntries.collectAsStateWithLifecycle()
    val professionRecords by viewModel.professionRecords.collectAsStateWithLifecycle()
    val availableVersions by viewModel.availableVersions.collectAsStateWithLifecycle()
    val workspaceTemplateVersions by viewModel.workspaceTemplateVersions.collectAsStateWithLifecycle()
    val selectedVersionForDiff by viewModel.selectedVersionForDiff.collectAsStateWithLifecycle()
    val versionDiffResult by viewModel.versionDiffResult.collectAsStateWithLifecycle()
    val operationStatus by viewModel.operationStatus.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val isFetchingFirestoreRoles by viewModel.isFetchingFirestoreRoles.collectAsStateWithLifecycle()
    val isUniversalCreateOpenFromVm by viewModel.isUniversalCreateOpen.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    var showRoleSwitcher by remember { mutableStateOf(false) }
    var showUniversalCreate by remember { mutableStateOf(false) }
    var showVersionManager by remember { mutableStateOf(false) }
    var showCloudAuthSheet by remember { mutableStateOf(false) }
    var showVersionHistoryEntryId by remember { mutableStateOf<String?>(null) }
    var showTrashManagement by remember { mutableStateOf(false) }
    var showCommandPalette by remember { mutableStateOf(false) }
    var showShortcutsModal by remember { mutableStateOf(false) }
    var isPcMode by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<ProfessionRecordEntity?>(null) }
    var newCategoryAction by remember { mutableStateOf<String?>(null) }

    // Responsive State Controls
    var isRightContextPanelOpen by remember { mutableStateOf(true) }
    var globalSearchQuery by remember { mutableStateOf("") }

    LaunchedEffect(operationStatus) {
        operationStatus?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatus()
        }
    }

    // Function to cleanly purge any active or stale local UI state on role transition
    val clearStaleUiState = {
        editingRecord = null
        newCategoryAction = null
        showUniversalCreate = false
        showVersionManager = false
        showCloudAuthSheet = false
    }

    // Role-keyed effect ensuring local form/modal references are reset whenever active role changes
    LaunchedEffect(activeRole?.id) {
        clearStaleUiState()
    }

    // Filtered records when global search query is entered
    val filteredRecords = remember(professionRecords, globalSearchQuery) {
        if (globalSearchQuery.isBlank()) {
            professionRecords
        } else {
            professionRecords.filter {
                it.title.contains(globalSearchQuery, ignoreCase = true) ||
                    it.subtitle.contains(globalSearchQuery, ignoreCase = true) ||
                    it.status.contains(globalSearchQuery, ignoreCase = true) ||
                    it.stage.contains(globalSearchQuery, ignoreCase = true) ||
                    it.recordCategory.contains(globalSearchQuery, ignoreCase = true) ||
                    it.tags.contains(globalSearchQuery, ignoreCase = true)
            }
        }
    }

    val filteredDiary = remember(diaryEntries, globalSearchQuery) {
        if (globalSearchQuery.isBlank()) {
            diaryEntries
        } else {
            diaryEntries.filter {
                it.title.contains(globalSearchQuery, ignoreCase = true) ||
                    it.content.contains(globalSearchQuery, ignoreCase = true) ||
                    it.tags.contains(globalSearchQuery, ignoreCase = true)
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val layoutSpec = rememberResponsiveLayoutSpec(
            maxWidth = maxWidth,
            isRightPanelUserToggled = isRightContextPanelOpen,
            forcePcMode = isPcMode
        )

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                RoleoraDrawerContent(
                    roles = activeRoles,
                    activeRole = activeRole,
                    currentTab = currentTab,
                    authState = authState,
                    onTabSelected = { newTab ->
                        currentTab = newTab
                    },
                    onRoleSelected = { selectedRole ->
                        clearStaleUiState()
                        viewModel.selectRole(selectedRole.id)
                    },
                    onOpenAddNewRole = {
                        clearStaleUiState()
                        onOpenSetup()
                    },
                    onOpenCloudAuth = { showCloudAuthSheet = true },
                    onOpenVersionManager = { showVersionManager = true },
                    onOpenTrash = { showTrashManagement = true },
                    onOpenShortcuts = { showShortcutsModal = true },
                    onOpenCommandPalette = { showCommandPalette = true },
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    Column {
                        if (layoutSpec.screenClass.isLaptopOrDesktop || isPcMode) {
                            DesktopTitleBar(
                                activeRole = activeRole,
                                currentTab = currentTab,
                                isPcMode = isPcMode,
                                onTogglePcMode = { isPcMode = !isPcMode },
                                onOpenCommandPalette = { showCommandPalette = true },
                                onOpenShortcutsModal = { showShortcutsModal = true }
                            )
                        }

                        RoleoraHeader(
                            activeRole = activeRole,
                            onOpenRoleSwitcher = { showRoleSwitcher = true },
                            onOpenVersionManager = { showVersionManager = true },
                            onOpenCloudAuth = { showCloudAuthSheet = true },
                            onOpenDrawer = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            isAuthenticated = authState is AuthState.Authenticated,
                            userDisplayName = (authState as? AuthState.Authenticated)?.displayName,
                            searchQuery = globalSearchQuery,
                            onSearchQueryChange = { globalSearchQuery = it },
                            showSearchAndActions = layoutSpec.screenClass.isLaptopOrDesktop,
                            onUniversalCreate = { showUniversalCreate = true },
                            showContextPanelToggle = layoutSpec.screenClass.isLaptopOrDesktop,
                            isContextPanelOpen = isRightContextPanelOpen,
                            onToggleContextPanel = { isRightContextPanelOpen = !isRightContextPanelOpen },
                            isPcMode = isPcMode,
                            onTogglePcMode = { isPcMode = !isPcMode },
                            onOpenCommandPalette = { showCommandPalette = true },
                            onOpenShortcutsModal = { showShortcutsModal = true }
                        )
                    }
                },
            bottomBar = {
                if (activeRole != null && layoutSpec.showBottomNavigation) {
                    WorkspaceBottomNavigation(
                        roles = activeRoles,
                        activeRole = activeRole,
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it },
                        onRoleSelected = { selectedRole ->
                            viewModel.selectRole(selectedRole.id)
                        },
                        onOpenRoleSwitcher = { showRoleSwitcher = true },
                        onOpenAddNewRole = { onOpenSetup() },
                        onClearStaleUiState = clearStaleUiState
                    )
                }
            },
            floatingActionButton = {
                // On Mobile and Tablet, show the contextual QuickActionFab
                if (activeRole != null && currentTab == NavigationTab.DASHBOARD && !layoutSpec.screenClass.isLaptopOrDesktop) {
                    QuickActionFab(
                        activeRole = activeRole,
                        onActionTriggered = { actionId ->
                            if (actionId == "NEW_DIARY") {
                                currentTab = NavigationTab.TIMELINE
                            } else {
                                newCategoryAction = actionId
                                editingRecord = null
                            }
                        },
                        onOpenFullCreateModal = {
                            showUniversalCreate = true
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (activeRole == null) {
                    // Zero-State Onboarding Launcher
                    ZeroStateWelcome(onOpenSetup = onOpenSetup)
                } else {
                    androidx.compose.runtime.key(activeRole?.id) {
                        // Multi-region Responsive Workspace
                        Row(modifier = Modifier.fillMaxSize()) {
                            // 1. LEFT REGION: Desktop Sidebar or Tablet Navigation Rail
                            if (layoutSpec.showLeftSidebar) {
                                DesktopSidebar(
                                    activeRole = activeRole,
                                    roles = activeRoles,
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it },
                                    onRoleSelected = { selectedRole -> viewModel.selectRole(selectedRole.id) },
                                    onOpenRoleSwitcher = { showRoleSwitcher = true },
                                    onOpenAddNewRole = onOpenSetup,
                                    onOpenCloudAuth = { showCloudAuthSheet = true },
                                    onClearStaleUiState = clearStaleUiState
                                )
                            } else if (layoutSpec.showNavigationRail) {
                                TabletNavigationRail(
                                    activeRole = activeRole,
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it },
                                    onOpenRoleSwitcher = { showRoleSwitcher = true },
                                    onOpenAddNewRole = onOpenSetup,
                                    onOpenCloudAuth = { showCloudAuthSheet = true }
                                )
                            }

                            // 2. CENTER REGION: Main Professional Workspace
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Live Work Session Timer Bar (if active)
                                    WorkSessionTimerWidget(viewModel = viewModel)

                                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                        when (currentTab) {
                                            NavigationTab.DASHBOARD -> {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(horizontal = layoutSpec.contentPadding)
                                                        .verticalScroll(rememberScrollState()),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Spacer(modifier = Modifier.height(16.dp))

                                                    Box(modifier = Modifier.widthIn(max = 1400.dp)) {
                                                        val role = activeRole!!
                                                        when (role.templateId) {
                                                            "movie_director" -> {
                                                                if (directorViewModel != null) {
                                                                    DirectorWorkspaceScreen(
                                                                        role = role,
                                                                        viewModel = directorViewModel
                                                                    )
                                                                } else {
                                                                    DirectorDashboard(
                                                                        role = role,
                                                                        records = filteredRecords,
                                                                        onOpenCreate = { action ->
                                                                            newCategoryAction = action
                                                                            editingRecord = null
                                                                        },
                                                                        onRecordClick = { rec -> editingRecord = rec },
                                                                        onQuickUpdateRecord = { rec -> viewModel.updateProfessionRecord(rec) }
                                                                    )
                                                                }
                                                            }
                                                            "college_student" -> StudentDashboard(
                                                                role = role,
                                                                records = filteredRecords,
                                                                onOpenCreate = { action ->
                                                                    newCategoryAction = action
                                                                    editingRecord = null
                                                                },
                                                                onRecordClick = { rec -> editingRecord = rec },
                                                                onQuickUpdateRecord = { rec -> viewModel.updateProfessionRecord(rec) }
                                                            )
                                                            "software_developer" -> DeveloperDashboard(
                                                                role = role,
                                                                records = filteredRecords,
                                                                onOpenCreate = { action ->
                                                                    newCategoryAction = action
                                                                    editingRecord = null
                                                                },
                                                                onRecordClick = { rec -> editingRecord = rec },
                                                                onQuickUpdateRecord = { rec -> viewModel.updateProfessionRecord(rec) }
                                                            )
                                                            "photographer" -> PhotographerDashboard(
                                                                role = role,
                                                                records = filteredRecords,
                                                                onOpenCreate = { action ->
                                                                    newCategoryAction = action
                                                                    editingRecord = null
                                                                },
                                                                onRecordClick = { rec -> editingRecord = rec },
                                                                onQuickUpdateRecord = { rec -> viewModel.updateProfessionRecord(rec) }
                                                            )
                                                            "farmer" -> FarmerDashboard(
                                                                role = role,
                                                                records = filteredRecords,
                                                                onOpenCreate = { action ->
                                                                    newCategoryAction = action
                                                                    editingRecord = null
                                                                },
                                                                onRecordClick = { rec -> editingRecord = rec },
                                                                onQuickUpdateRecord = { rec -> viewModel.updateProfessionRecord(rec) }
                                                            )
                                                            else -> DirectorDashboard(
                                                                role = role,
                                                                records = filteredRecords,
                                                                onOpenCreate = { action ->
                                                                    newCategoryAction = action
                                                                    editingRecord = null
                                                                },
                                                                onRecordClick = { rec -> editingRecord = rec },
                                                                onQuickUpdateRecord = { rec -> viewModel.updateProfessionRecord(rec) }
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(84.dp))
                                                }
                                            }
                                            NavigationTab.TIMELINE -> {
                                                UniversalTimelineScreen(
                                                    viewModel = viewModel,
                                                    onOpenVersionHistory = { showVersionHistoryEntryId = it },
                                                    onOpenTrash = { showTrashManagement = true }
                                                )
                                            }
                                            NavigationTab.TASKS -> {
                                                TasksAndRemindersScreen(
                                                    viewModel = viewModel
                                                )
                                            }
                                            NavigationTab.CALENDAR -> {
                                                CalendarEventsScreen(
                                                    viewModel = viewModel
                                                )
                                            }
                                            NavigationTab.AUDIT -> {
                                                SecurityAuditView(
                                                    activeRole = activeRole!!,
                                                    userEmail = (authState as? AuthState.Authenticated)?.email,
                                                    onRevokeOtherSessions = { viewModel.clearStatus() }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 3. RIGHT REGION: Contextual Panel (Laptop / Desktop)
                            if (layoutSpec.showRightContextPanel && isRightContextPanelOpen) {
                                ContextualRightPanel(
                                    activeRole = activeRole,
                                    records = professionRecords,
                                    diaryEntries = diaryEntries,
                                    onClosePanel = { isRightContextPanelOpen = false },
                                    onOpenCloudAuth = { showCloudAuthSheet = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    }

    // Role Switcher Modal Sheet
    if (showRoleSwitcher) {
        RoleSwitcherSheet(
            roles = activeRoles,
            activeRoleId = activeRole?.id,
            onSelectRole = { id ->
                clearStaleUiState()
                viewModel.selectRole(id)
            },
            onAddNewRole = {
                clearStaleUiState()
                onOpenSetup()
            },
            onDismiss = { showRoleSwitcher = false },
            onClearStaleUiState = clearStaleUiState,
            isFetchingFromFirestore = isFetchingFirestoreRoles,
            onFetchFromFirestore = { viewModel.fetchActiveRolesFromFirestore() },
            isCloudAuthenticated = authState is AuthState.Authenticated,
            onOpenCloudAuth = { showCloudAuthSheet = true }
        )
    }

    // Universal Create Modal (Phase 2 Multi-type create system)
    if (showUniversalCreate || isUniversalCreateOpenFromVm) {
        UniversalCreateModal(
            viewModel = viewModel,
            onDismiss = {
                showUniversalCreate = false
                viewModel.closeUniversalCreate()
            }
        )
    }

    // Version History & Audit Modal
    showVersionHistoryEntryId?.let { entryId ->
        VersionHistoryModal(
            entryId = entryId,
            viewModel = viewModel,
            onDismiss = { showVersionHistoryEntryId = null }
        )
    }

    // Trash Management Modal
    if (showTrashManagement) {
        TrashManagementModal(
            viewModel = viewModel,
            onDismiss = { showTrashManagement = false }
        )
    }

    // Workspace Template Version Manager & Snapshot Modal Sheet
    if (showVersionManager) {
        WorkspaceTemplateVersionSheet(
            activeRole = activeRole,
            versions = workspaceTemplateVersions,
            selectedDiffVersion = selectedVersionForDiff,
            diffResult = versionDiffResult,
            onSaveNewVersion = { label, summary, verNum, modules, workflow, tags ->
                viewModel.saveWorkspaceTemplateVersion(label, summary, verNum, modules, workflow, tags)
            },
            onRevertToVersion = { versionId, createBackup ->
                viewModel.revertWorkspaceToVersion(versionId, createBackup)
            },
            onCompareDiff = { version ->
                viewModel.compareWithActiveWorkspace(version)
            },
            onClearDiff = {
                viewModel.clearVersionDiff()
            },
            onDeleteVersion = { versionId ->
                viewModel.deleteWorkspaceTemplateVersion(versionId)
            },
            onToggleLock = { versionId ->
                viewModel.toggleWorkspaceVersionLock(versionId)
            },
            onToggleFavorite = { versionId ->
                viewModel.toggleWorkspaceVersionFavorite(versionId)
            },
            onDuplicateVersion = { versionId, newLabel ->
                viewModel.duplicateWorkspaceTemplateVersion(versionId, newLabel)
            },
            onDismiss = {
                viewModel.clearVersionDiff()
                showVersionManager = false
            }
        )
    }

    // Record Create / Detail Modal Sheet
    if (editingRecord != null || newCategoryAction != null) {
        val role = activeRole
        if (role != null) {
            RecordDetailSheet(
                record = editingRecord,
                newCategoryAction = newCategoryAction,
                roleId = role.id,
                professionType = role.templateId.uppercase(),
                onSave = { savedRec ->
                    if (editingRecord != null) {
                        viewModel.updateProfessionRecord(savedRec)
                    } else {
                        viewModel.addProfessionRecord(savedRec)
                    }
                    editingRecord = null
                    newCategoryAction = null
                },
                onDelete = { id ->
                    viewModel.deleteProfessionRecord(id)
                    editingRecord = null
                    newCategoryAction = null
                },
                onDismiss = {
                    editingRecord = null
                    newCategoryAction = null
                }
            )
        }
    }

    // Firebase Auth & Cloud Firestore Sync Modal Sheet
    if (showCloudAuthSheet) {
        AuthAndCloudSyncModalSheet(
            authState = authState,
            syncState = syncState,
            totalRolesCount = activeRoles.size,
            totalRecordsCount = professionRecords.size,
            totalDiaryCount = diaryEntries.size,
            onSignInWithGoogle = { ctx -> viewModel.signInWithGoogle(ctx) },
            onSignInWithDemo = { viewModel.signInWithDemoGoogleAccount() },
            onSignInWithEmail = { email, pass -> viewModel.signInWithEmail(email, pass) },
            onSignUpWithEmail = { email, pass, name -> viewModel.signUpWithEmail(email, pass, name) },
            onSendPasswordReset = { email -> viewModel.sendPasswordReset(email) },
            onUpdateProfile = { name -> viewModel.updateUserProfile(name) },
            onDeleteAccount = { viewModel.deleteEntireAccount() },
            onSignOut = { viewModel.signOut() },
            onSyncNow = { viewModel.syncAllWithCloud() },
            onRestoreFromCloud = { viewModel.restoreFromCloud() },
            onDismiss = { showCloudAuthSheet = false }
        )
    }

    // Omni Desktop Command Palette Dialog (Ctrl + K)
    if (showCommandPalette) {
        DesktopCommandPaletteModal(
            viewModel = viewModel,
            roles = activeRoles,
            activeRole = activeRole,
            onNavigateTab = { tab -> currentTab = tab },
            onOpenCreateType = { type ->
                viewModel.openUniversalCreate(defaultType = type)
            },
            onOpenVersionManager = { showVersionManager = true },
            onOpenTrash = { showTrashManagement = true },
            onOpenCloudAuth = { showCloudAuthSheet = true },
            onDismiss = { showCommandPalette = false }
        )
    }

    // Desktop Keyboard Shortcuts Cheat Sheet Dialog (Ctrl + ?)
    if (showShortcutsModal) {
        DesktopShortcutsModal(
            onDismiss = { showShortcutsModal = false }
        )
    }
}

@Composable
fun ZeroStateWelcome(onOpenSetup: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to ROLEORA",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Every Role. Your Way.\nAdaptive multi-role workspace engine for Movie Directors, Students, Developers, Photographers, and Farmers.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onOpenSetup,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .height(52.dp)
                .testTag("start_setup_button"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Your First Workspace", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SecurityAuditView(
    activeRole: com.example.roleora.data.model.RoleEntity,
    userEmail: String? = null,
    onRevokeOtherSessions: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Security & Device Isolation Center",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Zero-trust boundaries & cross-device sessions for ${activeRole.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PolishGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Active Privacy Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• Role Isolation: Data is strictly filtered by roleId (${activeRole.id.take(8)}...).\n• AI Engine: ${if (activeRole.aiEnabled) "Active with granular scope permissions" else "Strictly Disabled (Default)"}.\n• Persistence: Dual-layer Room SQLite (local) + Firebase Firestore (cloud encryption).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Cross-device security and connected sessions
        DeviceSessionManagerView(
            userEmail = userEmail,
            onRevokeAllOtherSessions = onRevokeOtherSessions
        )
    }
}
