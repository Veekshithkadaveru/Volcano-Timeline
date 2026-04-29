package app.krafted.volcanotimeline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.krafted.volcanotimeline.data.EruptionRepository
import app.krafted.volcanotimeline.data.db.AppDatabase
import app.krafted.volcanotimeline.data.db.RoundProgressDao
import app.krafted.volcanotimeline.ui.GameScreen
import app.krafted.volcanotimeline.ui.HomeScreen
import app.krafted.volcanotimeline.ui.ResultScreen
import app.krafted.volcanotimeline.ui.RoundCompleteScreen
import app.krafted.volcanotimeline.ui.RoundIntroScreen
import app.krafted.volcanotimeline.ui.SplashScreen
import app.krafted.volcanotimeline.ui.VictoryScreen
import app.krafted.volcanotimeline.ui.theme.VolcanoTimelineTheme
import app.krafted.volcanotimeline.viewmodel.GameViewModel
import app.krafted.volcanotimeline.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VolcanoTimelineTheme {
                val repository = remember { EruptionRepository(applicationContext) }
                val db = remember { AppDatabase.getDatabase(applicationContext) }
                val dao = remember { db.roundProgressDao() }
                VolcanoNavGraph(
                    repository = repository,
                    dao = dao,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val ROUND_INTRO = "round_intro/{roundId}"
    const val GAME_FLOW = "game_flow/{roundId}"
    const val GAME = "game/{roundId}"
    const val RESULT = "result/{roundId}"
    const val ROUND_COMPLETE = "round_complete/{roundId}"
    const val VICTORY = "victory/{totalScore}"

    fun roundIntro(roundId: Int) = "round_intro/$roundId"
    fun gameFlow(roundId: Int) = "game_flow/$roundId"
    fun game(roundId: Int) = "game/$roundId"
    fun result(roundId: Int) = "result/$roundId"
    fun roundComplete(roundId: Int) = "round_complete/$roundId"
    fun victory(totalScore: Int) = "victory/$totalScore"
}

@Composable
fun VolcanoNavGraph(
    repository: EruptionRepository,
    dao: RoundProgressDao,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val transitionDuration = 350
    val totalRounds = remember { repository.getEruptionRounds().size }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier,
        enterTransition = { fadeIn(tween(transitionDuration)) },
        exitTransition = { fadeOut(tween(transitionDuration)) },
        popEnterTransition = { fadeIn(tween(transitionDuration)) },
        popExitTransition = { fadeOut(tween(transitionDuration)) }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository, dao)
            )
            val homeState by homeViewModel.uiState.collectAsState()

            HomeScreen(
                viewModel = homeViewModel,
                onRoundSelected = { roundId ->
                    if (homeState.allCompleted) {
                        navController.navigate(Routes.victory(homeState.totalScore))
                    } else {
                        navController.navigate(Routes.roundIntro(roundId))
                    }
                },
                onLeaderboard = { }
            )
        }

        composable(
            Routes.ROUND_INTRO,
            arguments = listOf(navArgument("roundId") { type = NavType.IntType })
        ) { backStackEntry ->
            val roundId = (backStackEntry.arguments?.getInt("roundId") ?: 1).coerceIn(1, totalRounds.coerceAtLeast(1))
            val round = remember(roundId) { repository.getRound(roundId) }
            if (round != null) {
                RoundIntroScreen(
                    round = round,
                    onBegin = {
                        navController.navigate(Routes.gameFlow(roundId)) {
                            popUpTo(Routes.roundIntro(roundId)) { inclusive = true }
                        }
                    }
                )
            }
        }

        navigation(
            startDestination = Routes.GAME,
            route = Routes.GAME_FLOW,
            arguments = listOf(navArgument("roundId") { type = NavType.IntType })
        ) {
            composable(
                Routes.GAME,
                arguments = listOf(navArgument("roundId") { type = NavType.IntType })
            ) { backStackEntry ->
                val roundId = (backStackEntry.arguments?.getInt("roundId") ?: 1).coerceIn(1, totalRounds.coerceAtLeast(1))
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.GAME_FLOW)
                }
                val gameViewModel: GameViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = GameViewModel.Factory(repository, dao)
                )

                val uiState by gameViewModel.uiState.collectAsState()
                if (uiState.currentRound == null || uiState.currentRound?.roundId != roundId) {
                    gameViewModel.loadRound(roundId)
                }

                GameScreen(
                    viewModel = gameViewModel,
                    onConfirmed = {
                        navController.navigate(Routes.result(roundId))
                    }
                )
            }

            composable(
                Routes.RESULT,
                arguments = listOf(navArgument("roundId") { type = NavType.IntType })
            ) { backStackEntry ->
                val roundId = (backStackEntry.arguments?.getInt("roundId") ?: 1).coerceIn(1, totalRounds.coerceAtLeast(1))
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.GAME_FLOW)
                }
                val gameViewModel: GameViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = GameViewModel.Factory(repository, dao)
                )

                ResultScreen(
                    viewModel = gameViewModel,
                    onNextRound = {
                        if (roundId < totalRounds) {
                            navController.navigate(Routes.roundIntro(roundId + 1)) {
                                popUpTo(Routes.HOME)
                            }
                        }
                    },
                    onTryAgain = {
                        gameViewModel.nextAttempt()
                        navController.navigate(Routes.game(roundId)) {
                            popUpTo(Routes.gameFlow(roundId)) { inclusive = false }
                        }
                    },
                    onRoundComplete = {
                        navController.navigate(Routes.roundComplete(roundId))
                    }
                )
            }

            composable(
                Routes.ROUND_COMPLETE,
                arguments = listOf(navArgument("roundId") { type = NavType.IntType })
            ) { backStackEntry ->
                val roundId = (backStackEntry.arguments?.getInt("roundId") ?: 1).coerceIn(1, totalRounds.coerceAtLeast(1))
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.GAME_FLOW)
                }
                val gameViewModel: GameViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = GameViewModel.Factory(repository, dao)
                )

                RoundCompleteScreen(
                    viewModel = gameViewModel,
                    onNextRound = {
                        if (roundId < totalRounds) {
                            navController.navigate(Routes.roundIntro(roundId + 1)) {
                                popUpTo(Routes.HOME)
                            }
                        } else {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    },
                    onHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(
            Routes.VICTORY,
            arguments = listOf(navArgument("totalScore") { type = NavType.IntType })
        ) { backStackEntry ->
            val totalScore = backStackEntry.arguments?.getInt("totalScore") ?: 0
            VictoryScreen(
                totalScore = totalScore,
                onHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}