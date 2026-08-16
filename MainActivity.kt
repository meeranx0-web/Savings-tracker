package com.savingstracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.savingstracker.app.ui.screens.HistoryScreen
import com.savingstracker.app.ui.screens.HomeScreen
import com.savingstracker.app.ui.screens.SavingsScreen
import com.savingstracker.app.ui.screens.SettingsScreen
import com.savingstracker.app.ui.theme.SavingsTrackerTheme
import com.savingstracker.app.viewmodel.SavingsViewModel

private sealed class Tab(val route: String, val label: String) {
    data object Home : Tab("home", "Home")
    data object Savings : Tab("savings", "Savings")
    data object History : Tab("history", "History")
    data object Settings : Tab("settings", "Settings")
}

private val tabs = listOf(Tab.Home, Tab.Savings, Tab.History, Tab.Settings)

class MainActivity : ComponentActivity() {
    private val viewModel: SavingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val showResetDialog by viewModel.showResetDialog.collectAsState()

            SavingsTrackerTheme(themeMode = uiState.themeMode) {
                Surface {
                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val backStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = backStackEntry?.destination

                                tabs.forEach { tab ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(tab.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = when (tab) {
                                                    Tab.Home -> Icons.Filled.Home
                                                    Tab.Savings -> Icons.Filled.Savings
                                                    Tab.History -> Icons.Filled.History
                                                    Tab.Settings -> Icons.Filled.Settings
                                                },
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = { Text(tab.label) }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Tab.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Tab.Home.route) {
                                val todays = viewModel.todaysBoxes(uiState.boxes)
                                val smartPick = viewModel.smartPickSuggestion(uiState.boxes)
                                HomeScreen(
                                    boxes = uiState.boxes,
                                    summary = uiState.summary,
                                    todaysBoxes = todays,
                                    smartPick = smartPick,
                                    onSmartPickSave = { viewModel.saveSmartPick(it) }
                                )
                            }
                            composable(Tab.Savings.route) {
                                SavingsScreen(
                                    boxes = uiState.boxes,
                                    onToggle = { viewModel.toggleBox(it) }
                                )
                            }
                            composable(Tab.History.route) {
                                HistoryScreen(
                                    boxes = uiState.boxes,
                                    groupedByDate = viewModel.groupByDate(uiState.boxes)
                                )
                            }
                            composable(Tab.Settings.route) {
                                SettingsScreen(
                                    themeMode = uiState.themeMode,
                                    deadline = uiState.deadline,
                                    showResetDialog = showResetDialog,
                                    onThemeChange = { viewModel.setThemeMode(it) },
                                    onRequestReset = { viewModel.requestReset() },
                                    onConfirmReset = { viewModel.confirmReset() },
                                    onCancelReset = { viewModel.cancelReset() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
