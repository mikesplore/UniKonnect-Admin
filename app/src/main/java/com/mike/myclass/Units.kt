package com.mike.myclass

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalUriHandler
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.mike.myclass.MyDatabase.deleteItem
import com.mike.myclass.MyDatabase.readItems
import com.mike.myclass.MyDatabase.writeItem
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(courseCode: String, context: Context) {
    val notes = remember { mutableStateListOf<GridItem>() }
    val pastPapers = remember { mutableStateListOf<GridItem>() }
    val resources = remember { mutableStateListOf<GridItem>() }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var addItemToSection by remember { mutableStateOf<Section?>(null) }

    LaunchedEffect(courseCode) {
        isLoading = true
        readItems(courseCode, Section.NOTES) { fetchedNotes ->
            notes.addAll(fetchedNotes)
            isLoading = false
        }
        readItems(courseCode, Section.PAST_PAPERS) { fetchedPastPapers ->
            pastPapers.addAll(fetchedPastPapers)
            isLoading = false
        }
        readItems(courseCode, Section.RESOURCES) { fetchedResources ->
            resources.addAll(fetchedResources)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Course Screen") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(painterResource(id = android.R.drawable.ic_input_add), contentDescription = "Add Item")
            }
        },
        containerColor = GlobalColors.primaryColor
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlobalColors.primaryColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GlobalColors.textColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlobalColors.primaryColor)
                    .padding(it)
            ) {
                Section(
                    title = "Notes",
                    items = notes,
                    onAddClick = { addItemToSection = Section.NOTES; showAddDialog = true },
                    onDelete = { notes.remove(it); deleteItem(courseCode, Section.NOTES, it) },
                    context
                )
                Section(
                    title = "Past Papers",
                    items = pastPapers,
                    onAddClick = { addItemToSection = Section.PAST_PAPERS; showAddDialog = true },
                    onDelete = { pastPapers.remove(it); deleteItem(courseCode, Section.PAST_PAPERS, it) },
                    context
                )
                Section(
                    title = "Additional Resources",
                    items = resources,
                    onAddClick = { addItemToSection = Section.RESOURCES; showAddDialog = true },
                    onDelete = { resources.remove(it); deleteItem(courseCode, Section.RESOURCES, it) },
                    context
                )
            }
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            onDismiss = { showAddDialog = false },
            onAddItem = { title, description, thumbnail, link ->
                val newItem = GridItem(title, description, thumbnail, link)
                when (addItemToSection) {
                    Section.NOTES -> {
                        notes.add(newItem)
                        writeItem(courseCode, Section.NOTES, newItem)
                    }
                    Section.PAST_PAPERS -> {
                        pastPapers.add(newItem)
                        writeItem(courseCode, Section.PAST_PAPERS, newItem)
                    }
                    Section.RESOURCES -> {
                        resources.add(newItem)
                        writeItem(courseCode, Section.RESOURCES, newItem)
                    }
                    null -> { /* Do nothing */ }
                }
                showAddDialog = false
            }
        )
    }
}





@Composable
fun Section(
    title: String,
    items: List<GridItem>,
    onAddClick: () -> Unit,
    onDelete: (GridItem) -> Unit,
    context: Context
) {
    Text(
        text = title,
        style = CC.titleTextStyle(context)
    )

    Spacer(modifier = Modifier.height(10.dp))

    if (items.isEmpty()) {
        Text(
            text = "No items available",
            style = CC.descriptionTextStyle(context)
        )
    } else {
        LazyRow {
            items(items) { item ->
                GridItemCard(item = item, onDelete = onDelete)
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF007BFF),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text("Add Item", style = CC.descriptionTextStyle(context = context))
    }

    Spacer(modifier = Modifier.height(20.dp))
}


@Composable
fun GridItemCard(item: GridItem, onDelete: (GridItem) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = Modifier
            .width(200.dp)
            .padding(5.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = item.thumbnail),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = item.description,
                fontSize = 12.sp,
                color = Color(0xFF666666),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "View Document",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF007BFF), RoundedCornerShape(4.dp))
                    .padding(8.dp)
                    .clickable { uriHandler.openUri(item.link) },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Delete") }, onClick = {
                        onDelete(item)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onAddItem: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var thumbnail by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onAddItem(title, description, thumbnail, link)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text("Add New Item")
        },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                TextField(
                    value = thumbnail,
                    onValueChange = { thumbnail = it },
                    label = { Text("Thumbnail URL") }
                )
                TextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Document Link") }
                )
            }
        }
    )
}



@Preview(showBackground = true)
@Composable
fun CourseScreenPreview() {
    CourseScreen(
        courseCode = "CP123456",
        context = LocalContext.current
    )
}
