package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mike.myclass.MyDatabase.fetchUserDataByEmail
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import com.mike.myclass.MyDatabase.updatePassword
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, context: Context) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = CC.titleTextStyle(context)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = GlobalColors.textColor
                        )
                    }
                }
            )
        }, containerColor = GlobalColors.primaryColor
    ) {
        Box(modifier = Modifier.fillMaxSize()){
            Background(context)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .fillMaxSize()
                .background(GlobalColors.primaryColor)
                .padding(16.dp), // Added padding to the column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionTitle(context, Icons.Default.AccountCircle, "Profile")
            Spacer(modifier = Modifier.height(8.dp))
            ProfileCard(context = context)
            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing

            SectionWithRow(
                title = "Appearance",
                description = "Change the appearance of the app",
                navController = navController,
                route = "appearance",
                context = context
            )
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before system settings section
            SectionTitle(context, Icons.Default.SafetyCheck, "System")
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before system settings section
            SystemSettings(context)
            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
            SectionTitle(context, Icons.Default.Security, "Security")
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before password section
            Text("Change Password", style = CC.descriptionTextStyle(context))
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before password section
            PasswordUpdateSection(context)
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before feedback section
            SectionTitle(context, Icons.AutoMirrored.Filled.Message, "We value your Feedback")
            RatingAndFeedbackScreen(context)
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before feedback section
            BottomEnd(context)

        }
        }
    }
}
@Composable
fun BottomEnd(context: Context) {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName

    Column(
        modifier = Modifier
            .height(150.dp) // Increased height for more content
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(thickness = 1.dp, color = GlobalColors.tertiaryColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text("All rights reserved © 2024", style = CC.descriptionTextStyle(context))
        Text("Version $versionName", style = CC.descriptionTextStyle(context))
        Text("Student Portal", style = CC.descriptionTextStyle(context).copy(fontWeight = FontWeight.Bold))
        Text("Developed by Mike", style = CC.descriptionTextStyle(context))
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            // Add social media icons here
        }
    }
}
@Composable
fun SectionTitle(context: Context, icon: ImageVector, title: String) {
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context)
    }
    Row(
        modifier = Modifier
            .height(40.dp)
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
            )
            .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = "Icon", tint = GlobalColors.textColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = CC.titleTextStyle(context), fontSize = 20.sp)
    }
}

@Composable
fun SectionWithRow(
    title: String,
    description: String,
    navController: NavController,
    route: String,
    context: Context
) {
    Row(
        modifier = Modifier
            .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)

            )
            .fillMaxWidth()
            .padding(6.dp)
            .padding(start = 20.dp, end = 20.dp), // Added vertical padding
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, style = CC.titleTextStyle(context), fontSize = 20.sp)
            Text(description, style = CC.descriptionTextStyle(context))
        }

        IconButton(onClick = {
            navController.navigate(route)
        }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Arrow",
                tint = GlobalColors.textColor
            )
        }
    }
}

@Composable
fun SystemSettings(context: Context) {
    var checked by remember { mutableStateOf(false) }
    var edge by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        SettingSwitch(context, "Disable EdgeToEdge", edge) {
            Global.edgeToEdge.value = !Global.edgeToEdge.value
            edge = !edge
        }
        SettingSwitch(context, "Enable System Notifications", checked) {
            Global.showAlert.value = !Global.showAlert.value
            checked = !checked
        }
    }
}

@Composable
fun SettingSwitch(
    context: Context,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit // Callback for checked state changes
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = CC.descriptionTextStyle(context))
        Switch(
            checked = checked, onCheckedChange = { isChecked ->
                onCheckedChange(isChecked) // Invoke the callback with the new state
            }, colors = SwitchDefaults.colors(
                checkedThumbColor = GlobalColors.primaryColor,
                checkedTrackColor = GlobalColors.secondaryColor,
                uncheckedThumbColor = GlobalColors.primaryColor,
                uncheckedTrackColor = GlobalColors.secondaryColor
            )
        )
    }
}


