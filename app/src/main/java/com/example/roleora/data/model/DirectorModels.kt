package com.example.roleora.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// ============================================================================
// 1. ENUMS FOR DIRECTOR WORKSPACE
// ============================================================================

enum class DirectorSpecialisation(val displayName: String) {
    SHORT_FILM("Short film"),
    FEATURE_FILM("Feature film"),
    DOCUMENTARY("Documentary"),
    ADVERTISEMENT("Advertisement"),
    MUSIC_VIDEO("Music video"),
    WEB_SERIES("Web series"),
    YOUTUBE_PRODUCTION("YouTube production"),
    ANIMATION("Animation"),
    CUSTOM("Custom");

    companion object {
        fun fromString(value: String): DirectorSpecialisation =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) } ?: SHORT_FILM
    }
}

enum class ProductionStage(val displayName: String, val order: Int) {
    IDEA("Idea", 1),
    STORY_DEVELOPMENT("Story Development", 2),
    SCREENPLAY("Screenplay", 3),
    PRE_PRODUCTION("Pre-production", 4),
    REHEARSAL("Rehearsal", 5),
    PRODUCTION("Production", 6),
    POST_PRODUCTION("Post-production", 7),
    REVIEW("Review", 8),
    RELEASE("Release", 9),
    ARCHIVE("Archive", 10);

    companion object {
        fun fromString(value: String): ProductionStage =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) } ?: IDEA
    }
}

enum class ScreenplayElementType(val label: String, val shortcut: String) {
    SCENE_HEADING("Scene Heading", "Ctrl+1"),
    ACTION("Action", "Ctrl+2"),
    CHARACTER("Character", "Ctrl+3"),
    DIALOGUE("Dialogue", "Ctrl+4"),
    PARENTHETICAL("Parenthetical", "Ctrl+5"),
    TRANSITION("Transition", "Ctrl+6"),
    SHOT("Shot", "Ctrl+7"),
    GENERAL_TEXT("General Text", "Ctrl+8"),
    ACT_MARKER("Act Marker", "Ctrl+9"),
    PAGE_BREAK("Page Break", "Ctrl+0")
}

enum class SceneStatus(val label: String, val colorHex: Long) {
    DRAFT("Draft", 0xFF94A3B8),
    REVIEWED("Reviewed", 0xFF38BDF8),
    APPROVED("Approved", 0xFF818CF8),
    READY_FOR_REHEARSAL("Ready for Rehearsal", 0xFFFBBF24),
    READY_FOR_SHOOTING("Ready for Shooting", 0xFFF59E0B),
    PARTIALLY_SHOT("Partially Shot", 0xFFFB923C),
    COMPLETED("Completed", 0xFF10B981),
    NEEDS_RETAKE("Needs Retake", 0xFFEF4444),
    LOCKED("Locked", 0xFF6366F1);

    companion object {
        fun fromString(value: String): SceneStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: DRAFT
    }
}

enum class ShotStatus(val label: String, val colorHex: Long) {
    PLANNED("Planned", 0xFF94A3B8),
    READY("Ready", 0xFF38BDF8),
    IN_PROGRESS("In Progress", 0xFFF59E0B),
    COMPLETED("Completed", 0xFF10B981),
    NEEDS_RETAKE("Needs Retake", 0xFFEF4444),
    CANCELLED("Cancelled", 0xFF64748B);

    companion object {
        fun fromString(value: String): ShotStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: PLANNED
    }
}

enum class ShotSize(val label: String, val code: String) {
    EXTREME_WIDE("Extreme Wide Shot", "EWS"),
    WIDE("Wide Shot", "WS"),
    MEDIUM_WIDE("Medium Wide Shot", "MWS"),
    MEDIUM("Medium Shot", "MS"),
    MEDIUM_CLOSE_UP("Medium Close Up", "MCU"),
    CLOSE_UP("Close Up", "CU"),
    EXTREME_CLOSE_UP("Extreme Close Up", "ECU"),
    OVER_THE_SHOULDER("Over the Shoulder", "OTS"),
    POV("Point of View", "POV"),
    INSERT("Insert / Detail", "INS"),
    DRONE("Aerial / Drone", "AER")
}

enum class CameraAngle(val label: String) {
    EYE_LEVEL("Eye Level"),
    HIGH_ANGLE("High Angle"),
    LOW_ANGLE("Low Angle"),
    DUTCH_ANGLE("Dutch Angle / Tilt"),
    BIRDS_EYE("Bird's Eye"),
    WORMS_EYE("Worm's Eye")
}

