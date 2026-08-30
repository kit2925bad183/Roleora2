package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.roleora.data.local.AuditDao
import com.example.roleora.data.local.EntryDao
import com.example.roleora.data.local.RecordDao
import com.example.roleora.data.local.RoleDao
import com.example.roleora.data.local.RoleoraDatabase
import com.example.roleora.data.local.SessionDao
import com.example.roleora.data.local.TemplateDao
import com.example.roleora.data.local.UserDao
import com.example.roleora.data.model.AuditEventEntity
import com.example.roleora.data.model.DiaryEntryEntity
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.data.model.ProfessionTemplateEntity
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.SessionEntity
import com.example.roleora.data.model.UserEntity
import com.example.roleora.data.repository.RoleoraRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RoomDaoAndIsolationTest {

    private lateinit var db: RoleoraDatabase
    private lateinit var roleDao: RoleDao
    private lateinit var templateDao: TemplateDao
    private lateinit var recordDao: RecordDao
    private lateinit var entryDao: EntryDao
    private lateinit var auditDao: AuditDao
    private lateinit var userDao: UserDao
    private lateinit var sessionDao: SessionDao
    private lateinit var repository: RoleoraRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoleoraDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        roleDao = db.roleDao()
        templateDao = db.templateDao()
        recordDao = db.recordDao()
        entryDao = db.entryDao()
        auditDao = db.auditDao()
        userDao = db.userDao()
        sessionDao = db.sessionDao()

        repository = RoleoraRepository(roleDao, templateDao, entryDao, recordDao, auditDao, userDao, sessionDao)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testRoleInsertionAndQuerying() = runBlocking {
        val role1 = RoleEntity(
            id = "role_director_101",
            templateId = "movie_director",
            displayName = "Film Director",
            specialisation = "Cinematography",
            workType = "Independent",
            colorHex = "#1B3B5F",
            iconName = "movie",
            templateVersion = "1.0.0",
            category = "Media & Arts",
            aiEnabled = true,
            isPrivate = false
        )

        val role2 = RoleEntity(
            id = "role_farmer_202",
            templateId = "farmer",
            displayName = "Organic Farm",
            specialisation = "Paddy & Millets",
            workType = "Family Farm",
            colorHex = "#2E7D32",
            iconName = "agriculture",
            templateVersion = "1.0.0",
            category = "Agriculture",
            aiEnabled = false,
            isPrivate = true
        )

        roleDao.insertRole(role1)
        roleDao.insertRole(role2)

        val activeRoles = roleDao.getAllActiveRoles().first()
        assertEquals(2, activeRoles.size)

        val fetchedRole1 = roleDao.getRoleById("role_director_101")
        assertNotNull(fetchedRole1)
        assertEquals("Film Director", fetchedRole1?.displayName)
        assertTrue(fetchedRole1?.aiEnabled == true)

        val fetchedRole2 = roleDao.getRoleById("role_farmer_202")
        assertNotNull(fetchedRole2)
        assertEquals("Organic Farm", fetchedRole2?.displayName)
        assertFalse(fetchedRole2?.aiEnabled == true)
    }

    @Test
    fun testStrictRoleRecordIsolation() = runBlocking {
        val directorRoleId = "role_dir_1"
        val studentRoleId = "role_stud_2"

        val rec1 = ProfessionRecordEntity(
            id = "rec_scene_1",
            roleId = directorRoleId,
            professionType = "DIRECTOR",
            recordCategory = "SCREENPLAY",
            title = "Scene 1: Dawn at Thanjavur",
            subtitle = "Camera A & B Setup",
            stage = "Draft",
            numericValue1 = 1.0,
            numericValue2 = 4.5,
            detailsJson = "{\"lighting\":\"Golden Hour\"}"
        )

        val rec2 = ProfessionRecordEntity(
            id = "rec_exam_1",
            roleId = studentRoleId,
            professionType = "STUDENT",
            recordCategory = "EXAM",
            title = "Neural Networks Final Exam",
            subtitle = "Hall 402",
            stage = "Upcoming",
            numericValue1 = 92.0,
            numericValue2 = 100.0,
            detailsJson = "{\"syllabus\":\"Units 1-5\"}"
        )

        recordDao.insertRecord(rec1)
        recordDao.insertRecord(rec2)

        // Query director records
        val directorRecords = recordDao.getAllRecordsForRole(directorRoleId).first()
        assertEquals(1, directorRecords.size)
        assertEquals("rec_scene_1", directorRecords.first().id)
        assertEquals("Scene 1: Dawn at Thanjavur", directorRecords.first().title)

        // Query student records
        val studentRecords = recordDao.getAllRecordsForRole(studentRoleId).first()
        assertEquals(1, studentRecords.size)
        assertEquals("rec_exam_1", studentRecords.first().id)
        assertEquals("Neural Networks Final Exam", studentRecords.first().title)
    }

    @Test
    fun testDeletingOneRoleDoesNotAffectAnotherRole() = runBlocking {
        val roleA = RoleEntity(id = "role_A", templateId = "movie_director", displayName = "Role A", specialisation = "", workType = "", colorHex = "#000", iconName = "movie", templateVersion = "1.0", category = "Media", aiEnabled = false, isPrivate = true)
        val roleB = RoleEntity(id = "role_B", templateId = "software_developer", displayName = "Role B", specialisation = "", workType = "", colorHex = "#000", iconName = "code", templateVersion = "1.0", category = "Tech", aiEnabled = true, isPrivate = false)

        roleDao.insertRole(roleA)
        roleDao.insertRole(roleB)

        val recordA = ProfessionRecordEntity(id = "rec_A", roleId = "role_A", professionType = "DIR", recordCategory = "SHOT", title = "Shot A", subtitle = "", stage = "", detailsJson = "{}")
        val recordB = ProfessionRecordEntity(id = "rec_B", roleId = "role_B", professionType = "DEV", recordCategory = "BUG", title = "Bug B", subtitle = "", stage = "", detailsJson = "{}")

        recordDao.insertRecord(recordA)
        recordDao.insertRecord(recordB)

        // Delete Role A
        repository.deleteRole("role_A")

        // Role A records should be gone
        assertNull(roleDao.getRoleById("role_A"))
        val roleARecords = recordDao.getAllRecordsForRole("role_A").first()
        assertTrue(roleARecords.isEmpty())

        // Role B must be completely intact
        val fetchedRoleB = roleDao.getRoleById("role_B")
        assertNotNull(fetchedRoleB)
        assertEquals("Role B", fetchedRoleB?.displayName)
        val roleBRecords = recordDao.getAllRecordsForRole("role_B").first()
        assertEquals(1, roleBRecords.size)
        assertEquals("Bug B", roleBRecords.first().title)
    }

    @Test
    fun testDiaryEntriesIsolationAndOrdering() = runBlocking {
        val roleId = "role_photographer_1"
        val entry1 = DiaryEntryEntity(
            id = "entry_1",
            roleId = roleId,
            title = "Morning Shoot Reflections",
            content = "Lighting at 6:30 AM was exceptional for silhouette portraits.",
            entryType = "Notes",
            activityDate = 1000L,
            createdAt = 1000L
        )
        val entry2 = DiaryEntryEntity(
            id = "entry_2",
            roleId = roleId,
            title = "Lens Calibration Completed",
            content = "Calibrated 85mm prime with focus target.",
            entryType = "Log",
            activityDate = 2000L,
            createdAt = 2000L
        )

        entryDao.insertEntry(entry1)
        entryDao.insertEntry(entry2)

        val entries = entryDao.getEntriesForRole(roleId).first()
        assertEquals(2, entries.size)
        // Ordered by activityDate DESC
        assertEquals("entry_2", entries[0].id)
        assertEquals("entry_1", entries[1].id)
    }

    @Test
    fun testAuditEventLogging() = runBlocking {
        val roleId = "role_audit_test"
        repository.logAuditEvent(roleId, "ROLE_CREATED", "Test workspace created")
        repository.logAuditEvent(roleId, "RECORD_ADDED", "Added script breakdown")

        val events = auditDao.getAuditEventsForRole(roleId).first()
        assertEquals(2, events.size)
        assertTrue(events.any { it.eventType == "ROLE_CREATED" })
        assertTrue(events.any { it.eventType == "RECORD_ADDED" })
    }

    @Test
    fun testClearAllLocalData() = runBlocking {
        val role = RoleEntity(id = "r1", templateId = "dev", displayName = "Dev", specialisation = "", workType = "", colorHex = "", iconName = "", templateVersion = "", category = "", aiEnabled = false, isPrivate = false)
        roleDao.insertRole(role)
        recordDao.insertRecord(ProfessionRecordEntity(id = "rec1", roleId = "r1", professionType = "DEV", recordCategory = "PR", title = "PR #1", subtitle = "", stage = "", detailsJson = "{}"))
        entryDao.insertEntry(DiaryEntryEntity(id = "e1", roleId = "r1", title = "Log 1", content = "Test", entryType = "Log", activityDate = 100L))
        userDao.insertUser(UserEntity(userId = "u1", email = "test@example.com", displayName = "Tester"))
        sessionDao.insertSession(SessionEntity(sessionId = "s1", userId = "u1"))

        repository.clearAllLocalData()

        assertTrue(roleDao.getAllActiveRolesList().isEmpty())
        assertTrue(recordDao.getAllRecordsList().isEmpty())
        assertTrue(entryDao.getAllEntriesList().isEmpty())
        assertNull(userDao.getUserById("u1"))
        assertNull(sessionDao.getSessionById("s1"))
    }

    @Test
    fun testUserPersistenceAndRetrieval() = runBlocking {
        val user = UserEntity(
            userId = "user_offline_001",
            email = "filmmaker@studio.org",
            displayName = "Maya Raman",
            photoUrl = "https://example.com/avatar.jpg",
            isEmailVerified = true,
            lastLoginAt = 1700000000000L
        )

        userDao.insertUser(user)

        val fetched = userDao.getUserById("user_offline_001")
        assertNotNull(fetched)
        assertEquals("Maya Raman", fetched?.displayName)
        assertEquals("filmmaker@studio.org", fetched?.email)
        assertTrue(fetched?.isEmailVerified == true)

        // Update user
        val updatedUser = user.copy(displayName = "Maya Raman (Director)")
        userDao.updateUser(updatedUser)

        val refetched = userDao.getUserById("user_offline_001")
        assertEquals("Maya Raman (Director)", refetched?.displayName)
    }

    @Test
    fun testSessionStateAndOfflineLoginCapabilities() = runBlocking {
        val userId = "user_offline_777"
        val session1 = SessionEntity(
            sessionId = "sess_001",
            userId = userId,
            deviceId = "pixel_8_pro",
            deviceName = "Google Pixel 8 Pro",
            loginProvider = "google",
            isActive = true,
            createdAt = 1000L,
            lastActiveAt = 1000L
        )

        repository.saveSession(session1)

        val activeSession = repository.getActiveSession()
        assertNotNull(activeSession)
        assertEquals("sess_001", activeSession?.sessionId)
        assertEquals(userId, activeSession?.userId)
        assertTrue(activeSession?.isActive == true)

        // Simulate heartbeat
        sessionDao.updateSessionHeartbeat("sess_001", 5000L)
        val sessionWithHeartbeat = sessionDao.getSessionById("sess_001")
        assertEquals(5000L, sessionWithHeartbeat?.lastActiveAt)

        // New session replaces active status of previous sessions
        val session2 = SessionEntity(
            sessionId = "sess_002",
            userId = userId,
            loginProvider = "email",
            isActive = true,
            createdAt = 6000L,
            lastActiveAt = 6000L
        )
        repository.saveSession(session2)

        val currentActive = repository.getActiveSession()
        assertEquals("sess_002", currentActive?.sessionId)

        val oldSession = sessionDao.getSessionById("sess_001")
        assertFalse(oldSession?.isActive == true)

        // Deactivate on logout
        repository.deactivateAllSessionsForUser(userId)
        assertNull(repository.getActiveSession())
    }
}
