package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(navController: NavController, context: Context) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }), // Slide in from right
        exit = slideOutHorizontally(targetOffsetX = { -it }) // Slide out to left
    ) {
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
                    .border(
                        width = 1.dp,
                        color = GlobalColors.textColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(10.dp),
                        clip = true
                    )
                    .fillMaxHeight()
                    .width(350.dp)
                    .background(GlobalColors.secondaryColor, RoundedCornerShape(10.dp)),
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
                    .background(GlobalColors.tertiaryColor, RoundedCornerShape(10.dp)),
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
                    .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp)),
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
                        animationSpec = tween(
                            durationMillis = boxScrollDuration,
                            easing = EaseInOut
                        )
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
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = GlobalColors.textColor
                            )
                        }
                        DropdownMenu(
                            expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(GlobalColors.primaryColor)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Icon(
                                            Icons.Default.ManageAccounts,
                                            contentDescription = "",
                                            tint = GlobalColors.textColor
                                        )
                                        Text(" Users", style = CC.descriptionTextStyle)
                                    }
                                },
                                onClick = {
                                    navController.navigate("students")
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Icon(
                                            Icons.Default.AssignmentInd,
                                            contentDescription = "",
                                            tint = GlobalColors.textColor
                                        )
                                        Text(" Assignments", style = CC.descriptionTextStyle)
                                    }
                                },
                                onClick = {
                                    navController.navigate("assignments")
                                    expanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Icon(
                                            Icons.Default.Announcement,
                                            contentDescription = "",
                                            tint = GlobalColors.textColor
                                        )
                                        Text(" Announcements", style = CC.descriptionTextStyle)
                                    }
                                },
                                onClick = {
                                    navController.navigate("announcements")
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = "",
                                            tint = GlobalColors.textColor
                                        )
                                        Text(" Timetable", style = CC.descriptionTextStyle)
                                    }
                                },
                                onClick = {
                                    navController.navigate("timetable")
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Icon(
                                            Icons.Default.ManageAccounts,
                                            contentDescription = "",
                                            tint = GlobalColors.textColor
                                        )
                                        Text(" Colors", style = CC.descriptionTextStyle)
                                    }
                                },
                                onClick = {
                                    navController.navigate("colors")
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = "",
                                            tint = GlobalColors.textColor
                                        )
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GlobalColors.primaryColor,
                        titleContentColor = GlobalColors.textColor
                    )
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
                //the tabs column starts here
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    var selectedTabIndex by remember { mutableIntStateOf(0) }
                    val configuration = LocalConfiguration.current
                    val screenWidth = configuration.screenWidthDp.dp
                    val tabRowHorizontalScrollState by remember { mutableStateOf(ScrollState(0)) }
                    val tabTitles = listOf(
                        "Announcements",
                        "Attendance",
                        "Timetable",
                        "Assignments",
                        "Manage Students",
                        "Documentation",
                    )
                    val indicator = @Composable { tabPositions: List<TabPosition> ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .height(4.dp)
                                .width(screenWidth / tabTitles.size) // Divide by the number of tabs
                                .background(GlobalColors.textColor, CircleShape)
                        )
                    }
                    val coroutineScope = rememberCoroutineScope()

                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.background(GlobalColors.secondaryColor),
                        contentColor = GlobalColors.primaryColor,
                        indicator = indicator,
                        edgePadding = 0.dp,

                        ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(selected = selectedTabIndex == index, onClick = {
                                selectedTabIndex = index
                                coroutineScope.launch {
                                    tabRowHorizontalScrollState.animateScrollTo(
                                        (screenWidth.value / tabTitles.size * index).toInt()
                                    )
                                }
                            }, text = {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (selectedTabIndex == index) GlobalColors.primaryColor else GlobalColors.secondaryColor,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp), contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = title,
                                        style = CC.descriptionTextStyle,
                                        color = if (selectedTabIndex == index) GlobalColors.textColor else GlobalColors.tertiaryColor,
                                    )
                                }
                            }, modifier = Modifier.background(CC.backbrush)
                            )
                        }
                    }

                    when (selectedTabIndex) {
                        0 -> AttendanceItem()
                        1 -> TimetableItem()
                        2 -> AssignmentsItem()
                        3 -> DocumentationItem()
                        4 -> ManageUsersItem()
                        else -> {}
                    }


                }
                //the tabs column ends here
            }
        }
    }
}
@Composable
fun AttendanceItem(){
    Text("Attendance")
}
@Composable
fun TimetableItem(){
    Text("Timetable")
}
@Composable
fun AssignmentsItem(){
    Text("Assignments")
}
@Composable
fun DocumentationItem(){
    Text("Documentation")
}
@Composable
fun ManageUsersItem(){
    Text("Manage Users")

}

@Preview
@Composable
fun DashboardPreview() {
    Dashboard(navController = rememberNavController(), LocalContext.current)
}

