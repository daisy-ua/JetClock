package com.daisy.jetclock.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daisy.jetclock.navigation.MainDestinations.ALARMS_ROUTE
import com.daisy.jetclock.navigation.MainDestinations.ALARM_ID_KEY
import com.daisy.jetclock.navigation.MainDestinations.ALARM_ROUTE
import com.daisy.jetclock.navigation.MainDestinations.SELECT_SOUND_ROUTE
import com.daisy.jetclock.ui.screens.AlarmScreen
import com.daisy.jetclock.ui.screens.SelectSoundScreen
import com.daisy.jetclock.ui.screens.SetAlarmScreen
import com.daisy.jetclock.viewmodels.AlarmViewModel
import com.daisy.jetclock.viewmodels.NewAlarmViewModel


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

        composable(
            route = "${ALARM_ROUTE.name}/{${ALARM_ID_KEY.name}}",
            arguments = listOf(
                navArgument(ALARM_ID_KEY.name) {
                    type = NavType.IntType
                }
            ),
            enterTransition = enterTransition,
            exitTransition = { null },
            popEnterTransition = { null },
            popExitTransition = exitTransition
        ) { backStackEntry: NavBackStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val currentAlarmId = arguments.getLong(ALARM_ID_KEY.name)
            SetAlarmScreen(
                alarmId = currentAlarmId,
                onSelectSoundClicked = { actions.navigateToSelectSoundScreen(backStackEntry) },
                onUpClick = { actions.navigateUp(backStackEntry) }
            )
        }

        composable(
            route = SELECT_SOUND_ROUTE.name,
            enterTransition = enterTransition,
            exitTransition = exitTransition
        ) { backStackEntry: NavBackStackEntry ->
            SelectSoundScreen(
                onUpClick = { actions.navigateUp(backStackEntry) }
            )
        }
    }
}
