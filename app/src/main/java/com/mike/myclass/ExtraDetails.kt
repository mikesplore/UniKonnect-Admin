package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreDetails(context: Context, navController: NavController) {
    val database = MyDatabase.database.child("Users")
    var users by remember { mutableStateOf<List<User>?>(null) }

    fun checkEmailExists(email: String, onResult: (Boolean) -> Unit) {
        val query = database.orderByChild("email").equalTo(email) // Query for the email
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val emailExists = snapshot.exists() // Check if the email exists
                onResult(emailExists) // Call the callback with the result
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
                onResult(false) // You might want to handle errors differently
            }
        })
    }
    var loading by remember {  mutableStateOf(true)}
    var addloading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MyDatabase.getUsers { fetchedUsers ->
            users = fetchedUsers
        }

        checkEmailExists(Details.email.value) { exists ->
            if (exists) {
                loading = false
                Toast.makeText(context, "Your data already exists", Toast.LENGTH_SHORT).show()
                navController.navigate("dashboard")
            } else {
                loading = false
                Toast.makeText(context, "Email does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "More Details", style = CC.titleTextStyle(context)) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigate("dashboard") },
                    modifier = Modifier.absolutePadding(left = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = GlobalColors.textColor,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor)
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .background(CC.backbrush)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CC.backbrush)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    CC.SingleLinedTextField(
                        value = Details.name.value,
                        onValueChange = { Details.name.value = it },
                        label = "First name",
                        context = context,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                                    addloading = true
                                    database.push().setValue(
                                        User(
                                            email = Details.email.value,
                                            name = Details.name.value
                                        )
                                    ).addOnSuccessListener {
                                        addloading = false
                                        Toast.makeText(context, "Success data added!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("dashboard")
                                    }.addOnFailureListener {
                                        addloading = false
                                        Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
                                    }


                        },
                        modifier = Modifier
                            .width(275.dp)
                            .background(
                                CC.backbrush, RoundedCornerShape(10.dp)
                            ), // Background moved to outer Modifier
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically) {
                            if(loading || addloading){
                                CircularProgressIndicator(
                                    color = GlobalColors.primaryColor,
                                    trackColor = GlobalColors.textColor,
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                            if(loading){
                                Text("Checking Database", style = CC.descriptionTextStyle(context))
                            }else{
                                Text(if(addloading)"Adding" else "Add", style = CC.descriptionTextStyle(context))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Extra() {
    MoreDetails(
        navController = rememberNavController(), context = LocalContext.current
    )
}
