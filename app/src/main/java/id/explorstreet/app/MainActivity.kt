package id.explorstreet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.explorstreet.app.data.LocalRepository
import id.explorstreet.app.ui.screens.BuddyScreen
import id.explorstreet.app.ui.screens.FeedScreen
import id.explorstreet.app.ui.screens.NavScreen
import id.explorstreet.app.ui.screens.PartnerScreen
import id.explorstreet.app.ui.screens.RouteScreen
import id.explorstreet.app.ui.screens.StopsScreen
import id.explorstreet.app.ui.theme.ExplorstreetTheme

private enum class Tab(val route: String, val label: String, val icon: ImageVector) {
    Route("route", "Rute", Icons.Default.Explore),
    Nav("nav", "Navigasi", Icons.Default.Navigation),
    Buddy("buddy", "Buddy", Icons.Default.Groups),
    Stops("stops", "Pit-stop", Icons.Default.LocalCafe),
    Feed("feed", "Feed", Icons.Default.Forum),
}
private const val PARTNER = "partner"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = LocalRepository(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AppViewModel(repo) as T
        }
        setContent {
            ExplorstreetTheme {
                App(viewModel(factory = factory))
            }
        }
    }
}

private fun NavHostController.go(route: String) = navigate(route) {
    popUpTo(Tab.Route.route) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun App(vm: AppViewModel) {
    val nav = rememberNavController()
    val ui by vm.ui.collectAsStateWithLifecycle()
    val stored by vm.stored.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val current = nav.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(Unit) { vm.toasts.collect { snackbar.currentSnackbarData?.dismiss(); snackbar.showSnackbar(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("explorstreet", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton({ nav.go(PARTNER) }) {
                        Icon(Icons.Default.Storefront, "Portal mitra",
                            tint = if (current == PARTNER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = current == t.route,
                        onClick = { nav.go(t.route) },
                        icon = { Icon(t.icon, null) },
                        label = { Text(t.label, style = MaterialTheme.typography.labelMedium) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondary),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { pad ->
        NavHost(nav, startDestination = Tab.Route.route, modifier = Modifier.padding(pad)) {
            composable(Tab.Route.route) { RouteScreen(vm, ui) { nav.go(Tab.Nav.route) } }
            composable(Tab.Nav.route) { NavScreen(vm, ui, stored) { nav.go(Tab.Feed.route) } }
            composable(Tab.Buddy.route) { BuddyScreen(vm, ui, stored) }
            composable(Tab.Stops.route) { StopsScreen(vm, ui, stored) }
            composable(Tab.Feed.route) { FeedScreen(vm, stored) }
            composable(PARTNER) { PartnerScreen(vm, stored) { nav.go(Tab.Stops.route) } }
        }
    }
}