enum class CameraMovement(val label: String) {
    STATIC("Static (Tripod)"),
    PAN("Pan"),
    TILT("Tilt"),
    TRACKING("Tracking / Dolly"),
    CRANE("Crane / Jib"),
    HANDHELD("Handheld"),
    STEADICAM("Steadicam / Gimbal"),
    ZOOM("Zoom")
}

enum class BreakdownCategory(val label: String, val iconName: String) {
    CAST("Cast", "Person"),
    EXTRAS("Extras / Background", "Groups"),
    PROPS("Props", "Backpack"),
    SET_DRESSING("Set Dressing", "Chair"),
    COSTUME("Costume", "Checkroom"),
    MAKEUP("Makeup", "Face"),
    HAIR("Hair", "ContentCut"),
    VEHICLES("Vehicles", "DirectionsCar"),
    ANIMALS("Animals", "Pets"),
    STUNTS("Stunts", "SportsMartialArts"),
    SFX("Special Effects (SFX)", "LocalFireDepartment"),
    VFX("Visual Effects (VFX)", "AutoAwesome"),
    SOUND("Sound Requirements", "Mic"),
    MUSIC("Music", "MusicNote"),
    LIGHTING("Lighting", "Lightbulb"),
    CAMERA_EQUIPMENT("Camera Equipment", "Videocam"),
    PRODUCTION_EQUIPMENT("Production Equipment", "Construction"),
    SAFETY("Safety Requirements", "HealthAndSafety"),
    PERMISSIONS("Permissions & Permits", "VerifiedUser"),
    TRANSPORT("Transport", "LocalShipping"),
    CATERING("Catering & Food", "Restaurant"),
    OTHER("Other", "Category")
}

enum class AuditionStatus(val label: String, val colorHex: Long) {
    INVITED("Invited", 0xFF94A3B8),
    APPLIED("Applied", 0xFF38BDF8),
    SCHEDULED("Scheduled", 0xFF818CF8),
    AUDITIONED("Auditioned", 0xFFFBBF24),
    SHORTLISTED("Shortlisted", 0xFFF59E0B),
    CALLBACK("Callback", 0xFFA855F7),
    SELECTED("Selected", 0xFF10B981),
    REJECTED("Rejected", 0xFFEF4444);

    companion object {
        fun fromString(value: String): AuditionStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: INVITED
    }
}

enum class BudgetCategory(val label: String) {
    CAST("Cast & Talent"),
    CREW("Crew & Technical"),
    EQUIPMENT("Camera & Lighting Gear"),
    LOCATIONS("Location Fees & Permits"),
    ART("Art Department & Sets"),
    COSTUME("Costume & Wardrobe"),
    MAKEUP("Makeup & Hair"),
    TRANSPORT("Travel & Transport"),
    ACCOMMODATION("Accommodation"),
    FOOD("Food & Catering"),
    POST_PRODUCTION("Post-Production & VFX"),
    MUSIC("Music & Sound Design"),
    MARKETING("Marketing & Promotion"),
    PERMISSIONS("Legal & Insurance"),
    CONTINGENCY("Contingency Reserve"),
    CUSTOM("Custom / Miscellaneous")
}

// ============================================================================
// 2. ROOM ENTITIES FOR MOVIE DIRECTOR
// ============================================================================

/**
 * 1. Production Entity (Core Project)
 */
@Entity(tableName = "director_productions")
data class ProductionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val roleId: String,
    val ownerId: String = "user_default",
    val title: String,
    val workingTitle: String = "",
    val format: String = DirectorSpecialisation.SHORT_FILM.displayName,
    val genre: String = "Drama / Thriller",
    val language: String = "English / Tamil",
    val logline: String = "",
    val synopsis: String = "",
    val theme: String = "",
    val targetDurationMinutes: Int = 15,
    val status: String = "Active", // Active, On Hold, Completed, Archived, Trash
    val currentStage: String = ProductionStage.IDEA.displayName,
    val startDate: Long = System.currentTimeMillis(),
    val plannedReleaseDate: Long = System.currentTimeMillis() + (90L * 24 * 3600 * 1000),
    val coverImageUrl: String? = null,
    val productionCompany: String = "Indie Studio",
    val teamMembersJson: String = "[]",
    val budget: Double = 500000.0,
    val currency: String = "INR",
    val securityLevel: String = SecurityLevel.ROLE_RESTRICTED.name,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 2. Story Idea Entity
 */
