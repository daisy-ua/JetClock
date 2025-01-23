package com.daisy.jetclock.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daisy.jetclock.presentation.navigation.MainDestinations.ALARMS_ROUTE
import com.daisy.jetclock.presentation.navigation.MainDestinations.ALARM_DETAILS_GRAPH
import com.daisy.jetclock.presentation.navigation.MainDestinations.ALARM_ID_KEY
import com.daisy.jetclock.presentation.navigation.MainDestinations.ALARM_ROUTE
import com.daisy.jetclock.presentation.navigation.MainDestinations.SELECT_SOUND_ROUTE
import com.daisy.jetclock.presentation.navigation.MainDestinations.SOUND_ID_KEY
import com.daisy.jetclock.presentation.ui.screens.AlarmDetailsScreen
import com.daisy.jetclock.presentation.ui.screens.AlarmScreen
import com.daisy.jetclock.presentation.ui.screens.SoundSelectionScreen
import com.daisy.jetclock.presentation.viewmodel.AlarmDetailsViewModel
import com.daisy.jetclock.presentation.viewmodel.UIConfigurationViewModel


@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ALARMS_ROUTE.name,
) {
    val actions = remember(navController) { NavigationActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(
            route = ALARMS_ROUTE.name,
        ) { backStackEntry: NavBackStackEntry ->
            AlarmScreen(
                onAlarmClick = { alarmId ->
                    actions.navigateToSetAlarmScreen(alarmId, backStackEntry)
                },
            )
        }

        navigation(
            startDestination = "${ALARM_ROUTE.name}/{${ALARM_ID_KEY.name}}",
            route = ALARM_DETAILS_GRAPH.name
        ) {
            composable(
                route = "${ALARM_ROUTE.name}/{${ALARM_ID_KEY.name}}",
                arguments = listOf(
                    navArgument(ALARM_ID_KEY.name) {
                        type = NavType.LongType
                    }
                ),
                enterTransition = enterTransition,
                exitTransition = { null },
                popEnterTransition = { null },
                popExitTransition = exitTransition
            ) { backStackEntry: NavBackStackEntry ->
                val parentBackStackEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(ALARM_DETAILS_GRAPH.name)
                }

                val viewModel = hiltViewModel<AlarmDetailsViewModel>(parentBackStackEntry)

                val rootEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(ALARMS_ROUTE.name)
                }
                val configViewModel = hiltViewModel<UIConfigurationViewModel>(rootEntry)

                val selectedSound = navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.get<String?>(SOUND_ID_KEY.name)

                selectedSound?.let { file ->
                    viewModel.emitSoundFile(file)
                }

                AlarmDetailsScreen(
                    onSelectSoundClicked = { sound ->
                        actions.navigateToSelectSoundScreen(
                            sound,
                            backStackEntry
                        )
                    },
                    viewModel = viewModel,
                    configViewModel = configViewModel,
                    onUpClick = { actions.navigateUp(backStackEntry) },
                )
            }

            composable(
                route = "${SELECT_SOUND_ROUTE.name}/{${SOUND_ID_KEY.name}}",
                arguments = listOf(
                    navArgument(SOUND_ID_KEY.name) {
                        type = NavType.StringType
                    }
                ),
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) { backStackEntry: NavBackStackEntry ->
                SoundSelectionScreen(
                    onUpClick = { actions.navigateUp(backStackEntry) },
                    onSoundSelected = { selectedSound ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(SOUND_ID_KEY.name, selectedSound.soundFile)
                    }
                )
            }
        }
    }
}