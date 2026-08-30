package com.example.roleora.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Universal Entry Types for Roleora Phase 2 Universal Create System
 */
enum class EntryType(val displayName: String, val iconName: String) {
    DIARY("Diary entry", "MenuBook"),
    NOTE("Note", "Description"),
    TASK("Task", "CheckCircle"),
    EVENT("Event", "Event"),
    PHOTO("Photo", "PhotoCamera"),
    VOICE("Voice recording", "Mic"),
    VIDEO("Video", "Videocam"),
    DOCUMENT("Document", "InsertDriveFile"),
    EXPENSE("Expense", "AttachMoney"),
    PROJECT_UPDATE("Project update", "TrendingUp"),
    CUSTOM("Custom record placeholder", "Widgets");

    companion object {
        fun fromString(value: String): EntryType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) } ?: DIARY
    }
}

enum class SecurityLevel(val label: String, val levelNumber: Int) {
    PUBLIC("Public", 0),
    ROLE_RESTRICTED("Role Restricted", 1),
    CONFIDENTIAL("Confidential", 2),
    PRIVATE("Private (Password Locked)", 3);

    companion object {
        fun fromString(value: String): SecurityLevel =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: ROLE_RESTRICTED
    }
}

enum class SyncState {
    LOCAL_DRAFT,
    PENDING,
    SYNCHRONISING,
    SYNCED,
    CONFLICT,
    FAILED
}

enum class TaskStatus(val label: String) {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    WAITING("Waiting"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): TaskStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: NOT_STARTED
    }
}

enum class TaskPriority(val label: String, val colorHex: Long) {
    LOW("Low", 0xFF4CAF50),
    MEDIUM("Medium", 0xFF2196F3),
    HIGH("High", 0xFFFF9800),
    URGENT("Urgent", 0xFFF44336);

    companion object {
        fun fromString(value: String): TaskPriority =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: MEDIUM
    }
}

enum class RecurrencePattern(val label: String) {
    NONE("None"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly");

    companion object {
        fun fromString(value: String): RecurrencePattern =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: NONE
    }
}

enum class UploadStatus {
    QUEUED,
    UPLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED,
    DELETED
}

// -------------------------------------------------------------------------------------------------
// Room Entities
// -------------------------------------------------------------------------------------------------

/**
 * Universal Entry Entity meeting all Section 2 Common Entry Model requirements.
 */
@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey val entryId: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val roleId: String,
    val entryType: String = EntryType.DIARY.name,
    val title: String,
    val content: String = "",
    val activityDateTime: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val timezone: String = java.util.TimeZone.getDefault().id,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val duration: Long? = null,
    val status: String = "Active",
    val securityLevel: String = SecurityLevel.ROLE_RESTRICTED.name,
    val folderId: String? = null,
    val projectId: String? = null,
    val tags: String = "", // Comma-separated
    val location: String? = null,
    val attachmentIds: String = "", // Comma-separated
    val version: Int = 1,
    val syncStatus: String = SyncState.LOCAL_DRAFT.name,
    val deletedAt: Long? = null,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val diaryMood: String? = null,
    val diaryType: String = "Personal", // Personal vs Professional
    val extraJson: String = "{}"
)

/**
 * Version History Entity for Entry snapshots and rollback (Section 19).
 */
@Entity(tableName = "entry_versions")
data class EntryVersionEntity(
    @PrimaryKey val versionId: String = UUID.randomUUID().toString(),
    val entryId: String,
    val roleId: String,
    val versionNumber: Int,
    val editorId: String,
    val modifiedAt: Long = System.currentTimeMillis(),
    val changedFields: String = "",
    val snapshotJson: String,
    val changeReason: String? = null
)

/**
 * Tasks Entity meeting Section 6 requirements.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val taskId: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val roleId: String,
    val title: String,
    val description: String = "",
    val priority: String = TaskPriority.MEDIUM.name,
    val status: String = TaskStatus.NOT_STARTED.name,
    val startDate: Long? = null,
    val dueDate: Long? = null,
    val dueTime: String? = null,
    val reminderTime: Long? = null,
    val recurrence: String = RecurrencePattern.NONE.name,
    val subtasksJson: String = "[]",
    val tags: String = "",
    val attachmentsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = SyncState.LOCAL_DRAFT.name,
    val deletedAt: Long? = null
)

/**
 * Events and Reminders Entity meeting Section 7 requirements.
 */
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val eventId: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val roleId: String,
    val title: String,
    val description: String = "",
    val startDateTime: Long = System.currentTimeMillis(),
    val endDateTime: Long = System.currentTimeMillis() + 3600000L,
    val isAllDay: Boolean = false,
    val location: String? = null,
    val repeatPattern: String = RecurrencePattern.NONE.name,
    val reminderMinutesBefore: Int = 15,
    val notes: String? = null,
    val status: String = "Confirmed",
    val attachmentsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = SyncState.LOCAL_DRAFT.name,
    val deletedAt: Long? = null
)

/**
 * Attachment Metadata Entity meeting Section 13 requirements.
 */
@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey val attachmentId: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val roleId: String,
    val parentEntryId: String? = null,
    val originalFileName: String,
    val safeDisplayName: String,
    val mimeType: String,
    val size: Long = 0L,
    val storagePath: String = "",
    val localUri: String? = null,
    val downloadUrl: String? = null,
    val uploadStatus: String = UploadStatus.QUEUED.name,
    val processingStatus: String = "Pending",
    val createdAt: Long = System.currentTimeMillis(),
    val hash: String? = null,
    val securityLevel: String = SecurityLevel.ROLE_RESTRICTED.name,
    val thumbnailPath: String? = null,
    val deletedAt: Long? = null
)

/**
 * Work Session Timer Entity meeting Section 8 requirements.
 */
@Entity(tableName = "work_sessions")
data class WorkSessionEntity(
    @PrimaryKey val sessionId: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val roleId: String,
    val linkedTaskId: String? = null,
    val description: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val pausedDurationMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val lastPauseTimestamp: Long? = null,
    val savedAsDiaryEntryId: String? = null
)

/**
 * Synchronisation Queue Entity for offline action log & retry (Section 17).
 */
@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val queueId: String = UUID.randomUUID().toString(),
    val entityType: String, // entry, task, event, attachment, version
    val entityId: String,
    val roleId: String,
    val action: String, // CREATE, UPDATE, DELETE, RESTORE
    val payloadJson: String,
    val retryCount: Int = 0,
    val lastAttemptAt: Long = 0L,
    val status: String = "Pending",
    val createdAt: Long = System.currentTimeMillis()
)

// -------------------------------------------------------------------------------------------------
// Helper Data Classes for UI and JSON Parsers
// -------------------------------------------------------------------------------------------------

data class SubtaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false
)

data class ConflictResolutionData(
    val entityId: String,
    val localVersion: Int,
    val cloudVersion: Int,
    val localTitle: String,
    val cloudTitle: String,
    val localContent: String,
    val cloudContent: String,
    val localUpdatedAt: Long,
    val cloudUpdatedAt: Long
)
