
package com.ryffztech.predictiveback.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.ryffztech.predictiveback.ui.home.HomeScreen
import com.ryffztech.predictiveback.ui.gallery.GalleryScreen
import com.ryffztech.predictiveback.ui.settings.SettingsScreen

private const val NAV_DURATION_MS = 380
private const val BACK_SCREEN_SCALE = 0.92f

@Composable
fun MainNavigation(
    hasPermission: Boolean = false,
    onRequestPermission: () -> Unit = {}
) {
    val navController = rememberNavController()

    val currentEntry by navController.currentBackStackEntryAsState()

    val previousRoute = remember(currentEntry) {
        navController.previousBackStackEntry
            ?.destination
            ?.route
    }

    NavHost(
        navController = navController,
        startDestination = "home",

        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),

        // NEXT SCREEN: masuk dari kanan sambil zoom IN
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(NAV_DURATION_MS)
            ) + scaleIn(
                initialScale = BACK_SCREEN_SCALE,
                animationSpec = tween(NAV_DURATION_MS)
            )
        },

        // PREVIOUS SCREEN: bergeser ke kiri sambil zoom OUT
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it * 30 / 100 },
                animationSpec = tween(NAV_DURATION_MS)
            ) + scaleOut(
                targetScale = BACK_SCREEN_SCALE,
                animationSpec = tween(NAV_DURATION_MS)
            )
        },

        // Pop ditangani SwipeBackContainer
        popEnterTransition = {
            EnterTransition.None
        },

        popExitTransition = {
            ExitTransition.None
        }
    ) {

        composable("home") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HomeScreen(
                    hasPermission = hasPermission,
                    onRequestPermission = onRequestPermission,
                    onNavigate = { destination ->
                        navController.navigate(destination)
                    }
                )
            }
        }

        composable(
            route = "gallery",
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            SwipeBackContainer(
                onBack = {
                    navController.popBackStack()
                },
                previousContent = {
                    PreviousScreen(
                        route = previousRoute,
                        hasPermission = hasPermission,
                        onRequestPermission = onRequestPermission
                    )
                }
            ) { animateBack ->
                GalleryScreen(
                    onBack = animateBack
                )
            }
        }

        composable(
            route = "settings",
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            SwipeBackContainer(
                onBack = {
                    navController.popBackStack()
                },
                previousContent = {
                    PreviousScreen(
                        route = previousRoute,
                        hasPermission = hasPermission,
                        onRequestPermission = onRequestPermission
                    )
                }
            ) { animateBack ->
                SettingsScreen(
                    onBack = animateBack
                )
            }
        }
    }
}

@Composable
private fun PreviousScreen(
    route: String?,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (route) {
            "home" -> {
                HomeScreen(
                    hasPermission = hasPermission,
                    onRequestPermission = onRequestPermission,
                    onNavigate = {}
                )
            }

            "gallery" -> {
                GalleryScreen(
                    onBack = {}
                )
            }

            "settings" -> {
                SettingsScreen(
                    onBack = {}
                )
            }
        }
    }
}
