package com.mike.myclass

import android.content.Context
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.time.LocalTime
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(navController: NavController, context: Context) {
    fun getGreetingMessage(): String {
        val currentTime = LocalTime.now()
        return when (currentTime.hour) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    var expanded by remember { mutableStateOf(false) }
    val horizontalScrollState = rememberScrollState()

    @Composable
    fun FirstBox() {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(350.dp)
                .background(GlobalColors.secondaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text("This is the first Box", style = CC.descriptionTextStyle)
        }
    }

    @Composable
    fun SecondBox() {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(350.dp)
                .background(GlobalColors.tertiaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text("Second one", style = CC.descriptionTextStyle)
        }
    }

    @Composable
    fun ThirdBox() {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(350.dp)
                .background(GlobalColors.primaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text("Third one", style = CC.descriptionTextStyle)
        }
    }

    val totalDuration = 10000
    val delayDuration = 5000L
    val boxCount = 3
    val boxScrollDuration = (totalDuration / boxCount)

    LaunchedEffect(Unit) {
        while (true) {
            for (i in 0 until boxCount) {
                val targetScrollPosition = i * (horizontalScrollState.maxValue / (boxCount - 1))
                horizontalScrollState.animateScrollTo(
                    targetScrollPosition,
                    animationSpec = tween(durationMillis = boxScrollDuration, easing = EaseInOut)
                )
                delay(delayDuration)
            }
            horizontalScrollState.scrollTo(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${getGreetingMessage()}, ${Details.name.value}",
                        style = CC.descriptionTextStyle,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu", tint = GlobalColors.textColor)
                    }
                    DropdownMenu(expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(GlobalColors.primaryColor)) {
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "", tint = GlobalColors.textColor)
                                    Text(" Logout", style = CC.descriptionTextStyle)
                                }
                            },
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login")
                                expanded = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor, titleContentColor = GlobalColors.textColor)
            )
        },
        containerColor = GlobalColors.primaryColor,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(CC.backbrush)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .requiredHeight(200.dp)
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                FirstBox()
                Spacer(modifier = Modifier.width(10.dp))
                SecondBox()
                Spacer(modifier = Modifier.width(10.dp))
                ThirdBox()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                // Additional content here
            }
        }
    }
}

@Preview
@Composable
fun DashboardPreview() {
    Dashboard(navController = rememberNavController(), LocalContext.current)
}
