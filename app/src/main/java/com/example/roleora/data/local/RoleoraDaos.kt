package com.example.roleora.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roleora.data.model.AuditEventEntity
import com.example.roleora.data.model.DiaryEntryEntity
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.data.model.ProfessionTemplateEntity
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.SessionEntity
import com.example.roleora.data.model.TemplateInstallationEntity
import com.example.roleora.data.model.TemplateVersionEntity
import com.example.roleora.data.model.UserEntity
import com.example.roleora.data.model.WorkspaceTemplateVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoleDao {
    @Query("SELECT * FROM roles WHERE isArchived = 0 ORDER BY createdAt ASC")
    fun getAllActiveRoles(): Flow<List<RoleEntity>>

    @Query("SELECT * FROM roles WHERE isArchived = 0 ORDER BY createdAt ASC")
    suspend fun getAllActiveRolesList(): List<RoleEntity>

    @Query("SELECT * FROM roles WHERE id = :roleId")
    suspend fun getRoleById(roleId: String): RoleEntity?

    @Query("SELECT * FROM roles WHERE id = :roleId")
    fun observeRoleById(roleId: String): Flow<RoleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: RoleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoles(roles: List<RoleEntity>)

    @Update
    suspend fun updateRole(role: RoleEntity)

    @Query("UPDATE roles SET isArchived = 1, updatedAt = :timestamp WHERE id = :roleId")
    suspend fun archiveRole(roleId: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM roles WHERE id = :roleId")
    suspend fun deleteRole(roleId: String)

    @Query("SELECT COUNT(*) FROM roles WHERE isArchived = 0")
    suspend fun getActiveRoleCount(): Int

    @Query("DELETE FROM roles")
    suspend fun deleteAllRoles()
}

@Dao
interface TemplateDao {
    @Query("SELECT * FROM profession_templates ORDER BY popularityRank ASC")
    fun getAllTemplates(): Flow<List<ProfessionTemplateEntity>>

    @Query("SELECT * FROM profession_templates WHERE id = :id")
    suspend fun getTemplateById(id: String): ProfessionTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<ProfessionTemplateEntity>)

    @Query("SELECT * FROM template_versions WHERE templateId = :templateId ORDER BY versionNumber DESC")
    fun getVersionsForTemplate(templateId: String): Flow<List<TemplateVersionEntity>>

    @Query("SELECT * FROM template_versions WHERE id = :versionId")
    suspend fun getVersionById(versionId: String): TemplateVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersions(versions: List<TemplateVersionEntity>)

    @Query("SELECT * FROM template_installations WHERE roleId = :roleId")
    suspend fun getInstallationForRole(roleId: String): TemplateInstallationEntity?

    @Query("SELECT * FROM template_installations WHERE roleId = :roleId")
    fun observeInstallationForRole(roleId: String): Flow<TemplateInstallationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallation(installation: TemplateInstallationEntity)

    @Update
    suspend fun updateInstallation(installation: TemplateInstallationEntity)
}

@Dao
interface EntryDao {
    @Query("SELECT * FROM diary_entries WHERE roleId = :roleId ORDER BY activityDate DESC, createdAt DESC")
    fun getEntriesForRole(roleId: String): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries ORDER BY activityDate DESC")
    suspend fun getAllEntriesList(): List<DiaryEntryEntity>

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getEntryById(id: String): DiaryEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<DiaryEntryEntity>)

    @Update
    suspend fun updateEntry(entry: DiaryEntryEntity)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteEntry(id: String)

    @Query("DELETE FROM diary_entries WHERE roleId = :roleId")
    suspend fun deleteEntriesForRole(roleId: String)

    @Query("DELETE FROM diary_entries")
    suspend fun deleteAllEntries()
}

@Dao
interface RecordDao {
    @Query("SELECT * FROM profession_records WHERE roleId = :roleId ORDER BY updatedAt DESC")
    fun getAllRecordsForRole(roleId: String): Flow<List<ProfessionRecordEntity>>

    @Query("SELECT * FROM profession_records ORDER BY updatedAt DESC")
    suspend fun getAllRecordsList(): List<ProfessionRecordEntity>

    @Query("SELECT * FROM profession_records WHERE roleId = :roleId AND recordCategory = :category ORDER BY updatedAt DESC")
    fun getRecordsByCategory(roleId: String, category: String): Flow<List<ProfessionRecordEntity>>

