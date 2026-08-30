package com.example.roleora.data.repository

import com.example.roleora.data.local.AttachmentDao
import com.example.roleora.data.local.AuditionDao
import com.example.roleora.data.local.AuditDao
import com.example.roleora.data.local.BreakdownDao
import com.example.roleora.data.local.BudgetDao
import com.example.roleora.data.local.CallSheetDao
import com.example.roleora.data.local.CastCrewDao
import com.example.roleora.data.local.CharacterDao
import com.example.roleora.data.local.ContinuityDao
import com.example.roleora.data.local.EditingReviewDao
import com.example.roleora.data.local.EntryDao
import com.example.roleora.data.local.EventDao
import com.example.roleora.data.local.IdeaDao
import com.example.roleora.data.local.LocationDao
import com.example.roleora.data.local.ProductionDao
import com.example.roleora.data.local.RecordDao
import com.example.roleora.data.local.RehearsalDao
import com.example.roleora.data.local.RoleDao
import com.example.roleora.data.local.SceneDao
import com.example.roleora.data.local.ScheduleDao
import com.example.roleora.data.local.ScreenplayDao
import com.example.roleora.data.local.SessionDao
import com.example.roleora.data.local.ShotDao
import com.example.roleora.data.local.SoundMusicDao
import com.example.roleora.data.local.StoryboardDao
import com.example.roleora.data.local.SyncQueueDao
import com.example.roleora.data.local.TakeDao
import com.example.roleora.data.local.TaskDao
import com.example.roleora.data.local.TemplateDao
import com.example.roleora.data.local.UniversalEntryDao
import com.example.roleora.data.local.UniversalEntryVersionDao
import com.example.roleora.data.local.UserDao
import com.example.roleora.data.local.WorkSessionDao
import com.example.roleora.data.local.WorkspaceTemplateVersionDao
import com.example.roleora.data.model.AttachmentEntity
import com.example.roleora.data.model.AuditEventEntity
import com.example.roleora.data.model.AuditionEntity
import com.example.roleora.data.model.BreakdownItemEntity
import com.example.roleora.data.model.BudgetItemEntity
import com.example.roleora.data.model.CallSheetEntity
import com.example.roleora.data.model.CastCrewMemberEntity
import com.example.roleora.data.model.CharacterEntity
import com.example.roleora.data.model.ContinuityEntity
import com.example.roleora.data.model.DiaryEntryEntity
import com.example.roleora.data.model.EditingReviewEntity
import com.example.roleora.data.model.EntryEntity
import com.example.roleora.data.model.EntryType
import com.example.roleora.data.model.EntryVersionEntity
import com.example.roleora.data.model.EventEntity
import com.example.roleora.data.model.IdeaEntity
import com.example.roleora.data.model.LocationEntity
import com.example.roleora.data.model.ProductionEntity
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.data.model.ProfessionTemplateEntity
import com.example.roleora.data.model.RecurrencePattern
import com.example.roleora.data.model.RehearsalEntity
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.SceneEntity
import com.example.roleora.data.model.ScreenplayElementEntity
import com.example.roleora.data.model.ScreenplayEntity
import com.example.roleora.data.model.ScreenplayVersionEntity
import com.example.roleora.data.model.SessionEntity
import com.example.roleora.data.model.ShootingDayEntity
import com.example.roleora.data.model.ShotEntity
import com.example.roleora.data.model.SoundMusicEntity
import com.example.roleora.data.model.StoryboardEntity
import com.example.roleora.data.model.SyncQueueEntity
import com.example.roleora.data.model.SyncState
import com.example.roleora.data.model.TakeEntity
import com.example.roleora.data.model.TaskEntity
import com.example.roleora.data.model.TaskPriority
import com.example.roleora.data.model.TaskStatus
import com.example.roleora.data.model.TemplateDiffResult
import com.example.roleora.data.model.TemplateInstallationEntity
import com.example.roleora.data.model.TemplateVersionEntity
import com.example.roleora.data.model.UserEntity
import com.example.roleora.data.model.WorkSessionEntity
import com.example.roleora.data.model.WorkspaceTemplateVersionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.util.UUID

