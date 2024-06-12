package com.mike.myclass

import android.content.Context
import android.icu.util.Calendar
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mike.myclass.MyDatabase.getAnnouncements
import kotlinx.coroutines.delay
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(navController: NavController, context: Context) {

    var isAnnouncementLoading by rememberSaveable { mutableStateOf(true) }
    val announcements = remember { mutableStateListOf<Announcement>() }

    LaunchedEffect(Unit) {
        getAnnouncements { fetchedAnnouncements ->
            announcements.addAll(fetchedAnnouncements ?: emptyList())
            isAnnouncementLoading = false
        }
    }

    var addAnnouncementDialog by remember { mutableStateOf(false) }
    var editAnnouncementDialog by remember { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var showNotification  = remember { mutableStateOf(false)}
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    val date = "$day/$month/$year"
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var currentEditAnnouncement by remember { mutableStateOf<Announcement?>(null) }


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
            TopAppBar(
                title = { Text("Announcements", style = CC.titleTextStyle) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("dashboard") }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = GlobalColors.textColor)
                    }
                },
                actions = {
                    IconButton(onClick = { addAnnouncementDialog = !addAnnouncementDialog }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = GlobalColors.textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
                    isAnnouncementLoading = true
                    getAnnouncements { fetchedAnnouncements ->
                        announcements.clear()
                        announcements.addAll(fetchedAnnouncements ?: emptyList())
                        isAnnouncementLoading = false
                    }
                },
                containerColor = GlobalColors.tertiaryColor,
                contentColor = GlobalColors.primaryColor,
                content = {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .background(CC.backbrush)
                .fillMaxSize()
                .padding(it)
        ) {
            if (addAnnouncementDialog) {
                AlertDialog(
                    onDismissRequest = { addAnnouncementDialog = false },
                    title = { Text("Add Announcement", style = CC.titleTextStyle) },
                    text = {
                        Column(
                            modifier = Modifier.height(350.dp)
                        ) {
                            CC.SingleLinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = "Title",
                                singleLine = true,
                                
                            )
                            CC.SingleLinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = "Description",
                                singleLine = true,
                                modifier = Modifier.height(200.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showNotification.value = true
                                val newAnnouncement = Announcement(

                                    date = date,
                                    title = title,
                                    description = description,

                                )
                                MyDatabase.writeAnnouncement(newAnnouncement)
                                addAnnouncementDialog = false
                                Toast.makeText(context, "Announcement added", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor)
                        ) {
                            Text("Add", style = CC.descriptionTextStyle)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { addAnnouncementDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.tertiaryColor)
                        ) {
                            Text("Cancel", style = CC.descriptionTextStyle, color = GlobalColors.primaryColor)
                        }
                    },
                    modifier = Modifier.height(350.dp),
                    containerColor = GlobalColors.secondaryColor
                )
            }

            if (editAnnouncementDialog) {
                AlertDialog(
                    onDismissRequest = { editAnnouncementDialog = false },
                    title = { Text("Edit Announcement", style = CC.titleTextStyle) },
                    text = {
                        Column(
                            modifier = Modifier.height(350.dp)
                        ) {
                            CC.SingleLinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = "Title",
                                singleLine = true,
                                
                            )
                            CC.SingleLinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = "Description",
                                singleLine = true,

                                modifier = Modifier.height(200.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val editedAnnouncement = currentEditAnnouncement?.copy(
                                    title = title,
                                    description = description
                                )
                                if (editedAnnouncement != null) {
                                    MyDatabase.writeAnnouncement(editedAnnouncement)
                                    announcements.removeAll { it.id == editedAnnouncement.id }
                                    announcements.add(editedAnnouncement)
                                    editAnnouncementDialog = false
                                    Toast.makeText(context, "Announcement edited", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor)
                        ) {
                            Text("Save", style = CC.descriptionTextStyle)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { editAnnouncementDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.tertiaryColor)
                        ) {
                            Text("Cancel", style = CC.descriptionTextStyle, color = GlobalColors.primaryColor)
                        }
                    },
                    modifier = Modifier.height(350.dp),
                    containerColor = GlobalColors.secondaryColor
                )
            }

            if (isLoading) {
                Column(
                    modifier = Modifier
                        .background(GlobalColors.primaryColor)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = GlobalColors.secondaryColor,
                        trackColor = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading Announcements...", style = CC.descriptionTextStyle)
                }
            } else {
                NotificationCard(
                    title = title,
                    message = description,
                    visibleState = showNotification
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(announcements, key = { announcement -> announcement.id }) { announcement ->
                        AnnouncementCard(
                            announcement = announcement,
                            onEdit = {
                                currentEditAnnouncement = it
                                title = it.title
                                description = it.description
                                editAnnouncementDialog = true
                            },
                            onDelete = { id ->
                                MyDatabase.deleteAnnouncement(id)
                                announcements.removeAll { it.id == id }
                                Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show()
                            },
                            viewModel = FontViewModel()
                        )
                    }
                }
            }
        }
    }
}
}




@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onEdit: (Announcement) -> Unit,
    onDelete: (String) -> Unit,
    viewModel: FontViewModel
) {

    var expanded by remember { mutableStateOf(false) }
    val text = if (expanded) "Close" else "Open"
    val animatedHeight by animateDpAsState(
        targetValue = if (expanded) 200.dp else 100.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = ""
    )
    Column(
        modifier = Modifier
            .height(animatedHeight)
            .fillMaxWidth()
            .padding(16.dp)
            .background(GlobalColors.secondaryColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.student),
                contentDescription = "Announcement Icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp),
            )
            Text(
                text = announcement.title,
                style = CC.descriptionTextStyle,
                fontWeight = FontWeight.Bold,
                color = GlobalColors.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor)
            ) {
                Text(text, style = CC.descriptionTextStyle)
            }
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.description,
                style = CC.descriptionTextStyle.copy(fontSize = 14.sp),
                color = GlobalColors.textColor.copy(alpha = 0.8f),
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = announcement.author,
                    style = CC.descriptionTextStyle.copy(fontSize = 12.sp),
                    color = GlobalColors.textColor.copy(alpha = 0.6f),
                )
                Text(
                    text = announcement.date,
                    style = CC.descriptionTextStyle.copy(fontSize = 12.sp),
                    color = GlobalColors.textColor.copy(alpha = 0.6f),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onEdit(announcement) },
                    colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor)
                ) {
                    Text("Edit", style = CC.descriptionTextStyle)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onDelete(announcement.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.tertiaryColor)
                ) {
                    Text("Delete", style = CC.descriptionTextStyle)
                }
            }
        }
    }
}



@Preview
@Composable
fun AlertsPreview(){
    AnnouncementsScreen(navController = rememberNavController(), context = LocalContext.current,
    )
}