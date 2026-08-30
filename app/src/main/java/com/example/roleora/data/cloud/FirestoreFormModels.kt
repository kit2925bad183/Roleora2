package com.example.roleora.data.cloud

import com.example.roleora.data.model.DevProjectForm
import com.example.roleora.data.model.FarmInventoryForm
import com.example.roleora.data.model.PhotoShootForm
import com.example.roleora.data.model.ProfessionFormPayload
import com.example.roleora.data.model.ProfessionFormType
import com.example.roleora.data.model.ScreenplayMetaForm
import com.example.roleora.data.model.StudentAttendanceForm
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Firestore Document Model wrapper for profession-specific forms and metadata.
 */
data class FirestoreProfessionRecordDocument(
    val id: String = "",
    val roleId: String = "",
    val formType: String = "",
    val formVersion: String = "1.0.0",
    val title: String = "",
    val subtitle: String = "",
    val details: Map<String, Any?> = emptyMap(),
    val isDraft: Boolean = false,
    val draftSessionId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "roleId" to roleId,
        "formType" to formType,
        "formVersion" to formVersion,
        "title" to title,
        "subtitle" to subtitle,
        "details" to details,
        "isDraft" to isDraft,
        "draftSessionId" to draftSessionId,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    companion object {
        fun fromDocument(doc: DocumentSnapshot): FirestoreProfessionRecordDocument? {
            val id = doc.getString("id") ?: doc.id
            val roleId = doc.getString("roleId") ?: return null
            val formType = doc.getString("formType") ?: ""
            val formVersion = doc.getString("formVersion") ?: "1.0.0"
            val title = doc.getString("title") ?: ""
            val subtitle = doc.getString("subtitle") ?: ""
            @Suppress("UNCHECKED_CAST")
            val details = (doc.get("details") as? Map<String, Any?>) ?: emptyMap()
            val isDraft = doc.getBoolean("isDraft") ?: false
            val draftSessionId = doc.getString("draftSessionId")
            val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()

            return FirestoreProfessionRecordDocument(
                id = id,
                roleId = roleId,
                formType = formType,
                formVersion = formVersion,
                title = title,
                subtitle = subtitle,
                details = details,
                isDraft = isDraft,
                draftSessionId = draftSessionId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        fun fromPayload(
            recordId: String,
            roleId: String,
            payload: ProfessionFormPayload,
            isDraft: Boolean = false,
            draftSessionId: String? = null
        ): FirestoreProfessionRecordDocument {
            val (title, subtitle) = when (payload) {
                is ScreenplayMetaForm -> payload.title to "${payload.genre} • ${payload.draftVersion}"
                is StudentAttendanceForm -> payload.subjectCode to "${payload.subjectName} (${payload.currentAttendancePercentage.toInt()}%)"
                is FarmInventoryForm -> payload.itemName to "${payload.quantityOnHand} ${payload.measurementUnit} in ${payload.storageLocation}"
                is DevProjectForm -> payload.projectName to "${payload.branchName} • PR #${payload.pullRequestNumber}"
                is PhotoShootForm -> payload.clientName to "${payload.shootType} Shoot @ ${payload.location}"
                else -> "Record" to ""
            }

            return FirestoreProfessionRecordDocument(
                id = recordId,
                roleId = roleId,
                formType = payload.formType.name,
                formVersion = payload.formVersion,
                title = title,
                subtitle = subtitle,
                details = payload.toDetailsJsonMap(),
                isDraft = isDraft,
                draftSessionId = draftSessionId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}

/**
 * Extension helpers to convert Firestore Document maps to concrete Kotlin Form models.
 */
object FirestoreFormModelMapper {

    fun parseScreenplayMeta(details: Map<String, Any?>): ScreenplayMetaForm {
        @Suppress("UNCHECKED_CAST")
        val locations = (details["shootingLocations"] as? List<String>) ?: emptyList()

        return ScreenplayMetaForm(
            title = details["title"] as? String ?: "",
            logline = details["logline"] as? String ?: "",
            genre = details["genre"] as? String ?: "Drama",
            draftVersion = details["draftVersion"] as? String ?: "Draft 1.0",
            pageCount = (details["pageCount"] as? Number)?.toInt() ?: 1,
            sceneCount = (details["sceneCount"] as? Number)?.toInt() ?: 1,
            targetRuntimeMinutes = (details["targetRuntimeMinutes"] as? Number)?.toInt() ?: 90,
            shootingLocations = locations,
            primaryProtagonist = details["primaryProtagonist"] as? String ?: "",
            antagonist = details["antagonist"] as? String ?: "",
            loglinePitch = details["loglinePitch"] as? String ?: "",
            estimatedBudgetUsd = (details["estimatedBudgetUsd"] as? Number)?.toDouble() ?: 50000.0,
            productionStage = details["productionStage"] as? String ?: "Scriptwriting",
            pacingNotes = details["pacingNotes"] as? String ?: "",
            copyrightRegistrationNumber = details["copyrightRegistrationNumber"] as? String ?: "",
            isWgaRegistered = details["isWgaRegistered"] as? Boolean ?: false,
            formVersion = details["formVersion"] as? String ?: "1.0.0",
            lastEditedTimestamp = (details["lastEditedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }

    fun parseStudentAttendance(details: Map<String, Any?>): StudentAttendanceForm {
        return StudentAttendanceForm(
            subjectCode = details["subjectCode"] as? String ?: "",
            subjectName = details["subjectName"] as? String ?: "",
            professorName = details["professorName"] as? String ?: "",
            attendedClasses = (details["attendedClasses"] as? Number)?.toInt() ?: 0,
            totalClasses = (details["totalClasses"] as? Number)?.toInt() ?: 0,
            minimumRequiredAttendancePercent = (details["minimumRequiredAttendancePercent"] as? Number)?.toDouble() ?: 75.0,
            semester = (details["semester"] as? Number)?.toInt() ?: 1,
            creditHours = (details["creditHours"] as? Number)?.toInt() ?: 3,
            classroomOrHall = details["classroomOrHall"] as? String ?: "",
            gradingScale = details["gradingScale"] as? String ?: "Letter (A-F)",
            currentGradeOrScore = (details["currentGradeOrScore"] as? Number)?.toDouble() ?: 0.0,
            nextExamDate = (details["nextExamDate"] as? Number)?.toLong(),
            pendingAssignmentsCount = (details["pendingAssignmentsCount"] as? Number)?.toInt() ?: 0,
            academicNotes = details["academicNotes"] as? String ?: "",
            formVersion = details["formVersion"] as? String ?: "1.0.0",
            lastEditedTimestamp = (details["lastEditedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }

    fun parseFarmInventory(details: Map<String, Any?>): FarmInventoryForm {
        return FarmInventoryForm(
            itemName = details["itemName"] as? String ?: "",
            itemCategory = details["itemCategory"] as? String ?: "CROP_HARVEST",
            fieldOrParcelId = details["fieldOrParcelId"] as? String ?: "",
            quantityOnHand = (details["quantityOnHand"] as? Number)?.toDouble() ?: 0.0,
            measurementUnit = details["measurementUnit"] as? String ?: "kg",
            reorderThreshold = (details["reorderThreshold"] as? Number)?.toDouble() ?: 10.0,
            costPerUnitUsd = (details["costPerUnitUsd"] as? Number)?.toDouble() ?: 0.0,
            supplierOrSource = details["supplierOrSource"] as? String ?: "",
            storageLocation = details["storageLocation"] as? String ?: "Silo A",
            expiryOrHarvestDate = (details["expiryOrHarvestDate"] as? Number)?.toLong(),
            batchOrLotNumber = details["batchOrLotNumber"] as? String ?: "",
            soilConditionOrPh = (details["soilConditionOrPh"] as? Number)?.toDouble() ?: 6.5,
            irrigationFrequency = details["irrigationFrequency"] as? String ?: "Weekly",
            organicCertified = details["organicCertified"] as? Boolean ?: true,
            storageTemperatureCelsius = (details["storageTemperatureCelsius"] as? Number)?.toDouble() ?: 20.0,
            notesAndAlerts = details["notesAndAlerts"] as? String ?: "",
            formVersion = details["formVersion"] as? String ?: "1.0.0",
            lastEditedTimestamp = (details["lastEditedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }

    fun parseDevProject(details: Map<String, Any?>): DevProjectForm {
        @Suppress("UNCHECKED_CAST")
        val stack = (details["techStack"] as? List<String>) ?: listOf("Kotlin", "Jetpack Compose")

        return DevProjectForm(
            projectName = details["projectName"] as? String ?: "",
            repositoryUrl = details["repositoryUrl"] as? String ?: "",
            branchName = details["branchName"] as? String ?: "main",
            pullRequestTitle = details["pullRequestTitle"] as? String ?: "",
            pullRequestNumber = (details["pullRequestNumber"] as? Number)?.toInt() ?: 0,
            storyPoints = (details["storyPoints"] as? Number)?.toInt() ?: 3,
            priority = details["priority"] as? String ?: "Medium",
            techStack = stack,
            ciStatus = details["ciStatus"] as? String ?: "Passing",
            targetReleaseSprint = details["targetReleaseSprint"] as? String ?: "Sprint 24",
            architecturalNotes = details["architecturalNotes"] as? String ?: "",
            formVersion = details["formVersion"] as? String ?: "1.0.0",
            lastEditedTimestamp = (details["lastEditedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }

    fun parsePhotoShoot(details: Map<String, Any?>): PhotoShootForm {
        return PhotoShootForm(
            clientName = details["clientName"] as? String ?: "",
            shootType = details["shootType"] as? String ?: "Portrait",
            shootDate = (details["shootDate"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            location = details["location"] as? String ?: "",
            packageTier = details["packageTier"] as? String ?: "Standard",
            primaryCameraBody = details["primaryCameraBody"] as? String ?: "Sony A7 IV",
            primaryLens = details["primaryLens"] as? String ?: "24-70mm f/2.8 GM II",
            shotCountEstimate = (details["shotCountEstimate"] as? Number)?.toInt() ?: 500,
            agreedDeliveryDeadline = (details["agreedDeliveryDeadline"] as? Number)?.toLong() ?: (System.currentTimeMillis() + 604800000L),
            totalFeeUsd = (details["totalFeeUsd"] as? Number)?.toDouble() ?: 1200.0,
            depositReceived = details["depositReceived"] as? Boolean ?: true,
            rawStorageDriveId = details["rawStorageDriveId"] as? String ?: "SSD-01",
            clientSpecialRequests = details["clientSpecialRequests"] as? String ?: "",
            formVersion = details["formVersion"] as? String ?: "1.0.0",
            lastEditedTimestamp = (details["lastEditedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }
}
