package com.example.gyme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.gyme.navigation.NavGraph
import com.example.gyme.theme.GymeTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val user = remember<com.example.gyme.core.model.User?> { com.example.gyme.util.SessionManager.loadSession(context) }
                    val startDestination = if (user != null) "home" else com.example.gyme.navigation.Screen.Onboarding.route
                    
                    val navController = rememberNavController()
                    NavGraph(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}