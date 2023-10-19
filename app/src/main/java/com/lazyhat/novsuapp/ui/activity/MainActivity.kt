package com.lazyhat.novsuapp.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.lazyhat.novsuapp.ui.navigation.NavActions
import com.lazyhat.novsuapp.ui.navigation.NavDestination
import com.lazyhat.novsuapp.ui.screens.groupsettings.GroupSettingsScreen
import com.lazyhat.novsuapp.ui.screens.timetable.TimeTableScreen
import com.lazyhat.novsuapp.ui.theme.NovsuTimeTableTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NovsuTimeTableTheme {
                MainScreen()
            }
        }
    }
}


@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    startDestination: NavDestination = NavDestination.TimeTable,
    navActions: NavActions = NavActions(navController)
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    Surface {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        "Menu",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = Bold
                    )
                    Spacer(Modifier.height(50.dp))
                    NavigationDrawerItem(
                        label = { Text("Timetable") },
                        selected = backStackEntry?.destination?.route == NavDestination.TimeTable.name,
                        onClick = { navActions.navigateTo(NavDestination.TimeTable); scope.launch { drawerState.close() } })
                    NavigationDrawerItem(
                        label = { Text("Group settings") },
                        selected = backStackEntry?.destination?.route == NavDestination.GroupSettings.name,
                        onClick = { navActions.navigateTo(NavDestination.GroupSettings); scope.launch { drawerState.close() } })
                }
            }) {
            NavHost(navController = navController, startDestination.name) {
                composable(NavDestination.TimeTable.name) {
                    TimeTableScreen { scope.launch { drawerState.open() } }
                }
                composable(NavDestination.GroupSettings.name) {
                    GroupSettingsScreen {
                        scope.launch { drawerState.open() }
                    }
                }
            }
        }
    }
}