@Entity(tableName = "director_ideas")
data class IdeaEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val roleId: String,
    val title: String,
    val premise: String = "",
    val logline: String = "",
    val theme: String = "",
    val genre: String = "Drama",
    val tone: String = "Atmospheric, Grounded",
    val setting: String = "Urban Contemporary",
    val storyWorld: String = "",
    val researchNotes: String = "",
    val beginningBeat: String = "",
    val middleBeat: String = "",
    val endingBeat: String = "",
    val conflict: String = "",
    val characterRelationships: String = "",
    val tags: String = "Concept, Story",
    val voiceAttachmentUri: String? = null,
    val photoAttachmentUri: String? = null,
    val version: Int = 1,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 3. Screenplay Header Entity
 */
@Entity(tableName = "director_screenplays")
data class ScreenplayEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val roleId: String,
    val title: String,
    val author: String = "Director",
    val currentVersion: String = "Draft 1.0",
    val targetPages: Int = 20,
    val logline: String = "",
    val isLocked: Boolean = false,
    val approvalStatus: String = "In Progress", // In Progress, Reviewed, Approved, Locked
    val fountainContent: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 4. Screenplay Version Snapshot Entity (Never Overwrites History)
 */
@Entity(tableName = "director_screenplay_versions")
data class ScreenplayVersionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val screenplayId: String,
    val productionId: String,
    val roleId: String,
    val versionNumber: Int,
    val draftName: String,
    val author: String,
    val changeSummary: String = "Autosaved draft snapshot",
    val isLocked: Boolean = false,
    val approvalStatus: String = "Draft",
    val elementsJson: String, // Full JSON snapshot of screenplay elements
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)

/**
 * 5. Screenplay Element / Block Entity
 */
@Entity(tableName = "director_screenplay_elements")
data class ScreenplayElementEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val screenplayId: String,
    val sceneId: String? = null,
    val elementOrder: Int,
    val elementType: String = ScreenplayElementType.ACTION.name,
    val text: String,
    val characterName: String = "",
    val location: String = "",
    val dayOrNight: String = "DAY",
    val intOrExt: String = "INT",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 6. Character Profile Entity
 */
@Entity(tableName = "director_characters")
data class CharacterEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val roleId: String,
    val name: String,
    val roleType: String = "Protagonist", // Protagonist, Antagonist, Supporting, Guest, Cameo, Extra
    val ageRange: String = "25-35",
    val gender: String = "Any",
    val appearance: String = "",
    val personality: String = "",
    val background: String = "",
    val goal: String = "",
    val motivation: String = "",
    val conflict: String = "",
    val strength: String = "",
    val weakness: String = "",
    val relationshipsJson: String = "[]",
    val characterArc: String = "",
    val dialogueStyle: String = "",
    val costumeNotes: String = "",
    val makeupNotes: String = "",
    val props: String = "",
    val assignedActorName: String = "",
    val actorContact: String = "",
    val auditionNotes: String = "",
    val photoUri: String? = null,
    val voiceRefUri: String? = null,
    val securityLevel: String = SecurityLevel.ROLE_RESTRICTED.name,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 7. Scene Entity
 */
@Entity(tableName = "director_scenes")
data class SceneEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val screenplayId: String? = null,
    val sceneNumber: Int,
    val actOrSequence: String = "Act I",
    val heading: String, // e.g. "INT. COFFEE SHOP - DAY"
    val intExt: String = "INT", // INT, EXT, INT/EXT
    val locationName: String = "Coffee Shop",
    val timeOfDay: String = "DAY", // DAY, NIGHT, DUSK, DAWN, CONTINUOUS
    val description: String = "",
    val characterNames: String = "", // Comma-separated
    val estimatedDurationSeconds: Int = 120, // 2 minutes
    val storyPurpose: String = "Inciting incident revelation",
    val emotionalTone: String = "Tense, mysterious",
    val status: String = SceneStatus.DRAFT.name,
    val shootingStatus: String = "Unscheduled",
    val scriptPageRange: String = "1 - 2",
    val attachmentsJson: String = "[]",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 8. Script Breakdown Item Entity
 */