@Composable
fun ProfileCard(
    context: Context
) {
    var user by remember { mutableStateOf(User()) }
    var isExpanded by remember { mutableStateOf(false) }
    var currentName by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }

    // Fetch user data when the composable is launched
    LaunchedEffect(CC.getCurrentUser()) {
        fetchUserDataByEmail(CC.getCurrentUser()) { fetchedUser ->
            fetchedUser?.let {
                user = it
                currentName = it.name
                currentEmail = it.email
                currentAdmissionNumber = it.id
            }
        }
    }

    Card(
        modifier = Modifier
            .border(
                    1.dp,GlobalColors.secondaryColor,RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlobalColors.primaryColor
        ),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(GlobalColors.primaryColor)
                .padding(16.dp)
        ) {
            // Only show the name field initially
            Row(modifier = Modifier
                .padding(vertical = 8.dp)
                .border(
                    1.dp,
                    GlobalColors.secondaryColor,
                    RoundedCornerShape(10.dp)
                )
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {
                Text(
                    text = currentName,
                    style = CC.descriptionTextStyle(context).copy(
                        fontSize = 14.sp,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                )
            }
            // Animated visibility for other fields
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Row(modifier = Modifier
                        .padding(vertical = 8.dp)
                        .border(
                            1.dp,
                            GlobalColors.secondaryColor,
                            RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start) {
                        Text(
                            text = "Email: $currentEmail",
                            style = CC.descriptionTextStyle(context).copy(
                                fontSize = 14.sp,
                                color = Color.White
                            ),
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        )
                    }
                    Row(modifier = Modifier
                        .padding(vertical = 8.dp)
                        .border(
                            1.dp,
                            GlobalColors.secondaryColor,
                            RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Admission Number: $currentAdmissionNumber",
                        style = CC.descriptionTextStyle(context).copy(
                            fontSize = 14.sp,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                            .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    )
                    }
                }
            }
        }
    }
}






