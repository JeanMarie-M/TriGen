package com.example.trigen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.trigen.data.seeder.AcademySeeder
import com.example.trigen.navigation.AppNavHost
import com.example.trigen.ui.theme.TriGenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var academySeeder: AcademySeeder

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            academySeeder.seedIfNeeded()
        }

        enableEdgeToEdge()
        setContent {
            TriGenTheme {
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}