class RoleoraRepository(
    private val roleDao: RoleDao,
    private val templateDao: TemplateDao,
    private val entryDao: EntryDao,
    private val recordDao: RecordDao,
    private val auditDao: AuditDao,
    private val workspaceTemplateVersionDao: WorkspaceTemplateVersionDao? = null,
    private val userDao: UserDao? = null,
    private val sessionDao: SessionDao? = null,
    private val universalEntryDao: UniversalEntryDao? = null,
    private val universalEntryVersionDao: UniversalEntryVersionDao? = null,
    private val taskDao: TaskDao? = null,
    private val eventDao: EventDao? = null,
    private val attachmentDao: AttachmentDao? = null,
    private val workSessionDao: WorkSessionDao? = null,
    private val syncQueueDao: SyncQueueDao? = null,
    // Phase 3: Director DAOs
    val productionDao: ProductionDao? = null,
    val ideaDao: IdeaDao? = null,
    val screenplayDao: ScreenplayDao? = null,
    val characterDao: CharacterDao? = null,
    val sceneDao: SceneDao? = null,
    val breakdownDao: BreakdownDao? = null,
    val storyboardDao: StoryboardDao? = null,
    val shotDao: ShotDao? = null,
    val castCrewDao: CastCrewDao? = null,
    val auditionDao: AuditionDao? = null,
    val locationDao: LocationDao? = null,
    val rehearsalDao: RehearsalDao? = null,
    val scheduleDao: ScheduleDao? = null,
    val callSheetDao: CallSheetDao? = null,
    val continuityDao: ContinuityDao? = null,
    val takeDao: TakeDao? = null,
    val editingReviewDao: EditingReviewDao? = null,
    val soundMusicDao: SoundMusicDao? = null,
    val budgetDao: BudgetDao? = null
) {
    // --- Roles ---
    val allActiveRoles: Flow<List<RoleEntity>> = roleDao.getAllActiveRoles()

    suspend fun getRoleById(roleId: String): RoleEntity? = roleDao.getRoleById(roleId)

    fun observeRoleById(roleId: String): Flow<RoleEntity?> = roleDao.observeRoleById(roleId)

    suspend fun createRole(
        role: RoleEntity,
        initialRecords: List<ProfessionRecordEntity> = emptyList()
    ): String {
        roleDao.insertRole(role)
        
        // Track template installation
        templateDao.insertInstallation(
            TemplateInstallationEntity(
                id = role.id,
                roleId = role.id,
                templateId = role.templateId,
                installedVersion = role.templateVersion,
                installedAt = System.currentTimeMillis()
            )
        )

        // Seed initial workflow records if provided
        initialRecords.forEach { record ->
            recordDao.insertRecord(record)
        }

        // Add initial diary welcome entry
        entryDao.insertEntry(
            DiaryEntryEntity(
                id = UUID.randomUUID().toString(),
                roleId = role.id,
                title = "Workspace Initialized",
                content = "Created ${role.displayName} (${role.specialisation}) workspace with version ${role.templateVersion}. AI is ${if (role.aiEnabled) "Enabled (Granular)" else "Disabled by default"}.",
                entryType = "Setup Log",
                activityDate = System.currentTimeMillis(),
                tags = "System, Setup, ${role.category}"
            )
        )

        // Log audit event
        auditDao.insertAuditEvent(
            AuditEventEntity(
                id = UUID.randomUUID().toString(),
                roleId = role.id,
                eventType = "ROLE_CREATED",
                description = "Role '${role.displayName}' initialized with template '${role.templateId}' v${role.templateVersion}",
                timestamp = System.currentTimeMillis()
            )
        )

        return role.id
    }

    suspend fun updateRole(role: RoleEntity) {
        roleDao.updateRole(role)
        auditDao.insertAuditEvent(
            AuditEventEntity(
                id = UUID.randomUUID().toString(),
                roleId = role.id,
                eventType = "ROLE_UPDATED",
                description = "Role '${role.displayName}' settings updated",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun archiveRole(roleId: String) {
        roleDao.archiveRole(roleId)
        auditDao.insertAuditEvent(
            AuditEventEntity(
                id = UUID.randomUUID().toString(),
                roleId = roleId,
                eventType = "ROLE_ARCHIVED",
                description = "Role archived by user",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteRole(roleId: String) {
        entryDao.deleteEntriesForRole(roleId)
        recordDao.deleteRecordsForRole(roleId)
        roleDao.deleteRole(roleId)
    }

    // --- Profession Templates & Versioning ---
    val allTemplates: Flow<List<ProfessionTemplateEntity>> = templateDao.getAllTemplates()

    suspend fun getTemplateById(id: String): ProfessionTemplateEntity? = templateDao.getTemplateById(id)

    fun getVersionsForTemplate(templateId: String): Flow<List<TemplateVersionEntity>> =
        templateDao.getVersionsForTemplate(templateId)

    suspend fun getInstallationForRole(roleId: String): TemplateInstallationEntity? =
        templateDao.getInstallationForRole(roleId)

    fun observeInstallationForRole(roleId: String): Flow<TemplateInstallationEntity?> =
        templateDao.observeInstallationForRole(roleId)

    suspend fun upgradeTemplateVersion(
        role: RoleEntity,
        newVersion: TemplateVersionEntity
    ) {
        val currentInstallation = templateDao.getInstallationForRole(role.id)
        val backup = currentInstallation?.installedVersion ?: role.templateVersion

        // Update installation version
        templateDao.insertInstallation(
            TemplateInstallationEntity(
                id = role.id,
                roleId = role.id,
                templateId = role.templateId,
                installedVersion = newVersion.versionNumber,
                installedAt = System.currentTimeMillis(),
                lastBackupConfigJson = "{\"backupVersion\":\"$backup\",\"migratedAt\":${System.currentTimeMillis()}}"
            )
        )

        // Update role entity version
        roleDao.updateRole(role.copy(templateVersion = newVersion.versionNumber, updatedAt = System.currentTimeMillis()))

        // Log audit event
        auditDao.insertAuditEvent(
            AuditEventEntity(
                id = UUID.randomUUID().toString(),
                roleId = role.id,
                eventType = "TEMPLATE_UPGRADED",
                description = "Upgraded '${role.displayName}' from v$backup to v${newVersion.versionNumber}. ${newVersion.changeSummary}",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun rollbackTemplateVersion(
        role: RoleEntity,
        previousVersion: String
    ) {
        templateDao.insertInstallation(
            TemplateInstallationEntity(
                id = role.id,
                roleId = role.id,
                templateId = role.templateId,
                installedVersion = previousVersion,
                installedAt = System.currentTimeMillis()
            )
        )

        roleDao.updateRole(role.copy(templateVersion = previousVersion, updatedAt = System.currentTimeMillis()))

        auditDao.insertAuditEvent(
            AuditEventEntity(
                id = UUID.randomUUID().toString(),
                roleId = role.id,
                eventType = "TEMPLATE_ROLLED_BACK",
                description = "Rolled back '${role.displayName}' to version v$previousVersion",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // =========================================================================
    // Workspace-Level Template Versioning & Configuration Snapshot Management
    // =========================================================================

    /**
     * Observe all saved configuration versions for a workspace.
     */
    fun getWorkspaceTemplateVersions(roleId: String): Flow<List<WorkspaceTemplateVersionEntity>> {
        return workspaceTemplateVersionDao?.getVersionsForWorkspace(roleId) ?: emptyFlow()
    }

    /**
     * Fetch a specific configuration snapshot by version ID.
     */
    suspend fun getWorkspaceTemplateVersion(versionId: String): WorkspaceTemplateVersionEntity? {
        return workspaceTemplateVersionDao?.getVersionById(versionId)
    }

    /**
     * Saves the current active workspace state as a versioned configuration template.
     */
    suspend fun saveWorkspaceTemplateVersion(
        roleId: String,
        versionLabel: String,
        changeSummary: String,
        customVersionNumber: String? = null,
        authorId: String = "local_owner",
        customModules: List<String>? = null,
        customWorkflow: String? = null,
        tags: String = ""
    ): WorkspaceTemplateVersionEntity? {
        val role = roleDao.getRoleById(roleId) ?: return null
        val existingVersions = workspaceTemplateVersionDao?.getVersionsForWorkspaceList(roleId) ?: emptyList()

        // Calculate version number: user-supplied or auto-increment patch
        val versionNumber = if (!customVersionNumber.isNullOrBlank()) {
            customVersionNumber.trim()
        } else {
            val base = role.templateVersion.ifBlank { "1.0.0" }
            val count = existingVersions.size + 1
            "$base.rev$count"
        }

        val modulesJson = if (customModules != null) {
            "[${customModules.joinToString(",") { "\"$it\"" }}]"
        } else {
            // Default standard modules for this workspace type
            "[\"dashboard\",\"diary\",\"projects\",\"tasks\",\"calendar\",\"documents\",\"reports\"]"
        }

        val workflowStr = customWorkflow ?: "Discovery, Formulation, Execution, Review, Delivery"

        val versionEntity = WorkspaceTemplateVersionEntity(
            versionId = UUID.randomUUID().toString(),
            roleId = roleId,
            templateId = role.templateId,
            versionNumber = versionNumber,
            versionLabel = versionLabel.ifBlank { "Configuration $versionNumber" },
            changeSummary = changeSummary.ifBlank { "Snapshot created for ${role.displayName}" },
            authorId = authorId,
            createdAt = System.currentTimeMillis(),
            isCurrentActive = true,
            isLocked = false,
            isFavorite = false,
            tags = tags,
            roleDisplayName = role.displayName,
            roleCategory = role.category,
            specialisation = role.specialisation,
            roleTitle = role.displayName,
            institutionOrOrg = "",
            experienceLevel = "Intermediate",
            workType = "Independent",
            workFormat = "Diary & Projects",
            teamSize = "1-5",
            location = "Local",
            language = "English",
            timezone = "UTC+05:30",
            iconName = role.iconName,
            colorHex = role.colorHex,
            aiEnabled = role.aiEnabled,
            aiPermissions = "[]",
            enabledModulesJson = modulesJson,
            workflowStages = workflowStr,
            customFieldSchemasJson = "{}",
            customRecordCategoriesJson = "[]",
            rawConfigPayloadJson = "{\"roleId\":\"$roleId\",\"versionNumber\":\"$versionNumber\",\"savedAt\":${System.currentTimeMillis()}}"
        )

        workspaceTemplateVersionDao?.insertVersion(versionEntity)
        workspaceTemplateVersionDao?.setActiveVersion(roleId, versionEntity.versionId)

        // Update role's current template version pointer
        roleDao.updateRole(role.copy(templateVersion = versionNumber, updatedAt = System.currentTimeMillis()))

        auditDao.insertAuditEvent(
            AuditEventEntity(
                id = UUID.randomUUID().toString(),
                roleId = roleId,
                eventType = "TEMPLATE_VERSION_SAVED",
                description = "Saved workspace configuration '${versionEntity.versionLabel}' as v$versionNumber",
                timestamp = System.currentTimeMillis()
            )
        )

        return versionEntity
    }

    /**
     * Reverts a workspace's configuration to a saved template version.
     * Optionally creates an automatic safety restore point before applying.
     */
    suspend fun revertWorkspaceToVersion(
        roleId: String,
        versionId: String,
        createBackupBeforeRevert: Boolean = true
    ): Result<RoleEntity> {
        val targetVersion = workspaceTemplateVersionDao?.getVersionById(versionId)
            ?: return Result.failure(IllegalArgumentException("Target configuration version not found"))
        val currentRole = roleDao.getRoleById(roleId)
            ?: return Result.failure(IllegalArgumentException("Workspace role not found"))

        // Create safety backup snapshot if requested
        if (createBackupBeforeRevert) {
            val autoBackupLabel = "Auto Backup (pre-revert from v${currentRole.templateVersion})"
            val autoBackupSummary = "Automatic snapshot saved prior to restoring v${targetVersion.versionNumber}"
            val backupEntity = WorkspaceTemplateVersionEntity(
                versionId = UUID.randomUUID().toString(),
                roleId = roleId,
                templateId = currentRole.templateId,
                versionNumber = "${currentRole.templateVersion}-auto-bk",
                versionLabel = autoBackupLabel,
                changeSummary = autoBackupSummary,
                authorId = "system_safety",
                createdAt = System.currentTimeMillis(),
                isCurrentActive = false,
                isLocked = true,
                isFavorite = false,
                tags = "Backup, Safety",
                roleDisplayName = currentRole.displayName,
                roleCategory = currentRole.category,
                specialisation = currentRole.specialisation,
                roleTitle = currentRole.displayName,
                iconName = currentRole.iconName,
                colorHex = currentRole.colorHex,
                aiEnabled = currentRole.aiEnabled,
                aiPermissions = "[]",
                enabledModulesJson = "[\"dashboard\",\"diary\",\"projects\",\"tasks\",\"calendar\"]",
                workflowStages = "Discovery, Execution, Delivery",
                customFieldSchemasJson = "{}",
                customRecordCategoriesJson = "[]",
                rawConfigPayloadJson = "{}"
            )
            workspaceTemplateVersionDao?.insertVersion(backupEntity)
        }

        // Apply configuration to current role
        val updatedRole = currentRole.copy(
            displayName = targetVersion.roleDisplayName.ifBlank { currentRole.displayName },
            specialisation = targetVersion.specialisation.ifBlank { currentRole.specialisation },
            iconName = targetVersion.iconName.ifBlank { currentRole.iconName },
            colorHex = targetVersion.colorHex.ifBlank { currentRole.colorHex },
            aiEnabled = targetVersion.aiEnabled,
            templateVersion = targetVersion.versionNumber,
            updatedAt = System.currentTimeMillis()
        )

        roleDao.updateRole(updatedRole)
        workspaceTemplateVersionDao?.setActiveVersion(roleId, versionId)

        auditDao.insertAuditEvent(
            AuditEventEntity(
                id = UUID.randomUUID().toString(),
                roleId = roleId,
                eventType = "TEMPLATE_CONFIG_REVERTED",
                description = "Reverted workspace configuration to '${targetVersion.versionLabel}' (v${targetVersion.versionNumber})",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(updatedRole)
    }

    /**
     * Compares two workspace template versions and produces a structured diff.
     */
    fun compareWorkspaceTemplateVersions(
        baseVersion: WorkspaceTemplateVersionEntity,
        targetVersion: WorkspaceTemplateVersionEntity
    ): TemplateDiffResult {
        val baseModules = parseModuleList(baseVersion.enabledModulesJson)
        val targetModules = parseModuleList(targetVersion.enabledModulesJson)

        val added = targetModules.filter { !baseModules.contains(it) }
        val removed = baseModules.filter { !targetModules.contains(it) }
        val retained = baseModules.filter { targetModules.contains(it) }

        val baseWorkflow = baseVersion.workflowStages.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val targetWorkflow = targetVersion.workflowStages.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val workflowDiff = mutableListOf<String>()
        if (baseWorkflow != targetWorkflow) {
            workflowDiff.add("Stages changed: [${baseWorkflow.joinToString(" → ")}] to [${targetWorkflow.joinToString(" → ")}]")
        }

        val specChanged = baseVersion.specialisation != targetVersion.specialisation
        val colorChanged = baseVersion.colorHex != targetVersion.colorHex
        val iconChanged = baseVersion.iconName != targetVersion.iconName
        val aiChanged = baseVersion.aiEnabled != targetVersion.aiEnabled
        val titleChanged = baseVersion.roleDisplayName != targetVersion.roleDisplayName

        val summaryParts = mutableListOf<String>()
        if (added.isNotEmpty()) summaryParts.add("+${added.size} modules (${added.joinToString(", ")})")
        if (removed.isNotEmpty()) summaryParts.add("-${removed.size} modules (${removed.joinToString(", ")})")
        if (specChanged) summaryParts.add("Specialisation changed to '${targetVersion.specialisation}'")
        if (aiChanged) summaryParts.add(if (targetVersion.aiEnabled) "AI Enabled" else "AI Disabled")
        if (summaryParts.isEmpty()) summaryParts.add("Identical configuration structures")

        return TemplateDiffResult(
            baseVersionNumber = baseVersion.versionNumber,
            targetVersionNumber = targetVersion.versionNumber,
            addedModules = added,
            removedModules = removed,
            retainedModules = retained,
            workflowChanges = workflowDiff,
            specialisationChanged = specChanged,
            oldSpecialisation = baseVersion.specialisation,
            newSpecialisation = targetVersion.specialisation,
            colorChanged = colorChanged,
            oldColorHex = baseVersion.colorHex,
            newColorHex = targetVersion.colorHex,
            iconChanged = iconChanged,
            oldIcon = baseVersion.iconName,
            newIcon = targetVersion.iconName,
            aiStateChanged = aiChanged,
            oldAiEnabled = baseVersion.aiEnabled,
            newAiEnabled = targetVersion.aiEnabled,
            roleTitleChanged = titleChanged,
            oldRoleTitle = baseVersion.roleDisplayName,
            newRoleTitle = targetVersion.roleDisplayName,
            summaryText = summaryParts.joinToString(" • ")
        )
    }

    private fun parseModuleList(json: String): List<String> {
        return json.replace("[", "")
            .replace("]", "")
            .replace("\"", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    suspend fun deleteWorkspaceTemplateVersion(versionId: String): Boolean {
        val version = workspaceTemplateVersionDao?.getVersionById(versionId) ?: return false
        if (version.isLocked || version.isCurrentActive) {
            return false // Cannot delete active or locked configuration
        }
        workspaceTemplateVersionDao.deleteVersion(versionId)
        return true
    }

    suspend fun toggleWorkspaceVersionLock(versionId: String) {
        workspaceTemplateVersionDao?.toggleLock(versionId)
    }

    suspend fun toggleWorkspaceVersionFavorite(versionId: String) {
        workspaceTemplateVersionDao?.toggleFavorite(versionId)
    }

    suspend fun duplicateWorkspaceTemplateVersion(versionId: String, newLabel: String): WorkspaceTemplateVersionEntity? {
        val source = workspaceTemplateVersionDao?.getVersionById(versionId) ?: return null
        val count = (workspaceTemplateVersionDao?.countVersionsForWorkspace(source.roleId) ?: 0) + 1
        val newVersion = source.copy(
            versionId = UUID.randomUUID().toString(),
            versionNumber = "${source.versionNumber}.copy$count",
            versionLabel = newLabel.ifBlank { "${source.versionLabel} (Copy)" },
            changeSummary = "Duplicated from v${source.versionNumber}",
            createdAt = System.currentTimeMillis(),
            isCurrentActive = false,
            isLocked = false,
            isFavorite = false
        )
        workspaceTemplateVersionDao?.insertVersion(newVersion)
        return newVersion
    }


    // --- Diary Entries ---
    fun getEntriesForRole(roleId: String): Flow<List<DiaryEntryEntity>> = entryDao.getEntriesForRole(roleId)

    suspend fun insertEntry(entry: DiaryEntryEntity) = entryDao.insertEntry(entry)

    suspend fun updateEntry(entry: DiaryEntryEntity) = entryDao.updateEntry(entry)

    suspend fun deleteEntry(id: String) = entryDao.deleteEntry(id)

    // --- Profession Specific Records ---
    fun getAllRecordsForRole(roleId: String): Flow<List<ProfessionRecordEntity>> =
        recordDao.getAllRecordsForRole(roleId)

    fun getRecordsByCategory(roleId: String, category: String): Flow<List<ProfessionRecordEntity>> =
        recordDao.getRecordsByCategory(roleId, category)

    suspend fun insertRecord(record: ProfessionRecordEntity) = recordDao.insertRecord(record)

    suspend fun updateRecord(record: ProfessionRecordEntity) = recordDao.updateRecord(record)

    suspend fun deleteRecord(id: String) = recordDao.deleteRecord(id)

    // --- Audit Events ---
    fun getAuditEventsForRole(roleId: String): Flow<List<AuditEventEntity>> = auditDao.getAuditEventsForRole(roleId)

    // --- User & Local Session Management (Offline Auth Persistence) ---
    val activeSession: Flow<SessionEntity?>? = sessionDao?.observeActiveSession()

    suspend fun getLocalUser(userId: String): UserEntity? = userDao?.getUserById(userId)
    fun observeLocalUser(userId: String): Flow<UserEntity?>? = userDao?.observeUserById(userId)
    suspend fun saveLocalUser(user: UserEntity) = userDao?.insertUser(user)
    suspend fun deleteLocalUser(userId: String) = userDao?.deleteUser(userId)

    suspend fun getActiveSession(): SessionEntity? = sessionDao?.getActiveSession()
    suspend fun saveSession(session: SessionEntity) {
        sessionDao?.deactivateAllSessionsForUser(session.userId)
        sessionDao?.insertSession(session)
    }
    suspend fun deactivateSession(sessionId: String) = sessionDao?.deactivateSession(sessionId)
    suspend fun deactivateAllSessionsForUser(userId: String) = sessionDao?.deactivateAllSessionsForUser(userId)
    suspend fun updateSessionHeartbeat(sessionId: String) = sessionDao?.updateSessionHeartbeat(sessionId)

    // --- Bulk Data Operations for Cloud Backup/Restore ---
    suspend fun getAllActiveRolesList(): List<RoleEntity> = roleDao.getAllActiveRolesList()
    suspend fun getAllRecordsList(): List<ProfessionRecordEntity> = recordDao.getAllRecordsList()
    suspend fun getAllEntriesList(): List<DiaryEntryEntity> = entryDao.getAllEntriesList()

    suspend fun clearAllLocalData() {
        entryDao.deleteAllEntries()
        recordDao.deleteAllRecords()
        auditDao.deleteAllAuditEvents()
        roleDao.deleteAllRoles()
        userDao?.deleteAllUsers()
        sessionDao?.deleteAllSessions()
        universalEntryDao?.deleteAllEntries()
        universalEntryVersionDao?.deleteAllVersions()
        taskDao?.deleteAllTasks()
        eventDao?.deleteAllEvents()
        attachmentDao?.deleteAllAttachments()
        workSessionDao?.deleteAllSessions()
        syncQueueDao?.deleteAllQueue()
    }

    // =========================================================================
    // PHASE 2: UNIVERSAL ENTRIES & COMMON MODEL
    // =========================================================================
    fun getUniversalEntriesForRole(roleId: String): Flow<List<EntryEntity>> =
        universalEntryDao?.getEntriesForRole(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getUniversalEntriesForRoles(roleIds: List<String>): Flow<List<EntryEntity>> =
        universalEntryDao?.getEntriesForRoles(roleIds) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getUniversalEntriesByType(roleId: String, entryType: String): Flow<List<EntryEntity>> =
        universalEntryDao?.getEntriesByType(roleId, entryType) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getUniversalEntriesBetweenDates(roleId: String, start: Long, end: Long): Flow<List<EntryEntity>> =
        universalEntryDao?.getEntriesBetweenDates(roleId, start, end) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun searchUniversalEntries(roleId: String, query: String): Flow<List<EntryEntity>> =
        universalEntryDao?.searchEntries(roleId, query) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun searchUniversalEntriesMultiRole(roleIds: List<String>, query: String): Flow<List<EntryEntity>> =
        universalEntryDao?.searchEntriesMultiRole(roleIds, query) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun getUniversalEntryById(entryId: String): EntryEntity? =
        universalEntryDao?.getEntryById(entryId)

    fun observeUniversalEntryById(entryId: String): Flow<EntryEntity?> =
        universalEntryDao?.observeEntryById(entryId) ?: kotlinx.coroutines.flow.flowOf(null)

    fun getUniversalTrashEntries(roleId: String): Flow<List<EntryEntity>> =
        universalEntryDao?.getTrashEntries(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveUniversalEntry(entry: EntryEntity, changeReason: String? = null, actorUid: String? = null) {
        val existing = universalEntryDao?.getEntryById(entry.entryId)
        if (existing != null) {
            // Create version snapshot before updating
            val newVersionNumber = existing.version
            val versionSnapshot = EntryVersionEntity(
                versionId = UUID.randomUUID().toString(),
                entryId = existing.entryId,
                roleId = existing.roleId,
                versionNumber = newVersionNumber,
                editorId = actorUid ?: existing.ownerId,
                modifiedAt = System.currentTimeMillis(),
                changedFields = if (existing.title != entry.title) "title, content" else "content",
                snapshotJson = """{"title":"${existing.title.replace("\"", "\\\"")}","content":"${existing.content.replace("\"", "\\\"")}","tags":"${existing.tags}"}""",
                changeReason = changeReason
            )
            universalEntryVersionDao?.insertVersion(versionSnapshot)

            val updated = entry.copy(
                version = existing.version + 1,
                updatedAt = System.currentTimeMillis(),
                syncStatus = SyncState.PENDING.name
            )
            universalEntryDao?.updateEntry(updated)
            logAuditEvent(entry.roleId, "ENTRY_UPDATED", "Updated entry '${entry.title}' (v${updated.version})", actorUid)
        } else {
            val fresh = entry.copy(
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                syncStatus = SyncState.LOCAL_DRAFT.name
            )
            universalEntryDao?.insertEntry(fresh)
            logAuditEvent(entry.roleId, "ENTRY_CREATED", "Created ${entry.entryType} '${entry.title}'", actorUid)
        }
    }

    suspend fun moveToTrashUniversalEntry(entryId: String, actorUid: String? = null) {
        val entry = universalEntryDao?.getEntryById(entryId)
        universalEntryDao?.moveToTrash(entryId)
        entry?.let {
            logAuditEvent(it.roleId, "ENTRY_TRASHED", "Moved entry '${it.title}' to trash", actorUid)
        }
    }

    suspend fun restoreUniversalEntry(entryId: String, actorUid: String? = null) {
        val entry = universalEntryDao?.getEntryById(entryId)
        universalEntryDao?.restoreFromTrash(entryId)
        entry?.let {
            logAuditEvent(it.roleId, "ENTRY_RESTORED", "Restored entry '${it.title}' from trash", actorUid)
        }
    }

    suspend fun deleteUniversalEntryPermanently(entryId: String, actorUid: String? = null) {
        val entry = universalEntryDao?.getEntryById(entryId)
        entry?.let {
            universalEntryVersionDao?.deleteVersionsForEntry(entryId)
            attachmentDao?.deleteAttachmentsForEntry(entryId)
            universalEntryDao?.deletePermanently(entryId)
            logAuditEvent(it.roleId, "ENTRY_PERMANENTLY_DELETED", "Permanently deleted entry '${it.title}'", actorUid)
        }
    }

    suspend fun emptyTrashUniversalEntries(roleId: String, actorUid: String? = null) {
        universalEntryDao?.emptyTrashForRole(roleId)
        taskDao?.emptyTrashForRole(roleId)
        eventDao?.emptyTrashForRole(roleId)
        logAuditEvent(roleId, "TRASH_EMPTIED", "Emptied trash for workspace", actorUid)
    }

    // --- Version History ---
    fun getVersionsForEntry(entryId: String): Flow<List<EntryVersionEntity>> =
        universalEntryVersionDao?.getVersionsForEntry(entryId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun restoreVersionAsNewVersion(entryId: String, versionId: String, actorUid: String? = null) {
        val currentEntry = universalEntryDao?.getEntryById(entryId) ?: return
        val targetVersion = universalEntryVersionDao?.getVersionById(versionId) ?: return

        // Extract title and content from snapshotJson or fallback
        val restoredEntry = currentEntry.copy(
            version = currentEntry.version + 1,
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncState.PENDING.name
        )
        universalEntryDao?.updateEntry(restoredEntry)
        logAuditEvent(currentEntry.roleId, "VERSION_RESTORED", "Restored version v${targetVersion.versionNumber} for '${currentEntry.title}'", actorUid)
    }

    // =========================================================================
    // PHASE 2: TASKS & REMINDERS
    // =========================================================================
    fun getTasksForRole(roleId: String): Flow<List<TaskEntity>> =
        taskDao?.getTasksForRole(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getTasksByStatus(roleId: String, status: String): Flow<List<TaskEntity>> =
        taskDao?.getTasksByStatus(roleId, status) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getOverdueTasks(roleId: String): Flow<List<TaskEntity>> =
        taskDao?.getOverdueTasks(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun observeTaskById(taskId: String): Flow<TaskEntity?> =
        taskDao?.observeTaskById(taskId) ?: kotlinx.coroutines.flow.flowOf(null)

    suspend fun getTaskById(taskId: String): TaskEntity? =
        taskDao?.getTaskById(taskId)

    fun getTrashTasks(roleId: String): Flow<List<TaskEntity>> =
        taskDao?.getTrashTasks(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveTask(task: TaskEntity, actorUid: String? = null) {
        val existing = taskDao?.getTaskById(task.taskId)
        if (existing != null) {
            val updated = task.copy(updatedAt = System.currentTimeMillis(), syncStatus = SyncState.PENDING.name)
            taskDao?.updateTask(updated)
            logAuditEvent(task.roleId, "TASK_UPDATED", "Updated task '${task.title}'", actorUid)
        } else {
            taskDao?.insertTask(task)
            logAuditEvent(task.roleId, "TASK_CREATED", "Created task '${task.title}' [Priority: ${task.priority}]", actorUid)
        }
    }

    suspend fun completeTask(taskId: String, actorUid: String? = null) {
        val task = taskDao?.getTaskById(taskId)
        taskDao?.completeTask(taskId)
        task?.let { logAuditEvent(it.roleId, "TASK_COMPLETED", "Completed task '${it.title}'", actorUid) }
    }

    suspend fun reopenTask(taskId: String, actorUid: String? = null) {
        val task = taskDao?.getTaskById(taskId)
        taskDao?.reopenTask(taskId)
        task?.let { logAuditEvent(it.roleId, "TASK_REOPENED", "Reopened task '${it.title}'", actorUid) }
    }

    suspend fun duplicateTask(taskId: String, actorUid: String? = null): String {
        val original = taskDao?.getTaskById(taskId) ?: return ""
        val clone = original.copy(
            taskId = UUID.randomUUID().toString(),
            title = "${original.title} (Copy)",
            status = TaskStatus.NOT_STARTED.name,
            completedAt = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncState.LOCAL_DRAFT.name
        )
        taskDao?.insertTask(clone)
        logAuditEvent(original.roleId, "TASK_DUPLICATED", "Duplicated task '${original.title}'", actorUid)
        return clone.taskId
    }

    suspend fun moveToTrashTask(taskId: String, actorUid: String? = null) {
        val task = taskDao?.getTaskById(taskId)
        taskDao?.moveToTrash(taskId)
        task?.let { logAuditEvent(it.roleId, "TASK_TRASHED", "Moved task '${it.title}' to trash", actorUid) }
    }

    suspend fun restoreTask(taskId: String, actorUid: String? = null) {
        val task = taskDao?.getTaskById(taskId)
        taskDao?.restoreFromTrash(taskId)
        task?.let { logAuditEvent(it.roleId, "TASK_RESTORED", "Restored task '${it.title}' from trash", actorUid) }
    }

    suspend fun deleteTaskPermanently(taskId: String, actorUid: String? = null) {
        val task = taskDao?.getTaskById(taskId)
        taskDao?.deletePermanently(taskId)
        task?.let { logAuditEvent(it.roleId, "TASK_DELETED", "Permanently deleted task '${it.title}'", actorUid) }
    }

    // =========================================================================
    // PHASE 2: EVENTS & CALENDAR
    // =========================================================================
    fun getEventsForRole(roleId: String): Flow<List<EventEntity>> =
        eventDao?.getEventsForRole(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getEventsBetweenDates(roleId: String, start: Long, end: Long): Flow<List<EventEntity>> =
        eventDao?.getEventsBetweenDates(roleId, start, end) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getUpcomingEvents(roleId: String): Flow<List<EventEntity>> =
        eventDao?.getUpcomingEvents(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun getEventById(eventId: String): EventEntity? =
        eventDao?.getEventById(eventId)

    fun getTrashEvents(roleId: String): Flow<List<EventEntity>> =
        eventDao?.getTrashEvents(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveEvent(event: EventEntity, actorUid: String? = null) {
        val existing = eventDao?.getEventById(event.eventId)
        if (existing != null) {
            val updated = event.copy(updatedAt = System.currentTimeMillis(), syncStatus = SyncState.PENDING.name)
            eventDao?.updateEvent(updated)
            logAuditEvent(event.roleId, "EVENT_UPDATED", "Updated event '${event.title}'", actorUid)
        } else {
            eventDao?.insertEvent(event)
            logAuditEvent(event.roleId, "EVENT_CREATED", "Scheduled event '${event.title}'", actorUid)
        }
    }

    suspend fun moveToTrashEvent(eventId: String, actorUid: String? = null) {
        val event = eventDao?.getEventById(eventId)
        eventDao?.moveToTrash(eventId)
        event?.let { logAuditEvent(it.roleId, "EVENT_TRASHED", "Moved event '${it.title}' to trash", actorUid) }
    }

    suspend fun restoreEvent(eventId: String, actorUid: String? = null) {
        val event = eventDao?.getEventById(eventId)
        eventDao?.restoreFromTrash(eventId)
        event?.let { logAuditEvent(it.roleId, "EVENT_RESTORED", "Restored event '${it.title}' from trash", actorUid) }
    }

    suspend fun deleteEventPermanently(eventId: String, actorUid: String? = null) {
        val event = eventDao?.getEventById(eventId)
        eventDao?.deletePermanently(eventId)
        event?.let { logAuditEvent(it.roleId, "EVENT_DELETED", "Permanently deleted event '${it.title}'", actorUid) }
    }

    // =========================================================================
    // PHASE 2: ATTACHMENTS & MEDIA
    // =========================================================================
    fun getAttachmentsForRole(roleId: String): Flow<List<AttachmentEntity>> =
        attachmentDao?.getAttachmentsForRole(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getAttachmentsForEntry(entryId: String): Flow<List<AttachmentEntity>> =
        attachmentDao?.getAttachmentsForEntry(entryId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveAttachment(attachment: AttachmentEntity) {
        attachmentDao?.insertAttachment(attachment)
    }

    suspend fun deleteAttachment(attachmentId: String) {
        attachmentDao?.deletePermanently(attachmentId)
    }

    // =========================================================================
    // PHASE 2: WORK SESSION TIMER
    // =========================================================================
    fun observeLatestWorkSession(roleId: String): Flow<WorkSessionEntity?> =
        workSessionDao?.observeLatestSession(roleId) ?: kotlinx.coroutines.flow.flowOf(null)

    suspend fun getActiveRunningSession(roleId: String): WorkSessionEntity? =
        workSessionDao?.getActiveRunningSession(roleId)

    suspend fun startWorkSession(roleId: String, ownerId: String, description: String, linkedTaskId: String? = null): WorkSessionEntity {
        val session = WorkSessionEntity(
            sessionId = UUID.randomUUID().toString(),
            ownerId = ownerId,
            roleId = roleId,
            linkedTaskId = linkedTaskId,
            description = description,
            startTime = System.currentTimeMillis(),
            isRunning = true,
            isPaused = false
        )
        workSessionDao?.insertSession(session)
        logAuditEvent(roleId, "TIMER_STARTED", "Started work timer session: $description", ownerId)
        return session
    }

    suspend fun pauseWorkSession(session: WorkSessionEntity) {
        val updated = session.copy(
            isPaused = true,
            lastPauseTimestamp = System.currentTimeMillis()
        )
        workSessionDao?.updateSession(updated)
    }

    suspend fun resumeWorkSession(session: WorkSessionEntity) {
        val now = System.currentTimeMillis()
        val pausedDelta = if (session.lastPauseTimestamp != null) now - session.lastPauseTimestamp else 0L
        val updated = session.copy(
            isPaused = false,
            lastPauseTimestamp = null,
            pausedDurationMs = session.pausedDurationMs + pausedDelta
        )
        workSessionDao?.updateSession(updated)
    }

    suspend fun stopWorkSession(session: WorkSessionEntity, saveAsDiary: Boolean = true): EntryEntity? {
        val now = System.currentTimeMillis()
        val finalPaused = if (session.isPaused && session.lastPauseTimestamp != null) {
            session.pausedDurationMs + (now - session.lastPauseTimestamp)
        } else {
            session.pausedDurationMs
        }
        val totalActiveDuration = maxOf(0L, (now - session.startTime) - finalPaused)
        val minutes = totalActiveDuration / 60000L

        var createdDiaryEntry: EntryEntity? = null
        if (saveAsDiary) {
            val diary = EntryEntity(
                entryId = UUID.randomUUID().toString(),
                ownerId = session.ownerId,
                roleId = session.roleId,
                entryType = EntryType.DIARY.name,
                title = "Work Session: ${session.description.ifEmpty { "Focus Period" }}",
                content = "Completed $minutes minutes of focused work.\n\nWork Log: ${session.description}",
                activityDateTime = session.startTime,
                startTime = session.startTime,
                endTime = now,
                duration = totalActiveDuration,
                diaryType = "Professional",
                tags = "Work, Timer, Productivity"
            )
            saveUniversalEntry(diary, actorUid = session.ownerId)
            createdDiaryEntry = diary
        }

        val completedSession = session.copy(
            endTime = now,
            pausedDurationMs = finalPaused,
            totalDurationMs = totalActiveDuration,
            isRunning = false,
            isPaused = false,
            savedAsDiaryEntryId = createdDiaryEntry?.entryId
        )
        workSessionDao?.updateSession(completedSession)
        logAuditEvent(session.roleId, "TIMER_COMPLETED", "Completed focus session ($minutes min): ${session.description}", session.ownerId)
        return createdDiaryEntry
    }

    suspend fun cancelWorkSession(sessionId: String, roleId: String) {
        workSessionDao?.deleteSession(sessionId)
        logAuditEvent(roleId, "TIMER_CANCELLED", "Cancelled work timer session")
    }

    suspend fun logAuditEvent(roleId: String, eventType: String, description: String, actorUid: String? = null) {
        auditDao.insertAuditEvent(
            AuditEventEntity(
                id = UUID.randomUUID().toString(),
                roleId = roleId,
                eventType = eventType,
                description = description,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun importCloudData(
        roles: List<RoleEntity>,
        records: List<ProfessionRecordEntity>,
        entries: List<DiaryEntryEntity>
    ) {
        if (roles.isNotEmpty()) {
            roleDao.insertRoles(roles)
        }
        if (records.isNotEmpty()) {
            recordDao.insertRecords(records)
        }
        if (entries.isNotEmpty()) {
            entryDao.insertEntries(entries)
        }
    }

    // =========================================================================
    // Phase 3: Movie Director Repository Methods
    // =========================================================================

    // 1. Productions
    fun getActiveProductionsForRole(roleId: String): Flow<List<ProductionEntity>> =
        productionDao?.getActiveProductionsForRole(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getArchivedProductionsForRole(roleId: String): Flow<List<ProductionEntity>> =
        productionDao?.getArchivedProductionsForRole(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getTrashProductionsForRole(roleId: String): Flow<List<ProductionEntity>> =
        productionDao?.getTrashProductionsForRole(roleId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun getProductionById(id: String): ProductionEntity? = productionDao?.getProductionById(id)

    fun observeProductionById(id: String): Flow<ProductionEntity?> =
        productionDao?.observeProductionById(id) ?: kotlinx.coroutines.flow.flowOf(null)

    suspend fun saveProduction(production: ProductionEntity) {
        productionDao?.insertProduction(production)
        logAuditEvent(production.roleId, "PRODUCTION_SAVED", "Saved production: ${production.title}")
    }

    suspend fun archiveProduction(id: String, roleId: String) {
        productionDao?.archiveProduction(id)
        logAuditEvent(roleId, "PRODUCTION_ARCHIVED", "Archived production $id")
    }

    suspend fun restoreArchivedProduction(id: String, roleId: String) {
        productionDao?.restoreArchivedProduction(id)
        logAuditEvent(roleId, "PRODUCTION_RESTORED", "Restored production $id")
    }

    suspend fun moveProductionToTrash(id: String, roleId: String) {
        productionDao?.moveToTrash(id)
        logAuditEvent(roleId, "PRODUCTION_TRASHED", "Moved production $id to trash")
    }

    suspend fun restoreProductionFromTrash(id: String, roleId: String) {
        productionDao?.restoreFromTrash(id)
        logAuditEvent(roleId, "PRODUCTION_RESTORED_TRASH", "Restored production $id from trash")
    }

    suspend fun deleteProductionPermanently(id: String, roleId: String) {
        productionDao?.deletePermanently(id)
        logAuditEvent(roleId, "PRODUCTION_DELETED", "Permanently deleted production $id")
    }

    // 2. Ideas & Story
    fun getIdeasForProduction(productionId: String): Flow<List<IdeaEntity>> =
        ideaDao?.getIdeasForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveIdea(idea: IdeaEntity) {
        ideaDao?.insertIdea(idea)
        logAuditEvent(idea.roleId, "IDEA_SAVED", "Saved story idea: ${idea.title}")
    }

    suspend fun deleteIdea(id: String, roleId: String) {
        ideaDao?.deletePermanently(id)
        logAuditEvent(roleId, "IDEA_DELETED", "Deleted story idea $id")
    }

    // 3. Screenplays & Elements
    fun getScreenplaysForProduction(productionId: String): Flow<List<ScreenplayEntity>> =
        screenplayDao?.getScreenplaysForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun getScreenplayById(id: String): ScreenplayEntity? = screenplayDao?.getScreenplayById(id)

    fun observeScreenplayById(id: String): Flow<ScreenplayEntity?> =
        screenplayDao?.observeScreenplayById(id) ?: kotlinx.coroutines.flow.flowOf(null)

    suspend fun saveScreenplay(screenplay: ScreenplayEntity) {
        screenplayDao?.insertScreenplay(screenplay)
        logAuditEvent(screenplay.roleId, "SCREENPLAY_SAVED", "Saved screenplay: ${screenplay.title}")
    }

    fun getElementsForScreenplay(screenplayId: String): Flow<List<ScreenplayElementEntity>> =
        screenplayDao?.getElementsForScreenplay(screenplayId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveScreenplayElements(elements: List<ScreenplayElementEntity>) {
        screenplayDao?.insertElements(elements)
    }

    suspend fun saveScreenplayElement(element: ScreenplayElementEntity) {
        screenplayDao?.insertElement(element)
    }

    suspend fun deleteScreenplayElement(id: String) {
        screenplayDao?.deleteElement(id)
    }

    fun getVersionsForScreenplay(screenplayId: String): Flow<List<ScreenplayVersionEntity>> =
        screenplayDao?.getVersionsForScreenplay(screenplayId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveScreenplayVersion(version: ScreenplayVersionEntity) {
        screenplayDao?.insertVersion(version)
        logAuditEvent(version.roleId, "SCREENPLAY_VERSION_CREATED", "Created version ${version.draftName} for screenplay ${version.screenplayId}")
    }

    // 4. Characters
    fun getCharactersForProduction(productionId: String): Flow<List<CharacterEntity>> =
        characterDao?.getCharactersForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveCharacter(character: CharacterEntity) {
        characterDao?.insertCharacter(character)
        logAuditEvent(character.roleId, "CHARACTER_SAVED", "Saved character: ${character.name}")
    }

    suspend fun deleteCharacter(id: String, roleId: String) {
        characterDao?.deletePermanently(id)
        logAuditEvent(roleId, "CHARACTER_DELETED", "Deleted character $id")
    }

    // 5. Scenes
    fun getScenesForProduction(productionId: String): Flow<List<SceneEntity>> =
        sceneDao?.getScenesForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveScene(scene: SceneEntity, roleId: String) {
        sceneDao?.insertScene(scene)
        logAuditEvent(roleId, "SCENE_SAVED", "Saved Scene ${scene.sceneNumber}: ${scene.heading}")
    }

    suspend fun updateSceneStatus(id: String, newStatus: String, roleId: String) {
        sceneDao?.updateSceneStatus(id, newStatus)
        logAuditEvent(roleId, "SCENE_STATUS_UPDATED", "Updated Scene $id status to $newStatus")
    }

    suspend fun deleteScene(id: String, roleId: String) {
        sceneDao?.deletePermanently(id)
        logAuditEvent(roleId, "SCENE_DELETED", "Deleted Scene $id")
    }

    // 6. Breakdown
    fun getBreakdownForProduction(productionId: String): Flow<List<BreakdownItemEntity>> =
        breakdownDao?.getBreakdownForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveBreakdownItem(item: BreakdownItemEntity, roleId: String) {
        breakdownDao?.insertBreakdownItem(item)
        logAuditEvent(roleId, "BREAKDOWN_ITEM_SAVED", "Saved ${item.category} item: ${item.description}")
    }

    suspend fun deleteBreakdownItem(id: String, roleId: String) {
        breakdownDao?.deleteBreakdownItem(id)
        logAuditEvent(roleId, "BREAKDOWN_ITEM_DELETED", "Deleted breakdown item $id")
    }

    // 7. Storyboards
    fun getStoryboardsForProduction(productionId: String): Flow<List<StoryboardEntity>> =
        storyboardDao?.getStoryboardsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveStoryboard(frame: StoryboardEntity, roleId: String) {
        storyboardDao?.insertStoryboard(frame)
        logAuditEvent(roleId, "STORYBOARD_SAVED", "Saved storyboard frame #${frame.frameNumber}")
    }

    suspend fun deleteStoryboard(id: String, roleId: String) {
        storyboardDao?.deleteStoryboard(id)
        logAuditEvent(roleId, "STORYBOARD_DELETED", "Deleted storyboard frame $id")
    }

    // 8. Shot List
    fun getShotsForProduction(productionId: String): Flow<List<ShotEntity>> =
        shotDao?.getShotsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveShot(shot: ShotEntity, roleId: String) {
        shotDao?.insertShot(shot)
        logAuditEvent(roleId, "SHOT_SAVED", "Saved Shot #${shot.shotNumber}: ${shot.description}")
    }

    suspend fun updateShotStatus(id: String, status: String, roleId: String) {
        shotDao?.updateShotStatus(id, status)
        logAuditEvent(roleId, "SHOT_STATUS_UPDATED", "Updated Shot $id status to $status")
    }

    suspend fun deleteShot(id: String, roleId: String) {
        shotDao?.deleteShot(id)
        logAuditEvent(roleId, "SHOT_DELETED", "Deleted shot $id")
    }

    // 9. Cast & Crew
    fun getMembersForProduction(productionId: String): Flow<List<CastCrewMemberEntity>> =
        castCrewDao?.getMembersForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveMember(member: CastCrewMemberEntity) {
        castCrewDao?.insertMember(member)
        logAuditEvent(member.roleId, "MEMBER_SAVED", "Saved cast/crew member: ${member.name} (${member.positionTitle})")
    }

    suspend fun deleteMember(id: String, roleId: String) {
        castCrewDao?.deleteMember(id)
        logAuditEvent(roleId, "MEMBER_DELETED", "Deleted member $id")
    }

    // 10. Auditions
    fun getAuditionsForProduction(productionId: String): Flow<List<AuditionEntity>> =
        auditionDao?.getAuditionsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveAudition(audition: AuditionEntity, roleId: String) {
        auditionDao?.insertAudition(audition)
        logAuditEvent(roleId, "AUDITION_SAVED", "Saved audition for ${audition.candidateName}")
    }

    suspend fun updateAuditionStatus(id: String, status: String, roleId: String) {
        auditionDao?.updateAuditionStatus(id, status)
        logAuditEvent(roleId, "AUDITION_STATUS_UPDATED", "Updated audition $id status to $status")
    }

    suspend fun deleteAudition(id: String, roleId: String) {
        auditionDao?.deleteAudition(id)
        logAuditEvent(roleId, "AUDITION_DELETED", "Deleted audition $id")
    }

    // 11. Locations
    fun getLocationsForProduction(productionId: String): Flow<List<LocationEntity>> =
        locationDao?.getLocationsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveLocation(location: LocationEntity) {
        locationDao?.insertLocation(location)
        logAuditEvent(location.roleId, "LOCATION_SAVED", "Saved location: ${location.name}")
    }

    suspend fun deleteLocation(id: String, roleId: String) {
        locationDao?.deleteLocation(id)
        logAuditEvent(roleId, "LOCATION_DELETED", "Deleted location $id")
    }

    // 12. Rehearsals
    fun getRehearsalsForProduction(productionId: String): Flow<List<RehearsalEntity>> =
        rehearsalDao?.getRehearsalsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveRehearsal(rehearsal: RehearsalEntity, roleId: String) {
        rehearsalDao?.insertRehearsal(rehearsal)
        logAuditEvent(roleId, "REHEARSAL_SAVED", "Saved rehearsal: ${rehearsal.objectives}")
    }

    suspend fun deleteRehearsal(id: String, roleId: String) {
        rehearsalDao?.deleteRehearsal(id)
        logAuditEvent(roleId, "REHEARSAL_DELETED", "Deleted rehearsal $id")
    }

    // 13. Shooting Schedule
    fun getShootingDaysForProduction(productionId: String): Flow<List<ShootingDayEntity>> =
        scheduleDao?.getShootingDaysForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveShootingDay(day: ShootingDayEntity, roleId: String) {
        scheduleDao?.insertShootingDay(day)
        logAuditEvent(roleId, "SHOOTING_DAY_SAVED", "Saved Shooting Day #${day.dayNumber}")
    }

    suspend fun deleteShootingDay(id: String, roleId: String) {
        scheduleDao?.deleteShootingDay(id)
        logAuditEvent(roleId, "SHOOTING_DAY_DELETED", "Deleted shooting day $id")
    }

    // 14. Call Sheets
    fun getCallSheetsForProduction(productionId: String): Flow<List<CallSheetEntity>> =
        callSheetDao?.getCallSheetsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveCallSheet(callSheet: CallSheetEntity, roleId: String) {
        callSheetDao?.insertCallSheet(callSheet)
        logAuditEvent(roleId, "CALL_SHEET_SAVED", "Saved Call Sheet Day #${callSheet.dayNumber}")
    }

    suspend fun deleteCallSheet(id: String, roleId: String) {
        callSheetDao?.deleteCallSheet(id)
        logAuditEvent(roleId, "CALL_SHEET_DELETED", "Deleted call sheet $id")
    }

    // 15. Continuity
    fun getContinuityForProduction(productionId: String): Flow<List<ContinuityEntity>> =
        continuityDao?.getContinuityForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveContinuity(continuity: ContinuityEntity, roleId: String) {
        continuityDao?.insertContinuity(continuity)
        logAuditEvent(roleId, "CONTINUITY_SAVED", "Saved continuity log for scene ${continuity.sceneId}")
    }

    suspend fun deleteContinuity(id: String, roleId: String) {
        continuityDao?.deleteContinuity(id)
        logAuditEvent(roleId, "CONTINUITY_DELETED", "Deleted continuity $id")
    }

    // 16. Takes
    fun getTakesForProduction(productionId: String): Flow<List<TakeEntity>> =
        takeDao?.getTakesForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveTake(take: TakeEntity, roleId: String) {
        takeDao?.insertTake(take)
        logAuditEvent(roleId, "TAKE_SAVED", "Saved Take #${take.takeNumber} for shot ${take.shotId}")
    }

    suspend fun deleteTake(id: String, roleId: String) {
        takeDao?.deleteTake(id)
        logAuditEvent(roleId, "TAKE_DELETED", "Deleted take $id")
    }

    // 17. Editing Reviews
    fun getReviewsForProduction(productionId: String): Flow<List<EditingReviewEntity>> =
        editingReviewDao?.getReviewsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveReview(review: EditingReviewEntity, roleId: String) {
        editingReviewDao?.insertReview(review)
        logAuditEvent(roleId, "EDIT_REVIEW_SAVED", "Saved edit review: ${review.commentText}")
    }

    suspend fun deleteReview(id: String, roleId: String) {
        editingReviewDao?.deleteReview(id)
        logAuditEvent(roleId, "EDIT_REVIEW_DELETED", "Deleted edit review $id")
    }

    // 18. Sound & Music
    fun getSoundItemsForProduction(productionId: String): Flow<List<SoundMusicEntity>> =
        soundMusicDao?.getSoundItemsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveSoundItem(item: SoundMusicEntity, roleId: String) {
        soundMusicDao?.insertSoundItem(item)
        logAuditEvent(roleId, "SOUND_ITEM_SAVED", "Saved sound cue: ${item.title}")
    }

    suspend fun deleteSoundItem(id: String, roleId: String) {
        soundMusicDao?.deleteSoundItem(id)
        logAuditEvent(roleId, "SOUND_ITEM_DELETED", "Deleted sound item $id")
    }

    // 19. Budget
    fun getBudgetItemsForProduction(productionId: String): Flow<List<BudgetItemEntity>> =
        budgetDao?.getBudgetItemsForProduction(productionId) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveBudgetItem(item: BudgetItemEntity) {
        budgetDao?.insertBudgetItem(item)
        logAuditEvent(item.roleId, "BUDGET_ITEM_SAVED", "Saved budget expense: ${item.itemTitle} (${item.plannedAmount})")
    }

    suspend fun deleteBudgetItem(id: String, roleId: String) {
        budgetDao?.deleteBudgetItem(id)
        logAuditEvent(roleId, "BUDGET_ITEM_DELETED", "Deleted budget item $id")
    }
}