@Entity(tableName = "director_breakdowns")
data class BreakdownItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val sceneId: String,
    val category: String = BreakdownCategory.PROPS.name,
    val description: String,
    val quantity: Int = 1,
    val responsibleDepartment: String = "Art Department",
    val assignedPerson: String = "",
    val status: String = "Planned", // Planned, Sourced, Ready, On Set, Returned
    val costEstimate: Double = 0.0,
    val notes: String = "",
    val attachmentsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 9. Storyboard Frame Entity
 */
@Entity(tableName = "director_storyboards")
data class StoryboardEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val sceneId: String,
    val shotId: String? = null,
    val frameNumber: Int,
    val imageUri: String? = null,
    val drawingPointsJson: String = "[]", // SVG/Vector stroke paths for canvas drawings
    val caption: String = "",
    val actionDescription: String = "",
    val dialogueReference: String = "",
    val cameraAngle: String = CameraAngle.EYE_LEVEL.label,
    val cameraMovement: String = CameraMovement.STATIC.label,
    val durationSeconds: Int = 5,
    val notes: String = "",
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 10. Shot List Entity
 */
@Entity(tableName = "director_shots")
data class ShotEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val sceneId: String,
    val shotNumber: String, // e.g. "1A", "1B"
    val description: String,
    val shotSize: String = ShotSize.MEDIUM.code,
    val cameraAngle: String = CameraAngle.EYE_LEVEL.label,
    val lens: String = "35mm Prime",
    val cameraMovement: String = CameraMovement.STATIC.label,
    val frameRate: String = "24 fps",
    val aspectRatio: String = "2.39:1 (Anamorphic)",
    val cameraBody: String = "Arri Alexa Mini / FX6",
    val stabilisation: String = "Tripod Fluid Head",
    val lightingNotes: String = "Key light 45 deg, soft fill, warm practicals",
    val actorBlocking: String = "Actor enters frame left, sits at center table",
    val audioRequirement: String = "Boom overhead + 2x Wireless Lavs",
    val estimatedDurationSeconds: Int = 15,
    val storyboardRefId: String? = null,
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, MUST_HAVE
    val status: String = ShotStatus.PLANNED.name,
    val plannedShootingDate: Long? = null,
    val completedTakesCount: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 11. Cast and Crew Member Entity
 */
@Entity(tableName = "director_cast_crew")
data class CastCrewMemberEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val roleId: String,
    val memberType: String = "CAST", // CAST, CREW
    val name: String,
    val characterNameOrDepartment: String = "", // e.g. "David (Lead)" or "Camera Department"
    val positionTitle: String = "Actor", // Lead Actor, DP, 1st AD, Sound Recordist, Gaffer, etc.
    val phone: String = "",
    val email: String = "",
    val agentOrManager: String = "",
    val availabilityStatus: String = "Available", // Available, Tentative, Booked, Conflict
    val auditionNotes: String = "",
    val costumeMeasurements: String = "Chest: 38, Waist: 32, Height: 5'10\"",
    val makeupNotes: String = "Natural look, minor scar prosthetic",
    val travelInfo: String = "Self transport / Hotel Room 402",
    val paymentStatus: String = "Pending Contract", // Pending Contract, Agreed, Advance Paid, Completed
    val paymentAmount: Double = 0.0,
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val isRestrictedAccess: Boolean = false, // If true, only Director/Owner can view private data
    val privateNotes: String = "",
    val attachmentsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 12. Audition Entity
 */
@Entity(tableName = "director_auditions")
data class AuditionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val characterId: String,
    val candidateName: String,
    val contact: String = "",
    val auditionDateTime: Long = System.currentTimeMillis() + (24 * 3600 * 1000),
    val locationOrLink: String = "Studio Room B / Zoom",
    val scriptSides: String = "Scene 4 Dialogue lines 12-30",
    val photosJson: String = "[]",
    val auditionVideoUri: String? = null,
    val directorNotes: String = "Great screen presence, strong emotional range",
    val rating: Int = 4, // 1 to 5
    val status: String = AuditionStatus.SCHEDULED.name,
    val followUpNotes: String = "Invite for Callback with Co-star",
    val consentRecord: String = "Signed ND & Audition Consent",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 13. Location Entity
 */
