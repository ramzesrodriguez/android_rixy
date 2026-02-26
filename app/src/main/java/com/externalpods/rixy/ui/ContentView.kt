package com.externalpods.rixy.ui

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.externalpods.rixy.feature.admin.dashboard.AdminDashboardScreen
import com.externalpods.rixy.feature.owner.dashboard.OwnerDashboardScreen
import com.externalpods.rixy.feature.user.main.UserTabBarView
import com.externalpods.rixy.navigation.AppMode
import com.externalpods.rixy.navigation.AppStateViewModel
import org.koin.java.KoinJavaComponent.get

/**
 * ContentView - Main entry point (mirrors iOS ContentView)
 * 
 * Switches between User/Owner/Admin modes based on AppState
 */
@Composable
fun ContentView() {
    val viewModel: AppStateViewModel = remember {
        get(AppStateViewModel::class.java)
    }
    val currentMode by viewModel.currentMode
    
    Crossfade(targetState = currentMode, label = "mode_switch") { mode ->
        when (mode) {
            AppMode.USER -> UserTabBarView(appState = viewModel)
            AppMode.OWNER -> OwnerDashboardScreen(
                onBackToUser = { viewModel.switchMode(AppMode.USER) }
            )
            AppMode.ADMIN -> AdminDashboardScreen()
        }
    }
}
