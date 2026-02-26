package com.externalpods.rixy.core.designsystem.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavBackStackEntry
import com.externalpods.rixy.core.designsystem.animations.RixyAnimations

/**
 * Rixy Navigation Transitions - iOS-style transitions
 */
object RixyNavigationTransitions {
    
    private const val NAVIGATION_DURATION = 300
    private val NavigationEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)
    
    val pushEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION / 2,
                easing = NavigationEasing
            )
        )
    }
    
    val popExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION / 2,
                delayMillis = NAVIGATION_DURATION / 2,
                easing = NavigationEasing
            )
        )
    }
    
    val popEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 3 },
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        )
    }
    
    val pushExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 3 },
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION / 2,
                easing = NavigationEasing
            )
        )
    }
    
    val modalEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION / 2,
                easing = NavigationEasing
            )
        )
    }
    
    val modalExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION / 2,
                easing = NavigationEasing
            )
        )
    }
    
    val fadeEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(
            animationSpec = tween(
                durationMillis = RixyAnimations.DEFAULT_DURATION,
                easing = NavigationEasing
            )
        )
    }
    
    val fadeExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(
            animationSpec = tween(
                durationMillis = RixyAnimations.DEFAULT_DURATION,
                easing = NavigationEasing
            )
        )
    }
    
    val scaleEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION / 2,
                easing = NavigationEasing
            )
        )
    }
    
    val scaleExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION,
                easing = NavigationEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = NAVIGATION_DURATION / 2,
                easing = NavigationEasing
            )
        )
    }
}

object SwipeBackGesture {
    const val COMPLETION_THRESHOLD = 0.5f
    const val VELOCITY_THRESHOLD = 300f
}
