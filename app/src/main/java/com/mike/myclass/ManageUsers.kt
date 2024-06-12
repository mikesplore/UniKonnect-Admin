package com.mike.myclass

import android.content.Context
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

    LaunchedEffect(Unit) {
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
                    .background(CC.backbrush)
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
                        val total = users?.size
                        HorizontalDivider(color = GlobalColors.textColor)
                        users?.forEach { user ->
                            AnimatedUserRow(
                                user = user,
                                context = context,
                                navController = navController
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center) {  
                            Text("Total Users: $total", style = CC. descriptionTextStyle(context))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedUserRow(user: User, context: Context, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp)
            .background(GlobalColors.primaryColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(user.name, style = CC.descriptionTextStyle(context))
            Text(user.email, style = CC.descriptionTextStyle(context))
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Additional user details can go here.", style = CC.descriptionTextStyle(context))
        }
        HorizontalDivider(color = GlobalColors.textColor)
    }

}

@Preview
@Composable
fun ManagementUsersProfile() {
    ManageUsers(navController = rememberNavController())
}
