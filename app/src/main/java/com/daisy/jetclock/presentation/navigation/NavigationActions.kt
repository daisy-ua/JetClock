package com.daisy.jetclock.presentation.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController


class NavigationActions(navController: NavHostController) {
    val navigateToSetAlarmScreen = { alarmId: Long, from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) {
            navController.navigate("${MainDestinations.ALARM_ROUTE.name}/$alarmId")
        }
    }

    val navigateToSelectSoundScreen = { sound: String, from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) {
            navController.navigate("${MainDestinations.SELECT_SOUND_ROUTE.name}/$sound")
        }
    }

    val navigateUp: (from: NavBackStackEntry) -> Unit = { from ->
        if (from.lifecycleIsResumed()) {
            navController.navigateUp()
        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED


