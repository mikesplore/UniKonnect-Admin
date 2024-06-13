package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mike.myclass.MyDatabase.getAnnouncements
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
            val present by remember { mutableIntStateOf(10) }
            val absent by remember { mutableStateOf(1) }
            val total by remember { derivedStateOf { present + absent } } // Calculate total efficiently
            val percentage by remember { derivedStateOf { (present.toFloat() / total) * 100 } }
            val brush = Brush.linearGradient(
                listOf(
                    GlobalColors.primaryColor,
                    GlobalColors.secondaryColor,
                    GlobalColors.primaryColor
                )
            )
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
                    .background(brush, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Attendance Summary",
                            style = CC.titleTextStyle(context),
                            fontSize = 20.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("Present: $present", style = CC.descriptionTextStyle(context))
                            Text("Absent: $absent", style = CC.descriptionTextStyle(context))
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlobalColors.primaryColor
                                )
                            ) {
                                Text(
                                    "Sign Attendance",
                                    style = CC.descriptionTextStyle(context),
                                    fontSize = 13.sp
                                )
                            }

                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            AttendanceProgressIndicator(percentage, context)
                        }
                    }

                }
            }
        }

        @Composable
        fun SecondBox() {
            val brush = Brush.linearGradient(
                listOf(
                    GlobalColors.secondaryColor,
                    GlobalColors.primaryColor,
                    GlobalColors.secondaryColor
                )
            )
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = GlobalColors.textColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxHeight()
                    .width(350.dp)
                    .background(brush, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Second one", style = CC.descriptionTextStyle(context))
            }
        }

        @Composable
        fun ThirdBox() {
            val brush = Brush.linearGradient(
                listOf(
                    GlobalColors.primaryColor,
                    GlobalColors.secondaryColor
                )
            )
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = GlobalColors.textColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxHeight()
                    .width(350.dp)
                    .background(brush, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Third one", style = CC.descriptionTextStyle(context))
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
                            style = CC.descriptionTextStyle(context),
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
                                        Text(" Users", style = CC.descriptionTextStyle(context))
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
                                        Text(
                                            " Assignments",
                                            style = CC.descriptionTextStyle(context)
                                        )
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
                                            Icons.AutoMirrored.Filled.Announcement,
                                            contentDescription = "",
                                            tint = GlobalColors.textColor
                                        )
                                        Text(
                                            " Announcements",
                                            style = CC.descriptionTextStyle(context)
                                        )
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
                                        Text(" Timetable", style = CC.descriptionTextStyle(context))
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
                                        Text(" Colors", style = CC.descriptionTextStyle(context))
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
                                        Text(" Logout", style = CC.descriptionTextStyle(context))
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
                    SecondBox()
                    Spacer(modifier = Modifier.width(10.dp))
                    FirstBox()
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
                                        style = CC.descriptionTextStyle(context),
                                        color = if (selectedTabIndex == index) GlobalColors.textColor else GlobalColors.tertiaryColor,
                                    )
                                }
                            }, modifier = Modifier.background(CC.backbrush)
                            )
                        }
                    }

                    when (selectedTabIndex) {
                        0 -> AnnouncementItem(context)
                        1 -> AttendanceItem()
                        2 -> TimetableItem()
                        3 -> AssignmentsItem()
                        4 -> DocumentationItem()
                        5 -> ManageUsersItem()
                        else -> {}
                    }


                }
                //the tabs column ends here
            }
        }
    }
}

