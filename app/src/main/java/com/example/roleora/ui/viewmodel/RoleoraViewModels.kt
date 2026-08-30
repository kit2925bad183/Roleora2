package com.example.roleora.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roleora.data.auth.AuthState
import com.example.roleora.data.auth.FirebaseAuthManager
import com.example.roleora.data.cloud.CloudSyncState
import com.example.roleora.data.cloud.FirestoreSyncManager
import com.example.roleora.data.model.AttachmentEntity
import com.example.roleora.data.model.AuditEventEntity
import com.example.roleora.data.model.DiaryEntryEntity
import com.example.roleora.data.model.EntryEntity
import com.example.roleora.data.model.EntryType
import com.example.roleora.data.model.EntryVersionEntity
import com.example.roleora.data.model.EventEntity
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.data.model.ProfessionTemplateEntity
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.SecurityLevel
import com.example.roleora.data.model.SessionEntity
import com.example.roleora.data.model.TaskEntity
import com.example.roleora.data.model.TaskPriority
import com.example.roleora.data.model.TaskStatus
import com.example.roleora.data.model.TemplateDiffResult
import com.example.roleora.data.model.TemplateInstallationEntity
import com.example.roleora.data.model.TemplateVersionEntity
import com.example.roleora.data.model.UserEntity
import com.example.roleora.data.model.WorkSessionEntity
import com.example.roleora.data.model.WorkspaceTemplateVersionEntity
import com.example.roleora.data.repository.RoleoraRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Main application ViewModel handling Active Role State, Role Switching,
 * Version Updates, Universal Diary, Universal Create, Timeline, Tasks, Events,
 * Media Attachments, Work Timer, Navigation, Firebase Auth, and Firestore Cloud Sync.
 */