@Composable
fun PasswordTextField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    context: Context
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = CC.descriptionTextStyle(context)) },
        enabled = isEditing,
        textStyle = CC.descriptionTextStyle(context),
        colors = TextFieldDefaults.colors(
            focusedTextColor = GlobalColors.textColor,
            disabledContainerColor = GlobalColors.secondaryColor,
            focusedContainerColor = GlobalColors.primaryColor,
            unfocusedContainerColor = GlobalColors.primaryColor,
            disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
            disabledLabelColor = LocalContentColor.current.copy(ContentAlpha.medium)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun PasswordUpdateSection(context: Context) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PasswordTextField(
            label = "Current Password",
            value = currentPassword,
            isEditing = true,
            onValueChange = { currentPassword = it },
            context = context
        )
        PasswordTextField(
            label = "New Password",
            value = newPassword,
            isEditing = true,
            onValueChange = { newPassword = it },
            context = context
        )
        PasswordTextField(
            label = "Confirm Password",
            value = confirmPassword,
            isEditing = true,
            onValueChange = { confirmPassword = it },
            context = context
        )

        Button(
            onClick = {
                loading = true
                if (newPassword == confirmPassword && newPassword.isNotEmpty() && currentPassword.isNotEmpty()) {
                    currentUser?.let { user ->
                        val credential =
                            EmailAuthProvider.getCredential(user.email!!, currentPassword)
                        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                updatePassword(newPassword, onSuccess = {
                                    // Handle success (e.g., show a success message)
                                    loading = false
                                    Toast.makeText(
                                        context, "Password updated successfully", Toast.LENGTH_SHORT
                                    ).show()
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                }, onFailure = { exception ->
                                    // Handle failure (e.g., show an error message)
                                    loading = false
                                    Toast.makeText(
                                        context,
                                        "Failed to Change password: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                })
                            } else {
                                // Handle reauthentication failure
                                loading = false
                                Toast.makeText(
                                    context,
                                    "Authentication failed: ${reauthTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    // Handle password mismatch
                    loading = false
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.padding(top = 16.dp), colors = ButtonDefaults.buttonColors(
                containerColor = GlobalColors.tertiaryColor, contentColor = Color.White
            ), shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = GlobalColors.primaryColor,
                        trackColor = GlobalColors.tertiaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Change Password", style = CC.descriptionTextStyle(context))
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun StarRating(
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val color = when {
                i <= currentRating -> when (i) {
                    in 1..2 -> Color.Red
                    3 -> GlobalColors.extraColor2
                    else -> Color.Green
                }

                else -> GlobalColors.secondaryColor
            }
            val animatedScale by animateFloatAsState(
                targetValue = if (i <= currentRating) 1.2f else 1.0f,
                animationSpec = tween(durationMillis = 300),
                label = ""
            )
            Star(filled = i <= currentRating,
                color = color,
                scale = animatedScale,
                onClick = { onRatingChanged(i) })
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun Star(
    filled: Boolean, color: Color, scale: Float, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val path = Path().apply {
        moveTo(50f, 0f)
        lineTo(61f, 35f)
        lineTo(98f, 35f)
        lineTo(68f, 57f)
        lineTo(79f, 91f)
        lineTo(50f, 70f)
        lineTo(21f, 91f)
        lineTo(32f, 57f)
        lineTo(2f, 35f)
        lineTo(39f, 35f)
        close()
    }

    Canvas(
        modifier = modifier
            .size((40 * scale).dp)
            .clickable(onClick = onClick)
    ) {
        drawPath(
            path = path,
            color = if (filled) color else Color.Gray,
            style = if (filled) Stroke(width = 8f) else Stroke(
                width = 8f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun RatingAndFeedbackScreen(context: Context) {
    var currentRating by remember { mutableIntStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    var averageRatings by remember { mutableStateOf("") }
    val user by remember { mutableStateOf(User()) }
    var showFeedbackForm by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MyDatabase.fetchAverageRating { averageRating ->
            averageRatings = averageRating
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (averageRatings.isEmpty()) "No ratings yet" else "Average Rating: $averageRatings",
            style = CC.descriptionTextStyle(context),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        StarRating(
            currentRating = currentRating, onRatingChanged = { rating ->
                currentRating = rating
                showFeedbackForm = true
            }, context = context
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = showFeedbackForm) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = {
                        Text(
                            "Enter your feedback (optional)", style = CC.descriptionTextStyle(context)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    textStyle = CC.descriptionTextStyle(context),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        loading = true
                        MyDatabase.writeFeedback(Feedback(
                            rating = currentRating,
                            sender = user.name,
                            message = feedbackText,
                            admissionNumber = user.id
                        ), onSuccess = {
                            loading = false
                            Toast.makeText(
                                context, "Thanks for your feedback", Toast.LENGTH_SHORT
                            ).show()
                            feedbackText = ""
                            MyDatabase.fetchAverageRating { averageRating ->
                                averageRatings = averageRating
                            }
                            showFeedbackForm = false
                        }, onFailure = {
                            loading = false
                            Toast.makeText(
                                context,
                                "Failed to send feedback: ${it?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlobalColors.extraColor1,
                        contentColor = GlobalColors.secondaryColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = GlobalColors.primaryColor,
                                trackColor = GlobalColors.tertiaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Submit Feedback", style = CC.descriptionTextStyle(context))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Background(context: Context) {
    val icons = listOf(
        Icons.Outlined.Home,
        Icons.AutoMirrored.Outlined.Assignment,
        Icons.Outlined.School,
        Icons.Outlined.AccountCircle,
        Icons.Outlined.BorderColor,
        Icons.Outlined.Book,
    )
    LaunchedEffect (Unit){
        GlobalColors.loadColorScheme(context)

    }
    // Calculate the number of repetitions needed to fill the screen
    val repetitions = 1000 // Adjust this value as needed
    val repeatedIcons = mutableListOf<ImageVector>()
    repeat(repetitions) {
        repeatedIcons.addAll(icons.shuffled())
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(10),
        modifier = Modifier
            .fillMaxSize()
            .background(GlobalColors.primaryColor)
            .padding(10.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(repeatedIcons) { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GlobalColors.secondaryColor.copy(0.5f),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(rememberNavController(), LocalContext.current)
}
