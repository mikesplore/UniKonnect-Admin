package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.mike.myclass.CommonComponents as CC


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(navController: NavController, context: Context) {
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var lecturer by remember { mutableStateOf("") }
    var unitName by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(CC.currentDayID() - 1) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var loading by remember { mutableStateOf(true) }
    val days = remember { mutableStateListOf<Day>() }
    var timetableDialog by remember { mutableStateOf(false) }
    var showaddDay by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Timetable", style = CC.titleTextStyle(context)) }, navigationIcon = {
                IconButton(onClick = {navController.navigate("dashboard")}) {
                    Icon(Icons.Default.ArrowBackIosNew, "Back", tint = GlobalColors.textColor)
                }
            }, actions = {
                IconButton(onClick = {
                    loading = true
                    MyDatabase.getDays { fetchedDays ->
                        days.clear()
                        days.addAll(fetchedDays ?: emptyList())
                        loading = false
                    }
                }) {
                    Icon(Icons.Default.Refresh, "Refresh", tint = GlobalColors.textColor)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, "Add", tint = GlobalColors.textColor)
                }
                DropdownMenu(
                    onDismissRequest = { expanded = false },
                    expanded = expanded,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = GlobalColors.textColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(GlobalColors.primaryColor)
                ) {
                    DropdownMenuItem(text = {
                        Row(modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.AddCircleOutline,"Add Day",
                                tint = GlobalColors.textColor)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Add day", style = CC.descriptionTextStyle(context))

                        }},
                        onClick = {
                            showaddDay = true
                            expanded = false
                        })
                    DropdownMenuItem(text = {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.AddCircleOutline,"Add timetable",
                                tint = GlobalColors.textColor)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Add Timetable", style = CC.descriptionTextStyle(context))

                        }
                    }, onClick = {
                        timetableDialog = true
                        expanded = false
                    })
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
                .fillMaxSize(),
        ) {

            LaunchedEffect(Unit) {
                MyDatabase.getDays { fetchedDays ->
                    days.clear()
                    days.addAll(fetchedDays ?: emptyList())
                    loading = false
                }
            }


            val indicator = @Composable { tabPositions: List<TabPosition> ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(4.dp)
                        .width(screenWidth / (days.size.coerceAtLeast(1))) // Avoid division by zero
                        .background(GlobalColors.secondaryColor, CircleShape)
                )
            }

            val coroutineScope = rememberCoroutineScope()

            if (loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = GlobalColors.secondaryColor,
                        trackColor = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Loading Days...Please wait", style = CC.descriptionTextStyle(context))

                }

            } else {

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.background(GlobalColors.primaryColor),
                    contentColor = Color.Black,
                    indicator = indicator,
                    edgePadding = 0.dp,
                    containerColor = GlobalColors.primaryColor
                ) {
                    days.forEachIndexed { index, day ->

                        Tab(selected = selectedTabIndex == index, onClick = {
                            selectedTabIndex = index
                            coroutineScope.launch {
                                //load days
                            }
                        }, text = {

                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTabIndex == index) GlobalColors.secondaryColor else GlobalColors.primaryColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.name,
                                    color = if (selectedTabIndex == index) GlobalColors.textColor else GlobalColors.tertiaryColor,
                                )
                            }
                        }, modifier = Modifier.background(GlobalColors.primaryColor)
                        )
                    }
                }

                when (selectedTabIndex) {
                    in days.indices -> {
                        DayList(dayid = days[selectedTabIndex].id, context)
                    }
                }
            }

            if (showaddDay) {
                BasicAlertDialog(onDismissRequest = { showaddDay = false }) {
                    Column(
                        modifier = Modifier
                            .width(250.dp)
                            .background(
                                color = GlobalColors.primaryColor, shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Add Day",
                            style = CC.titleTextStyle(context),
                            color = GlobalColors.textColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomOutlinedTextField(
                            value = day,
                            onValueChange = {day = it}
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { showaddDay = false },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlobalColors.secondaryColor,
                                    contentColor = GlobalColors.primaryColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    "Cancel",
                                    style = CC.descriptionTextStyle(context),
                                    color = GlobalColors.primaryColor
                                )
                            }
                            Button(
                                onClick = {
                                    MyDatabase.writeDays(day = Day(
                                        name = day
                                    ), onComplete = {
                                        Toast.makeText(
                                            context, "Day Added", Toast.LENGTH_SHORT
                                        ).show()
                                        showaddDay = false
                                    })
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlobalColors.secondaryColor,
                                    contentColor = GlobalColors.primaryColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    "Add",
                                    style = CC.descriptionTextStyle(context),
                                    color = GlobalColors.primaryColor
                                )
                            }
                        }
                    }
                }
            }



            if (timetableDialog) {
                BasicAlertDialog(onDismissRequest = { timetableDialog = false }) {
                    Column(
                        modifier = Modifier
                            .width(250.dp)
                            .background(
                                color = GlobalColors.primaryColor, shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Add Timetable",
                            style = CC.titleTextStyle(context),
                            color = GlobalColors.textColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomOutlinedTextField(
                            value = unitName,
                            label = "Unit name",
                            onValueChange = { it -> unitName = it },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomOutlinedTextField(
                            value = venue,
                            label = "Venue",
                            onValueChange = { it -> venue = it },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomOutlinedTextField(
                            value = lecturer,
                            label = "Lecturer",
                            onValueChange = { it -> lecturer = it },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomOutlinedTextField(
                            value = startTime,
                            label = "Start time",
                            onValueChange = { it -> startTime = it },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomOutlinedTextField(
                            value = endTime,
                            label = "End time",
                            onValueChange = { it -> endTime = it },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { timetableDialog = false },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlobalColors.secondaryColor,
                                    contentColor = GlobalColors.primaryColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    "Cancel",
                                    style = CC.descriptionTextStyle(context),
                                    color = GlobalColors.primaryColor
                                )
                            }
                            Button(
                                onClick = {
                                    MyDatabase.writeTimetable(timetable = Timetable(
                                        dayId = days[selectedTabIndex].id,
                                        unitName = unitName,
                                        lecturer = lecturer,
                                        venue = venue,
                                        startTime = startTime,
                                        endTime = endTime
                                    ), onComplete = {
                                        Toast.makeText(
                                            context, "Timetable item Added", Toast.LENGTH_SHORT

                                        ).show()
                                        timetableDialog = false
                                        showNotification(
                                            context,
                                            title = "New Timetable Item",
                                            message = "${Details.name.value} added an Event.  "
                                        )
                                    })
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlobalColors.secondaryColor,
                                    contentColor = GlobalColors.primaryColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    "Add",
                                    style = CC.descriptionTextStyle(context),
                                    color = GlobalColors.primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun DayList(dayid: String, context: Context) {
    var timetables by remember { mutableStateOf<List<Timetable>?>(null) }
    LaunchedEffect(dayid) {
        MyDatabase.getTimetable(dayid) { fetchedTimetable ->
            timetables = fetchedTimetable
        }
    }

    if (timetables == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = GlobalColors.secondaryColor,
                trackColor = GlobalColors.textColor
            )
            Text("Loading Events...Please wait", style = CC.descriptionTextStyle(context))
            Text("If this takes longer, please check your internet connection", style = CC.descriptionTextStyle(context), textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn {
            if (timetables!!.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No event found.", style = CC.descriptionTextStyle(context))
                    }
                }
            }
            items(timetables!!) { timetable ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                        animationSpec = tween(500)
                    ),
                    exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(
                        animationSpec = tween(500)
                    )
                ) {
                    TimetableCard(timetable = timetable, onEdit = {
                        MyDatabase.editTimetable(it) { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(
                                    context, "Timetable Edited", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context, "Failed to edit timetable", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }, onDelete = {
                        MyDatabase.deleteTimetable(it) { isSuccess ->
                            if (isSuccess) {
                                timetables = timetables?.filter { it.id != timetable.id }
                                Toast.makeText(
                                    context, "Event Deleted", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context, "Failed to delete Event", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }, context = context)
                }
            }
        }
    }
}


@Composable
fun TimetableCard(
    timetable: Timetable,
    onEdit: (Timetable) -> Unit = {},
    onDelete: (String) -> Unit = {},
    context: Context
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedUnitName by remember { mutableStateOf(timetable.unitName) }
    var editedVenue by remember { mutableStateOf(timetable.venue) }
    var editedStartTime by remember { mutableStateOf(timetable.startTime) }
    var editedEndTime by remember { mutableStateOf(timetable.endTime) }
    var editedLecturer by remember { mutableStateOf(timetable.lecturer) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlobalColors.secondaryColor,
            contentColor = GlobalColors.textColor
        ),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = GlobalColors.textColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isEditing) {
                    CustomOutlinedTextField(
                        value = editedUnitName,
                        label = "Unit Name",
                        onValueChange = { editedUnitName = it },
                    )
                } else {
                    Text(
                        text = timetable.unitName,
                        style = CC.titleTextStyle(context).copy(fontSize = 18.sp),
                        color = GlobalColors.textColor
                    )
                }
                Row {
                    IconButton(
                        onClick = {
                            if (isEditing) {
                                isSaving = true
                                val updatedTimetable = timetable.copy(
                                    unitName = editedUnitName,
                                    venue = editedVenue,
                                    lecturer = editedLecturer,
                                    startTime = editedStartTime,
                                    endTime = editedEndTime
                                )
                                MyDatabase.editTimetable(updatedTimetable) { isSuccess ->
                                    isSaving = false
                                    if (isSuccess) {
                                        onEdit(updatedTimetable)
                                        isEditing = false
                                    } else {
                                        errorMessage = "Failed to save changes. Please try again."
                                    }
                                }
                            } else {
                                isEditing = true
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save Event" else "Edit Event",
                            tint = GlobalColors.textColor
                        )
                    }
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                editedLecturer = timetable.lecturer
                                editedVenue = timetable.venue
                                editedEndTime = timetable.endTime
                                editedStartTime = timetable.startTime
                                editedUnitName = timetable.unitName

                                isEditing = false
                                errorMessage = null
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Edit",
                                tint = GlobalColors.primaryColor
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { onDelete(timetable.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Event",
                                tint = GlobalColors.textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                CustomOutlinedTextField(
                    value = editedVenue,
                    label = "Venue",
                    onValueChange = { editedVenue = it },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = "Venue", tint = GlobalColors.textColor)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomOutlinedTextField(
                    value = editedLecturer,
                    label = "Lecturer",
                    onValueChange = { editedLecturer = it },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Lecturer", tint = GlobalColors.textColor)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomOutlinedTextField(
                    value = editedStartTime,
                    label = "Start Time",
                    onValueChange = { editedStartTime = it },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = "Start Time", tint = GlobalColors.textColor)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomOutlinedTextField(
                    value = editedEndTime,
                    label = "End Time",
                    onValueChange = { editedEndTime = it },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = "End Time", tint = GlobalColors.textColor)
                    }
                )
            } else {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Venue", tint = GlobalColors.textColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timetable.venue,
                            style = CC.descriptionTextStyle(context),
                            color = GlobalColors.textColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "Lecturer", tint = GlobalColors.textColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timetable.lecturer,
                            style = CC.descriptionTextStyle(context),
                            color = GlobalColors.textColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = "Time", tint = GlobalColors.textColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${timetable.startTime} - ${timetable.endTime}",
                            style = CC.descriptionTextStyle(context),
                            color = GlobalColors.textColor
                        )
                    }
                }
            }

            if (isSaving) {
                CircularProgressIndicator(
                    color = GlobalColors.textColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = GlobalColors.textColor,
            unfocusedIndicatorColor = GlobalColors.tertiaryColor,
            focusedTextColor = GlobalColors.textColor,
            unfocusedTextColor = GlobalColors.textColor,
            focusedContainerColor = GlobalColors.primaryColor,
            unfocusedContainerColor = GlobalColors.primaryColor,
            focusedLabelColor = GlobalColors.textColor,
            unfocusedLabelColor = GlobalColors.textColor
        ),
        modifier = modifier
    )
}


@Preview
@Composable
fun TimetableScreenPreview() {
    //TimetableScreen(rememberNavController(), LocalContext.current)
    TimetableCard(
        timetable = Timetable(
            lecturer = "Michael",
            startTime = "11:30",
            endTime = "12:30",
            venue = "Here"),
        {},{},LocalContext.current
        )

}
