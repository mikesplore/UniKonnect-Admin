package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsers(navController: NavController) {
    var users by remember { mutableStateOf<List<User>?>(null) }
    var loading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(loading) {
        MyDatabase.getUsers { fetchedUsers ->
            users = fetchedUsers
            loading = false
        }
    }

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
                    actions = {
                        IconButton(onClick = {loading = true}) { 
                            Icon(Icons.Default.Refresh,"refresh", tint = GlobalColors.textColor)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBackIosNew, "back", tint = GlobalColors.textColor
                            )
                        }
                    },
                    title = { Text("Manage Users", style = CC.titleTextStyle(context)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GlobalColors.primaryColor,
                        titleContentColor = GlobalColors.textColor
                    )
                )
            },
            containerColor = GlobalColors.primaryColor
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .background(GlobalColors.primaryColor)
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (loading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = GlobalColors.secondaryColor,
                            trackColor = GlobalColors.textColor
                        )
                        Text("Users Loading...", style = CC.descriptionTextStyle(context))
                    }
                } else {

                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Name", style = CC.descriptionTextStyle(context), fontWeight = FontWeight.Bold)
                            Text("Email", style = CC.descriptionTextStyle(context), fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = GlobalColors.textColor)
                        users?.forEach { user ->
                            UserCard(
                                user = user,
                                context = context,
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Total Users: ${users?.size ?: 0}", style = CC.descriptionTextStyle(context))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(user: User, context: Context) {
    var expanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user.name) }
    var isediting by remember { mutableStateOf(false) }
    if(isediting){
        BasicAlertDialog(onDismissRequest = {isediting  = false}) {
            Column(modifier = Modifier
                .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
                .width(300.dp)
                .height(200.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Text("Edit User Name", style = CC.titleTextStyle(context),
                        modifier = Modifier.padding(top = 10.dp))
                }
                OutlinedTextField(
                    value = name,
                    textStyle = CC.descriptionTextStyle(context = LocalContext.current),
                    onValueChange = {name = it},
                    label = { Text("Name", style = CC.descriptionTextStyle(context = LocalContext.current)) },
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(0.9f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GlobalColors.primaryColor,
                        unfocusedLabelColor = GlobalColors.tertiaryColor,
                        focusedIndicatorColor = GlobalColors.textColor,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = GlobalColors.textColor,
                        focusedTextColor = GlobalColors.textColor,
                        focusedLabelColor = GlobalColors.primaryColor,
                        unfocusedIndicatorColor = GlobalColors.textColor
                    ),
                    singleLine = true
                )
                Row(modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {
                    Button(onClick = {

                        MyDatabase.updateUser(user.id, name,
                            onSuccess = {
                                // Show success message or perform other actions
                                Toast.makeText(context, "User name updated successfully!", Toast.LENGTH_SHORT).show()
                                isediting = false
                            },
                            onFailure = { exception ->
                                // Handle the failure, e.g., display an error message
                                if (exception != null) {
                                    Toast.makeText(context, "Error updating user name: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                    },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GlobalColors.primaryColor,
                            contentColor = GlobalColors.textColor
                        )
                    ) {
                        Text("Save", style = CC.descriptionTextStyle(context))
                    }
                    Button(onClick = { isediting = false},
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GlobalColors.primaryColor,
                            contentColor = GlobalColors.textColor
                        )
                    ) {
                        Text("Cancel", style = CC.descriptionTextStyle(context))
                    }
                }
            }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(GlobalColors.primaryColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(user.name, style = CC.descriptionTextStyle(context))
                Text(user.email, style = CC.descriptionTextStyle(context))
                Row {
                    IconButton(onClick = {isediting= true}) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GlobalColors.textColor)
                    }
                    IconButton(onClick = {
                        MyDatabase.deleteUser(user.id,
                            onSuccess = {
                                // Show success message or perform other actions
                                Toast.makeText(context, "User deleted successfully!", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { exception ->
                                // Handle the failure, e.g., display an error message
                                if (exception != null) {
                                    Toast.makeText(context, "Error deleting user: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GlobalColors.textColor)
                    }
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Additional user details can go here.", style = CC.descriptionTextStyle(context))
            }
        }
    }
}

@Preview
@Composable
fun ManagementUsersProfile() {
    ManageUsers(navController = rememberNavController())

}
