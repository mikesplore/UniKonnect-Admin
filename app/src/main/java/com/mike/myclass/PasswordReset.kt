package com.mike.myclass

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mike.myclass.CommonComponents as CC



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordReset(navController: NavController,context: Context) {
    var email by remember { mutableStateOf("") }
    val message by remember { mutableStateOf("") }
    val auth: FirebaseAuth = Firebase.auth

    Scaffold(
        topBar = {
            
            TopAppBar(title = { Text("Password Reset", style = CC.titleTextStyle, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor,)
                )
        },
        containerColor = GlobalColors.primaryColor
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CC.SingleLinedTextField(
                value = email,
                onValueChange = { it -> email = it },
                label = "Email",
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Password reset email sent to $email",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to send password reset email.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            ) {
                Text("Send Reset Email", style = CC.descriptionTextStyle)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(message)
        }
    }
}

@Preview
@Composable
fun MyPrev(){
    PasswordReset(rememberNavController(), LocalContext.current)
}