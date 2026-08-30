package com.example.roleora.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roleora.data.model.AuditionEntity
import com.example.roleora.data.model.AuditionStatus
import com.example.roleora.data.model.BreakdownCategory
import com.example.roleora.data.model.BreakdownItemEntity
import com.example.roleora.data.model.BudgetItemEntity
import com.example.roleora.data.model.CallSheetEntity
import com.example.roleora.data.model.CastCrewMemberEntity
import com.example.roleora.data.model.CharacterEntity
import com.example.roleora.data.model.ContinuityEntity
import com.example.roleora.data.model.DirectorSpecialisation
import com.example.roleora.data.model.EditingReviewEntity
import com.example.roleora.data.model.IdeaEntity
import com.example.roleora.data.model.LocationEntity
import com.example.roleora.data.model.ProductionEntity
import com.example.roleora.data.model.ProductionStage
import com.example.roleora.data.model.RehearsalEntity
import com.example.roleora.data.model.SceneEntity
import com.example.roleora.data.model.SceneStatus
import com.example.roleora.data.model.ScreenplayElementEntity
import com.example.roleora.data.model.ScreenplayElementType
import com.example.roleora.data.model.ScreenplayEntity
import com.example.roleora.data.model.ScreenplayVersionEntity
import com.example.roleora.data.model.ShootingDayEntity
import com.example.roleora.data.model.ShotEntity
import com.example.roleora.data.model.ShotStatus
import com.example.roleora.data.model.SoundMusicEntity
import com.example.roleora.data.model.StoryboardEntity
import com.example.roleora.data.model.TakeEntity
import com.example.roleora.data.repository.RoleoraRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class DirectorViewModel(
    private val repository: RoleoraRepository
) : ViewModel() {

    private val _currentRoleId = MutableStateFlow<String?>(null)
    val currentRoleId: StateFlow<String?> = _currentRoleId.asStateFlow()

    // 1. Productions State
    private val _productions = MutableStateFlow<List<ProductionEntity>>(emptyList())
    val productions: StateFlow<List<ProductionEntity>> = _productions.asStateFlow()

    private val _selectedProductionId = MutableStateFlow<String?>(null)
    val selectedProductionId: StateFlow<String?> = _selectedProductionId.asStateFlow()

    private val _selectedProduction = MutableStateFlow<ProductionEntity?>(null)
    val selectedProduction: StateFlow<ProductionEntity?> = _selectedProduction.asStateFlow()

    // 2. Ideas
    private val _ideas = MutableStateFlow<List<IdeaEntity>>(emptyList())
    val ideas: StateFlow<List<IdeaEntity>> = _ideas.asStateFlow()

    // 3. Screenplays
    private val _screenplays = MutableStateFlow<List<ScreenplayEntity>>(emptyList())
    val screenplays: StateFlow<List<ScreenplayEntity>> = _screenplays.asStateFlow()

    private val _selectedScreenplayId = MutableStateFlow<String?>(null)
    val selectedScreenplayId: StateFlow<String?> = _selectedScreenplayId.asStateFlow()

    private val _screenplayElements = MutableStateFlow<List<ScreenplayElementEntity>>(emptyList())
    val screenplayElements: StateFlow<List<ScreenplayElementEntity>> = _screenplayElements.asStateFlow()

    private val _screenplayVersions = MutableStateFlow<List<ScreenplayVersionEntity>>(emptyList())
    val screenplayVersions: StateFlow<List<ScreenplayVersionEntity>> = _screenplayVersions.asStateFlow()

    // 4. Characters
    private val _characters = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val characters: StateFlow<List<CharacterEntity>> = _characters.asStateFlow()

    // 5. Scenes
    private val _scenes = MutableStateFlow<List<SceneEntity>>(emptyList())
    val scenes: StateFlow<List<SceneEntity>> = _scenes.asStateFlow()

    // 6. Breakdown
    private val _breakdownItems = MutableStateFlow<List<BreakdownItemEntity>>(emptyList())
    val breakdownItems: StateFlow<List<BreakdownItemEntity>> = _breakdownItems.asStateFlow()

    // 7. Storyboards
    private val _storyboards = MutableStateFlow<List<StoryboardEntity>>(emptyList())
    val storyboards: StateFlow<List<StoryboardEntity>> = _storyboards.asStateFlow()

    // 8. Shot List
    private val _shots = MutableStateFlow<List<ShotEntity>>(emptyList())
    val shots: StateFlow<List<ShotEntity>> = _shots.asStateFlow()

    // 9. Cast & Crew
    private val _castCrewMembers = MutableStateFlow<List<CastCrewMemberEntity>>(emptyList())
    val castCrewMembers: StateFlow<List<CastCrewMemberEntity>> = _castCrewMembers.asStateFlow()

    // 10. Auditions
    private val _auditions = MutableStateFlow<List<AuditionEntity>>(emptyList())
    val auditions: StateFlow<List<AuditionEntity>> = _auditions.asStateFlow()

    // 11. Locations
    private val _locations = MutableStateFlow<List<LocationEntity>>(emptyList())
    val locations: StateFlow<List<LocationEntity>> = _locations.asStateFlow()

    // 12. Rehearsals
    private val _rehearsals = MutableStateFlow<List<RehearsalEntity>>(emptyList())
    val rehearsals: StateFlow<List<RehearsalEntity>> = _rehearsals.asStateFlow()

    // 13. Shooting Schedule
    private val _shootingDays = MutableStateFlow<List<ShootingDayEntity>>(emptyList())
    val shootingDays: StateFlow<List<ShootingDayEntity>> = _shootingDays.asStateFlow()

    // 14. Call Sheets
    private val _callSheets = MutableStateFlow<List<CallSheetEntity>>(emptyList())
    val callSheets: StateFlow<List<CallSheetEntity>> = _callSheets.asStateFlow()

    // 15. Continuity
    private val _continuities = MutableStateFlow<List<ContinuityEntity>>(emptyList())
    val continuities: StateFlow<List<ContinuityEntity>> = _continuities.asStateFlow()

    // 16. Takes
    private val _takes = MutableStateFlow<List<TakeEntity>>(emptyList())
    val takes: StateFlow<List<TakeEntity>> = _takes.asStateFlow()

    // 17. Editing Reviews
    private val _editingReviews = MutableStateFlow<List<EditingReviewEntity>>(emptyList())
    val editingReviews: StateFlow<List<EditingReviewEntity>> = _editingReviews.asStateFlow()

    // 18. Sound & Music
    private val _soundItems = MutableStateFlow<List<SoundMusicEntity>>(emptyList())
    val soundItems: StateFlow<List<SoundMusicEntity>> = _soundItems.asStateFlow()

    // 19. Budget
    private val _budgetItems = MutableStateFlow<List<BudgetItemEntity>>(emptyList())
    val budgetItems: StateFlow<List<BudgetItemEntity>> = _budgetItems.asStateFlow()

    // Navigation and sub-view state
    private val _activeDirectorSection = MutableStateFlow(DirectorSection.DASHBOARD)
    val activeDirectorSection: StateFlow<DirectorSection> = _activeDirectorSection.asStateFlow()

    private var productionCollectorsJob: Job? = null
    private var screenplayElementsJob: Job? = null

    fun initializeForRole(roleId: String) {
        if (_currentRoleId.value == roleId && _productions.value.isNotEmpty()) return
        _currentRoleId.value = roleId

        viewModelScope.launch {
            repository.getActiveProductionsForRole(roleId).collectLatest { prods ->
                _productions.value = prods
                if (prods.isEmpty()) {
                    // Pre-seed sample cinematic production if empty
                    seedSampleDirectorProduction(roleId)
                } else {
                    if (_selectedProductionId.value == null || prods.none { it.id == _selectedProductionId.value }) {
                        selectProduction(prods.first().id)
                    }
                }
            }
        }
    }

    fun selectSection(section: DirectorSection) {
        _activeDirectorSection.value = section
    }

    fun selectProduction(productionId: String) {
        _selectedProductionId.value = productionId
        _selectedProduction.value = _productions.value.firstOrNull { it.id == productionId }

        productionCollectorsJob?.cancel()
        productionCollectorsJob = viewModelScope.launch {
            // Collect Ideas
            launch {
                repository.getIdeasForProduction(productionId).collect {
                    _ideas.value = it
                }
            }
            // Collect Screenplays
            launch {
                repository.getScreenplaysForProduction(productionId).collect { sList ->
                    _screenplays.value = sList
                    if (_selectedScreenplayId.value == null || sList.none { it.id == _selectedScreenplayId.value }) {
                        sList.firstOrNull()?.let { selectScreenplay(it.id) }
                    }
                }
            }
            // Collect Characters
            launch {
                repository.getCharactersForProduction(productionId).collect {
                    _characters.value = it
                }
            }
            // Collect Scenes
            launch {
                repository.getScenesForProduction(productionId).collect {
                    _scenes.value = it
                }
            }
            // Collect Breakdowns
            launch {
                repository.getBreakdownForProduction(productionId).collect {
                    _breakdownItems.value = it
                }
            }
            // Collect Storyboards
            launch {
                repository.getStoryboardsForProduction(productionId).collect {
                    _storyboards.value = it
                }
            }
            // Collect Shots
            launch {
                repository.getShotsForProduction(productionId).collect {
                    _shots.value = it
                }
            }
            // Collect Cast & Crew
            launch {
                repository.getMembersForProduction(productionId).collect {
                    _castCrewMembers.value = it
                }
            }
            // Collect Auditions
            launch {
                repository.getAuditionsForProduction(productionId).collect {
                    _auditions.value = it
                }
            }
            // Collect Locations
            launch {
                repository.getLocationsForProduction(productionId).collect {
                    _locations.value = it
                }
            }
            // Collect Rehearsals
            launch {
                repository.getRehearsalsForProduction(productionId).collect {
                    _rehearsals.value = it
                }
            }
            // Collect Shooting Schedule
            launch {
                repository.getShootingDaysForProduction(productionId).collect {
                    _shootingDays.value = it
                }
            }
            // Collect Call Sheets
            launch {
                repository.getCallSheetsForProduction(productionId).collect {
                    _callSheets.value = it
                }
            }
            // Collect Continuity
            launch {
                repository.getContinuityForProduction(productionId).collect {
                    _continuities.value = it
                }
            }
            // Collect Takes
            launch {
                repository.getTakesForProduction(productionId).collect {
                    _takes.value = it
                }
            }
            // Collect Editing Reviews
            launch {
                repository.getReviewsForProduction(productionId).collect {
                    _editingReviews.value = it
                }
            }
            // Collect Sound Items
            launch {
                repository.getSoundItemsForProduction(productionId).collect {
                    _soundItems.value = it
                }
            }
            // Collect Budget Items
            launch {
                repository.getBudgetItemsForProduction(productionId).collect {
                    _budgetItems.value = it
                }
            }
        }
    }

    fun selectScreenplay(screenplayId: String) {
        _selectedScreenplayId.value = screenplayId
        screenplayElementsJob?.cancel()
        screenplayElementsJob = viewModelScope.launch {
            launch {
                repository.getElementsForScreenplay(screenplayId).collect {
                    _screenplayElements.value = it
                }
            }
            launch {
                repository.getVersionsForScreenplay(screenplayId).collect {
                    _screenplayVersions.value = it
                }
            }
        }
    }

    // =========================================================================
    // CRUD ACTIONS
    // =========================================================================

    fun createProduction(
        title: String,
        format: String,
        genre: String,
        language: String,
        logline: String,
        budget: Double,
        currency: String
    ) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val prod = ProductionEntity(
                id = UUID.randomUUID().toString(),
                roleId = roleId,
                title = title,
                format = format,
                genre = genre,
                language = language,
                logline = logline,
                budget = budget,
                currency = currency,
                currentStage = ProductionStage.IDEA.displayName,
                status = "Active"
            )
            repository.saveProduction(prod)
            selectProduction(prod.id)
        }
    }

    fun updateProduction(production: ProductionEntity) {
        viewModelScope.launch {
            repository.saveProduction(production.copy(updatedAt = System.currentTimeMillis()))
            _selectedProduction.value = production
        }
    }

    fun archiveProduction(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.archiveProduction(id, roleId)
        }
    }

    fun moveProductionToTrash(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.moveProductionToTrash(id, roleId)
        }
    }

    // 2. Ideas
    fun saveStoryIdea(
        title: String,
        premise: String,
        logline: String,
        genre: String,
        tone: String,
        theme: String,
        beginningBeat: String,
        middleBeat: String,
        endingBeat: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val idea = IdeaEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                roleId = roleId,
                title = title,
                premise = premise,
                logline = logline,
                genre = genre,
                tone = tone,
                theme = theme,
                beginningBeat = beginningBeat,
                middleBeat = middleBeat,
                endingBeat = endingBeat
            )
            repository.saveIdea(idea)
        }
    }

    fun deleteStoryIdea(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteIdea(id, roleId)
        }
    }

    // 3. Screenplay Elements
    fun addScreenplayElement(
        type: ScreenplayElementType,
        text: String,
        characterName: String = "",
        location: String = "",
        dayNight: String = "DAY",
        intExt: String = "INT"
    ) {
        val spId = _selectedScreenplayId.value ?: return
        val currentMaxOrder = _screenplayElements.value.maxOfOrNull { it.elementOrder } ?: 0
        viewModelScope.launch {
            val element = ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = currentMaxOrder + 1,
                elementType = type.name,
                text = text,
                characterName = characterName,
                location = location,
                dayOrNight = dayNight,
                intOrExt = intExt
            )
            repository.saveScreenplayElement(element)
        }
    }

    fun updateScreenplayElement(element: ScreenplayElementEntity) {
        viewModelScope.launch {
            repository.saveScreenplayElement(element.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteScreenplayElement(id: String) {
        viewModelScope.launch {
            repository.deleteScreenplayElement(id)
        }
    }

    fun createScreenplayVersion(draftName: String, summary: String) {
        val spId = _selectedScreenplayId.value ?: return
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        val currentCount = _screenplayVersions.value.size
        viewModelScope.launch {
            val snapshotJson = _screenplayElements.value.joinToString(separator = "\n") { el ->
                "${el.elementType}: ${el.text}"
            }
            val version = ScreenplayVersionEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                productionId = prodId,
                roleId = roleId,
                versionNumber = currentCount + 1,
                draftName = draftName,
                author = "Director",
                changeSummary = summary,
                elementsJson = snapshotJson
            )
            repository.saveScreenplayVersion(version)
        }
    }

    // 4. Characters
    fun saveCharacter(
        name: String,
        roleType: String,
        ageRange: String,
        goal: String,
        conflict: String,
        dialogueStyle: String,
        assignedActor: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val character = CharacterEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                roleId = roleId,
                name = name,
                roleType = roleType,
                ageRange = ageRange,
                goal = goal,
                conflict = conflict,
                dialogueStyle = dialogueStyle,
                assignedActorName = assignedActor
            )
            repository.saveCharacter(character)
        }
    }

    fun deleteCharacter(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteCharacter(id, roleId)
        }
    }

    // 5. Scenes
    fun saveScene(
        sceneNumber: Int,
        heading: String,
        intExt: String,
        locationName: String,
        timeOfDay: String,
        description: String,
        characters: String,
        durationSeconds: Int,
        status: SceneStatus
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val scene = SceneEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                screenplayId = _selectedScreenplayId.value,
                sceneNumber = sceneNumber,
                heading = heading,
                intExt = intExt,
                locationName = locationName,
                timeOfDay = timeOfDay,
                description = description,
                characterNames = characters,
                estimatedDurationSeconds = durationSeconds,
                status = status.name
            )
            repository.saveScene(scene, roleId)
        }
    }

    fun updateSceneStatus(id: String, status: SceneStatus) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.updateSceneStatus(id, status.name, roleId)
        }
    }

    fun deleteScene(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteScene(id, roleId)
        }
    }

    // 6. Breakdown Items
    fun saveBreakdownItem(
        sceneId: String,
        category: BreakdownCategory,
        description: String,
        quantity: Int,
        department: String,
        costEstimate: Double
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val item = BreakdownItemEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = sceneId,
                category = category.name,
                description = description,
                quantity = quantity,
                responsibleDepartment = department,
                costEstimate = costEstimate
            )
            repository.saveBreakdownItem(item, roleId)
        }
    }

    fun deleteBreakdownItem(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteBreakdownItem(id, roleId)
        }
    }

    // 7. Storyboards
    fun saveStoryboardFrame(
        sceneId: String,
        frameNumber: Int,
        caption: String,
        action: String,
        cameraAngle: String,
        cameraMovement: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val frame = StoryboardEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = sceneId,
                frameNumber = frameNumber,
                caption = caption,
                actionDescription = action,
                cameraAngle = cameraAngle,
                cameraMovement = cameraMovement
            )
            repository.saveStoryboard(frame, roleId)
        }
    }

    fun deleteStoryboard(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteStoryboard(id, roleId)
        }
    }

    // 8. Shots
    fun saveShot(
        sceneId: String,
        shotNumber: String,
        description: String,
        shotSize: String,
        cameraAngle: String,
        lens: String,
        cameraMovement: String,
        priority: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val shot = ShotEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = sceneId,
                shotNumber = shotNumber,
                description = description,
                shotSize = shotSize,
                cameraAngle = cameraAngle,
                lens = lens,
                cameraMovement = cameraMovement,
                priority = priority,
                status = ShotStatus.PLANNED.name
            )
            repository.saveShot(shot, roleId)
        }
    }

    fun updateShotStatus(id: String, status: ShotStatus) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.updateShotStatus(id, status.name, roleId)
        }
    }

    fun deleteShot(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteShot(id, roleId)
        }
    }

    // 9. Cast & Crew
    fun saveCastCrewMember(
        name: String,
        memberType: String,
        positionTitle: String,
        characterOrDept: String,
        phone: String,
        email: String,
        availability: String,
        isRestricted: Boolean
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val member = CastCrewMemberEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                roleId = roleId,
                name = name,
                memberType = memberType,
                positionTitle = positionTitle,
                characterNameOrDepartment = characterOrDept,
                phone = phone,
                email = email,
                availabilityStatus = availability,
                isRestrictedAccess = isRestricted
            )
            repository.saveMember(member)
        }
    }

    fun deleteMember(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteMember(id, roleId)
        }
    }

    // 10. Auditions
    fun saveAudition(
        characterId: String,
        candidateName: String,
        contact: String,
        rating: Int,
        status: AuditionStatus,
        notes: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val audition = AuditionEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                characterId = characterId,
                candidateName = candidateName,
                contact = contact,
                rating = rating,
                status = status.name,
                directorNotes = notes
            )
            repository.saveAudition(audition, roleId)
        }
    }

    fun updateAuditionStatus(id: String, status: AuditionStatus) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.updateAuditionStatus(id, status.name, roleId)
        }
    }

    fun deleteAudition(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteAudition(id, roleId)
        }
    }

    // 11. Locations
    fun saveLocation(
        name: String,
        address: String,
        contactPerson: String,
        phone: String,
        dailyCost: Double,
        permissionStatus: String,
        noiseLevel: String,
        power: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val loc = LocationEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                roleId = roleId,
                name = name,
                address = address,
                contactPerson = contactPerson,
                contactPhone = phone,
                dailyCost = dailyCost,
                permissionStatus = permissionStatus,
                noiseLevel = noiseLevel,
                powerAvailable = power
            )
            repository.saveLocation(loc)
        }
    }

    fun deleteLocation(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteLocation(id, roleId)
        }
    }

    // 12. Rehearsals
    fun saveRehearsal(
        locationName: String,
        objectives: String,
        blockingNotes: String,
        dialogueNotes: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val rehearsal = RehearsalEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                locationName = locationName,
                objectives = objectives,
                blockingNotes = blockingNotes,
                dialogueNotes = dialogueNotes
            )
            repository.saveRehearsal(rehearsal, roleId)
        }
    }

    fun deleteRehearsal(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteRehearsal(id, roleId)
        }
    }

    // 13. Shooting Schedule
    fun saveShootingDay(
        dayNumber: Int,
        callTime: String,
        wrapTime: String,
        primaryLocation: String,
        equipmentNotes: String,
        notes: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val day = ShootingDayEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                dayNumber = dayNumber,
                generalCallTime = callTime,
                wrapTime = wrapTime,
                primaryLocation = primaryLocation,
                equipmentNotes = equipmentNotes,
                notes = notes
            )
            repository.saveShootingDay(day, roleId)
        }
    }

    fun deleteShootingDay(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteShootingDay(id, roleId)
        }
    }

    // 14. Call Sheets
    fun saveCallSheet(
        dayNumber: Int,
        generalCallTime: String,
        locationAddress: String,
        weatherNote: String,
        specialInstructions: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val sheet = CallSheetEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                shootingDayId = "day_$dayNumber",
                dayNumber = dayNumber,
                generalCallTime = generalCallTime,
                locationAddress = locationAddress,
                weatherNote = weatherNote,
                specialInstructions = specialInstructions
            )
            repository.saveCallSheet(sheet, roleId)
        }
    }

    fun deleteCallSheet(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteCallSheet(id, roleId)
        }
    }

    // 15. Continuity
    fun saveContinuity(
        sceneId: String,
        shotId: String,
        takeNumber: Int,
        costumeNotes: String,
        makeupNotes: String,
        propsNotes: String,
        warnings: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val cont = ContinuityEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = sceneId,
                shotId = shotId,
                takeNumber = takeNumber,
                costumeNotes = costumeNotes,
                makeupNotes = makeupNotes,
                propsNotes = propsNotes,
                continuityWarnings = warnings
            )
            repository.saveContinuity(cont, roleId)
        }
    }

    fun deleteContinuity(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteContinuity(id, roleId)
        }
    }

    // 16. Takes
    fun logTake(
        sceneId: String,
        shotId: String,
        takeNumber: Int,
        fileRef: String,
        directorRating: Int,
        isSelectedBest: Boolean,
        problemNotes: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val take = TakeEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = sceneId,
                shotId = shotId,
                takeNumber = takeNumber,
                fileUriOrReference = fileRef,
                directorRating = directorRating,
                isSelectedBestTake = isSelectedBest,
                problemNotes = problemNotes
            )
            repository.saveTake(take, roleId)
        }
    }

    fun deleteTake(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteTake(id, roleId)
        }
    }

    // 17. Editing Reviews
    fun saveEditingReview(
        timestampCode: String,
        sceneNumber: Int,
        category: String,
        priority: String,
        commentText: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val review = EditingReviewEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                timestampCode = timestampCode,
                sceneNumber = sceneNumber,
                commentCategory = category,
                priority = priority,
                commentText = commentText
            )
            repository.saveReview(review, roleId)
        }
    }

    fun deleteEditingReview(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteReview(id, roleId)
        }
    }

    // 18. Sound & Music
    fun saveSoundItem(
        title: String,
        category: String,
        description: String,
        composer: String,
        licensing: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val sound = SoundMusicEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                title = title,
                category = category,
                description = description,
                composerOrDesigner = composer,
                licensingStatus = licensing
            )
            repository.saveSoundItem(sound, roleId)
        }
    }

    fun deleteSoundItem(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteSoundItem(id, roleId)
        }
    }

    // 19. Budget Items
    fun saveBudgetItem(
        itemTitle: String,
        category: String,
        plannedAmount: Double,
        actualExpense: Double,
        vendor: String,
        paymentStatus: String
    ) {
        val prodId = _selectedProductionId.value ?: return
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            val item = BudgetItemEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                roleId = roleId,
                itemTitle = itemTitle,
                category = category,
                plannedAmount = plannedAmount,
                actualExpense = actualExpense,
                vendor = vendor,
                paymentStatus = paymentStatus
            )
            repository.saveBudgetItem(item)
        }
    }

    fun deleteBudgetItem(id: String) {
        val roleId = _currentRoleId.value ?: return
        viewModelScope.launch {
            repository.deleteBudgetItem(id, roleId)
        }
    }

    // =========================================================================
    // SEED CINEMATIC SAMPLE PRODUCTION (Instant Polish & Rich Workspace)
    // =========================================================================
    private suspend fun seedSampleDirectorProduction(roleId: String) {
        val prodId = "prod_sample_${UUID.randomUUID().toString().take(8)}"
        val sampleProd = ProductionEntity(
            id = prodId,
            roleId = roleId,
            title = "The Whispering Shadows",
            workingTitle = "Project Nyx",
            format = DirectorSpecialisation.FEATURE_FILM.displayName,
            genre = "Neo-Noir Psychological Thriller",
            language = "English / Tamil",
            logline = "A reclusive forensic archivist discovers encrypted audio tapes from 1994 that predict unsolved cold-case disappearances in modern Chennai.",
            synopsis = "When archivist Maya finds an uncataloged spool of 1/4-inch magnetic tape in the state archives basement, she expects audio degradation. Instead, she hears the distinct voice of her estranged mentor reciting minute-by-minute details of an unsolved crime that occurred 30 years later.",
            theme = "Memory, Truth, and the Burden of Obsession",
            targetDurationMinutes = 118,
            status = "Active",
            currentStage = ProductionStage.PRODUCTION.displayName,
            budget = 12500000.0,
            currency = "INR",
            productionCompany = "Nocturne Cinema Labs"
        )
        repository.saveProduction(sampleProd)

        // Seed Screenplay
        val spId = "sp_$prodId"
        val sampleScreenplay = ScreenplayEntity(
            id = spId,
            productionId = prodId,
            roleId = roleId,
            title = "The Whispering Shadows - Final Shooting Draft",
            author = "Director & Co-Writer",
            currentVersion = "Draft 3.2 (Locked)",
            targetPages = 110,
            logline = sampleProd.logline,
            isLocked = true,
            approvalStatus = "Approved"
        )
        repository.saveScreenplay(sampleScreenplay)

        // Seed Screenplay Elements
        val sampleElements = listOf(
            ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = 1,
                elementType = ScreenplayElementType.SCENE_HEADING.name,
                text = "INT. STATE ARCHIVES BASEMENT - NIGHT",
                location = "State Archives Basement",
                dayOrNight = "NIGHT",
                intOrExt = "INT"
            ),
            ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = 2,
                elementType = ScreenplayElementType.ACTION.name,
                text = "Fluorescent tubes flicker with an erratic 50Hz hum. Stacks of yellowed microfilm cannisters stretch into the damp shadows like forgotten monoliths."
            ),
            ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = 3,
                elementType = ScreenplayElementType.CHARACTER.name,
                text = "MAYA (34)",
                characterName = "MAYA"
            ),
            ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = 4,
                elementType = ScreenplayElementType.PARENTHETICAL.name,
                text = "(adjusting her magnification glasses, hands trembling)"
            ),
            ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = 5,
                elementType = ScreenplayElementType.DIALOGUE.name,
                text = "This reel was logged as destroyed in the 1998 flood. Why is it here with fresh adhesive splices?",
                characterName = "MAYA"
            ),
            ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = 6,
                elementType = ScreenplayElementType.CHARACTER.name,
                text = "RAGHAV (58)",
                characterName = "RAGHAV"
            ),
            ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = 7,
                elementType = ScreenplayElementType.DIALOGUE.name,
                text = "Some records refuse to stay buried, Maya. Turn the machine off before security does their 2 AM perimeter sweep.",
                characterName = "RAGHAV"
            ),
            ScreenplayElementEntity(
                id = UUID.randomUUID().toString(),
                screenplayId = spId,
                elementOrder = 8,
                elementType = ScreenplayElementType.TRANSITION.name,
                text = "MATCH CUT TO:"
            )
        )
        repository.saveScreenplayElements(sampleElements)

        // Seed Characters
        val characters = listOf(
            CharacterEntity(
                id = "char_maya_$prodId",
                productionId = prodId,
                roleId = roleId,
                name = "Maya Sundaram",
                roleType = "Protagonist",
                ageRange = "32-36",
                gender = "Female",
                goal = "Uncover the authentic source of the premonitory tape recordings",
                conflict = "Her mentor Raghav actively conceals his involvement in the 1994 incident",
                dialogueStyle = "Precise, articulate, rarely raises voice",
                assignedActorName = "Ananya Ramachandran"
            ),
            CharacterEntity(
                id = "char_raghav_$prodId",
                productionId = prodId,
                roleId = roleId,
                name = "Raghavan Nambiar",
                roleType = "Antagonist",
                ageRange = "55-62",
                gender = "Male",
                goal = "Protect the institution's clandestine acoustic surveillance legacy",
                conflict = "Guilt over the original recorder's death",
                dialogueStyle = "Paternalistic, calm authority, veiled warnings",
                assignedActorName = "Prakash Varadhan"
            )
        )
        characters.forEach { repository.saveCharacter(it) }

        // Seed Scenes
        val scene1 = SceneEntity(
            id = "scene_1_$prodId",
            productionId = prodId,
            screenplayId = spId,
            sceneNumber = 1,
            actOrSequence = "Act I - Opening",
            heading = "INT. STATE ARCHIVES BASEMENT - NIGHT",
            intExt = "INT",
            locationName = "Archives Basement Vault",
            timeOfDay = "NIGHT",
            description = "Maya discovers the uncatalogued reel-to-reel tape. Raghav confronts her.",
            characterNames = "Maya Sundaram, Raghavan Nambiar",
            estimatedDurationSeconds = 180,
            status = SceneStatus.READY_FOR_SHOOTING.name
        )
        val scene2 = SceneEntity(
            id = "scene_2_$prodId",
            productionId = prodId,
            screenplayId = spId,
            sceneNumber = 2,
            actOrSequence = "Act I - Catalyst",
            heading = "EXT. RAIN-SLICK HARBOR ALLEY - NIGHT",
            intExt = "EXT",
            locationName = "North Chennai Old Port Alley",
            timeOfDay = "NIGHT",
            description = "Maya follows the coordinates scratched onto the tape spool.",
            characterNames = "Maya Sundaram",
            estimatedDurationSeconds = 120,
            status = SceneStatus.READY_FOR_SHOOTING.name
        )
        repository.saveScene(scene1, roleId)
        repository.saveScene(scene2, roleId)

        // Seed Shots for Scene 1
        val shots = listOf(
            ShotEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = scene1.id,
                shotNumber = "1A",
                description = "Slow tracking shot pushing in on Nagra tape recorder spools turning",
                shotSize = "CU",
                cameraAngle = "Eye Level",
                lens = "50mm Master Prime",
                cameraMovement = "Tracking / Dolly",
                priority = "MUST_HAVE",
                status = ShotStatus.COMPLETED.name,
                completedTakesCount = 4
            ),
            ShotEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = scene1.id,
                shotNumber = "1B",
                description = "Over-the-shoulder Maya looking up as Raghav enters doorway",
                shotSize = "OTS",
                cameraAngle = "Low Angle",
                lens = "35mm Prime",
                cameraMovement = "Static",
                priority = "HIGH",
                status = ShotStatus.READY.name
            )
        )
        shots.forEach { repository.saveShot(it, roleId) }

        // Seed Breakdown Items
        val breakdowns = listOf(
            BreakdownItemEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = scene1.id,
                category = BreakdownCategory.PROPS.name,
                description = "Vintage 1980s Nagra 4.2 Reel-to-Reel Tape Recorder + 1/4\" magnetic tape spools",
                quantity = 1,
                responsibleDepartment = "Art Department",
                costEstimate = 25000.0,
                status = "Ready"
            ),
            BreakdownItemEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                sceneId = scene1.id,
                category = BreakdownCategory.LIGHTING.name,
                description = "Aputure 600c with Fresnel for flickering fluorescent fixture simulation",
                quantity = 2,
                responsibleDepartment = "Camera / Grip",
                costEstimate = 12000.0,
                status = "Ready"
            )
        )
        breakdowns.forEach { repository.saveBreakdownItem(it, roleId) }

        // Seed Shooting Day & Call Sheet
        val shootDay = ShootingDayEntity(
            id = "shoot_day_1_$prodId",
            productionId = prodId,
            dayNumber = 1,
            dateMillis = System.currentTimeMillis() + (24 * 3600 * 1000),
            generalCallTime = "06:30 AM",
            wrapTime = "07:00 PM",
            primaryLocation = "Heritage Archives Vault, Old Port Road",
            equipmentNotes = "Arri Alexa Mini LF, Master Prime 24/35/50/85, Dana Dolly",
            status = "Planned"
        )
        repository.saveShootingDay(shootDay, roleId)

        val callSheet = CallSheetEntity(
            id = UUID.randomUUID().toString(),
            productionId = prodId,
            shootingDayId = shootDay.id,
            dayNumber = 1,
            shootingDateMillis = shootDay.dateMillis,
            generalCallTime = "06:30 AM",
            locationAddress = "Heritage Archives Vault, Old Port Road, Chennai",
            weatherNote = "29°C Night, Clear Sky, Humid",
            specialInstructions = "Silent floor footwear mandatory for all crew inside vault soundstage"
        )
        repository.saveCallSheet(callSheet, roleId)

        // Seed Budget Items
        val budgetList = listOf(
            BudgetItemEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                roleId = roleId,
                category = "EQUIPMENT",
                itemTitle = "Camera Package - Alexa Mini LF + Master Primes (10 Days)",
                plannedAmount = 650000.0,
                actualExpense = 620000.0,
                vendor = "Cinewave Rentals",
                paymentStatus = "Paid"
            ),
            BudgetItemEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                roleId = roleId,
                category = "LOCATIONS",
                itemTitle = "Heritage Archives Basement Permission & Security Deposit",
                plannedAmount = 150000.0,
                actualExpense = 145000.0,
                vendor = "State Heritage Board",
                paymentStatus = "Paid"
            ),
            BudgetItemEntity(
                id = UUID.randomUUID().toString(),
                productionId = prodId,
                roleId = roleId,
                category = "ART",
                itemTitle = "Vintage Period Reel-to-Reel Props & Archive Dressing",
                plannedAmount = 85000.0,
                actualExpense = 80000.0,
                vendor = "Retro Artworks",
                paymentStatus = "Paid"
            )
        )
        budgetList.forEach { repository.saveBudgetItem(it) }

        // Select the new production
        selectProduction(prodId)
    }
}

