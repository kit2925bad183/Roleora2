package com.example.roleora.data.model

/**
 * Profession Form Type discriminator.
 */
enum class ProfessionFormType {
    SCREENPLAY_META,
    STUDENT_ATTENDANCE,
    FARM_INVENTORY,
    DEV_PROJECT,
    PHOTO_SHOOT
}

/**
 * Common Base Interface for all Profession Form Payloads.
 */
interface ProfessionFormPayload {
    val formType: ProfessionFormType
    val formVersion: String
    val lastEditedTimestamp: Long
    fun toDetailsJsonMap(): Map<String, Any?>
}

// -----------------------------------------------------------------------------
// 1. Movie Director / Filmmaker: Screenplay & Production Meta
// -----------------------------------------------------------------------------

data class ScreenplayMetaForm(
    val title: String = "",
    val logline: String = "",
    val genre: String = "Drama",
    val draftVersion: String = "Draft 1.0",
    val pageCount: Int = 1,
    val sceneCount: Int = 1,
    val targetRuntimeMinutes: Int = 90,
    val shootingLocations: List<String> = emptyList(),
    val primaryProtagonist: String = "",
    val antagonist: String = "",
    val loglinePitch: String = "",
    val estimatedBudgetUsd: Double = 50000.0,
    val productionStage: String = "Scriptwriting", // Scriptwriting, Pre-Production, Shooting, Post-Production, Distribution
    val pacingNotes: String = "",
    val copyrightRegistrationNumber: String = "",
    val isWgaRegistered: Boolean = false,
    override val formVersion: String = "1.0.0",
    override val lastEditedTimestamp: Long = System.currentTimeMillis()
) : ProfessionFormPayload {
    override val formType: ProfessionFormType = ProfessionFormType.SCREENPLAY_META

    override fun toDetailsJsonMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "logline" to logline,
        "genre" to genre,
        "draftVersion" to draftVersion,
        "pageCount" to pageCount,
        "sceneCount" to sceneCount,
        "targetRuntimeMinutes" to targetRuntimeMinutes,
        "shootingLocations" to shootingLocations,
        "primaryProtagonist" to primaryProtagonist,
        "antagonist" to antagonist,
        "loglinePitch" to loglinePitch,
        "estimatedBudgetUsd" to estimatedBudgetUsd,
        "productionStage" to productionStage,
        "pacingNotes" to pacingNotes,
        "copyrightRegistrationNumber" to copyrightRegistrationNumber,
        "isWgaRegistered" to isWgaRegistered,
        "formVersion" to formVersion,
        "lastEditedTimestamp" to lastEditedTimestamp
    )
}

// -----------------------------------------------------------------------------
// 2. College Student / Academic: Attendance & Subject Meta
// -----------------------------------------------------------------------------

data class StudentAttendanceForm(
    val subjectCode: String = "",
    val subjectName: String = "",
    val professorName: String = "",
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val minimumRequiredAttendancePercent: Double = 75.0,
    val semester: Int = 1,
    val creditHours: Int = 3,
    val classroomOrHall: String = "",
    val gradingScale: String = "Letter (A-F)",
    val currentGradeOrScore: Double = 0.0,
    val nextExamDate: Long? = null,
    val pendingAssignmentsCount: Int = 0,
    val academicNotes: String = "",
    override val formVersion: String = "1.0.0",
    override val lastEditedTimestamp: Long = System.currentTimeMillis()
) : ProfessionFormPayload {
    override val formType: ProfessionFormType = ProfessionFormType.STUDENT_ATTENDANCE

    val currentAttendancePercentage: Double
        get() = if (totalClasses > 0) (attendedClasses.toDouble() / totalClasses) * 100.0 else 100.0

    val isBelowThreshold: Boolean
        get() = currentAttendancePercentage < minimumRequiredAttendancePercent

    override fun toDetailsJsonMap(): Map<String, Any?> = mapOf(
        "subjectCode" to subjectCode,
        "subjectName" to subjectName,
        "professorName" to professorName,
        "attendedClasses" to attendedClasses,
        "totalClasses" to totalClasses,
        "minimumRequiredAttendancePercent" to minimumRequiredAttendancePercent,
        "semester" to semester,
        "creditHours" to creditHours,
        "classroomOrHall" to classroomOrHall,
        "gradingScale" to gradingScale,
        "currentGradeOrScore" to currentGradeOrScore,
        "nextExamDate" to nextExamDate,
        "pendingAssignmentsCount" to pendingAssignmentsCount,
        "academicNotes" to academicNotes,
        "currentAttendancePercentage" to currentAttendancePercentage,
        "isBelowThreshold" to isBelowThreshold,
        "formVersion" to formVersion,
        "lastEditedTimestamp" to lastEditedTimestamp
    )
}

// -----------------------------------------------------------------------------
// 3. Farmer / Agricultural Manager: Crop & Inventory Meta
// -----------------------------------------------------------------------------

