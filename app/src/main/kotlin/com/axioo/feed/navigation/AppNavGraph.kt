package com.axioo.feed.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.axioo.feed.ui.bookmarks.BookmarksScreen
import com.axioo.feed.ui.bookmarks.FocusedPitchScreen
import com.axioo.feed.ui.components.AxiooBottomBar
import com.axioo.feed.ui.components.AxiooTab
import com.axioo.feed.ui.feed.FeedScreen
import com.axioo.feed.ui.profile.ProfileScreen
import com.axioo.feed.ui.theme.AxiooSurfaceMode
import com.axioo.feed.ui.theme.AxiooTheme

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route.orEmpty()

    val tab =
        when {
            currentRoute.contains(RouteNames.FEED) -> AxiooTab.Feed
            currentRoute.contains(RouteNames.BOOKMARKS) -> AxiooTab.Bookmarks
            currentRoute.contains(RouteNames.PROFILE) -> AxiooTab.Profile
            else -> AxiooTab.Feed
        }
    val isDarkSurface = tab == AxiooTab.Feed || currentRoute.contains(RouteNames.FOCUSED_PITCH)
    val mode = if (isDarkSurface) AxiooSurfaceMode.Dark else AxiooSurfaceMode.Light
    val showBottomBar = !currentRoute.contains(RouteNames.FOCUSED_PITCH)

    AxiooTheme(mode = mode) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = navController,
                    startDestination = FeedRoute,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable<FeedRoute> {
                        FeedScreen()
                    }
                    composable<BookmarksRoute> {
                        BookmarksScreen(
                            onOpenFocused = { id ->
                                navController.navigate(FocusedPitchRoute(id.value))
                            },
                        )
                    }
                    composable<ProfileRoute> {
                        ProfileScreen(
                            onOpenFocused = { id ->
                                navController.navigate(FocusedPitchRoute(id.value))
                            },
                        )
                    }
                    composable<FocusedPitchRoute> { entry ->
                        val args = entry.toRoute<FocusedPitchRoute>()
                        FocusedPitchScreen(
                            pitchId = args.pitchId,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
            if (showBottomBar) {
                AxiooBottomBar(
                    selected = tab,
                    onSelect = { selected ->
                        val target =
                            when (selected) {
                                AxiooTab.Feed -> FeedRoute
                                AxiooTab.Bookmarks -> BookmarksRoute
                                AxiooTab.Profile -> ProfileRoute
                            }
                        navController.navigate(target) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onDarkSurface = isDarkSurface,
                )
            }
        }
    }
}
