package com.mike.myclass

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDashboard(navController: NavController, context: Context) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween // Distribute space evenly
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.student),
                                contentDescription = "Image",
                                modifier = Modifier.size(50.dp)
                            )
                            Column(modifier = Modifier.width(100.dp)) { // Added width constraint
                                Text(
                                    "Welcome",
                                    style = CC.descriptionTextStyle(context),
                                    fontSize = 12.sp
                                )
                                Text(
                                    "Mike 👋",
                                    style = CC.descriptionTextStyle(context),
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Row { // Actions on the right
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = GlobalColors.tertiaryColor,
                                        shape = CircleShape
                                    )
                                    .size(50.dp)
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Outlined.Search,
                                        contentDescription = "search",
                                        tint = GlobalColors.textColor
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = GlobalColors.tertiaryColor,
                                        shape = CircleShape
                                    )
                                    .size(50.dp)
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.height(100.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                )
            )
        },
        containerColor = GlobalColors.primaryColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Add your content here
        }
    }

}


@Preview
@Composable
fun NewDashboardPreview(){
    NewDashboard(navController = rememberNavController(), context = LocalContext.current)
}
