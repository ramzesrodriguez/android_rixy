package com.externalpods.rixy.ui

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.feature.admin.dashboard.AdminDashboardScreen
import com.externalpods.rixy.feature.owner.dashboard.OwnerDashboardScreen
import com.externalpods.rixy.feature.user.main.UserTabBarViewV2
import com.externalpods.rixy.navigation.AppMode
import com.externalpods.rixy.navigation.AppStateViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * ContentView - Main entry point (mirrors iOS ContentView)
 * 
 * Switches between User/Owner/Admin modes based on AppState
 */
@Composable
fun ContentView(
    viewModel: AppStateViewModel = koinViewModel()
) {
    val currentMode by viewModel.currentMode
    
    Crossfade(targetState = currentMode, label = "mode_switch") { mode ->
        when (mode) {
            AppMode.USER -> UserTabBarViewV2()
            AppMode.OWNER -> OwnerDashboardScreen()
            AppMode.ADMIN -> AdminDashboardScreen()
        }
    }
}
