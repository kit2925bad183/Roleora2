package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roleora.data.auth.FirebaseAuthManager
import com.example.roleora.data.cloud.FirestoreSyncManager
import com.example.roleora.data.local.RoleoraDatabase
import com.example.roleora.data.repository.RoleoraRepository
import com.example.roleora.ui.screens.MainAppScreen
import com.example.roleora.ui.screens.onboarding.AdaptiveSetupScreen
import com.example.roleora.ui.theme.RoleoraTheme
import com.example.roleora.ui.viewmodel.DirectorViewModel
import com.example.roleora.ui.viewmodel.RoleoraViewModel
import com.example.roleora.ui.viewmodel.SetupViewModel
import com.example.roleora.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var repository: RoleoraRepository
    private lateinit var authManager: FirebaseAuthManager
    private lateinit var syncManager: FirestoreSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = RoleoraDatabase.getDatabase(applicationContext, lifecycleScope)
        repository = RoleoraRepository(
            roleDao = database.roleDao(),
            templateDao = database.templateDao(),
            entryDao = database.entryDao(),
            recordDao = database.recordDao(),
            auditDao = database.auditDao(),
            workspaceTemplateVersionDao = database.workspaceTemplateVersionDao(),
            userDao = database.userDao(),
            sessionDao = database.sessionDao(),
            universalEntryDao = database.universalEntryDao(),
            universalEntryVersionDao = database.universalEntryVersionDao(),
            taskDao = database.taskDao(),
            eventDao = database.eventDao(),
            attachmentDao = database.attachmentDao(),
            workSessionDao = database.workSessionDao(),
            syncQueueDao = database.syncQueueDao(),
            productionDao = database.productionDao(),
            ideaDao = database.ideaDao(),
            screenplayDao = database.screenplayDao(),
            characterDao = database.characterDao(),
            sceneDao = database.sceneDao(),
            breakdownDao = database.breakdownDao(),
            storyboardDao = database.storyboardDao(),
            shotDao = database.shotDao(),
            castCrewDao = database.castCrewDao(),
            auditionDao = database.auditionDao(),
            locationDao = database.locationDao(),
            rehearsalDao = database.rehearsalDao(),
            scheduleDao = database.scheduleDao(),
            callSheetDao = database.callSheetDao(),
            continuityDao = database.continuityDao(),
            takeDao = database.takeDao(),
            editingReviewDao = database.editingReviewDao(),
            soundMusicDao = database.soundMusicDao(),
            budgetDao = database.budgetDao()
        )
        authManager = FirebaseAuthManager(applicationContext, lifecycleScope)
        syncManager = FirestoreSyncManager()

        setContent {
            RoleoraTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RoleoraAppNavigation(repository, authManager, syncManager)
                }
            }
        }
    }
}

enum class AppScreenState {
    MAIN,
    SETUP
}

@Composable
fun RoleoraAppNavigation(
    repository: RoleoraRepository,
    authManager: FirebaseAuthManager,
    syncManager: FirestoreSyncManager
) {
    var currentScreen by remember { mutableStateOf(AppScreenState.MAIN) }

    val factory = remember { ViewModelFactory(repository, authManager, syncManager) }
    val mainViewModel: RoleoraViewModel = viewModel(factory = factory)
    val setupViewModel: SetupViewModel = viewModel(factory = factory)
    val directorViewModel: DirectorViewModel = viewModel(factory = factory)

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "AppScreenTransition"
    ) { screen ->
        when (screen) {
            AppScreenState.MAIN -> {
                MainAppScreen(
                    viewModel = mainViewModel,
                    directorViewModel = directorViewModel,
                    onOpenSetup = { currentScreen = AppScreenState.SETUP }
                )
            }
            AppScreenState.SETUP -> {
                AdaptiveSetupScreen(
                    viewModel = setupViewModel,
                    onNavigateBack = { currentScreen = AppScreenState.MAIN },
                    onWorkspaceCreated = { roleId ->
                        mainViewModel.selectRole(roleId)
                        currentScreen = AppScreenState.MAIN
                    }
                )
            }
        }
    }
}