data class FarmInventoryForm(
    val itemName: String = "",
    val itemCategory: String = "CROP_HARVEST", // SEED, FERTILIZER, PESTICIDE, CROP_HARVEST, EQUIPMENT_PARTS, LIVESTOCK_FEED
    val fieldOrParcelId: String = "",
    val quantityOnHand: Double = 0.0,
    val measurementUnit: String = "kg", // kg, tonnes, liters, bags, bushels, acres
    val reorderThreshold: Double = 10.0,
    val costPerUnitUsd: Double = 0.0,
    val supplierOrSource: String = "",
    val storageLocation: String = "Silo A",
    val expiryOrHarvestDate: Long? = null,
    val batchOrLotNumber: String = "",
    val soilConditionOrPh: Double = 6.5,
    val irrigationFrequency: String = "Weekly", // Daily, Alternate Days, Weekly, Bi-Weekly
    val organicCertified: Boolean = true,
    val storageTemperatureCelsius: Double = 20.0,
    val notesAndAlerts: String = "",
    override val formVersion: String = "1.0.0",
    override val lastEditedTimestamp: Long = System.currentTimeMillis()
) : ProfessionFormPayload {
    override val formType: ProfessionFormType = ProfessionFormType.FARM_INVENTORY

    val isLowStock: Boolean
        get() = quantityOnHand <= reorderThreshold

    val totalValueUsd: Double
        get() = quantityOnHand * costPerUnitUsd

    override fun toDetailsJsonMap(): Map<String, Any?> = mapOf(
        "itemName" to itemName,
        "itemCategory" to itemCategory,
        "fieldOrParcelId" to fieldOrParcelId,
        "quantityOnHand" to quantityOnHand,
        "measurementUnit" to measurementUnit,
        "reorderThreshold" to reorderThreshold,
        "costPerUnitUsd" to costPerUnitUsd,
        "supplierOrSource" to supplierOrSource,
        "storageLocation" to storageLocation,
        "expiryOrHarvestDate" to expiryOrHarvestDate,
        "batchOrLotNumber" to batchOrLotNumber,
        "soilConditionOrPh" to soilConditionOrPh,
        "irrigationFrequency" to irrigationFrequency,
        "organicCertified" to organicCertified,
        "storageTemperatureCelsius" to storageTemperatureCelsius,
        "notesAndAlerts" to notesAndAlerts,
        "isLowStock" to isLowStock,
        "totalValueUsd" to totalValueUsd,
        "formVersion" to formVersion,
        "lastEditedTimestamp" to lastEditedTimestamp
    )
}

// -----------------------------------------------------------------------------
// 4. Software Developer: Project & Sprint Task Meta
// -----------------------------------------------------------------------------

data class DevProjectForm(
    val projectName: String = "",
    val repositoryUrl: String = "",
    val branchName: String = "main",
    val pullRequestTitle: String = "",
    val pullRequestNumber: Int = 0,
    val storyPoints: Int = 3,
    val priority: String = "Medium", // Low, Medium, High, Critical
    val techStack: List<String> = listOf("Kotlin", "Jetpack Compose"),
    val ciStatus: String = "Passing", // Passing, Failing, In-Progress, Skipped
    val targetReleaseSprint: String = "Sprint 24",
    val architecturalNotes: String = "",
    override val formVersion: String = "1.0.0",
    override val lastEditedTimestamp: Long = System.currentTimeMillis()
) : ProfessionFormPayload {
    override val formType: ProfessionFormType = ProfessionFormType.DEV_PROJECT

    override fun toDetailsJsonMap(): Map<String, Any?> = mapOf(
        "projectName" to projectName,
        "repositoryUrl" to repositoryUrl,
        "branchName" to branchName,
        "pullRequestTitle" to pullRequestTitle,
        "pullRequestNumber" to pullRequestNumber,
        "storyPoints" to storyPoints,
        "priority" to priority,
        "techStack" to techStack,
        "ciStatus" to ciStatus,
        "targetReleaseSprint" to targetReleaseSprint,
        "architecturalNotes" to architecturalNotes,
        "formVersion" to formVersion,
        "lastEditedTimestamp" to lastEditedTimestamp
    )
}

// -----------------------------------------------------------------------------
// 5. Photographer: Shoot & Client Booking Meta
// -----------------------------------------------------------------------------

data class PhotoShootForm(
    val clientName: String = "",
    val shootType: String = "Portrait", // Portrait, Wedding, Commercial, Wildlife, Landscape
    val shootDate: Long = System.currentTimeMillis(),
    val location: String = "",
    val packageTier: String = "Standard",
    val primaryCameraBody: String = "Sony A7 IV",
    val primaryLens: String = "24-70mm f/2.8 GM II",
    val shotCountEstimate: Int = 500,
    val agreedDeliveryDeadline: Long = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000),
    val totalFeeUsd: Double = 1200.0,
    val depositReceived: Boolean = true,
    val rawStorageDriveId: String = "SSD-01",
    val clientSpecialRequests: String = "",
    override val formVersion: String = "1.0.0",
    override val lastEditedTimestamp: Long = System.currentTimeMillis()
) : ProfessionFormPayload {
    override val formType: ProfessionFormType = ProfessionFormType.PHOTO_SHOOT

    override fun toDetailsJsonMap(): Map<String, Any?> = mapOf(
        "clientName" to clientName,
        "shootType" to shootType,
        "shootDate" to shootDate,
        "location" to location,
        "packageTier" to packageTier,
        "primaryCameraBody" to primaryCameraBody,
        "primaryLens" to primaryLens,
        "shotCountEstimate" to shotCountEstimate,
        "agreedDeliveryDeadline" to agreedDeliveryDeadline,
        "totalFeeUsd" to totalFeeUsd,
        "depositReceived" to depositReceived,
        "rawStorageDriveId" to rawStorageDriveId,
        "clientSpecialRequests" to clientSpecialRequests,
        "formVersion" to formVersion,
        "lastEditedTimestamp" to lastEditedTimestamp
    )
}