@Composable
fun AnnouncementItem(context: Context) {
    var title by remember { mutableStateOf("") }
    val date = CC.CurrentDate()
    var description by remember { mutableStateOf("") }
    val author = Details.name.value
    var loading by remember { mutableStateOf(true) }
    val announcements = remember { mutableStateListOf<Announcement>() }
    LaunchedEffect(Unit) {
        getAnnouncements { fetchedAnnouncements ->
            announcements.addAll(fetchedAnnouncements ?: emptyList())
            loading = false
        }
    }

    Spacer(modifier = Modifier.height(10.dp))


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Latest Announcement",
            style = CC.descriptionTextStyle(context = context),
            fontWeight = FontWeight.Bold
        )
        Column(
            modifier = Modifier
                .background(Color.Transparent, RoundedCornerShape(10.dp))
                .padding(10.dp)
                .fillMaxWidth(0.9f)
                .height(200.dp)
                .border(
                    width = 1.dp,
                    color = GlobalColors.textColor,
                    shape = RoundedCornerShape(10.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loading) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent, RoundedCornerShape(10.dp))
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = GlobalColors.secondaryColor,
                        trackColor = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Fetching data...", style = CC.descriptionTextStyle(context))
                }

            } else if (announcements.isNotEmpty()) {
                val firstAnnouncement = announcements[announcements.lastIndex]
                Box(modifier = Modifier.background(Color.Transparent, RoundedCornerShape(10.dp))) {
                    Image(
                        painter = painterResource(R.drawable.announcement),
                        contentDescription = "", // Provide a meaningful content description
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop // Fill the Box while maintaining aspect ratio
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        // Title row
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                firstAnnouncement.title,
                                style = CC.titleTextStyle(context),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        // Content column with vertical scrolling
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .fillMaxHeight(1f)
                                .background(
                                    GlobalColors.primaryColor.copy(alpha = 0.5f),
                                    RoundedCornerShape(10.dp)
                                )  // Adding a background color for better contrast
                        ) {
                            // Author and date row
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween  // Space items evenly across the row
                            ) {
                                Text(
                                    firstAnnouncement.author,
                                    style = CC.descriptionTextStyle(context),
                                    // Adding color for better visual separation
                                )
                                Text(
                                    firstAnnouncement.date,
                                    style = CC.descriptionTextStyle(context),
                                )
                            }

                            // Description column
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.Start  // Align text to the start (left)
                            ) {
                                Text(
                                    firstAnnouncement.description,
                                    style = CC.descriptionTextStyle(context),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(8.dp)  // Adding padding around the text
                                )
                            }
                        }
                    }
                }

            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No announcements available",
                        style = CC.descriptionTextStyle(context)
                    ) // Handle the case of an empty list
                }

            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Make Quick Announcement",
            style = CC.descriptionTextStyle(context),
            fontWeight = FontWeight.Bold
        )



        Box(modifier = Modifier.fillMaxHeight(1f)) {
            Image(
                painter = painterResource(R.drawable.write),
                contentDescription = "Write Icon", // Provide a meaningful content description
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .padding(10.dp), // Add padding around the image
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(10.dp)
                    .border(
                        width = 1.dp,
                        color = GlobalColors.textColor,
                        shape = RoundedCornerShape(10.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image now inside the Column


                Spacer(modifier = Modifier.height(10.dp))
                QuickInput(
                    modifier = Modifier.width(50.dp),
                    value = title,
                    label = "Title",
                    singleLine = true,
                    onValueChange = {
                        title = it
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                QuickInput(
                    Modifier
                        .height(200.dp)
                        .width(50.dp),
                    value = description,
                    label = "Description",
                    singleLine = false,
                    onValueChange = { description = it },
                )
                Row(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            loading = true
                            if (title.isEmpty() && description.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please enter a title and description",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading = false
                            } else {


                                val newAnnouncement = Announcement(
                                    author = Details.name.value,
                                    date = date,
                                    title = title,
                                    description = description,

                                    )

                                MyDatabase.writeAnnouncement(newAnnouncement)
                                showNotification(
                                    context,
                                    title = title,
                                    message = description
                                )
                                title = ""
                                description = ""

                                Toast.makeText(context, "Announcement posted", Toast.LENGTH_SHORT)
                                    .show()
                                getAnnouncements { fetchedAnnouncements ->
                                    announcements.clear()
                                    announcements.addAll(fetchedAnnouncements ?: emptyList())
                                }
                                loading = false
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GlobalColors.primaryColor,
                            contentColor = GlobalColors.textColor
                        )
                    ) {
                        Text("Post & Refresh")
                    }
                }
            }

        }
    }
}

@Composable
fun QuickInput(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    singleLine: Boolean,
    onValueChange: (String) -> Unit

) {
    TextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .padding(10.dp)
            .width(250.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GlobalColors.primaryColor,
            unfocusedLabelColor = GlobalColors.primaryColor,
            focusedIndicatorColor = GlobalColors.textColor,
            unfocusedContainerColor = Color.Transparent,
            unfocusedTextColor = GlobalColors.textColor,
            focusedTextColor = GlobalColors.textColor,
            focusedLabelColor = GlobalColors.primaryColor,
            unfocusedIndicatorColor = GlobalColors.textColor
        ),
        singleLine = singleLine
    )


}

@Composable
fun AttendanceItem() {
    Text("Attendance")
}

@Composable
fun TimetableItem() {
    Text("Timetable")
}

@Composable
fun AssignmentsItem() {
    Text("Assignments")
}

@Composable
fun DocumentationItem() {
    Text("Documentation")
}

@Composable
fun ManageUsersItem() {
    Text("Manage Users")

}

@Composable
fun AttendanceProgressIndicator(progress: Float, context: Context) {
    val color = when {
        progress < 30 -> Color.Red
        progress < 70 -> Color.Yellow
        else -> Color.Green
    }

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = {
                progress / 100f // Directly use progress as a float (0-1)
            },
            modifier = Modifier.size(130.dp),
            color = color,
            strokeWidth = 10.dp,
            trackColor = GlobalColors.tertiaryColor,
            strokeCap = StrokeCap.Round
        )
        Text(
            text = "${progress.toInt()}%",
            style = CC.titleTextStyle(context)
        )
    }
}


@Preview
@Composable
fun DashboardPreview() {
    Dashboard(navController = rememberNavController(), LocalContext.current)
}

