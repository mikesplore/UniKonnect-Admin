package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
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
import kotlinx.coroutines.launch
import com.mike.myclass.CommonComponents as CC
object Debugg{
    var visible: MutableState<Boolean> = mutableStateOf(true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(context: Context) {
    var editUnit by remember { mutableStateOf(false) }
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
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Assignments", style = CC.titleTextStyle) }, navigationIcon = {
                Icon(Icons.Default.ArrowBackIosNew, "Back", tint = GlobalColors.textColor)
            }, actions = {
                IconButton(onClick = {
                    loading = true
                    MyDatabase.getSubjects { fetchedSubjects ->
                        subjects.clear()
                        subjects.addAll(fetchedSubjects ?: emptyList())
                        loading = false
                    }
                    Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.Refresh, "Refresh", tint = GlobalColors.textColor)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, "Add", tint = GlobalColors.textColor)
                }
                DropdownMenu(onDismissRequest = { expanded = false },
                    expanded = expanded,
                    modifier = Modifier.background(GlobalColors.primaryColor)
                ) {
                    DropdownMenuItem(text = { Text("Add Unit", style = CC.descriptionTextStyle) },
                        onClick = {
                            showaddSubject = true
                            expanded = false
                        })
                    DropdownMenuItem(text = { Text("Edit Unit", style = CC.descriptionTextStyle) },
                        onClick = {
                            //edit unit functionality
                        })
                    DropdownMenuItem(text = {
                        Text(
                            "Add Assignment", style = CC.descriptionTextStyle
                        )
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
                        .background(Color.Black, CircleShape)
                )
            }

            val coroutineScope = rememberCoroutineScope()

            if (loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
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
                        AssignmentsList(subjectId = subjects[selectedTabIndex].id)
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
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = GlobalColors.secondaryColor,
                                unfocusedBorderColor = GlobalColors.tertiaryColor,
                                focusedTextColor = GlobalColors.textColor,
                                unfocusedTextColor = GlobalColors.textColor
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
            if(editUnit){
                BasicAlertDialog(onDismissRequest = { editUnit = false }) {
                    Column(
                        modifier = Modifier
                            .width(250.dp)
                            .background(
                                color = GlobalColors.primaryColor, shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Edit Unit",
                            style = CC.titleTextStyle,
                            color = GlobalColors.textColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = subjectName,
                            onValueChange = { subjectName = it },
                            label = { Text("Unit Name", color = GlobalColors.tertiaryColor) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = GlobalColors.secondaryColor,
                                unfocusedBorderColor = GlobalColors.tertiaryColor,
                                focusedTextColor = GlobalColors.textColor,
                                unfocusedTextColor = GlobalColors.textColor
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { editUnit = false },
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
                                            context, "Success! Unit Edited", Toast.LENGTH_SHORT
                                        ).show()
                                        editUnit = false
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
                                    "Edit",
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
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = GlobalColors.secondaryColor,
                                unfocusedBorderColor = GlobalColors.tertiaryColor,
                                focusedTextColor = GlobalColors.textColor,
                                unfocusedTextColor = GlobalColors.textColor
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = description,
                            onValueChange = { description = it },
                            label = { Text("Description", color = GlobalColors.tertiaryColor) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = GlobalColors.secondaryColor,
                                unfocusedBorderColor = GlobalColors.tertiaryColor,
                                focusedTextColor = GlobalColors.textColor,
                                unfocusedTextColor = GlobalColors.textColor
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
}

@Composable
fun AssignmentsList(subjectId: String) {
    var assignments by remember { mutableStateOf<List<Assignment>?>(null) }
    LaunchedEffect(subjectId) {
        MyDatabase.getAssignments(subjectId) { fetchedAssignments ->
            assignments = fetchedAssignments
        }
    }

    if (assignments == null) {
        Column(
            modifier = Modifier.fillMaxSize(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text("Loading Assignments...Please wait", style = CC.descriptionTextStyle)

        }
    } else {
        LazyColumn {
            if (assignments!!.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No assignments found.", style = CC.descriptionTextStyle)
                    }
                }
            }
            items(assignments!!) { assignment ->
                AssignmentCard(assignment)
            }
        }
    }
}

@Composable
fun AssignmentCard(assignment: Assignment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), colors = CardDefaults.cardColors(
            containerColor = GlobalColors.secondaryColor, contentColor = GlobalColors.textColor
        ), elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = assignment.name,
                    style = CC.titleTextStyle.copy(fontSize = 18.sp),
                    color = GlobalColors.textColor
                )
                Row {
                    IconButton(
                        onClick = { /* Edit assignment logic */ }, modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Assignment",
                            tint = GlobalColors.primaryColor
                        )
                    }
                    IconButton(
                        onClick = { /* Delete assignment logic */ }, modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Assignment",
                            tint = GlobalColors.primaryColor
                        )
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
            Text(
                text = assignment.description,
                style = CC.descriptionTextStyle,
                color = GlobalColors.textColor
            )
        }
    }
}

@Preview
@Composable
fun AssignmentScreenPreview() {
    AssignmentScreen(LocalContext.current)
}
