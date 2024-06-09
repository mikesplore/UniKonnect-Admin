package com.mike.myclass

import android.content.Context
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mike.myclass.ui.theme.RobotoMono
import kotlinx.coroutines.delay
import java.time.LocalTime
import com.mike.myclass.CommonComponents as CC
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(navController: NavController, context: Context) {
    fun getGreetingMessage(): String {
        val currentTime = LocalTime.now()
        return when (currentTime.hour) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Night"
        }
    }

    var expanded by remember { mutableStateOf(true) }
    var announcements by remember { mutableStateOf<List<Announcement>?>(null) }
    var users by remember { mutableStateOf<List<User>?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        MyDatabase.getAnnouncements { fetchedAnnouncements ->
            announcements = fetchedAnnouncements ?: emptyList()
        }
        MyDatabase.getUsers { fetchedUsers ->
            users = fetchedUsers
            loading = false
        }
    }

    val horizontalScrollState = rememberScrollState()
    val boxes = listOf(
        R.drawable.announcement to "date" to "announcements",
        R.drawable.attendance to "Have you updated attendance sheet?" to "RecordAttendance",
        R.drawable.assignment to "assignment" to "assignments",
        R.drawable.timetable to "timetable" to "timetable"
    )

    val totalDuration = 10000
    val delayDuration = 5000L
    val boxCount = boxes.size
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
            TopAppBar(title = {
                Text(
                    "${getGreetingMessage()}, ${Details.name.value}",
                    style = CC.descriptionTextStyle,
                    fontSize = 20.sp
                )
            }, actions = {
                Icon(imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = GlobalColors.textColor,
                    modifier = Modifier.clickable { expanded = !expanded })
                DropdownMenu(expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(GlobalColors.primaryColor)
                ) {

                    DropdownMenuItem(
                        text = {
                            Row {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "", tint = GlobalColors.textColor )
                                Text(" Logout", style = CC.descriptionTextStyle)}
                        }, onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login")

                            expanded = false
                        }
                    )

                }

            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GlobalColors.primaryColor,
                titleContentColor = GlobalColors.textColor,

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
            // Combine the two boxes into one
            Box(
                modifier = Modifier

                    .background(CC.backbrush, RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                    .fillMaxWidth()
                    .height(200.dp)
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

                    boxes.forEach { item ->
                        TopBoxes(
                            image = painterResource(id = item.first.first),
                            description = item.first.second,
                            route = item.second,
                            navController = navController,
                            context = context
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                }
                Box(
                    modifier = Modifier
                        .shadow(
                            ambientColor = Color.Red, elevation = 0.dp, spotColor = Color.Blue
                        )
                        .fillMaxWidth(0.9f)
                        .align(Alignment.Center)
                        .height(200.dp)
                        .offset(y = 130.dp)
                        .background(GlobalColors.secondaryColor, RoundedCornerShape(20.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 20.dp,
                                clip = true,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = DefaultShadowColor,
                                spotColor = Color.Transparent
                            )
                            .fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .background(CC.backbrush)
                                .fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                ActionImages(
                                    icon = Icons.Filled.AddAlert,
                                    title = "Alerts",
                                    route = "alerts",
                                    navController = navController
                                )
                                ActionImages(
                                    icon = Icons.Filled.AssignmentInd,
                                    title = "Assignments",
                                    route = "menu",
                                    navController = navController
                                )
                                ActionImages(
                                    icon = Icons.Filled.ChatBubble,
                                    title = "Add",
                                    route = "addstudent",
                                    navController = navController
                                )


                            }
                            Row(
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                ActionImages(
                                    icon = Icons.Filled.Colorize,
                                    title = "Colors",
                                    route = "colorsettings",
                                    navController = navController
                                )
                                ActionImages(
                                    icon = Icons.Filled.Attachment,
                                    title = "Users",
                                    route = "students",
                                    navController = navController
                                )
                                ActionImages(
                                    icon = Icons.Filled.Schedule,
                                    title = "Timetable",
                                    route = "menu",
                                    navController = navController
                                )

                            }
                        }
                    }
                }

            }

            Column(
                modifier = Modifier

                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(135.dp))
                //column starts here

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Upcoming Events",
                        style = CC.descriptionTextStyle,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )

                    //upcoming events here
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (!loading) {
                            announcements?.take(3)?.forEach { announcement ->
                                FirstAnnouncement(announcement = announcement)
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .width(350.dp)
                                    .height(150.dp)
                                    .background(
                                        GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = GlobalColors.tertiaryColor,
                                        shape = RoundedCornerShape(15.dp)
                                    ),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = GlobalColors.primaryColor
                                )
                                Text("Fetching data...", style = CC.descriptionTextStyle)
                            }
                        }

                    }


                    //Users here
                    Text(
                        "Users",
                        style = CC.descriptionTextStyle,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                    Row(
                        modifier = Modifier
                            .height(230.dp)
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())

                    ) {
                        if (!loading) {
                            users?.forEach { user ->
                                Profile(
                                    user = user, route = "profile", navController = navController
                                )
                            }
                        } else {
                            for (i in 1..3) {
                                Column(
                                    modifier = Modifier
                                        .absolutePadding(10.dp)
                                        .width(150.dp)
                                        .fillMaxHeight()
                                        .background(
                                            GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = GlobalColors.tertiaryColor,
                                            shape = RoundedCornerShape(15.dp)
                                        ),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = GlobalColors.primaryColor
                                    )
                                    Text("Fetching data...", style = CC.descriptionTextStyle)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun ActionImages(icon: ImageVector, title: String, route: String, navController: NavController) {
    Column(modifier = Modifier
        .clickable { navController.navigate(route) }
        .fillMaxHeight()
        .width(100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .background(GlobalColors.secondaryColor, CircleShape)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = GlobalColors.textColor)
        }

        Text(title, style = CC.descriptionTextStyle)
    }
}


@Composable
fun TopBoxes(
    image: Painter,
    description: String,
    route: String,
    navController: NavController,
    context: Context
) {
    Row(modifier = Modifier
        .clickable {
            navController.navigate(route)
        }
        .background(Color.Transparent, shape = RoundedCornerShape(30.dp))
        .fillMaxHeight()
        .width(350.dp)) {
        Box(modifier = Modifier) {
            LaunchedEffect(Unit) {
                GlobalColors.currentScheme = GlobalColors.loadColorScheme(context)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .fillMaxSize()
            ) {
                Image(
                    painter = image,
                    contentDescription = "sample",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter) // Position at the bottom
                        .background(
                            GlobalColors.secondaryColor.copy(alpha = 0.3f), // Semi-transparent black background
                            shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                        )
                        .padding(16.dp),
                ) {
                    Text(
                        text = description,
                        color = GlobalColors.textColor,
                        style = TextStyle(
                            fontFamily = RobotoMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        ),
                    )
                }
            }
        }
    }
}


@Composable
fun Profile(user: User, route: String, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .width(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Add shadow
        shape = RoundedCornerShape(20.dp) // Maintain rounded corners
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GlobalColors.primaryColor), // Change background color
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center content vertically
        ) {
            // Image in a Circle with a border
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(60.dp)
                    .background(GlobalColors.tertiaryColor, CircleShape)
                    .border(2.dp, GlobalColors.textColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.student),
                    contentDescription = "Student image",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(2.dp)
                )
            }

            Text(
                text = user.name,
                style = CC.descriptionTextStyle,  // Use titleTextStyle for prominence
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )

            // "View Profile" Button with rounded corners and padding
            Button(
                onClick = { navController.navigate(route) },
                modifier = Modifier.fillMaxWidth(0.8f),

                colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.secondaryColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("View ", style = CC.descriptionTextStyle) // Customize button text style
            }
        }
    }
}


@Composable
fun FirstAnnouncement(announcement: Announcement) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .width(350.dp)
            .clickable {},
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(15.dp) // Add rounded corners
    ) {
        Column(
            modifier = Modifier
                .background(GlobalColors.secondaryColor) // Use a more contrasting color for the background
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Image(
                    painter = painterResource(id = R.drawable.student),
                    contentDescription = "Announcement Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp),
                )
                Text(
                    text = announcement.title,
                    style = CC.descriptionTextStyle,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // Add spacing between title and description

            Text(
                text = announcement.description,
                style = CC.descriptionTextStyle.copy(fontSize = 14.sp), // Adjust font size for description
                color = GlobalColors.textColor.copy(alpha = 0.8f), // Slightly transparent text
                maxLines = 2, // Limit the number of lines displayed
                overflow = TextOverflow.Ellipsis // Show ellipsis if text overflows
            )

            // Add a "Read More" button if the description is too long
            if (announcement.description.length > 100) { // Example threshold
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Navigate to full announcement details */ },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor)
                ) {
                    Text("Read More", style = CC.descriptionTextStyle)
                }
            }
        }
    }
}

@Preview
@Composable
fun DashboardPreview() {
    Dashboard(navController = rememberNavController(), LocalContext.current)


}

