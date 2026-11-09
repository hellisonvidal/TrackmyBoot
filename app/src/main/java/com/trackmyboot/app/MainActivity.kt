package com.trackmyboot.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trackmyboot.app.data.catalog.ClubCatalog
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.ui.AppViewModel
import com.trackmyboot.app.ui.ClubsViewModel
import com.trackmyboot.app.ui.components.FitText
import com.trackmyboot.app.ui.OffersViewModel
import com.trackmyboot.app.ui.screens.ClubHeader
import com.trackmyboot.app.ui.screens.ClubsScreen
import com.trackmyboot.app.ui.screens.HomeScreen
import com.trackmyboot.app.ui.screens.OffersContent
import com.trackmyboot.app.ui.screens.SearchScreen
import com.trackmyboot.app.ui.screens.SettingsScreen
import com.trackmyboot.app.ui.screens.WatchlistScreen
import com.trackmyboot.app.ui.theme.TrackmyBootTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as TrackmyBootApp).container
        setContent { TrackmyBootTheme { AppRoot(container) } }
    }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "Início", Icons.Filled.Home),
    Tab("clubs", "Clubes", Icons.Filled.Shield),
    Tab("search", "Buscar", Icons.Filled.Search),
    Tab("watchlist", "Alertas", Icons.Filled.NotificationsActive),
    Tab("settings", "Configurações", Icons.Filled.Settings),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot(container: AppContainer) {
    val nav = rememberNavController()
    val app: AppViewModel = viewModel { AppViewModel(container) }
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val watched by app.watched.collectAsStateWithLifecycle()

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    val isTab = tabs.any { it.route == route }
    val title = when {
        route == "home" -> "TrackmyBoot"
        route?.startsWith("category") == true -> backStack?.arguments?.getString("id")?.let { id -> Category.entries.firstOrNull { it.name == id }?.label }
        route?.startsWith("club") == true && route != "clubs" -> "Ofertas do clube"
        else -> tabs.firstOrNull { it.route == route }?.label
    } ?: "TrackmyBoot"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (!isTab) IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar") }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = route == tab.route,
                        onClick = { nav.goToTab(tab.route) },
                        icon = {
                            if (tab.route == "watchlist" && watched.isNotEmpty()) {
                                BadgedBox(badge = { Badge { Text("${watched.size}") } }) { Icon(tab.icon, null) }
                            } else {
                                Icon(tab.icon, null)
                            }
                        },
                        label = { FitText(tab.label) },
                        alwaysShowLabel = true,
                    )
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") {
                HomeScreen(
                    app,
                    onCategory = { nav.navigate("category/${it.name}") },
                    onClub = { nav.navigate("club/${it.id}") },
                    onPickClubs = { nav.goToTab("clubs") },
                )
            }
            composable("clubs") {
                val vm: ClubsViewModel = viewModel { ClubsViewModel(app) }
                ClubsScreen(app, vm, onClub = { nav.navigate("club/${it.id}") })
            }
            composable("search") {
                val vm: OffersViewModel = viewModel(key = "search") { OffersViewModel(container, null, null) }
                SearchScreen(app, vm)
            }
            composable("watchlist") { WatchlistScreen(app) }
            composable("settings") { SettingsScreen(app) }
            composable("category/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                val category = Category.valueOf(entry.arguments?.getString("id") ?: Category.CAMISAS.name)
                val vm: OffersViewModel = viewModel(key = "category-$category") { OffersViewModel(container, null, category) }
                OffersContent(app, vm, showCategoryChips = false)
            }
            composable("club/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                val club = ClubCatalog.find(entry.arguments?.getString("id").orEmpty()) ?: ClubCatalog.clubs.first()
                val settings by app.settings.collectAsStateWithLifecycle()
                val vm: OffersViewModel = viewModel(key = "club-${club.id}") { OffersViewModel(container, club, Category.CAMISAS) }
                OffersContent(
                    app, vm, showCategoryChips = true,
                    header = { ClubHeader(club, club.id in settings.favoriteClubs) { app.toggleFavorite(club) } },
                )
            }
        }
    }
}

private fun NavHostController.goToTab(route: String) = navigate(route) {
    popUpTo(graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
}
