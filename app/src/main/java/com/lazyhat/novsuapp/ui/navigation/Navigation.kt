package com.lazyhat.novsuapp.ui.navigation

import androidx.navigation.NavHostController

enum class NavDestination {
    TimeTable,
    GroupSettings
}

class NavActions(private val navHostController: NavHostController) {
    fun navigateTo(dest: NavDestination, vararg args: String) {
        navHostController.navigate(dest.name) {
            if (dest == NavDestination.TimeTable)
                popUpTo(dest.name){
                    inclusive = true
                }
        }
    }
}