package com.daisy.jetclock.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daisy.jetclock.presentation.navigation.MainDestinations.ALARMS_ROUTE
import com.daisy.jetclock.presentation.navigation.MainDestinations.ALARM_ID_KEY
import com.daisy.jetclock.presentation.navigation.MainDestinations.ALARM_ROUTE
import com.daisy.jetclock.presentation.navigation.MainDestinations.SELECT_SOUND_ROUTE
import com.daisy.jetclock.presentation.ui.screens.AlarmScreen
import com.daisy.jetclock.presentation.ui.screens.SelectSoundScreen
import com.daisy.jetclock.presentation.ui.screens.AlarmDetailsScreen


@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ALARMS_ROUTE.name,
) {
    val actions = remember(navController) { NavigationActions(navController) }
    var viewModelStoreOwner: ViewModelStoreOwner? = null

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
                    type = NavType.LongType
                }
            ),
            enterTransition = enterTransition,
            exitTransition = { null },
            popEnterTransition = { null },
            popExitTransition = exitTransition
        ) { backStackEntry: NavBackStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val currentAlarmId = arguments.getLong(ALARM_ID_KEY.name)
            viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }
            AlarmDetailsScreen(
                alarmId = currentAlarmId,
                onSelectSoundClicked = { actions.navigateToSelectSoundScreen(backStackEntry) },
                onUpClick = { actions.navigateUp(backStackEntry) },
                soundViewModel = hiltViewModel(viewModelStoreOwner!!)
            )
        }

        composable(
            route = SELECT_SOUND_ROUTE.name,
            enterTransition = enterTransition,
            exitTransition = exitTransition
        ) { backStackEntry: NavBackStackEntry ->
            SelectSoundScreen(
                onUpClick = { actions.navigateUp(backStackEntry) },
                viewModel = hiltViewModel(viewModelStoreOwner!!),
            )
        }
    }
}
