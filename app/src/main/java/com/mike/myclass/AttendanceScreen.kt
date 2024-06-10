package com.mike.myclass

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import com.mike.myclass.ui.theme.RobotoMono
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import com.mike.myclass.CommonComponents as CC

data class Units(val name: String)
data class Student(val registrationID: String, val firstName: String)
data class AttendanceRecord(val studentId: String, val day: String, val isPresent: Boolean, val unit: String)

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecordAttendanceScreen(
    navController: NavController,
    context: Context
) {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysOfWeek = when (dayOfWeek) {
        1 -> "Sunday"
        2 -> "Monday"
        3 -> "Tuesday"
        4 -> "Wednesday"
        5 -> "Thursday"
        6 -> "Friday"
        7 -> "Saturday"
        else -> ""
    }

    // Hardcoded values for units and students
    val units = listOf(Units("Math"), Units("Science"), Units("History"), Units("English"), Units("Computer Science"))
    val students = listOf(
        Student("001", "Alice"),
        Student("002", "Bob"),
        Student("003", "Charlie")
    )

    val pagerState = rememberPagerState()
    val attendanceRecords = remember { mutableStateMapOf<String, MutableState<Boolean>>() }
    val checkboxStates = remember { mutableStateMapOf<String, MutableState<Boolean>>() }

    // Initialize the attendance and checkbox states
    units.forEach { unit ->
        students.forEach { student ->
            val key = "${student.registrationID}-${unit.name}"
            if (key !in attendanceRecords) {
                attendanceRecords[key] = mutableStateOf(false)
                checkboxStates[key] = mutableStateOf(true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Attendance $daysOfWeek",
                        style = CC.titleTextStyle, fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
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
                        // Here we would save the attendance records to a database or file, but we'll just print them for now
                        units.forEach { unit ->
                            students.forEach { student ->
                                val key = "${student.registrationID}-${unit.name}"
                                val isPresent = attendanceRecords[key]?.value ?: false
                                val record = AttendanceRecord(
                                    student.registrationID,
                                    daysOfWeek,
                                    isPresent,
                                    unit.name
                                )
                                println("Recorded: $record")
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(Color.Transparent)) {
                        Text(
                            "Save",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = RobotoMono,
                            color = GlobalColors.textColor
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
                units.forEachIndexed { index, unit ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                unit.name,
                                style = CC.descriptionTextStyle,
                                color = if (pagerState.currentPage == index) GlobalColors.textColor else GlobalColors.tertiaryColor
                            )
                        },
                        selectedContentColor = GlobalColors.textColor,
                        unselectedContentColor = GlobalColors.tertiaryColor,
                        modifier = Modifier.background(GlobalColors.primaryColor)
                    )
                }
            }
            HorizontalPager(
                count = units.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    itemsIndexed(students) { _, student ->
                        val key = "${student.registrationID}-${units[page].name}"
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
                                colors = CheckboxDefaults.colors(GlobalColors.textColor),
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
                        Divider(color = GlobalColors.tertiaryColor, thickness = 1.dp)
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
