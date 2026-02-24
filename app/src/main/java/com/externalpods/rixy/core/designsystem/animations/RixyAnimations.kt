package com.externalpods.rixy.core.designsystem.animations

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Rixy Design System v4.0 — Animation Specifications
 * Based on iOS animation curves and timing
 */
object RixyAnimations {
    
    // ============================================================================
    // Durations (in milliseconds)
    // ============================================================================
    
    /** iOS default animation duration */
    const val DEFAULT_DURATION = 150
    
    /** Quick feedback animations */
    const val QUICK_DURATION = 100
    
    /** Emphasis animations */
    const val EMPHASIS_DURATION = 200
    
    /** Page transitions */
    const val TRANSITION_DURATION = 300
    
    // ============================================================================
    // Spring Animations (Physical, natural feel)
    // ============================================================================
    
    /**
     * Default spring - balanced response
     * Use for: Cards, buttons, general UI elements
     */
    fun <T> springDefault(): SpringSpec<T> = spring(
        stiffness = 300f,
        dampingRatio = 0.8f
    )
    
    /**
     * Bouncy spring - playful feel
     * Use for: Success states, celebrations, playful interactions
     */
    fun <T> springBouncy(): SpringSpec<T> = spring(
        stiffness = 400f,
        dampingRatio = 0.6f
    )
    
    /**
     * Gentle spring - subtle movement
     * Use for: Subtle feedback, icon animations
     */
    fun <T> springGentle(): SpringSpec<T> = spring(
        stiffness = 200f,
        dampingRatio = 0.9f
    )
    
    /**
     * Snappy spring - quick response
     * Use for: Press states, quick toggles
     */
    fun <T> springSnappy(): SpringSpec<T> = spring(
        stiffness = 500f,
        dampingRatio = 0.85f
    )
    
    /**
     * Press animation spring - iOS button feel
     * stiffness: 400, damping: 0.85 gives that iOS button press feel
     */
    fun <T> springPress(): SpringSpec<T> = spring(
        stiffness = 400f,
        dampingRatio = 0.85f
    )
    
    // ============================================================================
    // Tween Animations (Time-based, predictable)
    // ============================================================================
    
    /**
     * Ease out - natural deceleration
     * iOS curve: Cubic(0.25, 0.1, 0.25, 1.0)
     */
    fun <T> easeOut(durationMillis: Int = DEFAULT_DURATION) = tween<T>(
        durationMillis = durationMillis,
        easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
    )
    
    /**
     * Ease in - acceleration start
     */
    fun <T> easeIn(durationMillis: Int = DEFAULT_DURATION) = tween<T>(
        durationMillis = durationMillis,
        easing = CubicBezierEasing(0.42f, 0f, 1f, 1f)
    )
    
    /**
     * Ease in-out - smooth both ways
     */
    fun <T> easeInOut(durationMillis: Int = DEFAULT_DURATION) = tween<T>(
        durationMillis = durationMillis,
        easing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
    )
    
    /**
     * iOS-style navigation transition
     */
    fun <T> navigationTransition(durationMillis: Int = TRANSITION_DURATION) = tween<T>(
        durationMillis = durationMillis,
        easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)
    )
    
    // ============================================================================
    // Float Animation Presets
    // ============================================================================
    
    /** Scale values for press animations */
    object PressScale {
        const val DEFAULT = 1f
        const val PRESSED = 0.96f
        const val ICON_PRESSED = 0.9f
    }
    
    /** Alpha values for state transitions */
    object Alpha {
        const val VISIBLE = 1f
        const val PARTIAL = 0.6f
        const val DISABLED = 0.38f
        const val INVISIBLE = 0f
    }
}

/**
 * Helper function to get the appropriate spring spec based on use case
 */
inline fun <reified T> animatedValue(
    useCase: AnimationUseCase = AnimationUseCase.DEFAULT
): SpringSpec<T> = when (useCase) {
    AnimationUseCase.PRESS -> RixyAnimations.springPress()
    AnimationUseCase.BOUNCE -> RixyAnimations.springBouncy()
    AnimationUseCase.GENTLE -> RixyAnimations.springGentle()
    AnimationUseCase.SNAPPY -> RixyAnimations.springSnappy()
    else -> RixyAnimations.springDefault()
}

enum class AnimationUseCase {
    DEFAULT,
    PRESS,
    BOUNCE,
    GENTLE,
    SNAPPY
}
