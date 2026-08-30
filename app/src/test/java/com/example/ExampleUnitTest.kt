package com.example

import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.data.model.RoleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testProfessionRecordModelCreation() {
        val record = ProfessionRecordEntity(
            id = UUID.randomUUID().toString(),
            roleId = "role_director_1",
            professionType = "MOVIE_DIRECTOR",
            recordCategory = "SCREENPLAY",
            title = "EXT. TEMPLE COURTYARD - DAWN",
            subtitle = "Maran uncovers the manuscript",
            stage = "Ready",
            status = "Active",
            numericValue1 = 1.0,
            numericValue2 = 4.0,
            detailsJson = "{\"fieldA\":\"Maran, Monk\",\"notes\":\"Natural lighting\"}",
            tags = "Temple, Dawn, Climax",
            updatedAt = System.currentTimeMillis()
        )

        assertNotNull(record.id)
        assertEquals("SCREENPLAY", record.recordCategory)
        assertEquals(1.0, record.numericValue1, 0.001)
        assertEquals(4.0, record.numericValue2, 0.001)
        assertTrue(record.detailsJson.contains("Maran"))
    }

    @Test
    fun testRoleEntityCreation() {
        val role = RoleEntity(
            id = "role_student_1",
            templateId = "college_student",
            displayName = "Akash Raman",
            specialisation = "Computer Science & AI",
            workType = "Undergraduate",
            colorHex = "#2E5C8A",
            iconName = "school",
            templateVersion = "1.0.0",
            category = "Education & Academics",
            aiEnabled = false,
            isPrivate = true
        )

        assertEquals("college_student", role.templateId)
        assertEquals("Akash Raman", role.displayName)
        assertEquals("Education & Academics", role.category)
        assertTrue(role.isPrivate)
    }

    @Test
    fun testRoleSwitcherPrivacyAndAiStatus() {
        val devRole = RoleEntity(
            id = "role_dev_1",
            templateId = "software_developer",
            displayName = "Dev Workspace",
            specialisation = "Mobile Architect",
            workType = "Full-Time",
            colorHex = "#2E5C8A",
            iconName = "code",
            templateVersion = "1.0.0",
            category = "Engineering & Technology",
            aiEnabled = true,
            isPrivate = false
        )

        assertTrue(devRole.aiEnabled)
        assertEquals(false, devRole.isPrivate)
        assertEquals("Engineering & Technology", devRole.category)
        assertEquals("Mobile Architect", devRole.specialisation)
        assertEquals("code", devRole.iconName)
    }

    @Test
    fun testStaleUiStateClearingTrigger() {
        var staleFormDraft: String? = "Unsaved director scene note"
        var editingRecordId: String? = "rec_123"
        var showModal: Boolean = true

        val clearStaleUiState: () -> Unit = {
            staleFormDraft = null
            editingRecordId = null
            showModal = false
        }

        // Trigger role switch callback
        clearStaleUiState()

        assertEquals(null, staleFormDraft)
        assertEquals(null, editingRecordId)
        assertEquals(false, showModal)
    }

    @Test
    fun testRoleDataIsolation() {
        val directorRoleId = "role_dir_101"
        val studentRoleId = "role_stud_202"

        val records = listOf(
            ProfessionRecordEntity(
                id = "rec_1",
                roleId = directorRoleId,
                professionType = "MOVIE_DIRECTOR",
                recordCategory = "SCREENPLAY",
                title = "Scene 1",
                subtitle = "Opening shot",
                stage = "Draft",
                status = "Active",
                numericValue1 = 1.0,
                numericValue2 = 0.0,
                detailsJson = "{}",
                tags = "Intro",
                updatedAt = 1000L
            ),
            ProfessionRecordEntity(
                id = "rec_2",
                roleId = studentRoleId,
                professionType = "COLLEGE_STUDENT",
                recordCategory = "ASSIGNMENTS",
                title = "Machine Learning Assignment",
                subtitle = "Due Monday",
                stage = "Pending",
                status = "In Progress",
                numericValue1 = 85.0,
                numericValue2 = 100.0,
                detailsJson = "{}",
                tags = "Homework",
                updatedAt = 2000L
            )
        )

        val directorRecords = records.filter { it.roleId == directorRoleId }
        val studentRecords = records.filter { it.roleId == studentRoleId }

        assertEquals(1, directorRecords.size)
        assertEquals("Scene 1", directorRecords.first().title)
        assertEquals(1, studentRecords.size)
        assertEquals("Machine Learning Assignment", studentRecords.first().title)
        assertTrue(directorRecords.none { it.roleId == studentRoleId })
    }
}

