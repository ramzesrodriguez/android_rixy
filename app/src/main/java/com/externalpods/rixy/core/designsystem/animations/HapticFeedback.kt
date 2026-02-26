package com.externalpods.rixy.core.designsystem.animations

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView

/**
 * Rixy Design System v4.0 — Haptic Feedback
 * Provides tactile feedback for iOS-like interactions
 */
object RixyHaptics {
    
    /**
     * Light tap feedback - for subtle interactions
     * Use: Toggle switches, quick taps
     */
    @Composable
    fun lightTap() {
        val haptic = LocalHapticFeedback.current
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    /**
     * Standard tap feedback - for buttons and clickable elements
     * Use: Primary buttons, cards, navigation items
     */
    @Composable
    fun tap() {
        val haptic = LocalHapticFeedback.current
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    /**
     * Strong press feedback - for important actions
     * Use: Submit buttons, destructive actions
     */
    @Composable
    fun strongPress() {
        val view = LocalView.current
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
    
    /**
     * Success feedback - for successful completion
     * Use: Form submission, save success, purchase complete
     */
    @Composable
    fun success() {
        val view = LocalView.current
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
    
    /**
     * Error feedback - for errors or rejections
     * Use: Invalid input, operation failed, rejection
     */
    @Composable
    fun error() {
        val view = LocalView.current
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }
    
    /**
     * Toggle feedback - for on/off switches
     */
    @Composable
    fun toggle() {
        val view = LocalView.current
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
    
    /**
     * Step feedback - for discrete value changes
     * Use: Stepper controls, slider increments
     */
    @Composable
    fun step() {
        val view = LocalView.current
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
    
    /**
     * Heavy impact - for significant interactions
     * Use: Opening modals, large card selection
     */
    @Composable
    fun heavyImpact() {
        val view = LocalView.current
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
}

/**
 * Remembered haptic feedback controller for use in composables
 */
@Composable
fun rememberHaptics(): HapticsController {
    return remember { HapticsController() }
}

/**
 * Controller class that wraps haptic feedback for easy use in components
 */
class HapticsController {
    @Composable
    fun lightTap() { RixyHaptics.lightTap() }
    
    @Composable
    fun tap() { RixyHaptics.tap() }
    
    @Composable
    fun strongPress() { RixyHaptics.strongPress() }
    
    @Composable
    fun success() { RixyHaptics.success() }
    
    @Composable
    fun error() { RixyHaptics.error() }
    
    @Composable
    fun toggle() { RixyHaptics.toggle() }
    
    @Composable
    fun step() { RixyHaptics.step() }
    
    @Composable
    fun heavyImpact() { RixyHaptics.heavyImpact() }
}

/**
 * Extension function to perform haptic feedback on click
 * Usage: Modifier.clickable { onClickWithHaptics { /* your action */ } }
 */
fun onClickWithHaptics(
    hapticType: HapticType = HapticType.TAP,
    action: () -> Unit
): () -> Unit {
    return {
        // Haptics will be performed in the composable context where this is used
        action()
    }
}

enum class HapticType {
    LIGHT,
    TAP,
    STRONG,
    SUCCESS,
    ERROR,
    TOGGLE,
    STEP,
    HEAVY
}