enum class DirectorSection(val title: String, val iconName: String) {
    DASHBOARD("Director Dashboard", "Dashboard"),
    PRODUCTIONS("Productions", "MovieFilter"),
    IDEAS("Ideas & Story", "Lightbulb"),
    SCREENPLAY("Screenplay Editor", "Description"),
    CHARACTERS("Characters", "Person"),
    BREAKDOWN("Script Breakdown", "AutoFixHigh"),
    SCENES("Scenes", "Theaters"),
    STORYBOARDS("Storyboards", "Brush"),
    SHOT_LISTS("Shot Lists", "Videocam"),
    CAST_CREW("Cast & Crew", "Groups"),
    AUDITIONS("Auditions", "Badge"),
    LOCATIONS("Locations", "Place"),
    REHEARSALS("Rehearsals", "RecordVoiceOver"),
    SHOOTING_SCHEDULE("Shooting Schedule", "Event"),
    CALL_SHEETS("Call Sheets", "Assignment"),
    CONTINUITY("Continuity Log", "CheckCircle"),
    FOOTAGE_TAKES("Takes & Footage", "VideoLibrary"),
    PRODUCTION_DIARY("Production Diary", "Book"),
    EDITING_REVIEW("Editing Review", "MovieCreation"),
    SOUND_MUSIC("Sound & Music", "MusicNote"),
    BUDGET("Budget & Expenses", "AccountBalanceWallet"),
    DOCUMENTS("Documents & Contracts", "Folder"),
    REPORTS("Reports & Export", "Assessment"),
    DIRECTOR_SETTINGS("Director Settings", "Tune")
}
