package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.mike.myclass.CommonComponents as CC


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(navController: NavController, context: Context) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subjectName by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var loading by remember { mutableStateOf(true) }
    val subjects = remember { mutableStateListOf<Subjects>() }
    var assignmentDialog by remember { mutableStateOf(false) }
    var showaddSubject by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }


    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }), // Slide in from right
        exit = slideOutHorizontally(targetOffsetX = { -it }) // Slide out to left
    ) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Assignments", style = CC.titleTextStyle) }, navigationIcon = {
                IconButton(onClick = {navController.navigate("dashboard")}) {
                    Icon(Icons.Default.ArrowBackIosNew, "Back", tint = GlobalColors.textColor)
                }
            }, actions = {
                IconButton(onClick = {
                    loading = true
                    MyDatabase.getSubjects { fetchedSubjects ->
                        subjects.clear()
                        subjects.addAll(fetchedSubjects ?: emptyList())
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
                            Icon(Icons.Default.AddCircleOutline,"Add unit",
                                tint = GlobalColors.textColor)
                            Spacer(modifier = Modifier.width(5.dp))
                        Text("Add Unit", style = CC.descriptionTextStyle)

                        }},
                        onClick = {
                            showaddSubject = true
                            expanded = false
                        })
                    DropdownMenuItem(text = {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.AddCircleOutline,"Add subject",
                                tint = GlobalColors.textColor)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Add Subject", style = CC.descriptionTextStyle)

                        }
                    }, onClick = {
                        assignmentDialog = true
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
                MyDatabase.getSubjects { fetchedSubjects ->
                    subjects.clear()
                    subjects.addAll(fetchedSubjects ?: emptyList())
                    loading = false
                }
            }


            val indicator = @Composable { tabPositions: List<TabPosition> ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(4.dp)
                        .width(screenWidth / (subjects.size.coerceAtLeast(1))) // Avoid division by zero
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
                    Text("Loading Units", style = CC.descriptionTextStyle)

                }

            } else {

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.background(Color.LightGray),
                    contentColor = Color.Black,
                    indicator = indicator,
                    edgePadding = 0.dp,
                    containerColor = GlobalColors.primaryColor
                ) {
                    subjects.forEachIndexed { index, subject ->

                        Tab(selected = selectedTabIndex == index, onClick = {
                            selectedTabIndex = index
                            coroutineScope.launch {
                                // Load assignments for the selected subject
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
                                    text = subject.name,
                                    color = if (selectedTabIndex == index) GlobalColors.textColor else GlobalColors.tertiaryColor,
                                )
                            }
                        }, modifier = Modifier.background(GlobalColors.primaryColor)
                        )
                    }
                }

                when (selectedTabIndex) {
                    in subjects.indices -> {
                        AssignmentsList(subjectId = subjects[selectedTabIndex].id, context)
                    }
                }
            }

            if (showaddSubject) {
                BasicAlertDialog(onDismissRequest = { showaddSubject = false }) {
                    Column(
                        modifier = Modifier
                            .width(250.dp)
                            .background(
                                color = GlobalColors.primaryColor, shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Add Subject",
                            style = CC.titleTextStyle,
                            color = GlobalColors.textColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = subjectName,
                            onValueChange = { subjectName = it },
                            label = { Text("Subject Name", color = GlobalColors.tertiaryColor) },
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = GlobalColors.tertiaryColor,
                                focusedTextColor = GlobalColors.textColor,
                                unfocusedTextColor = GlobalColors.textColor,
                                focusedContainerColor = GlobalColors.primaryColor,
                                unfocusedContainerColor = GlobalColors.primaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { showaddSubject = false },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlobalColors.secondaryColor,
                                    contentColor = GlobalColors.primaryColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    "Cancel",
                                    style = CC.descriptionTextStyle,
                                    color = GlobalColors.primaryColor
                                )
                            }
                            Button(
                                onClick = {
                                    MyDatabase.writeSubject(subject = Subjects(
                                        name = subjectName
                                    ), onComplete = {
                                        Toast.makeText(
                                            context, "Subject Added", Toast.LENGTH_SHORT
                                        ).show()
                                        showaddSubject = false
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
                                    style = CC.descriptionTextStyle,
                                    color = GlobalColors.primaryColor
                                )
                            }
                        }
                    }
                }
            }



            if (assignmentDialog) {
                BasicAlertDialog(onDismissRequest = { assignmentDialog = false }) {
                    Column(
                        modifier = Modifier
                            .width(250.dp)
                            .background(
                                color = GlobalColors.primaryColor, shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Add Assignment",
                            style = CC.titleTextStyle,
                            color = GlobalColors.textColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = title,
                            onValueChange = { title = it },
                            label = { Text("Assignment Name", color = GlobalColors.tertiaryColor) },
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = GlobalColors.tertiaryColor,
                                focusedTextColor = GlobalColors.textColor,
                                unfocusedTextColor = GlobalColors.textColor,
                                focusedContainerColor = GlobalColors.primaryColor,
                                unfocusedContainerColor = GlobalColors.primaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = description,
                            onValueChange = { description = it },
                            label = { Text("Description", color = GlobalColors.tertiaryColor) },
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = GlobalColors.tertiaryColor,
                                focusedTextColor = GlobalColors.textColor,
                                unfocusedTextColor = GlobalColors.textColor,
                                focusedContainerColor = GlobalColors.primaryColor,
                                unfocusedContainerColor = GlobalColors.primaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { assignmentDialog = false },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlobalColors.secondaryColor,
                                    contentColor = GlobalColors.primaryColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    "Cancel",
                                    style = CC.descriptionTextStyle,
                                    color = GlobalColors.primaryColor
                                )
                            }
                            Button(
                                onClick = {
                                    MyDatabase.writeAssignment(assignment = Assignment(
                                        subjectId = subjects[selectedTabIndex].id,
                                        name = title,
                                        description = description
                                    ), onComplete = {
                                        Toast.makeText(
                                            context, "Assignment Added", Toast.LENGTH_SHORT

                                        ).show()
                                        assignmentDialog = false
                                        showNotification(
                                            context,
                                            title = "New Assignment",
                                            message = "${Details.name.value} added an assignment.  "
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
                                    style = CC.descriptionTextStyle,
                                    color = GlobalColors.primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AssignmentsList(subjectId: String, context: Context) {
    var assignments by remember { mutableStateOf<List<Assignment>?>(null) }
    LaunchedEffect(subjectId) {
        MyDatabase.getAssignments(subjectId) { fetchedAssignments ->
            assignments = fetchedAssignments
        }
    }

    if (assignments == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = GlobalColors.secondaryColor,
                trackColor = GlobalColors.textColor
            )
            Text("Loading Assignments...Please wait", style = CC.descriptionTextStyle)
        }
    } else {
        LazyColumn {
            if (assignments!!.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No assignments found.", style = CC.descriptionTextStyle)
                    }
                }
            }
            items(assignments!!) { assignment ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                        animationSpec = tween(500)
                    ),
                    exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(
                        animationSpec = tween(500)
                    )
                ) {
                    AssignmentCard(assignment = assignment, onEdit = {
                        MyDatabase.editAssignment(it) { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(
                                    context, "Assignment Edited", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context, "Failed to edit assignment", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }, onDelete = {
                        MyDatabase.deleteAssignment(it) { isSuccess ->
                            if (isSuccess) {
                                assignments = assignments?.filter { it.id != assignment.id }
                                Toast.makeText(
                                    context, "Assignment Deleted", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context, "Failed to delete assignment", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
                }
            }
        }
    }
}


@Composable
fun AssignmentCard(
    assignment: Assignment, onEdit: (Assignment) -> Unit = {}, onDelete: (String) -> Unit = {}
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(assignment.name) }
    var editedDescription by remember { mutableStateOf(assignment.description) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), colors = CardDefaults.cardColors(
            containerColor = GlobalColors.secondaryColor, contentColor = GlobalColors.textColor
        ), elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isEditing) {
                    OutlinedTextField(value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Assignment Name", color = GlobalColors.tertiaryColor) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = GlobalColors.primaryColor,
                            unfocusedIndicatorColor = GlobalColors.tertiaryColor,
                            focusedTextColor = GlobalColors.textColor,
                            unfocusedTextColor = GlobalColors.textColor,
                            focusedContainerColor = GlobalColors.primaryColor,
                            unfocusedContainerColor = GlobalColors.primaryColor
                        )
                    )
                } else {
                    Text(
                        text = assignment.name,
                        style = CC.titleTextStyle.copy(fontSize = 18.sp),
                        color = GlobalColors.textColor
                    )
                }
                Row {
                    IconButton(
                        onClick = {
                            if (isEditing) {
                                // Save the edited assignment
                                isSaving = true
                                val updatedAssignment = assignment.copy(
                                    name = editedName,
                                    description = editedDescription
                                )
                                MyDatabase.editAssignment(updatedAssignment) { isSuccess ->
                                    isSaving = false
                                    if (isSuccess) {
                                        onEdit(updatedAssignment)
                                        isEditing = false
                                    } else {
                                        errorMessage = "Failed to save changes. Please try again."
                                    }
                                }
                            } else {
                                isEditing = true
                            }
                        }, modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save Assignment" else "Edit Assignment",
                            tint = GlobalColors.textColor
                        )
                    }
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                // Cancel the edit
                                editedName = assignment.name
                                editedDescription = assignment.description
                                isEditing = false
                                errorMessage = null
                            }, modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cancel Edit",
                                tint = GlobalColors.primaryColor
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { onDelete(assignment.id) }, modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Assignment",
                                tint = GlobalColors.textColor
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Author: ${Details.name.value}",
                style = CC.descriptionTextStyle,
                color = GlobalColors.tertiaryColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isEditing) {
                OutlinedTextField(value = editedDescription,
                    onValueChange = { editedDescription = it },
                    label = { Text("Description", color = GlobalColors.tertiaryColor) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = GlobalColors.primaryColor,
                        unfocusedIndicatorColor = GlobalColors.tertiaryColor,
                        focusedTextColor = GlobalColors.textColor,
                        unfocusedTextColor = GlobalColors.textColor,
                        focusedContainerColor = GlobalColors.primaryColor,
                        unfocusedContainerColor = GlobalColors.primaryColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = assignment.description,
                    style = CC.descriptionTextStyle,
                    color = GlobalColors.textColor
                )
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


@Preview
@Composable
fun AssignmentScreenPreview() {
    AssignmentScreen(rememberNavController(), LocalContext.current)
}