class RoleoraViewModel(
    private val repository: RoleoraRepository,
    val authManager: FirebaseAuthManager,
    val syncManager: FirestoreSyncManager
) : ViewModel() {

    val authState: StateFlow<AuthState> = authManager.authState
    val syncState: StateFlow<CloudSyncState> = syncManager.syncState

    private val _isFetchingFirestoreRoles = MutableStateFlow(false)
    val isFetchingFirestoreRoles: StateFlow<Boolean> = _isFetchingFirestoreRoles.asStateFlow()

    val activeRoles: StateFlow<List<RoleEntity>> = repository.allActiveRoles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: StateFlow<List<ProfessionTemplateEntity>> = repository.allTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeRoleId = MutableStateFlow<String?>(null)
    val activeRoleId: StateFlow<String?> = _activeRoleId.asStateFlow()

    private val _activeRole = MutableStateFlow<RoleEntity?>(null)
    val activeRole: StateFlow<RoleEntity?> = _activeRole.asStateFlow()

    private val _diaryEntries = MutableStateFlow<List<DiaryEntryEntity>>(emptyList())
    val diaryEntries: StateFlow<List<DiaryEntryEntity>> = _diaryEntries.asStateFlow()

    private val _professionRecords = MutableStateFlow<List<ProfessionRecordEntity>>(emptyList())
    val professionRecords: StateFlow<List<ProfessionRecordEntity>> = _professionRecords.asStateFlow()

    private val _availableVersions = MutableStateFlow<List<TemplateVersionEntity>>(emptyList())
    val availableVersions: StateFlow<List<TemplateVersionEntity>> = _availableVersions.asStateFlow()

    private val _workspaceTemplateVersions = MutableStateFlow<List<WorkspaceTemplateVersionEntity>>(emptyList())
    val workspaceTemplateVersions: StateFlow<List<WorkspaceTemplateVersionEntity>> = _workspaceTemplateVersions.asStateFlow()

    private val _selectedVersionForDiff = MutableStateFlow<WorkspaceTemplateVersionEntity?>(null)
    val selectedVersionForDiff: StateFlow<WorkspaceTemplateVersionEntity?> = _selectedVersionForDiff.asStateFlow()

    private val _versionDiffResult = MutableStateFlow<TemplateDiffResult?>(null)
    val versionDiffResult: StateFlow<TemplateDiffResult?> = _versionDiffResult.asStateFlow()

    private val _isSaveVersionDialogOpen = MutableStateFlow(false)
    val isSaveVersionDialogOpen: StateFlow<Boolean> = _isSaveVersionDialogOpen.asStateFlow()

    private val _roleInstallation = MutableStateFlow<TemplateInstallationEntity?>(null)
    val roleInstallation: StateFlow<TemplateInstallationEntity?> = _roleInstallation.asStateFlow()

    private val _auditEvents = MutableStateFlow<List<AuditEventEntity>>(emptyList())
    val auditEvents: StateFlow<List<AuditEventEntity>> = _auditEvents.asStateFlow()

    private val _operationStatus = MutableStateFlow<String?>(null)
    val operationStatus: StateFlow<String?> = _operationStatus.asStateFlow()

    // -------------------------------------------------------------------------
    // Phase 2: Universal Entries, Timeline & Filtering
    // -------------------------------------------------------------------------
    private val _universalEntries = MutableStateFlow<List<EntryEntity>>(emptyList())
    val universalEntries: StateFlow<List<EntryEntity>> = _universalEntries.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedEntryTypeFilter = MutableStateFlow<String?>("ALL")
    val selectedEntryTypeFilter: StateFlow<String?> = _selectedEntryTypeFilter.asStateFlow()

    private val _selectedMultiRoleIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedMultiRoleIds: StateFlow<Set<String>> = _selectedMultiRoleIds.asStateFlow()

    private val _isMultiRoleTimelineActive = MutableStateFlow(false)
    val isMultiRoleTimelineActive: StateFlow<Boolean> = _isMultiRoleTimelineActive.asStateFlow()

    private val _timelineSortOrder = MutableStateFlow("Newest") // Newest, Oldest, ActivityDate
    val timelineSortOrder: StateFlow<String> = _timelineSortOrder.asStateFlow()

    // -------------------------------------------------------------------------
    // Phase 2: Tasks & Reminders
    // -------------------------------------------------------------------------
    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    private val _taskFilterStatus = MutableStateFlow<String?>("ALL")
    val taskFilterStatus: StateFlow<String?> = _taskFilterStatus.asStateFlow()

    // -------------------------------------------------------------------------
    // Phase 2: Events & Calendar
    // -------------------------------------------------------------------------
    private val _events = MutableStateFlow<List<EventEntity>>(emptyList())
    val events: StateFlow<List<EventEntity>> = _events.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow(System.currentTimeMillis())
    val selectedCalendarDate: StateFlow<Long> = _selectedCalendarDate.asStateFlow()

    // -------------------------------------------------------------------------
    // Phase 2: Work Timer
    // -------------------------------------------------------------------------
    private val _activeWorkSession = MutableStateFlow<WorkSessionEntity?>(null)
    val activeWorkSession: StateFlow<WorkSessionEntity?> = _activeWorkSession.asStateFlow()

    private val _timerElapsedSeconds = MutableStateFlow(0L)
    val timerElapsedSeconds: StateFlow<Long> = _timerElapsedSeconds.asStateFlow()

    private var timerJob: Job? = null

    // -------------------------------------------------------------------------
    // Phase 2: Trash & Version History
    // -------------------------------------------------------------------------
    private val _trashEntries = MutableStateFlow<List<EntryEntity>>(emptyList())
    val trashEntries: StateFlow<List<EntryEntity>> = _trashEntries.asStateFlow()

    private val _trashTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val trashTasks: StateFlow<List<TaskEntity>> = _trashTasks.asStateFlow()

    private val _trashEvents = MutableStateFlow<List<EventEntity>>(emptyList())
    val trashEvents: StateFlow<List<EventEntity>> = _trashEvents.asStateFlow()

    private val _activeEntryVersions = MutableStateFlow<List<EntryVersionEntity>>(emptyList())
    val activeEntryVersions: StateFlow<List<EntryVersionEntity>> = _activeEntryVersions.asStateFlow()

    // -------------------------------------------------------------------------
    // Phase 2: Universal Create Dialog State
    // -------------------------------------------------------------------------
    private val _isUniversalCreateOpen = MutableStateFlow(false)
    val isUniversalCreateOpen: StateFlow<Boolean> = _isUniversalCreateOpen.asStateFlow()

    private val _universalCreateDefaultType = MutableStateFlow(EntryType.DIARY)
    val universalCreateDefaultType: StateFlow<EntryType> = _universalCreateDefaultType.asStateFlow()

    init {
        // Auto-select first active role when available
        viewModelScope.launch {
            repository.allActiveRoles.collect { roles ->
                if (_activeRoleId.value == null && roles.isNotEmpty()) {
                    selectRole(roles.first().id)
                } else if (_activeRoleId.value != null && roles.none { it.id == _activeRoleId.value }) {
                    if (roles.isNotEmpty()) selectRole(roles.first().id) else _activeRoleId.value = null
                }
            }
        }

        // Observe auth state and persist local user profile & session in Room for offline capability
        viewModelScope.launch {
            authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        val userEntity = UserEntity(
                            userId = state.uid,
                            email = state.email ?: "",
                            displayName = state.displayName ?: "User",
                            photoUrl = state.photoUrl,
                            isEmailVerified = true,
                            lastLoginAt = System.currentTimeMillis()
                        )
                        repository.saveLocalUser(userEntity)

                        val sessionEntity = SessionEntity(
                            sessionId = UUID.randomUUID().toString(),
                            userId = state.uid,
                            loginProvider = if (state.email?.contains("gmail") == true) "google" else "email",
                            isActive = true,
                            lastActiveAt = System.currentTimeMillis()
                        )
                        repository.saveSession(sessionEntity)
                    }
                    else -> {}
                }
            }
        }
    }

    fun selectRole(roleId: String) {
        // Clear stale data when switching roles to prevent data leak between isolated roles
        _diaryEntries.value = emptyList()
        _professionRecords.value = emptyList()
        _availableVersions.value = emptyList()
        _roleInstallation.value = null
        _universalEntries.value = emptyList()
        _tasks.value = emptyList()
        _events.value = emptyList()
        _trashEntries.value = emptyList()
        _trashTasks.value = emptyList()
        _trashEvents.value = emptyList()
        _activeRoleId.value = roleId

        viewModelScope.launch {
            val role = repository.getRoleById(roleId)
            _activeRole.value = role

            if (role != null) {
                // Collect diary entries
                launch {
                    repository.getEntriesForRole(roleId).collect {
                        _diaryEntries.value = it
                    }
                }
                // Collect profession records
                launch {
                    repository.getAllRecordsForRole(roleId).collect {
                        _professionRecords.value = it
                    }
                }
                // Collect available template versions
                launch {
                    repository.getVersionsForTemplate(role.templateId).collect {
                        _availableVersions.value = it
                    }
                }
                // Collect workspace template versions and snapshots
                launch {
                    repository.getWorkspaceTemplateVersions(roleId).collect {
                        _workspaceTemplateVersions.value = it
                    }
                }
                // Collect installation metadata
                launch {
                    repository.observeInstallationForRole(roleId).collect {
                        _roleInstallation.value = it
                    }
                }
                // Collect audit events
                launch {
                    repository.getAuditEventsForRole(roleId).collect {
                        _auditEvents.value = it
                    }
                }
                // Collect Phase 2 Universal Entries
                launch {
                    repository.getUniversalEntriesForRole(roleId).collect {
                        _universalEntries.value = it
                    }
                }
                // Collect Phase 2 Tasks
                launch {
                    repository.getTasksForRole(roleId).collect {
                        _tasks.value = it
                    }
                }
                // Collect Phase 2 Events
                launch {
                    repository.getEventsForRole(roleId).collect {
                        _events.value = it
                    }
                }
                // Collect Trash
                launch {
                    repository.getUniversalTrashEntries(roleId).collect {
                        _trashEntries.value = it
                    }
                }
                launch {
                    repository.getTrashTasks(roleId).collect {
                        _trashTasks.value = it
                    }
                }
                launch {
                    repository.getTrashEvents(roleId).collect {
                        _trashEvents.value = it
                    }
                }
                // Collect active work session
                launch {
                    repository.observeLatestWorkSession(roleId).collect { session ->
                        _activeWorkSession.value = session
                        if (session != null && session.isRunning && !session.isPaused) {
                            startTimerTicker(session.startTime, session.pausedDurationMs)
                        } else {
                            timerJob?.cancel()
                            if (session != null) {
                                val total = if (session.totalDurationMs > 0) session.totalDurationMs else maxOf(0L, (System.currentTimeMillis() - session.startTime) - session.pausedDurationMs)
                                _timerElapsedSeconds.value = total / 1000L
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startTimerTicker(startTime: Long, pausedMs: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val activeMs = maxOf(0L, (now - startTime) - pausedMs)
                _timerElapsedSeconds.value = activeMs / 1000L
                delay(1000L)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Phase 2: Universal Create Controls
    // -------------------------------------------------------------------------
    fun openUniversalCreate(defaultType: EntryType = EntryType.DIARY) {
        _universalCreateDefaultType.value = defaultType
        _isUniversalCreateOpen.value = true
    }

    fun closeUniversalCreate() {
        _isUniversalCreateOpen.value = false
    }

    fun saveUniversalEntry(
        roleId: String,
        entryType: EntryType,
        title: String,
        content: String,
        activityDateTime: Long = System.currentTimeMillis(),
        tags: String = "",
        securityLevel: SecurityLevel = SecurityLevel.ROLE_RESTRICTED,
        folderId: String? = null,
        projectId: String? = null,
        diaryMood: String? = null,
        diaryType: String = "Personal"
    ) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid ?: "local_owner"
            val entry = EntryEntity(
                entryId = UUID.randomUUID().toString(),
                ownerId = userUid,
                roleId = roleId,
                entryType = entryType.name,
                title = title,
                content = content,
                activityDateTime = activityDateTime,
                tags = tags,
                securityLevel = securityLevel.name,
                folderId = folderId,
                projectId = projectId,
                diaryMood = diaryMood,
                diaryType = diaryType
            )
            repository.saveUniversalEntry(entry, actorUid = userUid)
            _operationStatus.value = "Created ${entryType.displayName}: $title"

            // Auto-persist to cloud if authenticated
            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveUniversalEntry(currentAuth.uid, roleId, entry)
            }
        }
    }

    fun updateUniversalEntry(entry: EntryEntity, changeReason: String? = null) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid ?: "local_owner"
            repository.saveUniversalEntry(entry, changeReason = changeReason, actorUid = userUid)
            _operationStatus.value = "Updated '${entry.title}'"

            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveUniversalEntry(currentAuth.uid, entry.roleId, entry)
            }
        }
    }

    fun moveToTrashUniversalEntry(entryId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.moveToTrashUniversalEntry(entryId, actorUid = userUid)
            _operationStatus.value = "Moved entry to trash"
        }
    }

    fun restoreUniversalEntry(entryId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.restoreUniversalEntry(entryId, actorUid = userUid)
            _operationStatus.value = "Entry restored"
        }
    }

    fun deleteUniversalEntryPermanently(entryId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.deleteUniversalEntryPermanently(entryId, actorUid = userUid)
            _operationStatus.value = "Entry deleted permanently"
        }
    }

    fun emptyWorkspaceTrash() {
        val roleId = _activeRoleId.value ?: return
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.emptyTrashUniversalEntries(roleId, actorUid = userUid)
            _operationStatus.value = "Trash emptied"
        }
    }

    // -------------------------------------------------------------------------
    // Phase 2: Version History
    // -------------------------------------------------------------------------
    fun loadVersionHistory(entryId: String) {
        viewModelScope.launch {
            repository.getVersionsForEntry(entryId).collect {
                _activeEntryVersions.value = it
            }
        }
    }

    fun rollbackToVersion(entryId: String, versionId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.restoreVersionAsNewVersion(entryId, versionId, actorUid = userUid)
            _operationStatus.value = "Restored previous version snapshot"
        }
    }

    // -------------------------------------------------------------------------
    // Phase 2: Tasks Actions
    // -------------------------------------------------------------------------
    fun saveTask(
        roleId: String,
        title: String,
        description: String = "",
        priority: TaskPriority = TaskPriority.MEDIUM,
        dueDate: Long? = null,
        dueTime: String? = null,
        recurrence: String = "None",
        subtasksJson: String = "[]",
        tags: String = ""
    ) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid ?: "local_owner"
            val task = TaskEntity(
                taskId = UUID.randomUUID().toString(),
                ownerId = userUid,
                roleId = roleId,
                title = title,
                description = description,
                priority = priority.name,
                status = TaskStatus.NOT_STARTED.name,
                dueDate = dueDate,
                dueTime = dueTime,
                recurrence = recurrence,
                subtasksJson = subtasksJson,
                tags = tags
            )
            repository.saveTask(task, actorUid = userUid)
            _operationStatus.value = "Task created: $title"

            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveUniversalTask(currentAuth.uid, roleId, task)
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.saveTask(task, actorUid = userUid)
            _operationStatus.value = "Task updated: ${task.title}"

            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveUniversalTask(currentAuth.uid, task.roleId, task)
            }
        }
    }

    fun toggleTaskComplete(taskId: String, currentStatus: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            if (currentStatus == TaskStatus.COMPLETED.name) {
                repository.reopenTask(taskId, actorUid = userUid)
                _operationStatus.value = "Task reopened"
            } else {
                repository.completeTask(taskId, actorUid = userUid)
                _operationStatus.value = "Task completed!"
            }
        }
    }

    fun duplicateTask(taskId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            val newId = repository.duplicateTask(taskId, actorUid = userUid)
            if (newId.isNotEmpty()) {
                _operationStatus.value = "Task duplicated"
            }
        }
    }

    fun moveToTrashTask(taskId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.moveToTrashTask(taskId, actorUid = userUid)
            _operationStatus.value = "Task moved to trash"
        }
    }

    fun restoreTask(taskId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.restoreTask(taskId, actorUid = userUid)
            _operationStatus.value = "Task restored"
        }
    }

    fun deleteTaskPermanently(taskId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.deleteTaskPermanently(taskId, actorUid = userUid)
            _operationStatus.value = "Task permanently deleted"
        }
    }

    fun setTaskFilter(status: String?) {
        _taskFilterStatus.value = status
    }

    // -------------------------------------------------------------------------
    // Phase 2: Events Actions
    // -------------------------------------------------------------------------
    fun saveEvent(
        roleId: String,
        title: String,
        description: String = "",
        startDateTime: Long,
        endDateTime: Long,
        isAllDay: Boolean = false,
        location: String? = null,
        repeatPattern: String = "None",
        reminderMinutesBefore: Int = 15
    ) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid ?: "local_owner"
            val event = EventEntity(
                eventId = UUID.randomUUID().toString(),
                ownerId = userUid,
                roleId = roleId,
                title = title,
                description = description,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                isAllDay = isAllDay,
                location = location,
                repeatPattern = repeatPattern,
                reminderMinutesBefore = reminderMinutesBefore
            )
            repository.saveEvent(event, actorUid = userUid)
            _operationStatus.value = "Event scheduled: $title"

            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveUniversalEvent(currentAuth.uid, roleId, event)
            }
        }
    }

    fun moveToTrashEvent(eventId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.moveToTrashEvent(eventId, actorUid = userUid)
            _operationStatus.value = "Event moved to trash"
        }
    }

    fun restoreEvent(eventId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.restoreEvent(eventId, actorUid = userUid)
            _operationStatus.value = "Event restored"
        }
    }

    fun deleteEventPermanently(eventId: String) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid
            repository.deleteEventPermanently(eventId, actorUid = userUid)
            _operationStatus.value = "Event permanently deleted"
        }
    }

    fun selectCalendarDate(dateMillis: Long) {
        _selectedCalendarDate.value = dateMillis
    }

    // -------------------------------------------------------------------------
    // Phase 2: Work Session Timer Actions
    // -------------------------------------------------------------------------
    fun startWorkTimer(roleId: String, description: String, linkedTaskId: String? = null) {
        viewModelScope.launch {
            val userUid = (authState.value as? AuthState.Authenticated)?.uid ?: "local_owner"
            val session = repository.startWorkSession(roleId, userUid, description, linkedTaskId)
            _activeWorkSession.value = session
            startTimerTicker(session.startTime, session.pausedDurationMs)
            _operationStatus.value = "Work timer started"
        }
    }

    fun pauseWorkTimer() {
        val session = _activeWorkSession.value ?: return
        viewModelScope.launch {
            repository.pauseWorkSession(session)
            timerJob?.cancel()
            _operationStatus.value = "Timer paused"
        }
    }

    fun resumeWorkTimer() {
        val session = _activeWorkSession.value ?: return
        viewModelScope.launch {
            repository.resumeWorkSession(session)
            startTimerTicker(session.startTime, session.pausedDurationMs)
            _operationStatus.value = "Timer resumed"
        }
    }

    fun stopWorkTimer(saveAsDiary: Boolean = true) {
        val session = _activeWorkSession.value ?: return
        viewModelScope.launch {
            timerJob?.cancel()
            val diary = repository.stopWorkSession(session, saveAsDiary)
            _activeWorkSession.value = null
            _timerElapsedSeconds.value = 0L
            _operationStatus.value = if (diary != null) "Focus session saved to Diary" else "Timer finished"
        }
    }

    fun cancelWorkTimer() {
        val session = _activeWorkSession.value ?: return
        viewModelScope.launch {
            timerJob?.cancel()
            repository.cancelWorkSession(session.sessionId, session.roleId)
            _activeWorkSession.value = null
            _timerElapsedSeconds.value = 0L
            _operationStatus.value = "Timer cancelled"
        }
    }

    // -------------------------------------------------------------------------
    // Phase 2: Timeline Filters & Multi-Role Query
    // -------------------------------------------------------------------------
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setEntryTypeFilter(type: String?) {
        _selectedEntryTypeFilter.value = type
    }

    fun setTimelineSortOrder(order: String) {
        _timelineSortOrder.value = order
    }

    fun toggleMultiRoleSelection(roleId: String) {
        val current = _selectedMultiRoleIds.value.toMutableSet()
        if (current.contains(roleId)) current.remove(roleId) else current.add(roleId)
        _selectedMultiRoleIds.value = current
    }

    fun setMultiRoleTimelineActive(active: Boolean) {
        _isMultiRoleTimelineActive.value = active
    }

    // --- Authentication Actions ---
    fun signUpWithEmail(email: String, pass: String, name: String) {
        viewModelScope.launch {
            val result = authManager.signUpWithEmail(email, pass, name)
            if (result.isSuccess) {
                _operationStatus.value = "Registered successfully. Verification email sent."
                syncAllWithCloud()
            } else {
                _operationStatus.value = "Registration error: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            val result = authManager.signInWithEmail(email, pass)
            if (result.isSuccess) {
                _operationStatus.value = "Welcome back, ${result.getOrNull()?.displayName ?: "User"}"
                syncAllWithCloud()
            } else {
                _operationStatus.value = "Login error: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            val result = authManager.sendPasswordReset(email)
            if (result.isSuccess) {
                _operationStatus.value = "Password reset instructions sent to $email"
            } else {
                _operationStatus.value = "Password reset failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun updateUserProfile(displayName: String, photoUrl: String? = null) {
        viewModelScope.launch {
            val result = authManager.updateProfile(displayName, photoUrl)
            if (result.isSuccess) {
                _operationStatus.value = "Profile updated"
            } else {
                _operationStatus.value = "Profile update failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun signInWithGoogle(activityContext: Context, webClientId: String? = null) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle(activityContext, webClientId)
            if (result.isSuccess) {
                _operationStatus.value = "Signed in as ${result.getOrNull()?.displayName ?: "Google User"}"
                syncAllWithCloud()
            } else {
                _operationStatus.value = "Sign in note: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun signInWithDemoGoogleAccount(
        email: String = "kit29.25bad183@gmail.com",
        name: String = "Google Verified Specialist"
    ) {
        authManager.signInWithDemoGoogleAccount(email, name)
        _operationStatus.value = "Signed in as $email"
        syncAllWithCloud()
    }

    fun signOut() {
        val currentAuth = authState.value
        viewModelScope.launch {
            if (currentAuth is AuthState.Authenticated) {
                repository.deactivateAllSessionsForUser(currentAuth.uid)
            }
            authManager.signOut()
            _operationStatus.value = "Signed out"
        }
    }

    fun deleteEntireAccount() {
        val currentAuth = authState.value
        viewModelScope.launch {
            if (currentAuth is AuthState.Authenticated) {
                syncManager.deleteEntireUserAccount(currentAuth.uid)
                authManager.deleteUserAccount()
            }
            repository.clearAllLocalData()
            _activeRoleId.value = null
            _activeRole.value = null
            _diaryEntries.value = emptyList()
            _professionRecords.value = emptyList()
            _operationStatus.value = "Account and all local/cloud data completely wiped"
        }
    }

    fun archiveRole(roleId: String) {
        viewModelScope.launch {
            repository.archiveRole(roleId)
            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.archiveRole(currentAuth.uid, roleId, true)
            }
            if (_activeRoleId.value == roleId) {
                _activeRoleId.value = null
            }
            _operationStatus.value = "Role archived"
        }
    }

    fun deleteRole(roleId: String) {
        viewModelScope.launch {
            repository.deleteRole(roleId)
            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.deleteRole(currentAuth.uid, roleId)
            }
            if (_activeRoleId.value == roleId) {
                _activeRoleId.value = null
            }
            _operationStatus.value = "Role deleted permanently"
        }
    }

    // --- Cloud Firestore Sync Actions ---
    fun syncAllWithCloud() {
        val currentAuth = authState.value
        if (currentAuth !is AuthState.Authenticated) {
            _operationStatus.value = "Please sign in with Google to sync"
            return
        }

        viewModelScope.launch {
            val roles = repository.getAllActiveRolesList()
            val records = repository.getAllRecordsList()
            val entries = repository.getAllEntriesList()

            val result = syncManager.backupAllToFirestore(
                userId = currentAuth.uid,
                roles = roles,
                records = records,
                entries = entries,
                auditEvents = emptyList()
            )

            if (result.isSuccess) {
                _operationStatus.value = "All workspaces successfully synced to Firestore"
            } else {
                _operationStatus.value = "Firestore sync alert: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun restoreFromCloud() {
        val currentAuth = authState.value
        if (currentAuth !is AuthState.Authenticated) {
            _operationStatus.value = "Please sign in with Google to restore"
            return
        }

        viewModelScope.launch {
            val result = syncManager.restoreFromFirestore(currentAuth.uid)
            if (result.isSuccess) {
                val payload = result.getOrNull()
                if (payload != null) {
                    repository.importCloudData(
                        roles = payload.roles,
                        records = payload.records,
                        entries = payload.diaryEntries
                    )
                    _operationStatus.value = "Restored ${payload.roles.size} roles and ${payload.records.size} records from Firestore"
                }
            } else {
                _operationStatus.value = "Restore failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun fetchActiveRolesFromFirestore() {
        val currentAuth = authState.value
        if (currentAuth !is AuthState.Authenticated) {
            _operationStatus.value = "Please sign in with Google to fetch Firestore roles"
            return
        }

        viewModelScope.launch {
            _isFetchingFirestoreRoles.value = true
            val result = syncManager.fetchActiveRolesFromFirestore(currentAuth.uid)
            _isFetchingFirestoreRoles.value = false

            if (result.isSuccess) {
                val roles = result.getOrNull() ?: emptyList()
                if (roles.isNotEmpty()) {
                    repository.importCloudData(
                        roles = roles,
                        records = emptyList(),
                        entries = emptyList()
                    )
                    _operationStatus.value = "Fetched ${roles.size} active roles from Firestore"
                } else {
                    _operationStatus.value = "No remote roles found in Firestore"
                }
            } else {
                _operationStatus.value = "Firestore error: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun addDiaryEntry(title: String, content: String, entryType: String, tags: String = "") {
        val currentRole = _activeRole.value ?: return
        viewModelScope.launch {
            val entry = DiaryEntryEntity(
                id = UUID.randomUUID().toString(),
                roleId = currentRole.id,
                title = title,
                content = content,
                entryType = entryType,
                activityDate = System.currentTimeMillis(),
                tags = tags
            )
            repository.insertEntry(entry)
            _operationStatus.value = "Entry saved successfully"

            // Auto-persist to Firestore if user is authenticated
            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveDiaryEntry(currentAuth.uid, entry)
            }
        }
    }

    fun deleteDiaryEntry(entryId: String) {
        viewModelScope.launch {
            repository.deleteEntry(entryId)
            _operationStatus.value = "Entry removed"
        }
    }

    fun addProfessionRecord(record: ProfessionRecordEntity) {
        viewModelScope.launch {
            repository.insertRecord(record)
            _operationStatus.value = "${record.title} saved"

            // Auto-persist to Firestore if user is authenticated
            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveRecord(currentAuth.uid, record)
            }
        }
    }

    fun updateProfessionRecord(record: ProfessionRecordEntity) {
        viewModelScope.launch {
            repository.updateRecord(record)
            _operationStatus.value = "${record.title} updated"

            // Auto-persist to Firestore if user is authenticated
            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveRecord(currentAuth.uid, record)
            }
        }
    }

    fun deleteProfessionRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteRecord(recordId)
            _operationStatus.value = "Record deleted"
        }
    }

    fun upgradeTemplate(newVersion: TemplateVersionEntity) {
        val currentRole = _activeRole.value ?: return
        viewModelScope.launch {
            repository.upgradeTemplateVersion(currentRole, newVersion)
            val updated = currentRole.copy(templateVersion = newVersion.versionNumber)
            _activeRole.value = updated
            _operationStatus.value = "Upgraded to v${newVersion.versionNumber}"

            // Auto-persist to Firestore
            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveRole(currentAuth.uid, updated)
            }
        }
    }

    fun rollbackTemplate(previousVersion: String) {
        val currentRole = _activeRole.value ?: return
        viewModelScope.launch {
            repository.rollbackTemplateVersion(currentRole, previousVersion)
            val updated = currentRole.copy(templateVersion = previousVersion)
            _activeRole.value = updated
            _operationStatus.value = "Rolled back to v$previousVersion"

            // Auto-persist to Firestore
            val currentAuth = authState.value
            if (currentAuth is AuthState.Authenticated) {
                syncManager.saveRole(currentAuth.uid, updated)
            }
        }
    }

    // --- Workspace-Level Versioned Template Snapshot Actions ---
    fun openSaveVersionDialog() {
        _isSaveVersionDialogOpen.value = true
    }

    fun closeSaveVersionDialog() {
        _isSaveVersionDialogOpen.value = false
    }

    fun saveWorkspaceTemplateVersion(
        label: String,
        changeSummary: String,
        customVersionNumber: String = "",
        modules: List<String>? = null,
        workflow: String? = null,
        tags: String = ""
    ) {
        val currentRole = _activeRole.value ?: return
        val userUid = (authState.value as? AuthState.Authenticated)?.uid ?: "local_owner"

        viewModelScope.launch {
            val saved = repository.saveWorkspaceTemplateVersion(
                roleId = currentRole.id,
                versionLabel = label,
                changeSummary = changeSummary,
                customVersionNumber = customVersionNumber.ifBlank { null },
                authorId = userUid,
                customModules = modules,
                customWorkflow = workflow,
                tags = tags
            )
            if (saved != null) {
                _activeRole.value = currentRole.copy(templateVersion = saved.versionNumber)
                _operationStatus.value = "Saved template configuration '${saved.versionLabel}' (v${saved.versionNumber})"
                _isSaveVersionDialogOpen.value = false

                // Auto-sync role state to cloud
                val currentAuth = authState.value
                if (currentAuth is AuthState.Authenticated) {
                    syncManager.saveRole(currentAuth.uid, _activeRole.value!!)
                }
            }
        }
    }

    fun revertWorkspaceToVersion(versionId: String, createBackupBeforeRevert: Boolean = true) {
        val currentRole = _activeRole.value ?: return
        viewModelScope.launch {
            val result = repository.revertWorkspaceToVersion(
                roleId = currentRole.id,
                versionId = versionId,
                createBackupBeforeRevert = createBackupBeforeRevert
            )
            result.onSuccess { restoredRole ->
                _activeRole.value = restoredRole
                _operationStatus.value = "Workspace successfully restored to v${restoredRole.templateVersion}"
                _selectedVersionForDiff.value = null
                _versionDiffResult.value = null

                val currentAuth = authState.value
                if (currentAuth is AuthState.Authenticated) {
                    syncManager.saveRole(currentAuth.uid, restoredRole)
                }
            }.onFailure { err ->
                _operationStatus.value = "Revert error: ${err.message}"
            }
        }
    }

    fun compareWithActiveWorkspace(targetVersion: WorkspaceTemplateVersionEntity) {
        val currentRole = _activeRole.value ?: return
        val currentVersions = _workspaceTemplateVersions.value
        val activeVersionEntity = currentVersions.find { it.isCurrentActive } ?: WorkspaceTemplateVersionEntity(
            versionId = "active_current",
            roleId = currentRole.id,
            templateId = currentRole.templateId,
            versionNumber = currentRole.templateVersion,
            versionLabel = "Current Active Workspace",
            changeSummary = "Active in-memory configuration",
            isCurrentActive = true,
            roleDisplayName = currentRole.displayName,
            roleCategory = currentRole.category,
            specialisation = currentRole.specialisation,
            iconName = currentRole.iconName,
            colorHex = currentRole.colorHex,
            aiEnabled = currentRole.aiEnabled,
            enabledModulesJson = "[\"dashboard\",\"diary\",\"projects\",\"tasks\",\"calendar\",\"documents\",\"reports\"]",
            workflowStages = "Discovery, Formulation, Execution, Review, Delivery"
        )

        _selectedVersionForDiff.value = targetVersion
        _versionDiffResult.value = repository.compareWorkspaceTemplateVersions(
            baseVersion = activeVersionEntity,
            targetVersion = targetVersion
        )
    }

    fun clearVersionDiff() {
        _selectedVersionForDiff.value = null
        _versionDiffResult.value = null
    }

    fun deleteWorkspaceTemplateVersion(versionId: String) {
        viewModelScope.launch {
            val success = repository.deleteWorkspaceTemplateVersion(versionId)
            if (success) {
                _operationStatus.value = "Configuration snapshot deleted"
                if (_selectedVersionForDiff.value?.versionId == versionId) {
                    clearVersionDiff()
                }
            } else {
                _operationStatus.value = "Cannot delete active or locked configuration"
            }
        }
    }

    fun toggleWorkspaceVersionLock(versionId: String) {
        viewModelScope.launch {
            repository.toggleWorkspaceVersionLock(versionId)
        }
    }

    fun toggleWorkspaceVersionFavorite(versionId: String) {
        viewModelScope.launch {
            repository.toggleWorkspaceVersionFavorite(versionId)
        }
    }

    fun duplicateWorkspaceTemplateVersion(versionId: String, newLabel: String) {
        viewModelScope.launch {
            val duplicated = repository.duplicateWorkspaceTemplateVersion(versionId, newLabel)
            if (duplicated != null) {
                _operationStatus.value = "Duplicated configuration '${duplicated.versionLabel}'"
            }
        }
    }

    fun clearStatus() {
        _operationStatus.value = null
    }
}

/**
 * State and ViewModel for the Adaptive Multi-Step Setup Interview.
 */
data class SetupState(
    val currentStep: Int = 1, // 1 to 8
    val selectedTemplate: ProfessionTemplateEntity? = null,
    val specialisation: String = "",
    val customDepartment: String = "",
    val roleTitle: String = "",
    val institutionOrOrg: String = "",
    val experienceLevel: String = "Intermediate",
    val workType: String = "Independent", // Independent, Team, Organisation
    val teamSize: String = "1-5",
    val location: String = "Remote / Local",
    val language: String = "English",
    val selectedNeeds: Set<String> = emptySet(),
    val preferredWorkFormats: Set<String> = setOf("Diary", "Projects"),
    val collaborationPreference: String = "Work privately",
    val aiChoice: String = "Do not use AI",
    val selectedAiPermissions: Set<String> = emptySet(),
    val enabledModules: MutableList<String> = mutableListOf(),
    val roleColorHex: String = "#8B5CF6",
    val isCreating: Boolean = false,
    val createdRoleId: String? = null,
    val errorMessage: String? = null
)

class SetupViewModel(
    private val repository: RoleoraRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SetupState())
    val state: StateFlow<SetupState> = _state.asStateFlow()

    val availableTemplates: StateFlow<List<ProfessionTemplateEntity>> = repository.allTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTemplate(template: ProfessionTemplateEntity) {
        val defaultSpecialisation = template.defaultSpecialisations.split(",").firstOrNull()?.trim() ?: "General"
        val defaultModules = listOf(
            "dashboard", "diary", "projects", "tasks", "calendar", "documents", "reports"
        )

        _state.value = _state.value.copy(
            selectedTemplate = template,
            specialisation = defaultSpecialisation,
            roleTitle = template.name,
            roleColorHex = template.defaultColor,
            enabledModules = defaultModules.toMutableList(),
            currentStep = 2
        )
    }

    fun updateSpecialisation(specialisation: String, customDepartment: String = "") {
        _state.value = _state.value.copy(
            specialisation = specialisation,
            customDepartment = customDepartment
        )
    }

    fun updateWorkProfile(
        roleTitle: String,
        institutionOrOrg: String,
        experienceLevel: String,
        workType: String,
        teamSize: String,
        location: String,
        language: String
    ) {
        _state.value = _state.value.copy(
            roleTitle = roleTitle,
            institutionOrOrg = institutionOrOrg,
            experienceLevel = experienceLevel,
            workType = workType,
            teamSize = teamSize,
            location = location,
            language = language
        )
    }

    fun toggleNeed(need: String) {
        val current = _state.value.selectedNeeds.toMutableSet()
        if (current.contains(need)) current.remove(need) else current.add(need)
        _state.value = _state.value.copy(selectedNeeds = current)
    }

    fun toggleWorkFormat(format: String) {
        val current = _state.value.preferredWorkFormats.toMutableSet()
        if (current.contains(format)) {
            if (current.size > 1) current.remove(format)
        } else {
            current.add(format)
        }
        _state.value = _state.value.copy(preferredWorkFormats = current)
    }

    fun updateCollaboration(collab: String) {
        _state.value = _state.value.copy(collaborationPreference = collab)
    }

    fun updateAiChoice(choice: String, permissions: Set<String> = emptySet()) {
        _state.value = _state.value.copy(
            aiChoice = choice,
            selectedAiPermissions = permissions
        )
    }

    fun toggleModule(moduleId: String) {
        val current = _state.value.enabledModules.toMutableList()
        if (current.contains(moduleId)) {
            if (current.size > 2) current.remove(moduleId)
        } else {
            current.add(moduleId)
        }
        _state.value = _state.value.copy(enabledModules = current)
    }

    fun updateColor(colorHex: String) {
        _state.value = _state.value.copy(roleColorHex = colorHex)
    }

    fun goToStep(step: Int) {
        if (step in 1..8) {
            _state.value = _state.value.copy(currentStep = step)
        }
    }

    fun nextStep() {
        if (_state.value.currentStep < 8) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep + 1)
        }
    }

    fun previousStep() {
        if (_state.value.currentStep > 1) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep - 1)
        }
    }

    fun createWorkspace(onSuccess: (String) -> Unit) {
        val s = _state.value
        val template = s.selectedTemplate ?: return

        _state.value = _state.value.copy(isCreating = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val roleId = UUID.randomUUID().toString()
                val roleEntity = RoleEntity(
                    id = roleId,
                    templateId = template.id,
                    templateVersion = template.currentVersion,
                    displayName = if (s.roleTitle.isNotBlank()) s.roleTitle else template.name,
                    category = template.category,
                    specialisation = if (s.customDepartment.isNotBlank()) s.customDepartment else s.specialisation,
                    roleTitle = s.roleTitle,
                    institutionOrOrg = s.institutionOrOrg,
                    experienceLevel = s.experienceLevel,
                    workType = s.workType,
                    workFormat = s.preferredWorkFormats.joinToString(", "),
                    teamSize = s.teamSize,
                    location = s.location,
                    language = s.language,
                    iconName = template.iconName,
                    colorHex = s.roleColorHex,
                    aiEnabled = s.aiChoice != "Do not use AI",
                    aiPermissions = s.selectedAiPermissions.joinToString(",", prefix = "[", postfix = "]")
                )

                // Generate initial structured starter records based on profession
                val starterRecords = createStarterRecords(roleId, template.id, s.specialisation)

                repository.createRole(roleEntity, starterRecords)
                _state.value = _state.value.copy(isCreating = false, createdRoleId = roleId)
                onSuccess(roleId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isCreating = false, errorMessage = e.message ?: "Failed to create workspace")
            }
        }
    }

    private fun createStarterRecords(roleId: String, templateId: String, specialisation: String): List<ProfessionRecordEntity> {
        val now = System.currentTimeMillis()
        return when (templateId) {
            "movie_director" -> listOf(
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "DIRECTOR",
                    recordCategory = "PRODUCTION",
                    title = "Echoes of Silence ($specialisation)",
                    subtitle = "Principal Pre-Production",
                    stage = "Pre-production",
                    status = "Active",
                    numericValue1 = 125000.0, // Budget
                    detailsJson = "{\"synopsis\":\"A gripping atmospheric narrative exploring human resilience.\",\"director\":\"Lead Director\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "DIRECTOR",
                    recordCategory = "SCREENPLAY",
                    title = "Scene 1: EXT. TEMPLE COURTYARD - DAWN",
                    subtitle = "Opening Establishing Sequence",
                    stage = "Ready",
                    numericValue1 = 1.0, // Scene number
                    numericValue2 = 4.0, // Estimated duration (mins)
                    detailsJson = "{\"heading\":\"EXT. TEMPLE COURTYARD - DAWN\",\"action\":\"Mist rises from the ancient stone floor. Bell tolls softly in distance.\",\"character\":\"MARAN (40s)\",\"dialogue\":\"The dawn brings no forgiveness, only memories.\",\"cast\":\"Maran, Extra Monks (4)\",\"props\":\"Brass bell, Oil lamp, Bamboo stick\",\"location\":\"Thanjavur Temple Premises\",\"lighting\":\"Natural golden morning light\",\"sound\":\"Distant river flowing, Temple chants\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "DIRECTOR",
                    recordCategory = "SHOT",
                    title = "Shot 1A: Wide Establishing Crane",
                    subtitle = "Scene 1 • 50mm Prime",
                    stage = "Planned",
                    numericValue1 = 1.0, // Scene 1
                    numericValue2 = 1.0, // Shot 1
                    detailsJson = "{\"shotSize\":\"Extreme Wide Shot (EWS)\",\"cameraAngle\":\"High Angle Crane Down\",\"lens\":\"50mm Anamorphic\",\"movement\":\"Slow Crane Descending to Eye Level\",\"frameRate\":\"24 fps 4K Raw\",\"lighting\":\"Soft diffused morning backlight\",\"status\":\"Planned\",\"takes\":0}"
                )
            )
            "college_student" -> listOf(
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "STUDENT",
                    recordCategory = "SUBJECT",
                    title = "Artificial Intelligence & Neural Nets",
                    subtitle = "Course Code: CS8601",
                    stage = "Semester 6",
                    numericValue1 = 28.0, // Attended
                    numericValue2 = 32.0, // Total classes (87.5%)
                    detailsJson = "{\"professor\":\"Dr. Ramanathan\",\"credits\":4,\"minAttendance\":75.0,\"targetGrade\":\"O (Outstanding)\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "STUDENT",
                    recordCategory = "ASSIGNMENT",
                    title = "Deep Learning Model Implementation",
                    subtitle = "Due in 3 Days • Submit on Portal",
                    stage = "In Progress",
                    dateOrDeadline = now + (3 * 86400000L),
                    numericValue1 = 0.0,
                    numericValue2 = 25.0, // Max Marks
                    detailsJson = "{\"subject\":\"CS8601\",\"topics\":\"CNN, ResNet-50, PyTorch notebook\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "STUDENT",
                    recordCategory = "EXAM",
                    title = "Mid-Term Examination: AI & Data Mining",
                    subtitle = "Hall A-204 • 10:00 AM",
                    stage = "Upcoming",
                    dateOrDeadline = now + (10 * 86400000L),
                    numericValue1 = 0.0,
                    numericValue2 = 100.0,
                    detailsJson = "{\"subject\":\"CS8601\",\"syllabus\":\"Units 1 to 3\"}"
                )
            )
            "software_developer" -> listOf(
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "DEVELOPER",
                    recordCategory = "KANBAN",
                    title = "Implement Secure Token Rotation Engine",
                    subtitle = "Sprint 14 • High Priority",
                    stage = "In Progress",
                    numericValue1 = 8.0, // Estimated hours
                    detailsJson = "{\"type\":\"Feature\",\"assignee\":\"Lead Dev\",\"tags\":\"Auth, Crypto, Backend\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "DEVELOPER",
                    recordCategory = "SNIPPET",
                    title = "Kotlin Coroutine Flow Mutex Helper",
                    subtitle = "Concurrency Pattern",
                    stage = "Production",
                    detailsJson = "{\"language\":\"Kotlin\",\"code\":\"val mutex = Mutex()\\n\\nsuspend fun <T> withLockSafe(block: suspend () -> T): T {\\n    return mutex.withLock { block() }\\n}\",\"description\":\"Thread-safe execution wrapper for isolated role data dispatching.\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "DEVELOPER",
                    recordCategory = "BUG",
                    title = "Transient websocket reconnect debounce glitch",
                    subtitle = "Issue #104 • Medium Severity",
                    stage = "Investigating",
                    detailsJson = "{\"reproSteps\":\"1. Disconnect network\\n2. Reconnect within 300ms\\n3. Observe duplicate subscription handshake\",\"severity\":\"Medium\"}"
                )
            )
            "photographer" -> listOf(
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "PHOTOGRAPHER",
                    recordCategory = "BOOKING",
                    title = "Karthik & Divya Sunset Wedding",
                    subtitle = "Grand Palace Resort, ECR",
                    stage = "Shooting",
                    dateOrDeadline = now + (2 * 86400000L),
                    numericValue1 = 75000.0, // Total
                    numericValue2 = 25000.0, // Advance Paid
                    detailsJson = "{\"clientName\":\"Karthik Rajan\",\"phone\":\"+91 98401 23456\",\"crewCount\":3,\"packageType\":\"Premium Cinematic + 40-Page Album\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "PHOTOGRAPHER",
                    recordCategory = "SHOT_LIST",
                    title = "Sunset Couple Golden Hour Portrait",
                    subtitle = "Beachside Lawn • 85mm f/1.4",
                    stage = "Planned",
                    detailsJson = "{\"lens\":\"85mm f/1.4 GM\",\"lighting\":\"Softbox Fill + Rim Sun\",\"mood\":\"Warm nostalgic aesthetic\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "PHOTOGRAPHER",
                    recordCategory = "EQUIPMENT",
                    title = "Sony Alpha A7 IV (Body #1)",
                    subtitle = "Shutter Count: 24,120 • Good Health",
                    stage = "Ready",
                    detailsJson = "{\"type\":\"Camera Body\",\"sensorCleanDate\":\"2026-07-20\",\"batteryCount\":4}"
                )
            )
            "farmer" -> listOf(
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "FARMER",
                    recordCategory = "CROP",
                    title = "Ponni Samba Paddy (Plot North-3)",
                    subtitle = "4.5 Acres • Clay Loam Soil",
                    stage = "Growth",
                    dateOrDeadline = now + (45 * 86400000L), // Estimated Harvest
                    numericValue1 = 4.5, // Acres
                    numericValue2 = 3200.0, // Estimated yield kg/acre
                    detailsJson = "{\"variety\":\"BPT-5204 (Samba)\",\"sowingDate\":\"2026-06-10\",\"irrigationMethod\":\"Drip & Alternate Wetting\",\"currentHealth\":\"Thriving Tillers\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "FARMER",
                    recordCategory = "IRRIGATION",
                    title = "Plot North-3 Scheduled Drip Cycle",
                    subtitle = "3 Hours Morning Cycle",
                    stage = "Scheduled",
                    dateOrDeadline = now + 86400000L,
                    numericValue1 = 3.0, // Duration in hours
                    detailsJson = "{\"waterSource\":\"Solar Borewell #2\",\"field\":\"Plot North-3\"}"
                ),
                ProfessionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    roleId = roleId,
                    professionType = "FARMER",
                    recordCategory = "TREATMENT",
                    title = "Bio-Fertilizer (Azospirillum & Phosphobacteria)",
                    subtitle = "Soil application • Organic formulation",
                    stage = "Completed",
                    detailsJson = "{\"product\":\"Azospirillum Bio-enricher\",\"dosage\":\"2 kg/acre mixed with FYM\",\"observations\":\"Noticeable root nodule enhancement.\",\"safetyDisclaimer\":\"Record of organic farm observation only. Follow local certified agronomy norms.\"}"
                )
            )
            else -> emptyList()
        }
    }
}

class ViewModelFactory(
    private val repository: RoleoraRepository,
    private val authManager: FirebaseAuthManager,
    private val syncManager: FirestoreSyncManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoleoraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoleoraViewModel(repository, authManager, syncManager) as T
        }
        if (modelClass.isAssignableFrom(SetupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SetupViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(DirectorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DirectorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
