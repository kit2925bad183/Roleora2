package com.example.roleora.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roleora.data.model.AttachmentEntity
import com.example.roleora.data.model.AuditEventEntity
import com.example.roleora.data.model.AuditionEntity
import com.example.roleora.data.model.BreakdownItemEntity
import com.example.roleora.data.model.BudgetItemEntity
import com.example.roleora.data.model.CallSheetEntity
import com.example.roleora.data.model.CastCrewMemberEntity
import com.example.roleora.data.model.CharacterEntity
import com.example.roleora.data.model.ContinuityEntity
import com.example.roleora.data.model.DiaryEntryEntity
import com.example.roleora.data.model.EditingReviewEntity
import com.example.roleora.data.model.EntryEntity
import com.example.roleora.data.model.EntryVersionEntity
import com.example.roleora.data.model.EventEntity
import com.example.roleora.data.model.IdeaEntity
import com.example.roleora.data.model.LocationEntity
import com.example.roleora.data.model.ProductionEntity
import com.example.roleora.data.model.ProfessionRecordEntity
import com.example.roleora.data.model.ProfessionTemplateEntity
import com.example.roleora.data.model.RehearsalEntity
import com.example.roleora.data.model.RoleEntity
import com.example.roleora.data.model.SceneEntity
import com.example.roleora.data.model.ScreenplayElementEntity
import com.example.roleora.data.model.ScreenplayEntity
import com.example.roleora.data.model.ScreenplayVersionEntity
import com.example.roleora.data.model.SessionEntity
import com.example.roleora.data.model.ShootingDayEntity
import com.example.roleora.data.model.ShotEntity
import com.example.roleora.data.model.SoundMusicEntity
import com.example.roleora.data.model.StoryboardEntity
import com.example.roleora.data.model.SyncQueueEntity
import com.example.roleora.data.model.TakeEntity
import com.example.roleora.data.model.TaskEntity
import com.example.roleora.data.model.TemplateInstallationEntity
import com.example.roleora.data.model.TemplateVersionEntity
import com.example.roleora.data.model.UserEntity
import com.example.roleora.data.model.WorkSessionEntity
import com.example.roleora.data.model.WorkspaceTemplateVersionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RoleEntity::class,
        ProfessionTemplateEntity::class,
        TemplateVersionEntity::class,
        TemplateInstallationEntity::class,
        WorkspaceTemplateVersionEntity::class,
        DiaryEntryEntity::class,
        ProfessionRecordEntity::class,
        AuditEventEntity::class,
        UserEntity::class,
        SessionEntity::class,
        EntryEntity::class,
        EntryVersionEntity::class,
        TaskEntity::class,
        EventEntity::class,
        AttachmentEntity::class,
        WorkSessionEntity::class,
        SyncQueueEntity::class,
        // Phase 3: Movie Director Entities
        ProductionEntity::class,
        IdeaEntity::class,
        ScreenplayEntity::class,
        ScreenplayVersionEntity::class,
        ScreenplayElementEntity::class,
        CharacterEntity::class,
        SceneEntity::class,
        BreakdownItemEntity::class,
        StoryboardEntity::class,
        ShotEntity::class,
        CastCrewMemberEntity::class,
        AuditionEntity::class,
        LocationEntity::class,
        RehearsalEntity::class,
        ShootingDayEntity::class,
        CallSheetEntity::class,
        ContinuityEntity::class,
        TakeEntity::class,
        EditingReviewEntity::class,
        SoundMusicEntity::class,
        BudgetItemEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class RoleoraDatabase : RoomDatabase() {
    abstract fun roleDao(): RoleDao
    abstract fun templateDao(): TemplateDao
    abstract fun workspaceTemplateVersionDao(): WorkspaceTemplateVersionDao
    abstract fun entryDao(): EntryDao
    abstract fun recordDao(): RecordDao
    abstract fun auditDao(): AuditDao
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun universalEntryDao(): UniversalEntryDao
    abstract fun universalEntryVersionDao(): UniversalEntryVersionDao
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun workSessionDao(): WorkSessionDao
    abstract fun syncQueueDao(): SyncQueueDao

    // Phase 3: Director DAOs
    abstract fun productionDao(): ProductionDao
    abstract fun ideaDao(): IdeaDao
    abstract fun screenplayDao(): ScreenplayDao
    abstract fun characterDao(): CharacterDao
    abstract fun sceneDao(): SceneDao
    abstract fun breakdownDao(): BreakdownDao
    abstract fun storyboardDao(): StoryboardDao
    abstract fun shotDao(): ShotDao
    abstract fun castCrewDao(): CastCrewDao
    abstract fun auditionDao(): AuditionDao
    abstract fun locationDao(): LocationDao
    abstract fun rehearsalDao(): RehearsalDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun callSheetDao(): CallSheetDao
    abstract fun continuityDao(): ContinuityDao
    abstract fun takeDao(): TakeDao
    abstract fun editingReviewDao(): EditingReviewDao
    abstract fun soundMusicDao(): SoundMusicDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: RoleoraDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RoleoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoleoraDatabase::class.java,
                    "roleora_database.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // Ensure templates & versions are present on every launch
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: RoleoraDatabase) {
            val templateDao = db.templateDao()
            templateDao.insertTemplates(SeedData.initialTemplates)
            templateDao.insertVersions(SeedData.templateVersions)
        }
    }
}
