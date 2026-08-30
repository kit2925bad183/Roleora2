package com.example.roleora.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roleora.data.model.AuditionEntity
import com.example.roleora.data.model.BreakdownItemEntity
import com.example.roleora.data.model.BudgetItemEntity
import com.example.roleora.data.model.CallSheetEntity
import com.example.roleora.data.model.CastCrewMemberEntity
import com.example.roleora.data.model.CharacterEntity
import com.example.roleora.data.model.ContinuityEntity
import com.example.roleora.data.model.EditingReviewEntity
import com.example.roleora.data.model.IdeaEntity
import com.example.roleora.data.model.LocationEntity
import com.example.roleora.data.model.ProductionEntity
import com.example.roleora.data.model.RehearsalEntity
import com.example.roleora.data.model.SceneEntity
import com.example.roleora.data.model.ScreenplayElementEntity
import com.example.roleora.data.model.ScreenplayEntity
import com.example.roleora.data.model.ScreenplayVersionEntity
import com.example.roleora.data.model.ShootingDayEntity
import com.example.roleora.data.model.ShotEntity
import com.example.roleora.data.model.SoundMusicEntity
import com.example.roleora.data.model.StoryboardEntity
import com.example.roleora.data.model.TakeEntity
import kotlinx.coroutines.flow.Flow

