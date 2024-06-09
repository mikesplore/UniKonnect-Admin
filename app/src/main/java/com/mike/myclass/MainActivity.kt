package com.mike.myclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mike.myclass.ui.theme.MyClassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationMap()
        }
    }

    @Composable
    fun NavigationMap(){
        val navController = rememberNavController()
        val context = LocalContext.current
        NavHost(navController = navController, startDestination = "login"){
            composable("login"){
                LoginScreen(navController, context)
            }

            composable("announcements"){
                AnnouncementsScreen(navController, context)
            }
            composable("passwordreset"){
                PasswordReset(navController, context)
            }
            composable("dashboard"){
                Dashboard(navController, context)
            }
            composable("moredetails"){
                MoreDetails(context, navController)
            }

        }
    }
}

