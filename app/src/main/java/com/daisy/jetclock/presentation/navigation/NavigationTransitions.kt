package com.daisy.jetclock.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry


private const val TRANSITION_DURATION = 300

val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
    {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(TRANSITION_DURATION)
        )
    }

val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
    {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(TRANSITION_DURATION)
        )
    }