// ============================================================================
// 1. PRODUCTION DAO
// ============================================================================
@Dao
interface ProductionDao {
    @Query("SELECT * FROM director_productions WHERE roleId = :roleId AND deletedAt IS NULL AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getActiveProductionsForRole(roleId: String): Flow<List<ProductionEntity>>

    @Query("SELECT * FROM director_productions WHERE roleId = :roleId AND deletedAt IS NULL AND isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedProductionsForRole(roleId: String): Flow<List<ProductionEntity>>

    @Query("SELECT * FROM director_productions WHERE roleId = :roleId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashProductionsForRole(roleId: String): Flow<List<ProductionEntity>>

    @Query("SELECT * FROM director_productions WHERE id = :id LIMIT 1")
    suspend fun getProductionById(id: String): ProductionEntity?

    @Query("SELECT * FROM director_productions WHERE id = :id LIMIT 1")
    fun observeProductionById(id: String): Flow<ProductionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduction(production: ProductionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductions(productions: List<ProductionEntity>)

    @Update
    suspend fun updateProduction(production: ProductionEntity)

    @Query("UPDATE director_productions SET isArchived = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun archiveProduction(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE director_productions SET isArchived = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun restoreArchivedProduction(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE director_productions SET deletedAt = :timestamp WHERE id = :id")
    suspend fun moveToTrash(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE director_productions SET deletedAt = NULL WHERE id = :id")
    suspend fun restoreFromTrash(id: String)

    @Query("DELETE FROM director_productions WHERE id = :id")
    suspend fun deletePermanently(id: String)

    @Query("DELETE FROM director_productions WHERE roleId = :roleId")
    suspend fun deleteAllForRole(roleId: String)
}

// ============================================================================
// 2. IDEA & STORY DAO
// ============================================================================
@Dao
interface IdeaDao {
    @Query("SELECT * FROM director_ideas WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY updatedAt DESC")
    fun getIdeasForProduction(productionId: String): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM director_ideas WHERE roleId = :roleId AND deletedAt IS NULL ORDER BY updatedAt DESC")
    fun getIdeasForRole(roleId: String): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM director_ideas WHERE id = :id LIMIT 1")
    suspend fun getIdeaById(id: String): IdeaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: IdeaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdeas(ideas: List<IdeaEntity>)

    @Update
    suspend fun updateIdea(idea: IdeaEntity)

    @Query("UPDATE director_ideas SET deletedAt = :timestamp WHERE id = :id")
    suspend fun moveToTrash(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM director_ideas WHERE id = :id")
    suspend fun deletePermanently(id: String)
}

// ============================================================================
// 3. SCREENPLAY & VERSIONS DAO
// ============================================================================
@Dao
interface ScreenplayDao {
    @Query("SELECT * FROM director_screenplays WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY updatedAt DESC")
    fun getScreenplaysForProduction(productionId: String): Flow<List<ScreenplayEntity>>

    @Query("SELECT * FROM director_screenplays WHERE id = :id LIMIT 1")
    suspend fun getScreenplayById(id: String): ScreenplayEntity?

    @Query("SELECT * FROM director_screenplays WHERE id = :id LIMIT 1")
    fun observeScreenplayById(id: String): Flow<ScreenplayEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenplay(screenplay: ScreenplayEntity)

    @Update
    suspend fun updateScreenplay(screenplay: ScreenplayEntity)

    @Query("DELETE FROM director_screenplays WHERE id = :id")
    suspend fun deleteScreenplay(id: String)

    // Elements
    @Query("SELECT * FROM director_screenplay_elements WHERE screenplayId = :screenplayId ORDER BY elementOrder ASC")
    fun getElementsForScreenplay(screenplayId: String): Flow<List<ScreenplayElementEntity>>

    @Query("SELECT * FROM director_screenplay_elements WHERE screenplayId = :screenplayId ORDER BY elementOrder ASC")
    suspend fun getElementsList(screenplayId: String): List<ScreenplayElementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElements(elements: List<ScreenplayElementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElement(element: ScreenplayElementEntity)

    @Update
    suspend fun updateElement(element: ScreenplayElementEntity)

    @Query("DELETE FROM director_screenplay_elements WHERE id = :id")
    suspend fun deleteElement(id: String)

    @Query("DELETE FROM director_screenplay_elements WHERE screenplayId = :screenplayId")
    suspend fun clearElementsForScreenplay(screenplayId: String)

    // Versions
    @Query("SELECT * FROM director_screenplay_versions WHERE screenplayId = :screenplayId ORDER BY versionNumber DESC")
    fun getVersionsForScreenplay(screenplayId: String): Flow<List<ScreenplayVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: ScreenplayVersionEntity)
}

// ============================================================================
// 4. CHARACTER DAO
// ============================================================================
@Dao
interface CharacterDao {
    @Query("SELECT * FROM director_characters WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY name ASC")
    fun getCharactersForProduction(productionId: String): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM director_characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: String): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Query("UPDATE director_characters SET deletedAt = :timestamp WHERE id = :id")
    suspend fun moveToTrash(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM director_characters WHERE id = :id")
    suspend fun deletePermanently(id: String)
}

// ============================================================================
// 5. SCENE DAO
// ============================================================================
@Dao
interface SceneDao {
    @Query("SELECT * FROM director_scenes WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY sceneNumber ASC")
    fun getScenesForProduction(productionId: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM director_scenes WHERE id = :id LIMIT 1")
    suspend fun getSceneById(id: String): SceneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenes(scenes: List<SceneEntity>)

    @Update
    suspend fun updateScene(scene: SceneEntity)

    @Query("UPDATE director_scenes SET status = :newStatus WHERE id = :id")
    suspend fun updateSceneStatus(id: String, newStatus: String)

    @Query("UPDATE director_scenes SET deletedAt = :timestamp WHERE id = :id")
    suspend fun moveToTrash(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM director_scenes WHERE id = :id")
    suspend fun deletePermanently(id: String)
}

// ============================================================================
// 6. SCRIPT BREAKDOWN DAO
// ============================================================================
@Dao
interface BreakdownDao {
    @Query("SELECT * FROM director_breakdowns WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY category ASC")
    fun getBreakdownForProduction(productionId: String): Flow<List<BreakdownItemEntity>>

    @Query("SELECT * FROM director_breakdowns WHERE sceneId = :sceneId AND deletedAt IS NULL ORDER BY category ASC")
    fun getBreakdownForScene(sceneId: String): Flow<List<BreakdownItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakdownItem(item: BreakdownItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakdownItems(items: List<BreakdownItemEntity>)

    @Update
    suspend fun updateBreakdownItem(item: BreakdownItemEntity)

    @Query("DELETE FROM director_breakdowns WHERE id = :id")
    suspend fun deleteBreakdownItem(id: String)
}

// ============================================================================
// 7. STORYBOARD DAO
// ============================================================================
@Dao
interface StoryboardDao {
    @Query("SELECT * FROM director_storyboards WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY frameNumber ASC")
    fun getStoryboardsForProduction(productionId: String): Flow<List<StoryboardEntity>>

    @Query("SELECT * FROM director_storyboards WHERE sceneId = :sceneId AND deletedAt IS NULL ORDER BY frameNumber ASC")
    fun getStoryboardsForScene(sceneId: String): Flow<List<StoryboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoryboard(frame: StoryboardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoryboards(frames: List<StoryboardEntity>)

    @Update
    suspend fun updateStoryboard(frame: StoryboardEntity)

    @Query("DELETE FROM director_storyboards WHERE id = :id")
    suspend fun deleteStoryboard(id: String)
}

// ============================================================================
// 8. SHOT LIST DAO
// ============================================================================
@Dao
interface ShotDao {
    @Query("SELECT * FROM director_shots WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY shotNumber ASC")
    fun getShotsForProduction(productionId: String): Flow<List<ShotEntity>>

    @Query("SELECT * FROM director_shots WHERE sceneId = :sceneId AND deletedAt IS NULL ORDER BY shotNumber ASC")
    fun getShotsForScene(sceneId: String): Flow<List<ShotEntity>>

    @Query("SELECT * FROM director_shots WHERE id = :id LIMIT 1")
    suspend fun getShotById(id: String): ShotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShot(shot: ShotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShots(shots: List<ShotEntity>)

    @Update
    suspend fun updateShot(shot: ShotEntity)

    @Query("UPDATE director_shots SET status = :status WHERE id = :id")
    suspend fun updateShotStatus(id: String, status: String)

    @Query("DELETE FROM director_shots WHERE id = :id")
    suspend fun deleteShot(id: String)
}

// ============================================================================
// 9. CAST & CREW DAO
// ============================================================================
@Dao
interface CastCrewDao {
    @Query("SELECT * FROM director_cast_crew WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY memberType ASC, name ASC")
    fun getMembersForProduction(productionId: String): Flow<List<CastCrewMemberEntity>>

    @Query("SELECT * FROM director_cast_crew WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: String): CastCrewMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: CastCrewMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<CastCrewMemberEntity>)

    @Update
    suspend fun updateMember(member: CastCrewMemberEntity)

    @Query("DELETE FROM director_cast_crew WHERE id = :id")
    suspend fun deleteMember(id: String)
}

// ============================================================================
// 10. AUDITION DAO
// ============================================================================
@Dao
interface AuditionDao {
    @Query("SELECT * FROM director_auditions WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY auditionDateTime ASC")
    fun getAuditionsForProduction(productionId: String): Flow<List<AuditionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudition(audition: AuditionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditions(auditions: List<AuditionEntity>)

    @Update
    suspend fun updateAudition(audition: AuditionEntity)

    @Query("UPDATE director_auditions SET status = :status WHERE id = :id")
    suspend fun updateAuditionStatus(id: String, status: String)

    @Query("DELETE FROM director_auditions WHERE id = :id")
    suspend fun deleteAudition(id: String)
}

// ============================================================================
// 11. LOCATION DAO
// ============================================================================
@Dao
interface LocationDao {
    @Query("SELECT * FROM director_locations WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY name ASC")
    fun getLocationsForProduction(productionId: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM director_locations WHERE id = :id LIMIT 1")
    suspend fun getLocationById(id: String): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Query("DELETE FROM director_locations WHERE id = :id")
    suspend fun deleteLocation(id: String)
}

// ============================================================================
// 12. REHEARSAL DAO
// ============================================================================
@Dao
interface RehearsalDao {
    @Query("SELECT * FROM director_rehearsals WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY scheduledDateTime ASC")
    fun getRehearsalsForProduction(productionId: String): Flow<List<RehearsalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRehearsal(rehearsal: RehearsalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRehearsals(rehearsals: List<RehearsalEntity>)

    @Update
    suspend fun updateRehearsal(rehearsal: RehearsalEntity)

    @Query("DELETE FROM director_rehearsals WHERE id = :id")
    suspend fun deleteRehearsal(id: String)
}

// ============================================================================
// 13. SHOOTING SCHEDULE DAO
// ============================================================================
@Dao
interface ScheduleDao {
    @Query("SELECT * FROM director_shooting_days WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY dayNumber ASC, dateMillis ASC")
    fun getShootingDaysForProduction(productionId: String): Flow<List<ShootingDayEntity>>

    @Query("SELECT * FROM director_shooting_days WHERE id = :id LIMIT 1")
    suspend fun getShootingDayById(id: String): ShootingDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShootingDay(day: ShootingDayEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShootingDays(days: List<ShootingDayEntity>)

    @Update
    suspend fun updateShootingDay(day: ShootingDayEntity)

    @Query("DELETE FROM director_shooting_days WHERE id = :id")
    suspend fun deleteShootingDay(id: String)
}

// ============================================================================
// 14. CALL SHEET DAO
// ============================================================================
@Dao
interface CallSheetDao {
    @Query("SELECT * FROM director_call_sheets WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY dayNumber ASC, shootingDateMillis ASC")
    fun getCallSheetsForProduction(productionId: String): Flow<List<CallSheetEntity>>

    @Query("SELECT * FROM director_call_sheets WHERE id = :id LIMIT 1")
    suspend fun getCallSheetById(id: String): CallSheetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallSheet(callSheet: CallSheetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallSheets(callSheets: List<CallSheetEntity>)

    @Update
    suspend fun updateCallSheet(callSheet: CallSheetEntity)

    @Query("DELETE FROM director_call_sheets WHERE id = :id")
    suspend fun deleteCallSheet(id: String)
}

// ============================================================================
// 15. CONTINUITY DAO
// ============================================================================
@Dao
interface ContinuityDao {
    @Query("SELECT * FROM director_continuity WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY sceneId ASC, takeNumber ASC")
    fun getContinuityForProduction(productionId: String): Flow<List<ContinuityEntity>>

    @Query("SELECT * FROM director_continuity WHERE sceneId = :sceneId AND deletedAt IS NULL")
    fun getContinuityForScene(sceneId: String): Flow<List<ContinuityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContinuity(continuity: ContinuityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContinuities(continuities: List<ContinuityEntity>)

    @Update
    suspend fun updateContinuity(continuity: ContinuityEntity)

    @Query("DELETE FROM director_continuity WHERE id = :id")
    suspend fun deleteContinuity(id: String)
}

// ============================================================================
// 16. TAKE LOG DAO
// ============================================================================
@Dao
interface TakeDao {
    @Query("SELECT * FROM director_takes WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY startTimeMillis DESC")
    fun getTakesForProduction(productionId: String): Flow<List<TakeEntity>>

    @Query("SELECT * FROM director_takes WHERE shotId = :shotId AND deletedAt IS NULL ORDER BY takeNumber ASC")
    fun getTakesForShot(shotId: String): Flow<List<TakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTake(take: TakeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTakes(takes: List<TakeEntity>)

    @Update
    suspend fun updateTake(take: TakeEntity)

    @Query("DELETE FROM director_takes WHERE id = :id")
    suspend fun deleteTake(id: String)
}

// ============================================================================
// 17. EDITING REVIEW DAO
// ============================================================================
@Dao
interface EditingReviewDao {
    @Query("SELECT * FROM director_editing_reviews WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY reviewDateMillis DESC")
    fun getReviewsForProduction(productionId: String): Flow<List<EditingReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: EditingReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<EditingReviewEntity>)

    @Update
    suspend fun updateReview(review: EditingReviewEntity)

    @Query("DELETE FROM director_editing_reviews WHERE id = :id")
    suspend fun deleteReview(id: String)
}

// ============================================================================
// 18. SOUND AND MUSIC DAO
// ============================================================================
@Dao
interface SoundMusicDao {
    @Query("SELECT * FROM director_sound_music WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY category ASC")
    fun getSoundItemsForProduction(productionId: String): Flow<List<SoundMusicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoundItem(item: SoundMusicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoundItems(items: List<SoundMusicEntity>)

    @Update
    suspend fun updateSoundItem(item: SoundMusicEntity)

    @Query("DELETE FROM director_sound_music WHERE id = :id")
    suspend fun deleteSoundItem(id: String)
}

// ============================================================================
// 19. BUDGET DAO
// ============================================================================
@Dao
interface BudgetDao {
    @Query("SELECT * FROM director_budget_items WHERE productionId = :productionId AND deletedAt IS NULL ORDER BY expenseDateMillis DESC")
    fun getBudgetItemsForProduction(productionId: String): Flow<List<BudgetItemEntity>>

    @Query("SELECT * FROM director_budget_items WHERE roleId = :roleId AND deletedAt IS NULL ORDER BY expenseDateMillis DESC")
    fun getBudgetItemsForRole(roleId: String): Flow<List<BudgetItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItem(item: BudgetItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItems(items: List<BudgetItemEntity>)

    @Update
    suspend fun updateBudgetItem(item: BudgetItemEntity)

    @Query("DELETE FROM director_budget_items WHERE id = :id")
    suspend fun deleteBudgetItem(id: String)
}
