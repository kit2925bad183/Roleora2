package com.example.roleora.data.cloud

import android.util.Log
import com.example.roleora.data.model.AuditEventEntity
import com.example.roleora.data.model.DiaryEntryEntity
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.data.model.RoleEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed interface CloudSyncState {
    data object Idle : CloudSyncState
    data class Syncing(val progressMessage: String) : CloudSyncState
    data class Synced(
        val lastSyncTimestamp: Long,
        val rolesCount: Int,
        val recordsCount: Int,
        val entriesCount: Int
    ) : CloudSyncState
    data class Error(val message: String) : CloudSyncState
}

data class CloudBackupPayload(
    val roles: List<RoleEntity>,
    val records: List<ProfessionRecordEntity>,
    val diaryEntries: List<DiaryEntryEntity>,
    val auditEvents: List<AuditEventEntity>
)

class FirestoreSyncManager {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _syncState = MutableStateFlow<CloudSyncState>(CloudSyncState.Idle)
    val syncState: StateFlow<CloudSyncState> = _syncState.asStateFlow()

    /**
     * Backup all local roles, profession records, and diary entries to user's Firestore collection.
     */
    suspend fun backupAllToFirestore(
        userId: String,
        roles: List<RoleEntity>,
        records: List<ProfessionRecordEntity>,
        entries: List<DiaryEntryEntity>,
        auditEvents: List<AuditEventEntity>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        _syncState.value = CloudSyncState.Syncing("Uploading workspaces to Firestore...")
        try {
            val userDocRef = firestore.collection("users").document(userId)

            // 1. Update user root metadata
            val userMetadata = mapOf(
                "lastSyncAt" to System.currentTimeMillis(),
                "totalRoles" to roles.size,
                "totalRecords" to records.size,
                "totalEntries" to entries.size,
                "appVersion" to "1.0.0"
            )
            userDocRef.set(userMetadata, SetOptions.merge()).await()

            // 2. Sync roles
            _syncState.value = CloudSyncState.Syncing("Syncing ${roles.size} roles to Firestore...")
            for (role in roles) {
                val roleMap = mapOf(
                    "id" to role.id,
                    "templateId" to role.templateId,
                    "displayName" to role.displayName,
                    "specialisation" to role.specialisation,
                    "workType" to role.workType,
                    "colorHex" to role.colorHex,
                    "iconName" to role.iconName,
                    "templateVersion" to role.templateVersion,
                    "category" to role.category,
                    "aiEnabled" to role.aiEnabled,
                    "isPrivate" to role.isPrivate,
                    "isArchived" to role.isArchived,
                    "updatedAt" to role.updatedAt
                )
                userDocRef.collection("roles").document(role.id).set(roleMap, SetOptions.merge()).await()
            }

            // 3. Sync profession records
            _syncState.value = CloudSyncState.Syncing("Persisting ${records.size} profession records...")
            for (record in records) {
                val recMap = mapOf(
                    "id" to record.id,
                    "roleId" to record.roleId,
                    "professionType" to record.professionType,
                    "recordCategory" to record.recordCategory,
                    "title" to record.title,
                    "subtitle" to record.subtitle,
                    "stage" to record.stage,
                    "status" to record.status,
                    "numericValue1" to record.numericValue1,
                    "numericValue2" to record.numericValue2,
                    "detailsJson" to record.detailsJson,
                    "tags" to record.tags,
                    "updatedAt" to record.updatedAt
                )
                userDocRef.collection("roles")
                    .document(record.roleId)
                    .collection("records")
                    .document(record.id)
                    .set(recMap, SetOptions.merge())
                    .await()
            }

            // 4. Sync diary entries
            _syncState.value = CloudSyncState.Syncing("Syncing ${entries.size} diary entries...")
            for (entry in entries) {
                val entryMap = mapOf(
                    "id" to entry.id,
                    "roleId" to entry.roleId,
                    "title" to entry.title,
                    "content" to entry.content,
                    "entryType" to entry.entryType,
                    "activityDate" to entry.activityDate,
                    "tags" to entry.tags,
                    "securityLevel" to entry.securityLevel,
                    "isPinned" to entry.isPinned,
                    "moodOrStatus" to entry.moodOrStatus,
                    "updatedAt" to entry.updatedAt
                )
                userDocRef.collection("roles")
                    .document(entry.roleId)
                    .collection("diary_entries")
                    .document(entry.id)
                    .set(entryMap, SetOptions.merge())
                    .await()
            }

            val now = System.currentTimeMillis()
            _syncState.value = CloudSyncState.Synced(
                lastSyncTimestamp = now,
                rolesCount = roles.size,
                recordsCount = records.size,
                entriesCount = entries.size
            )
            Log.d("FirestoreSyncManager", "Successfully backed up ${roles.size} roles and ${records.size} records to Firestore.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Failed to backup to Firestore: ${e.message}", e)
            _syncState.value = CloudSyncState.Error(e.localizedMessage ?: "Firestore sync failed")
            Result.failure(e)
        }
    }

    /**
     * Fetch only active roles from Firestore for the given user.
     */
    suspend fun fetchActiveRolesFromFirestore(userId: String): Result<List<RoleEntity>> = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val rolesSnapshot = userDocRef.collection("roles").get().await()

            val roles = rolesSnapshot.documents.mapNotNull { doc ->
                val isArchived = doc.getBoolean("isArchived") ?: false
                if (isArchived) return@mapNotNull null

                RoleEntity(
                    id = doc.getString("id") ?: doc.id,
                    templateId = doc.getString("templateId") ?: "movie_director",
                    displayName = doc.getString("displayName") ?: "Workspace",
                    specialisation = doc.getString("specialisation") ?: "",
                    workType = doc.getString("workType") ?: "",
                    colorHex = doc.getString("colorHex") ?: "#1B3B5F",
                    iconName = doc.getString("iconName") ?: "movie",
                    templateVersion = doc.getString("templateVersion") ?: "1.0.0",
                    category = doc.getString("category") ?: "Media & Arts",
                    aiEnabled = doc.getBoolean("aiEnabled") ?: false,
                    isPrivate = doc.getBoolean("isPrivate") ?: true,
                    isArchived = false,
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            }
            Result.success(roles)
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Failed to fetch active roles from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Restore all data from Firestore for a given authenticated user.
     */
    suspend fun restoreFromFirestore(userId: String): Result<CloudBackupPayload> = withContext(Dispatchers.IO) {
        _syncState.value = CloudSyncState.Syncing("Fetching cloud records from Firestore...")
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val rolesSnapshot = userDocRef.collection("roles").get().await()

            val fetchedRoles = mutableListOf<RoleEntity>()
            val fetchedRecords = mutableListOf<ProfessionRecordEntity>()
            val fetchedEntries = mutableListOf<DiaryEntryEntity>()

            for (roleDoc in rolesSnapshot.documents) {
                val roleId = roleDoc.getString("id") ?: roleDoc.id
                val role = RoleEntity(
                    id = roleId,
                    templateId = roleDoc.getString("templateId") ?: "movie_director",
                    displayName = roleDoc.getString("displayName") ?: "Workspace",
                    specialisation = roleDoc.getString("specialisation") ?: "",
                    workType = roleDoc.getString("workType") ?: "",
                    colorHex = roleDoc.getString("colorHex") ?: "#1B3B5F",
                    iconName = roleDoc.getString("iconName") ?: "movie",
                    templateVersion = roleDoc.getString("templateVersion") ?: "1.0.0",
                    category = roleDoc.getString("category") ?: "Media & Arts",
                    aiEnabled = roleDoc.getBoolean("aiEnabled") ?: false,
                    isPrivate = roleDoc.getBoolean("isPrivate") ?: true,
                    isArchived = roleDoc.getBoolean("isArchived") ?: false,
                    updatedAt = roleDoc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
                fetchedRoles.add(role)

                // Fetch records for this role
                val recordsSnapshot = roleDoc.reference.collection("records").get().await()
                for (recDoc in recordsSnapshot.documents) {
                    val record = ProfessionRecordEntity(
                        id = recDoc.getString("id") ?: recDoc.id,
                        roleId = recDoc.getString("roleId") ?: roleId,
                        professionType = recDoc.getString("professionType") ?: "",
                        recordCategory = recDoc.getString("recordCategory") ?: "",
                        title = recDoc.getString("title") ?: "",
                        subtitle = recDoc.getString("subtitle") ?: "",
                        stage = recDoc.getString("stage") ?: "",
                        status = recDoc.getString("status") ?: "",
                        numericValue1 = recDoc.getDouble("numericValue1") ?: 0.0,
                        numericValue2 = recDoc.getDouble("numericValue2") ?: 0.0,
                        detailsJson = recDoc.getString("detailsJson") ?: "{}",
                        tags = recDoc.getString("tags") ?: "",
                        updatedAt = recDoc.getLong("updatedAt") ?: System.currentTimeMillis()
                    )
                    fetchedRecords.add(record)
                }

                // Fetch diary entries for this role
                val entriesSnapshot = roleDoc.reference.collection("diary_entries").get().await()
                for (entryDoc in entriesSnapshot.documents) {
                    val entry = DiaryEntryEntity(
                        id = entryDoc.getString("id") ?: entryDoc.id,
                        roleId = entryDoc.getString("roleId") ?: roleId,
                        title = entryDoc.getString("title") ?: "",
                        content = entryDoc.getString("content") ?: "",
                        entryType = entryDoc.getString("entryType") ?: "Log",
                        activityDate = entryDoc.getLong("activityDate") ?: System.currentTimeMillis(),
                        tags = entryDoc.getString("tags") ?: "",
                        securityLevel = entryDoc.getString("securityLevel") ?: "Private",
                        isPinned = entryDoc.getBoolean("isPinned") ?: false,
                        moodOrStatus = entryDoc.getString("moodOrStatus") ?: "Normal",
                        createdAt = entryDoc.getLong("createdAt") ?: System.currentTimeMillis(),
                        updatedAt = entryDoc.getLong("updatedAt") ?: System.currentTimeMillis()
                    )
                    fetchedEntries.add(entry)
                }
            }

            val now = System.currentTimeMillis()
            _syncState.value = CloudSyncState.Synced(
                lastSyncTimestamp = now,
                rolesCount = fetchedRoles.size,
                recordsCount = fetchedRecords.size,
                entriesCount = fetchedEntries.size
            )

            Result.success(
                CloudBackupPayload(
                    roles = fetchedRoles,
                    records = fetchedRecords,
                    diaryEntries = fetchedEntries,
                    auditEvents = emptyList()
                )
            )
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Failed to restore from Firestore: ${e.message}", e)
            _syncState.value = CloudSyncState.Error(e.localizedMessage ?: "Firestore restore failed")
            Result.failure(e)
        }
    }

    /**
     * Persist an individual role to Firestore.
     */
    suspend fun saveRole(userId: String, role: RoleEntity) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val roleMap = mapOf(
                "id" to role.id,
                "templateId" to role.templateId,
                "displayName" to role.displayName,
                "specialisation" to role.specialisation,
                "workType" to role.workType,
                "colorHex" to role.colorHex,
                "iconName" to role.iconName,
                "templateVersion" to role.templateVersion,
                "category" to role.category,
                "aiEnabled" to role.aiEnabled,
                "isPrivate" to role.isPrivate,
                "isArchived" to role.isArchived,
                "updatedAt" to role.updatedAt
            )
            userDocRef.collection("roles").document(role.id).set(roleMap, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error saving role to Firestore: ${e.message}")
        }
    }

    /**
     * Persist an individual profession record to Firestore.
     */
    suspend fun saveRecord(userId: String, record: ProfessionRecordEntity) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val recMap = mapOf(
                "id" to record.id,
                "roleId" to record.roleId,
                "professionType" to record.professionType,
                "recordCategory" to record.recordCategory,
                "title" to record.title,
                "subtitle" to record.subtitle,
                "stage" to record.stage,
                "status" to record.status,
                "numericValue1" to record.numericValue1,
                "numericValue2" to record.numericValue2,
                "detailsJson" to record.detailsJson,
                "tags" to record.tags,
                "updatedAt" to record.updatedAt
            )
            userDocRef.collection("roles")
                .document(record.roleId)
                .collection("records")
                .document(record.id)
                .set(recMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error saving record to Firestore: ${e.message}")
        }
    }

    /**
     * Persist an individual diary entry to Firestore.
     */
    suspend fun saveDiaryEntry(userId: String, entry: DiaryEntryEntity) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val entryMap = mapOf(
                "id" to entry.id,
                "roleId" to entry.roleId,
                "title" to entry.title,
                "content" to entry.content,
                "entryType" to entry.entryType,
                "activityDate" to entry.activityDate,
                "tags" to entry.tags,
                "securityLevel" to entry.securityLevel,
                "isPinned" to entry.isPinned,
                "moodOrStatus" to entry.moodOrStatus,
                "updatedAt" to entry.updatedAt
            )
            userDocRef.collection("roles")
                .document(entry.roleId)
                .collection("diary_entries")
                .document(entry.id)
                .set(entryMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error saving diary entry to Firestore: ${e.message}")
        }
    }

    /**
     * Delete role and all its nested subcollections from Firestore.
     */
    suspend fun deleteRole(userId: String, roleId: String) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val roleDocRef = userDocRef.collection("roles").document(roleId)
            
            // Delete nested records
            val recordsSnap = roleDocRef.collection("records").get().await()
            for (doc in recordsSnap.documents) {
                doc.reference.delete().await()
            }

            // Delete nested diary entries
            val diarySnap = roleDocRef.collection("diary_entries").get().await()
            for (doc in diarySnap.documents) {
                doc.reference.delete().await()
            }

            // Delete nested audit events
            val auditSnap = roleDocRef.collection("audit_events").get().await()
            for (doc in auditSnap.documents) {
                doc.reference.delete().await()
            }

            // Delete role doc
            roleDocRef.delete().await()
            Log.d("FirestoreSyncManager", "Deleted role $roleId and subcollections.")
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error deleting role from Firestore: ${e.message}")
        }
    }

    /**
     * Archive or unarchive role in Firestore.
     */
    suspend fun archiveRole(userId: String, roleId: String, isArchived: Boolean) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.collection("roles").document(roleId)
                .update(mapOf("isArchived" to isArchived, "updatedAt" to System.currentTimeMillis()))
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error updating role archive state: ${e.message}")
        }
    }

    /**
     * Delete record from Firestore.
     */
    suspend fun deleteRecord(userId: String, roleId: String, recordId: String) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.collection("roles")
                .document(roleId)
                .collection("records")
                .document(recordId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error deleting record from Firestore: ${e.message}")
        }
    }

    /**
     * Delete diary entry from Firestore.
     */
    suspend fun deleteDiaryEntry(userId: String, roleId: String, entryId: String) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.collection("roles")
                .document(roleId)
                .collection("diary_entries")
                .document(entryId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error deleting diary entry from Firestore: ${e.message}")
        }
    }

    /**
     * Record an audit event in Firestore.
     */
    suspend fun recordAuditEvent(userId: String, event: AuditEventEntity) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val auditMap = mapOf(
                "id" to event.id,
                "roleId" to event.roleId,
                "eventType" to event.eventType,
                "description" to event.description,
                "timestamp" to event.timestamp,
                "metadataJson" to event.metadataJson
            )
            userDocRef.collection("roles")
                .document(event.roleId)
                .collection("audit_events")
                .document(event.id)
                .set(auditMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error recording audit event to Firestore: ${e.message}")
        }
    }

    /**
     * Phase 2: Save universal entry to Firestore.
     */
    suspend fun saveUniversalEntry(userId: String, roleId: String, entry: com.example.roleora.data.model.EntryEntity) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val entryMap = mapOf(
                "entryId" to entry.entryId,
                "ownerId" to entry.ownerId,
                "roleId" to entry.roleId,
                "entryType" to entry.entryType,
                "title" to entry.title,
                "content" to entry.content,
                "activityDateTime" to entry.activityDateTime,
                "createdAt" to entry.createdAt,
                "updatedAt" to entry.updatedAt,
                "timezone" to entry.timezone,
                "startTime" to entry.startTime,
                "endTime" to entry.endTime,
                "duration" to entry.duration,
                "status" to entry.status,
                "securityLevel" to entry.securityLevel,
                "folderId" to entry.folderId,
                "projectId" to entry.projectId,
                "tags" to entry.tags,
                "location" to entry.location,
                "attachmentIds" to entry.attachmentIds,
                "version" to entry.version,
                "syncStatus" to "SYNCED",
                "deletedAt" to entry.deletedAt,
                "isPinned" to entry.isPinned,
                "isFavorite" to entry.isFavorite,
                "diaryMood" to entry.diaryMood,
                "diaryType" to entry.diaryType,
                "extraJson" to entry.extraJson
            )
            userDocRef.collection("roles")
                .document(roleId)
                .collection("entries")
                .document(entry.entryId)
                .set(entryMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error saving universal entry to Firestore: ${e.message}")
        }
    }

    /**
     * Phase 2: Save universal task to Firestore.
     */
    suspend fun saveUniversalTask(userId: String, roleId: String, task: com.example.roleora.data.model.TaskEntity) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val taskMap = mapOf(
                "taskId" to task.taskId,
                "ownerId" to task.ownerId,
                "roleId" to task.roleId,
                "title" to task.title,
                "description" to task.description,
                "priority" to task.priority,
                "status" to task.status,
                "startDate" to task.startDate,
                "dueDate" to task.dueDate,
                "dueTime" to task.dueTime,
                "reminderTime" to task.reminderTime,
                "recurrence" to task.recurrence,
                "subtasksJson" to task.subtasksJson,
                "tags" to task.tags,
                "attachmentsJson" to task.attachmentsJson,
                "createdAt" to task.createdAt,
                "completedAt" to task.completedAt,
                "updatedAt" to task.updatedAt,
                "syncStatus" to "SYNCED",
                "deletedAt" to task.deletedAt
            )
            userDocRef.collection("roles")
                .document(roleId)
                .collection("tasks")
                .document(task.taskId)
                .set(taskMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error saving task to Firestore: ${e.message}")
        }
    }

    /**
     * Phase 2: Save universal event to Firestore.
     */
    suspend fun saveUniversalEvent(userId: String, roleId: String, event: com.example.roleora.data.model.EventEntity) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val eventMap = mapOf(
                "eventId" to event.eventId,
                "ownerId" to event.ownerId,
                "roleId" to event.roleId,
                "title" to event.title,
                "description" to event.description,
                "startDateTime" to event.startDateTime,
                "endDateTime" to event.endDateTime,
                "isAllDay" to event.isAllDay,
                "location" to event.location,
                "repeatPattern" to event.repeatPattern,
                "reminderMinutesBefore" to event.reminderMinutesBefore,
                "notes" to event.notes,
                "status" to event.status,
                "attachmentsJson" to event.attachmentsJson,
                "createdAt" to event.createdAt,
                "updatedAt" to event.updatedAt,
                "syncStatus" to "SYNCED",
                "deletedAt" to event.deletedAt
            )
            userDocRef.collection("roles")
                .document(roleId)
                .collection("events")
                .document(event.eventId)
                .set(eventMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error saving event to Firestore: ${e.message}")
        }
    }

    /**
     * Phase 2: Save attachment metadata to Firestore.
     */
    suspend fun saveAttachmentMetadata(userId: String, roleId: String, attachment: com.example.roleora.data.model.AttachmentEntity) = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val attachMap = mapOf(
                "attachmentId" to attachment.attachmentId,
                "ownerId" to attachment.ownerId,
                "roleId" to attachment.roleId,
                "parentEntryId" to attachment.parentEntryId,
                "originalFileName" to attachment.originalFileName,
                "safeDisplayName" to attachment.safeDisplayName,
                "mimeType" to attachment.mimeType,
                "size" to attachment.size,
                "storagePath" to attachment.storagePath,
                "downloadUrl" to attachment.downloadUrl,
                "uploadStatus" to attachment.uploadStatus,
                "processingStatus" to attachment.processingStatus,
                "createdAt" to attachment.createdAt,
                "hash" to attachment.hash,
                "securityLevel" to attachment.securityLevel,
                "thumbnailPath" to attachment.thumbnailPath,
                "deletedAt" to attachment.deletedAt
            )
            userDocRef.collection("roles")
                .document(roleId)
                .collection("attachments")
                .document(attachment.attachmentId)
                .set(attachMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Error saving attachment metadata to Firestore: ${e.message}")
        }
    }

    /**
     * Completely wipe user account and all workspaces from Firestore.
     */
    suspend fun deleteEntireUserAccount(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userDocRef = firestore.collection("users").document(userId)
            val rolesSnap = userDocRef.collection("roles").get().await()
            for (roleDoc in rolesSnap.documents) {
                deleteRole(userId, roleDoc.id)
            }
            userDocRef.delete().await()
            Log.d("FirestoreSyncManager", "Wiped all cloud workspaces for user $userId.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Failed to wipe user account from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }
}