@Entity(tableName = "director_locations")
data class LocationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val roleId: String,
    val name: String,
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val contactPerson: String = "",
    val contactPhone: String = "",
    val photosJson: String = "[]",
    val videoTourUri: String? = null,
    val suitableScenesJson: String = "[]",
    val availabilityNotes: String = "Available Weekdays 8 AM - 6 PM",
    val dailyCost: Double = 15000.0,
    val permissionStatus: String = "Permit Granted", // Required, Applied, Permit Granted, Not Required, Rejected
    val noiseLevel: String = "Low (Soundproofed interior)",
    val naturalLightQuality: String = "High (North facing large windows)",
    val powerAvailable: String = "3-Phase 32A Generator connection ready",
    val parkingSpaces: String = "10 Vehicles + 1 Generator Van",
    val transportAccess: String = "Main Road, easily accessible for trucks",
    val accommodationNotes: String = "Hotel within 2 km",
    val safetyNotes: String = "Fire extinguishers verified, first aid kit on site",
    val weatherNotes: String = "Sheltered interior, rain safe",
    val visitRecordsJson: String = "[]",
    val attachmentsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 14. Rehearsal Entity
 */
@Entity(tableName = "director_rehearsals")
data class RehearsalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val sceneIdsJson: String = "[]",
    val castMemberIdsJson: String = "[]",
    val directorName: String = "Director",
    val scheduledDateTime: Long = System.currentTimeMillis() + (48 * 3600 * 1000),
    val endDateTime: Long = System.currentTimeMillis() + (51 * 3600 * 1000),
    val locationName: String = "Rehearsal Hall 1",
    val objectives: String = "Dialogue tempo and emotional beats calibration",
    val blockingNotes: String = "Move closer during confession at midpoint",
    val dialogueNotes: String = "Emphasize silence before final response",
    val costumePropRequirements: String = "Practice props (dummy letter, coffee mugs)",
    val attendanceJson: String = "[]",
    val recordingUri: String? = null,
    val outcomes: String = "Cast chemistry established, timing reduced by 15s",
    val followUpTasksJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 15. Shooting Day / Schedule Entity
 */
@Entity(tableName = "director_shooting_days")
data class ShootingDayEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val dayNumber: Int = 1,
    val dateMillis: Long = System.currentTimeMillis() + (72 * 3600 * 1000),
    val generalCallTime: String = "06:30 AM",
    val wrapTime: String = "06:30 PM",
    val primaryLocation: String = "Old Library Heritage Wing",
    val sceneIdsJson: String = "[]",
    val shotIdsJson: String = "[]",
    val castMemberIdsJson: String = "[]",
    val crewMemberIdsJson: String = "[]",
    val equipmentNotes: String = "Full A-Cam package, Ronin 2, HMI 4K & Skypanels",
    val transportNotes: String = "Unit bus leaves hotel at 05:45 AM",
    val mealSchedule: String = "Breakfast: 07:00 AM | Lunch: 01:30 PM | Tea: 05:00 PM",
    val weatherForecastNotes: String = "Partly Cloudy, 28°C, 10% precipitation chance",
    val status: String = "Planned", // Planned, Active Today, Completed, Postponed, Cancelled
    val conflictNotes: String = "None detected",
    val notes: String = "Shoot exterior daylight scenes first before 11:30 AM",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 16. Call Sheet Entity
 */
@Entity(tableName = "director_call_sheets")
data class CallSheetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val shootingDayId: String,
    val dayNumber: Int = 1,
    val shootingDateMillis: Long = System.currentTimeMillis() + (72 * 3600 * 1000),
    val generalCallTime: String = "06:30 AM",
    val locationAddress: String = "Heritage Library, Main Gate, Civil Lines",
    val weatherNote: String = "28°C, Golden Hour 05:45 PM",
    val scenesJson: String = "[]",
    val castCallsJson: String = "[]",
    val crewCallsJson: String = "[]",
    val equipmentJson: String = "[]",
    val transportNotes: String = "Crew Vans departing Basecamp 05:30 AM",
    val parkingInfo: String = "Permit lot at South Gate",
    val mealNotes: String = "Hot Breakfast at 07:00 AM, Caterer: Royal Table",
    val safetyNotes: String = "Keep cables taped, hard hats during rigging",
    val emergencyContactName: String = "Paramedic Team / 1st AD",
    val emergencyContactPhone: String = "+91 98765 43210",
    val specialInstructions: String = "Strict silence during sync sound takes",
    val versionNumber: Int = 1,
    val isApproved: Boolean = true,
    val approvedBy: String = "Director & Producer",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 17. Continuity Entity
 */
