package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.mike.myclass.MyDatabase.getAssignments
import com.mike.myclass.MyDatabase.getSubjects
import kotlinx.coroutines.launch





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmenScreen(context: Context) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignments") },
                navigationIcon = { /* ... */ }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            var subjectsLoading by remember { mutableStateOf(true) }
            val subjects = remember { mutableStateListOf<Subjects>() }
            /*LaunchedEffect(Unit) {
                getSubjects { fetchedSubjects ->
                    subjects.clear()
                    subjects.addAll(fetchedSubjects ?: emptyList())
                    subjectsLoading = false
                }
            }*/

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

            if (subjectsLoading) {
                CircularProgressIndicator()
            } else {
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
                                            if (selectedTabIndex == index) Color.Gray else Color.LightGray,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = subject.name,
                                        color = if (selectedTabIndex == index) Color.White else Color.Black,
                                    )
                                }
                            },
                            modifier = Modifier.background(Color.Transparent)
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
}

@Composable
fun AssignmensList(subjectId: String) {
    var assignments by remember { mutableStateOf<List<Assignment>?>(null) }
    LaunchedEffect(subjectId) {
        getAssignments(subjectId) { fetchedAssignments ->
            assignments = fetchedAssignments
        }
    }

    if (assignments == null) {
        CircularProgressIndicator()
    } else {
        LazyColumn {
            items(assignments!!) { assignment ->
                AssignmentItem(assignment)
            }
        }
    }
}

@Composable
fun Assignmenttem(assignment: Assignment) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = assignment.name, style = MaterialTheme.typography.headlineSmall)
        Text(text = assignment.description, style = MaterialTheme.typography.bodyLarge)
        Row {
            Button(onClick = { /* Edit assignment logic here */ }) {
                Text("Edit")
            }
            Button(onClick = { /* Delete assignment logic here */ }) {
                Text("Delete")
            }
        }
    }
}

@Preview
@Composable
fun AssignmentScreenPreiew() {
    AssignmentScreen(LocalContext.current)
}
