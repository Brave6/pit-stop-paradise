package com.seth.pitstopparadise

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)


        // Hide bottom nav on specific fragments
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.isVisible = destination.id != R.id.getStartedFragment
        }

        // Check if we should show GetStartedFragment
        val showGetStarted = intent.getBooleanExtra("SHOW_GET_STARTED", false)
        if (showGetStarted) {
            // Navigate to GetStartedFragment
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.getStartedFragment)
        }
    }
}