@Entity(tableName = "director_continuity")
data class ContinuityEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val sceneId: String,
    val shotId: String,
    val takeNumber: Int = 1,
    val costumeNotes: String = "Blue linen jacket, second button unfastened, watch on left wrist",
    val makeupNotes: String = "Sweat sheen on forehead, light bruise on right cheekbone",
    val hairNotes: String = "Left parted, slight stray lock over forehead",
    val propsNotes: String = "Coffee cup 3/4 full in right hand, pen cap on table",
    val actorPositionNotes: String = "Left foot crossed over right, leaning against door frame",
    val cameraPositionNotes: String = "Height 4'2\", 50mm lens, focus at 8.5 feet",
    val lightingContinuityNotes: String = "Afternoon sunlight through blinds, key ratio 3:1",
    val timeOfDayContinuity: String = "4:30 PM Sunset glow",
    val dialogueNotes: String = "Pause between 'Listen' and 'I didn't mean to'",
    val photosJson: String = "[]",
    val videoRefUri: String? = null,
    val continuityWarnings: String = "Verify jacket button before close-up reverse shot",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 18. Take and Footage Log Entity
 */
@Entity(tableName = "director_takes")
data class TakeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val shootingDayId: String? = null,
    val sceneId: String,
    val shotId: String,
    val takeNumber: Int,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val endTimeMillis: Long = System.currentTimeMillis() + 45000,
    val durationSeconds: Int = 45,
    val fileUriOrReference: String = "A001_C004_0829_001.MOV",
    val cameraLabel: String = "A-Cam",
    val audioTrackLabel: String = "Track 1-4 PolyWAV",
    val directorRating: Int = 5, // 1 to 5 stars
    val technicalRating: Int = 4, // Focus & Exposure
    val performanceRating: Int = 5, // Acting delivery
    val isSelectedBestTake: Boolean = true,
    val problemNotes: String = "Minor boom mic shadow on wall at 00:32",
    val isRetakeRequired: Boolean = false,
    val continuityReferenceId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 19. Editing Review Entity
 */
@Entity(tableName = "director_editing_reviews")
data class EditingReviewEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val projectName: String = "Rough Cut v1",
    val sequenceOrVersion: String = "Sequence 2 (Reunion Scene)",
    val reviewDateMillis: Long = System.currentTimeMillis(),
    val videoReferenceUri: String? = null,
    val timestampCode: String = "00:01:42",
    val sceneNumber: Int = 8,
    val commentCategory: String = "Pacing & Cut", // Pacing, Performance, Continuity, Audio, Color, VFX
    val priority: String = "HIGH", // LOW, MEDIUM, HIGH, BLOCKER
    val assignedEditor: String = "Lead Editor",
    val commentText: String = "Use Take 3 and shorten the reaction by approximately two seconds.",
    val status: String = "Open", // Open, In Progress, Resolved, Verified
    val resolutionNotes: String = "",
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 20. Sound and Music Entity
 */
@Entity(tableName = "director_sound_music")
data class SoundMusicEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val sceneId: String? = null,
    val category: String = "Background Score", // Dialogue, ADR, Foley, SFX, Ambience, Background Score, Song, Music Cue
    val title: String,
    val description: String = "Ominous cello drone building into string crescendo",
    val composerOrDesigner: String = "Music Director",
    val licensingStatus: String = "Original Composition", // Original, Licensed, Royalty Free, Pending Clearance
    val versionNumber: Int = 1,
    val reviewNotes: String = "Lower bass frequencies slightly during dialogue exchange",
    val isApproved: Boolean = true,
    val fileUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)

/**
 * 21. Budget and Expense Entity
 */
@Entity(tableName = "director_budget_items")
data class BudgetItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productionId: String,
    val roleId: String,
    val category: String = BudgetCategory.EQUIPMENT.name,
    val itemTitle: String,
    val plannedAmount: Double,
    val actualExpense: Double = 0.0,
    val vendor: String = "Apex Camera Rentals",
    val expenseDateMillis: Long = System.currentTimeMillis(),
    val receiptAttachmentUri: String? = null,
    val paymentStatus: String = "Paid", // Planned, Invoiced, Paid, Pending Approval, Overdue
    val isApproved: Boolean = true,
    val notes: String = "Includes 3-day rental with insurance",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)
