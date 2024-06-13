package com.mike.myclass

import android.content.Context
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Calendar.DAY_OF_WEEK
import com.mike.myclass.CommonComponents as CC


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecordAttendanceScreen(
    navController: NavController,
    context: Context
) {
    val calendar = Calendar.getInstance()
    val dayofWeek = calendar.get(DAY_OF_WEEK)
    val daysOfWeek = when (dayofWeek) {
        1 -> "Sunday"
        2 -> "Monday"
        3 -> "Tuesday"
        4 -> "Wednesday"
        5 -> "Thursday"
        6 -> "Friday"
        7 -> "Saturday"
        else -> ""
    }

    val subjects = remember { mutableStateOf<List<Subjects>>(emptyList()) }
    val students = remember { mutableStateOf<List<Student>>(emptyList()) }
    val pagerState = rememberPagerState()
    val attendanceRecords = remember { mutableStateMapOf<String, MutableState<Boolean>>() }
    val checkboxStates = remember { mutableStateMapOf<String, MutableState<Boolean>>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            MyDatabase.loadSubjectsAndAssignments { loadedSubjects ->
                if (loadedSubjects != null) {
                    subjects.value = loadedSubjects
                    MyDatabase.loadStudents { loadedStudents ->
                        if (loadedStudents != null) {
                            students.value = loadedStudents

                            // Initialize the attendance and checkbox states
                            loadedSubjects.forEach { subject ->
                                loadedStudents.forEach { student ->
                                    val key = "${student.id}-${subject.name}"
                                    if (key !in attendanceRecords) {
                                        attendanceRecords[key] = mutableStateOf(false)
                                        checkboxStates[key] = mutableStateOf(true)
                                    }
                                }
                            }

                            if (loadedSubjects.isEmpty() || loadedStudents.isEmpty()) {
                                errorMessage.value = "No subjects or students found."
                            }
                        } else {
                            errorMessage.value = "Error loading students."
                        }
                        isLoading.value = false
                    }
                } else {
                    errorMessage.value = "Error loading subjects."
                    isLoading.value = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        " Attendance $daysOfWeek",
                        style = CC.titleTextStyle(context), fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate("dashboard") },
                        modifier = Modifier.absolutePadding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = GlobalColors.textColor,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .background(Color.Transparent, shape = RoundedCornerShape(10.dp))
                                .size(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint = GlobalColors.textColor,
                            )
                        }
                    }
                },
                actions = {
                    Button(onClick = {
                        val allRecords = mutableListOf<AttendanceRecord>()

                        subjects.value.forEach { subject ->
                            students.value.forEach { student ->
                                val key = "${student.id}-${subject.name}"
                                val isPresent = attendanceRecords[key]?.value ?: false
                                allRecords.add(
                                    AttendanceRecord(
                                        student.id,
                                        daysOfWeek,
                                        isPresent,
                                        subject.name
                                    )
                                )
                            }
                        }
                        MyDatabase.saveAttendanceRecords(allRecords) { isSuccessful ->
                            if (isSuccessful) {
                                Toast.makeText(context, "Attendance saved", Toast.LENGTH_SHORT).show()
                                Log.d("Attendance", "Attendance saved")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to save attendance",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(Color.Transparent)) {
                        Text(
                            "Save",
                            style = CC.descriptionTextStyle(context)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor
                )
            )
        }
    ) { innerPadding ->
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CC.backbrush)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GlobalColors.primaryColor,
                    trackColor = GlobalColors.textColor)
            }
        } else if (!errorMessage.value.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CC.backbrush)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage.value ?: "Unknown error",
                    color = GlobalColors.textColor,
                    style = CC.titleTextStyle(context)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .background(CC.backbrush)
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp
                ) {
                    subjects.value.forEachIndexed { index, subject ->
                        Tab(

                            selected = pagerState.currentPage == index,
                            onClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                            text = {

                                Box(modifier  = Modifier.background(color = if (pagerState.currentPage == index) GlobalColors.secondaryColor else GlobalColors.primaryColor, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center){
                                    Text(
                                        subject.name,
                                        style = CC.descriptionTextStyle(context),
                                        color = if (pagerState.currentPage == index) GlobalColors.textColor else GlobalColors.tertiaryColor,
                                        modifier = Modifier.padding(5.dp)
                                    )
                                }
                            },
                            selectedContentColor = GlobalColors.primaryColor,
                            unselectedContentColor = GlobalColors.tertiaryColor,
                            modifier = Modifier.background(GlobalColors.primaryColor)
                        )

                    }

                }
                HorizontalPager(
                    count = subjects.value.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        itemsIndexed(students.value) { _, student ->
                            val key = "${student.id}-${subjects.value[page].name}"
                            val isPresent = attendanceRecords[key] ?: mutableStateOf(false)
                            val checkboxEnabled = checkboxStates[key] ?: mutableStateOf(true)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(student.firstName, color = GlobalColors.textColor)

                                Checkbox(

                                    colors = CheckboxDefaults.colors(GlobalColors.textColor, uncheckedColor = GlobalColors.tertiaryColor),
                                    enabled = checkboxEnabled.value,
                                    checked = isPresent.value,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            checkboxEnabled.value = false
                                            isPresent.value = true
                                        }
                                    }
                                )
                            }
                            HorizontalDivider(thickness = 1.dp, color = GlobalColors.tertiaryColor)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordAttendanceScreenPreview() {
    RecordAttendanceScreen(
        navController = rememberNavController(),
        context = LocalContext.current
    )
}