    @Query("SELECT * FROM profession_records WHERE id = :id")
    suspend fun getRecordById(id: String): ProfessionRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ProfessionRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<ProfessionRecordEntity>)

    @Update
    suspend fun updateRecord(record: ProfessionRecordEntity)

    @Query("DELETE FROM profession_records WHERE id = :id")
    suspend fun deleteRecord(id: String)

    @Query("DELETE FROM profession_records WHERE roleId = :roleId")
    suspend fun deleteRecordsForRole(roleId: String)

    @Query("DELETE FROM profession_records")
    suspend fun deleteAllRecords()
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_events WHERE roleId = :roleId ORDER BY timestamp DESC")
    fun getAuditEventsForRole(roleId: String): Flow<List<AuditEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditEvent(event: AuditEventEntity)

    @Query("DELETE FROM audit_events")
    suspend fun deleteAllAuditEvents()
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun observeUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY lastLoginAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY lastActiveAt DESC LIMIT 1")
    suspend fun getActiveSession(): SessionEntity?

    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY lastActiveAt DESC LIMIT 1")
    fun observeActiveSession(): Flow<SessionEntity?>

    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE userId = :userId ORDER BY lastActiveAt DESC")
    fun getSessionsForUser(userId: String): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("UPDATE sessions SET isActive = 0 WHERE userId = :userId")
    suspend fun deactivateAllSessionsForUser(userId: String)

    @Query("UPDATE sessions SET isActive = 0 WHERE sessionId = :sessionId")
    suspend fun deactivateSession(sessionId: String)

    @Query("UPDATE sessions SET lastActiveAt = :timestamp WHERE sessionId = :sessionId")
    suspend fun updateSessionHeartbeat(sessionId: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
}

@Dao
interface WorkspaceTemplateVersionDao {
    @Query("SELECT * FROM workspace_template_versions WHERE roleId = :roleId ORDER BY createdAt DESC")
    fun getVersionsForWorkspace(roleId: String): Flow<List<WorkspaceTemplateVersionEntity>>

    @Query("SELECT * FROM workspace_template_versions WHERE roleId = :roleId ORDER BY createdAt DESC")
    suspend fun getVersionsForWorkspaceList(roleId: String): List<WorkspaceTemplateVersionEntity>

    @Query("SELECT * FROM workspace_template_versions WHERE versionId = :versionId")
    suspend fun getVersionById(versionId: String): WorkspaceTemplateVersionEntity?

    @Query("SELECT * FROM workspace_template_versions WHERE versionId = :versionId")
    fun observeVersionById(versionId: String): Flow<WorkspaceTemplateVersionEntity?>

    @Query("SELECT * FROM workspace_template_versions WHERE roleId = :roleId AND isCurrentActive = 1 LIMIT 1")
    suspend fun getActiveVersionForWorkspace(roleId: String): WorkspaceTemplateVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: WorkspaceTemplateVersionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersions(versions: List<WorkspaceTemplateVersionEntity>)

    @Update
    suspend fun updateVersion(version: WorkspaceTemplateVersionEntity)

    @Query("UPDATE workspace_template_versions SET isCurrentActive = CASE WHEN versionId = :versionId THEN 1 ELSE 0 END WHERE roleId = :roleId")
    suspend fun setActiveVersion(roleId: String, versionId: String)

    @Query("UPDATE workspace_template_versions SET isFavorite = NOT isFavorite WHERE versionId = :versionId")
    suspend fun toggleFavorite(versionId: String)

    @Query("UPDATE workspace_template_versions SET isLocked = NOT isLocked WHERE versionId = :versionId")
    suspend fun toggleLock(versionId: String)

    @Query("DELETE FROM workspace_template_versions WHERE versionId = :versionId")
    suspend fun deleteVersion(versionId: String)

    @Query("DELETE FROM workspace_template_versions WHERE roleId = :roleId AND isLocked = 0")
    suspend fun deleteUnlockedVersionsForWorkspace(roleId: String)

    @Query("DELETE FROM workspace_template_versions WHERE roleId = :roleId")
    suspend fun deleteAllVersionsForWorkspace(roleId: String)

    @Query("SELECT COUNT(*) FROM workspace_template_versions WHERE roleId = :roleId")
    suspend fun countVersionsForWorkspace(roleId: String): Int
}


