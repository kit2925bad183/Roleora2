package com.example.roleora.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roleora.data.model.AttachmentEntity
import com.example.roleora.data.model.EntryEntity
import com.example.roleora.data.model.EntryVersionEntity
import com.example.roleora.data.model.EventEntity
import com.example.roleora.data.model.SyncQueueEntity
import com.example.roleora.data.model.TaskEntity
import com.example.roleora.data.model.WorkSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UniversalEntryDao {
    @Query("SELECT * FROM entries WHERE roleId = :roleId AND deletedAt IS NULL ORDER BY activityDateTime DESC")
    fun getEntriesForRole(roleId: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE roleId = :roleId AND deletedAt IS NULL ORDER BY activityDateTime DESC LIMIT :limit OFFSET :offset")
    suspend fun getEntriesForRolePaged(roleId: String, limit: Int, offset: Int): List<EntryEntity>

    @Query("SELECT * FROM entries WHERE roleId IN (:roleIds) AND deletedAt IS NULL ORDER BY activityDateTime DESC")
    fun getEntriesForRoles(roleIds: List<String>): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE roleId = :roleId AND entryType = :entryType AND deletedAt IS NULL ORDER BY activityDateTime DESC")
    fun getEntriesByType(roleId: String, entryType: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE roleId = :roleId AND activityDateTime BETWEEN :startTime AND :endTime AND deletedAt IS NULL ORDER BY activityDateTime ASC")
    fun getEntriesBetweenDates(roleId: String, startTime: Long, endTime: Long): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE roleId = :roleId AND deletedAt IS NULL AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%') ORDER BY activityDateTime DESC")
    fun searchEntries(roleId: String, query: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE roleId IN (:roleIds) AND deletedAt IS NULL AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%') ORDER BY activityDateTime DESC")
    fun searchEntriesMultiRole(roleIds: List<String>, query: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE entryId = :entryId LIMIT 1")
    suspend fun getEntryById(entryId: String): EntryEntity?

    @Query("SELECT * FROM entries WHERE entryId = :entryId LIMIT 1")
    fun observeEntryById(entryId: String): Flow<EntryEntity?>

    @Query("SELECT * FROM entries WHERE roleId = :roleId AND isPinned = 1 AND deletedAt IS NULL ORDER BY activityDateTime DESC")
    fun getPinnedEntries(roleId: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE roleId = :roleId AND isFavorite = 1 AND deletedAt IS NULL ORDER BY activityDateTime DESC")
    fun getFavoriteEntries(roleId: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE roleId = :roleId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashEntries(roleId: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE ownerId = :ownerId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getAllTrashForOwner(ownerId: String): Flow<List<EntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: EntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<EntryEntity>)

    @Update
    suspend fun updateEntry(entry: EntryEntity)

    @Query("UPDATE entries SET deletedAt = :timestamp, syncStatus = 'PENDING' WHERE entryId = :entryId")
    suspend fun moveToTrash(entryId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE entries SET deletedAt = NULL, syncStatus = 'PENDING' WHERE entryId = :entryId")
    suspend fun restoreFromTrash(entryId: String)

    @Query("DELETE FROM entries WHERE entryId = :entryId")
    suspend fun deletePermanently(entryId: String)

    @Query("DELETE FROM entries WHERE roleId = :roleId AND deletedAt IS NOT NULL")
    suspend fun emptyTrashForRole(roleId: String)

    @Query("DELETE FROM entries WHERE roleId = :roleId")
    suspend fun deleteAllEntriesForRole(roleId: String)

    @Query("DELETE FROM entries")
    suspend fun deleteAllEntries()

    @Query("SELECT * FROM entries WHERE syncStatus = 'PENDING' OR syncStatus = 'LOCAL_DRAFT'")
    suspend fun getPendingSyncEntries(): List<EntryEntity>
}

@Dao
interface UniversalEntryVersionDao {
    @Query("SELECT * FROM entry_versions WHERE entryId = :entryId ORDER BY versionNumber DESC")
    fun getVersionsForEntry(entryId: String): Flow<List<EntryVersionEntity>>

    @Query("SELECT * FROM entry_versions WHERE versionId = :versionId LIMIT 1")
    suspend fun getVersionById(versionId: String): EntryVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: EntryVersionEntity)

    @Query("DELETE FROM entry_versions WHERE entryId = :entryId")
    suspend fun deleteVersionsForEntry(entryId: String)

    @Query("DELETE FROM entry_versions WHERE roleId = :roleId")
    suspend fun deleteVersionsForRole(roleId: String)

    @Query("DELETE FROM entry_versions")
    suspend fun deleteAllVersions()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE roleId = :roleId AND deletedAt IS NULL ORDER BY createdAt DESC")
    fun getTasksForRole(roleId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE roleId = :roleId AND status = :status AND deletedAt IS NULL ORDER BY dueDate ASC")
    fun getTasksByStatus(roleId: String, status: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE roleId = :roleId AND dueDate IS NOT NULL AND dueDate < :currentTime AND status != 'COMPLETED' AND status != 'CANCELLED' AND deletedAt IS NULL")
    fun getOverdueTasks(roleId: String, currentTime: Long = System.currentTimeMillis()): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE taskId = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE taskId = :taskId LIMIT 1")
    fun observeTaskById(taskId: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE roleId = :roleId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashTasks(roleId: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = 'COMPLETED', completedAt = :timestamp, syncStatus = 'PENDING' WHERE taskId = :taskId")
    suspend fun completeTask(taskId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET status = 'IN_PROGRESS', completedAt = NULL, syncStatus = 'PENDING' WHERE taskId = :taskId")
    suspend fun reopenTask(taskId: String)

    @Query("UPDATE tasks SET deletedAt = :timestamp, syncStatus = 'PENDING' WHERE taskId = :taskId")
    suspend fun moveToTrash(taskId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET deletedAt = NULL, syncStatus = 'PENDING' WHERE taskId = :taskId")
    suspend fun restoreFromTrash(taskId: String)

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deletePermanently(taskId: String)

    @Query("DELETE FROM tasks WHERE roleId = :roleId AND deletedAt IS NOT NULL")
    suspend fun emptyTrashForRole(roleId: String)

    @Query("DELETE FROM tasks WHERE roleId = :roleId")
    suspend fun deleteAllTasksForRole(roleId: String)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM tasks WHERE syncStatus = 'PENDING' OR syncStatus = 'LOCAL_DRAFT'")
    suspend fun getPendingSyncTasks(): List<TaskEntity>
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE roleId = :roleId AND deletedAt IS NULL ORDER BY startDateTime ASC")
    fun getEventsForRole(roleId: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE roleId = :roleId AND startDateTime BETWEEN :startTime AND :endTime AND deletedAt IS NULL ORDER BY startDateTime ASC")
    fun getEventsBetweenDates(roleId: String, startTime: Long, endTime: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE roleId = :roleId AND startDateTime >= :currentTime AND deletedAt IS NULL ORDER BY startDateTime ASC LIMIT :limit")
    fun getUpcomingEvents(roleId: String, currentTime: Long = System.currentTimeMillis(), limit: Int = 10): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE eventId = :eventId LIMIT 1")
    suspend fun getEventById(eventId: String): EventEntity?

    @Query("SELECT * FROM events WHERE eventId = :eventId LIMIT 1")
    fun observeEventById(eventId: String): Flow<EventEntity?>

    @Query("SELECT * FROM events WHERE roleId = :roleId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashEvents(roleId: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("UPDATE events SET deletedAt = :timestamp, syncStatus = 'PENDING' WHERE eventId = :eventId")
    suspend fun moveToTrash(eventId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE events SET deletedAt = NULL, syncStatus = 'PENDING' WHERE eventId = :eventId")
    suspend fun restoreFromTrash(eventId: String)

    @Query("DELETE FROM events WHERE eventId = :eventId")
    suspend fun deletePermanently(eventId: String)

    @Query("DELETE FROM events WHERE roleId = :roleId AND deletedAt IS NOT NULL")
    suspend fun emptyTrashForRole(roleId: String)

    @Query("DELETE FROM events WHERE roleId = :roleId")
    suspend fun deleteAllEventsForRole(roleId: String)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()

    @Query("SELECT * FROM events WHERE syncStatus = 'PENDING' OR syncStatus = 'LOCAL_DRAFT'")
    suspend fun getPendingSyncEvents(): List<EventEntity>
}

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE roleId = :roleId AND deletedAt IS NULL ORDER BY createdAt DESC")
    fun getAttachmentsForRole(roleId: String): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE parentEntryId = :entryId AND deletedAt IS NULL ORDER BY createdAt ASC")
    fun getAttachmentsForEntry(entryId: String): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE parentEntryId = :entryId AND deletedAt IS NULL ORDER BY createdAt ASC")
    suspend fun getAttachmentsForEntryList(entryId: String): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE attachmentId = :id LIMIT 1")
    suspend fun getAttachmentById(id: String): AttachmentEntity?

    @Query("SELECT * FROM attachments WHERE uploadStatus IN ('QUEUED', 'UPLOADING', 'FAILED')")
    suspend fun getPendingUploads(): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE roleId = :roleId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashAttachments(roleId: String): Flow<List<AttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<AttachmentEntity>)

    @Update
    suspend fun updateAttachment(attachment: AttachmentEntity)

    @Query("UPDATE attachments SET deletedAt = :timestamp WHERE attachmentId = :id")
    suspend fun moveToTrash(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE attachments SET deletedAt = NULL WHERE attachmentId = :id")
    suspend fun restoreFromTrash(id: String)

    @Query("DELETE FROM attachments WHERE attachmentId = :id")
    suspend fun deletePermanently(id: String)

    @Query("DELETE FROM attachments WHERE parentEntryId = :entryId")
    suspend fun deleteAttachmentsForEntry(entryId: String)

    @Query("DELETE FROM attachments WHERE roleId = :roleId")
    suspend fun deleteAllAttachmentsForRole(roleId: String)

    @Query("DELETE FROM attachments")
    suspend fun deleteAllAttachments()
}

@Dao
interface WorkSessionDao {
    @Query("SELECT * FROM work_sessions WHERE roleId = :roleId AND isRunning = 1 LIMIT 1")
    suspend fun getActiveRunningSession(roleId: String): WorkSessionEntity?

    @Query("SELECT * FROM work_sessions WHERE roleId = :roleId ORDER BY startTime DESC LIMIT 1")
    fun observeLatestSession(roleId: String): Flow<WorkSessionEntity?>

    @Query("SELECT * FROM work_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): WorkSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkSessionEntity)

    @Update
    suspend fun updateSession(session: WorkSessionEntity)

    @Query("DELETE FROM work_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM work_sessions WHERE roleId = :roleId")
    suspend fun deleteAllSessionsForRole(roleId: String)

    @Query("DELETE FROM work_sessions")
    suspend fun deleteAllSessions()
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'Pending' ORDER BY createdAt ASC")
    suspend fun getPendingQueue(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItem(item: SyncQueueEntity)

    @Update
    suspend fun updateQueueItem(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE queueId = :queueId")
    suspend fun deleteQueueItem(queueId: String)

    @Query("DELETE FROM sync_queue WHERE roleId = :roleId")
    suspend fun deleteQueueForRole(roleId: String)

    @Query("DELETE FROM sync_queue")
    suspend fun deleteAllQueue()
}
