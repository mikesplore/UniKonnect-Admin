package com.mike.myclass

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.mike.myclass.CommonComponents as CC



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(navController: NavController, context: Context) {
    val courses = remember { mutableStateListOf<Course>() }
    var showAddDialog by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(loading) {
        MyDatabase.fetchCourses { fetchedCourses ->
            courses.clear() // Clear existing courses
            courses.addAll(fetchedCourses) // Add fetched courses
            loading = false // Set loading to false after fetching
        }
    }




    Scaffold(topBar = {
        TopAppBar(

            title = { Text("Courses") }, actions = {
                IconButton(onClick = {
                    loading = true
                }) {
                    Icon(
                        Icons.Default.Refresh, "refresh", tint = GlobalColors.textColor
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GlobalColors.primaryColor,
                titleContentColor = GlobalColors.textColor
            )
        )

    }, floatingActionButton = {
        FloatingActionButton(onClick = { showAddDialog = true },
            containerColor = GlobalColors.secondaryColor) {
            Icon(Icons.Default.Add, contentDescription = "Add Course",
                tint = GlobalColors.textColor)
        }
    }, containerColor = GlobalColors.primaryColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GlobalColors.primaryColor)
                .padding(it)
        ) {
            if (loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = GlobalColors.primaryColor, trackColor = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading...", color = GlobalColors.textColor
                    )
                }

            }
            courses.forEach { course ->
                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = course.courseCode, color = Color.White, fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = course.courseName, color = Color.White, fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        navController.navigate("course/${course.courseCode}")
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "View Course",
                            tint = Color.White
                        )
                    }

                }
            }
        }
    }

    if (showAddDialog) {
        AddCourseDialog(onDismiss = { showAddDialog = false },
            onAddCourse = { courseCode, courseName, lastDate ->
                val newCourse = Course(courseCode, courseName, lastDate)
                val database = Firebase.database.reference.child("Courses").child(courseCode)
                database.setValue(newCourse).addOnSuccessListener {
                        courses.add(newCourse)
                        showAddDialog = false
                    }.addOnFailureListener { exception ->
                        // Handle the error
                    }
            },
            context

        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onAddCourse: (String, String, String) -> Unit,
    context: Context
) {
    var courseCode by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    val lastDate = CC.lastDate

    BasicAlertDialog(
        onDismissRequest = onDismiss, modifier = Modifier.width(300.dp) // Set width for the dialog
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                "Add New Course", style = CC.titleTextStyle(LocalContext.current)
            )
            Spacer(modifier = Modifier.height(16.dp))
            InputDialogTextField(
                value = courseCode,
                onValueChange = { courseCode = it },
                label = "Course Code",
                context
            )
            Spacer(modifier = Modifier.height(8.dp))
            InputDialogTextField(
                value = courseName,
                onValueChange = { courseName = it },
                label = "Course Name",
                context
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Row with two buttons
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CC.buttonColors)
                ) {
                    Text("Cancel", style = CC.descriptionTextStyle(LocalContext.current))
                }
                Button(
                    onClick = { onAddCourse(courseCode, courseName, lastDate) },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CC.buttonColors)
                ) {
                    Text("Add", style = CC.descriptionTextStyle(LocalContext.current))
                }
            }
        }
    }
}


@Preview
@Composable
fun CoursesScreenPreview() {
    CoursesScreen(rememberNavController(), LocalContext.current)
}