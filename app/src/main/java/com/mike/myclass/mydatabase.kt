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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import kotlinx.coroutines.launch
import com.mike.myclass.CommonComponents as CC


// Commenting out the Firebase functions
/*
fun getSubjects(onSubjectsFetched: (List<Subjects>?) -> Unit) {
    // Simulated delay and fetch logic
    onSubjectsFetched(listOf(Subjects("1", "Math"), Subjects("2", "Science")))
}

fun getAssignments(subjectId: String, onAssignmentsFetched: (List<Assignment>?) -> Unit) {
    // Simulated delay and fetch logic
    onAssignmentsFetched(listOf(Assignment("1", subjectId, "Assignment 1", "Description 1")))
}
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(context: Context) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignments", style = CC.titleTextStyle) },
                navigationIcon = {
                    Icon(Icons.Default.ArrowBackIosNew,"Back", tint = GlobalColors.textColor)
                },
                actions = {
                    IconButton(onClick = { /* Add assignment logic here */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = GlobalColors.textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
            // Hardcoded subjects
            val subjects = listOf(
                Subjects("1", "Math"),
                Subjects("2", "Science"),
                Subjects("3", "History"),
                Subjects("4", "English"),
                Subjects("5", "Computer Science"),
            )

            var selectedTabIndex by remember { mutableIntStateOf(0) }
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
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

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.background(Color.LightGray),
                contentColor = Color.Black,
                indicator = indicator,
                edgePadding = 0.dp,
            ) {
                subjects.forEachIndexed { index, subject ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            coroutineScope.launch {
                                // Load assignments for the selected subject
                            }
                        },
                        text = {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTabIndex == index) GlobalColors.secondaryColor else GlobalColors.primaryColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = subject.name,
                                    color = if (selectedTabIndex == index) GlobalColors.textColor else GlobalColors.tertiaryColor,
                                )
                            }
                        },
                        modifier = Modifier.background(GlobalColors.primaryColor)
                    )
                }
            }

            when (selectedTabIndex) {
                in subjects.indices -> {
                    AssignmentsList(subjectId = subjects[selectedTabIndex].id)
                }
            }
        }
    }
}

@Composable
fun AssignmentsList(subjectId: String) {
    // Hardcoded assignments
    val assignments = listOf(
        Assignment("1", subjectId, "Assignment 1", "Description 1"),
        Assignment("2", subjectId, "Assignment 2", "Description 2")
    )

    LazyColumn {
        items(assignments) { assignment ->
            AssignmentCard(assignment)
        }
    }
}

@Composable
fun AssignmentCard(assignment: Assignment) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(0.9f)
                .background(GlobalColors.secondaryColor, RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = assignment.name, style = CC.descriptionTextStyle)
                Text(text = assignment.description, style = CC.descriptionTextStyle)
            }
        }
    }
}

@Preview
@Composable
fun AssignmentScreenPreview() {
    AssignmentScreen(LocalContext.current)
}
