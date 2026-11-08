package com.trackmyboot.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trackmyboot.app.R
import com.trackmyboot.app.domain.model.ProductCategory
import com.trackmyboot.app.presentation.viewmodel.AlertsViewModel
import com.trackmyboot.app.presentation.viewmodel.DetailViewModel
import com.trackmyboot.app.presentation.viewmodel.HomeViewModel
import com.trackmyboot.app.presentation.viewmodel.SearchViewModel
import com.trackmyboot.app.presentation.viewmodel.SettingsViewModel
import com.trackmyboot.app.presentation.viewmodel.WatchlistViewModel
import com.trackmyboot.app.ui.screens.AlertsScreen
import com.trackmyboot.app.ui.screens.DetailScreen
import com.trackmyboot.app.ui.screens.HomeScreen
import com.trackmyboot.app.ui.screens.SearchScreen
import com.trackmyboot.app.ui.screens.SettingsScreen
import com.trackmyboot.app.ui.screens.WatchlistScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val watchlistViewModel: WatchlistViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val alertsViewModel: AlertsViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()
                    val unreadAlertsCount by alertsViewModel.unreadAlerts.collectAsStateWithLifecycle(initialValue = 0).map { it.size }

                    NavHost(navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onSearchClick = { navController.navigate("search") },
                                onWatchlistClick = { navController.navigate("watchlist") },
                                onAlertsClick = { navController.navigate("alerts") },
                                onSettingsClick = { navController.navigate("settings") },
                                onProductClick = { productId ->
                                    navController.navigate("detail/$productId")
                                }
                            )
                        }
                        composable(
                            route = "search",
                            arguments = listOf()
                        ) {
                            SearchScreen(
                                viewModel = searchViewModel,
                                onProductClick = { productId ->
                                    navController.navigate("detail/$productId")
                                }
                            )
                        }
                        composable("watchlist") {
                            WatchlistScreen(
                                viewModel = watchlistViewModel,
                                onProductClick = { productId ->
                                    navController.navigate("detail/$productId")
                                }
                            )
                        }
                        composable("alerts") {
                            AlertsScreen(
                                viewModel = alertsViewModel,
                                onProductClick = { productId ->
                                    navController.navigate("detail/$productId")
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(viewModel = settingsViewModel)
                        }
                        composable(
                            route = "detail/{productId}",
                            arguments = listOf(androidx.navigation.navArgument("productId") { type = androidx.navigation.NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.getString()!!
                            DetailScreen(
                                viewModel = detailViewModel,
                                productId = productId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}