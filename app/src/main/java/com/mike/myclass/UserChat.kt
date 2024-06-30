package com.mike.myclass

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.mike.myclass.MyDatabase.fetchUserDataByEmail
import com.mike.myclass.MyDatabase.fetchUserToUserMessages
import com.mike.myclass.MyDatabase.sendUserToUserMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log
import com.mike.myclass.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChatScreen(navController: NavController, context: Context, targetUserId: String) {
    var user by remember { mutableStateOf(User()) }
    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(emptyList<Message>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currentName by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var retrievedadmission by remember { mutableStateOf("Empty") }

    // Fetch user data when the composable is launched
    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    user = it
                    currentName = it.name
                    currentEmail = it.email
                    currentAdmissionNumber = it.id
                    retrievedadmission = currentAdmissionNumber
                    Log.e("ProfileCard", "Fetched user: $currentAdmissionNumber")
                    Log.e("ProfileCard", "Fetched user: $currentName")
                    Log.e("ProfileCard", "Fetched user: $currentEmail")
                    //Debugging ain't easy my friend😂

                }
            }
        }
    }

    // Generate a unique conversation ID for the current user and the target user
    val conversationId = "Direct Messages/${generateConversationId(currentAdmissionNumber, targetUserId)}"
    Log.e("ProfileCard", "Admission ID: $currentAdmissionNumber")
    Log.e("ProfileCard", "Conversation ID: $conversationId")


    fun fetchMessages(conversationId: String) {
        try {
            fetchUserToUserMessages(conversationId) { fetchedMessages ->
                messages = fetchedMessages
            }
        } catch (e: Exception) {
            errorMessage = e.message
            scope.launch {
                snackbarHostState.showSnackbar("Failed to fetch messages: ${e.message}")
                Log.e("UserChatScreen", "Failed to fetch messages from $conversationId: ${e.message}", e) // Log the exception as well
            }
        }
    }

    LaunchedEffect(conversationId) {
        while (true) {
            fetchMessages(conversationId)
            if(messages.isEmpty()){
                Log.e("UserChatScreen", "No messages were found! in the path $conversationId")
            }
            delay(10) // Adjust the delay as needed
        }
    }

    fun sendMessage(messageContent: String) {
        try {
            val newMessage = Message(
                message = messageContent,
                senderName = user.name,
                senderID = currentAdmissionNumber,
                recipientID = targetUserId,
                time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            )
            sendUserToUserMessage(newMessage, conversationId) { success ->
                if (success) {
                    fetchMessages(conversationId)
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Failed to send message")
                    }
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
            scope.launch {
                snackbarHostState.showSnackbar("Failed to send message: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(targetUserId, style = CC.titleTextStyle(context)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor)
            )
        },
        content = { paddingValues ->
            Box {
                Background(context)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(messages) { message ->
                            MessageBubble(
                                message = message,
                                isUser = message.senderID == user.id,
                                context = context,
                                isAdmin = user.isAdmin
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Message") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = GlobalColors.primaryColor,
                                cursorColor = GlobalColors.textColor,
                                focusedLabelColor = GlobalColors.textColor,
                                unfocusedLabelColor = GlobalColors.textColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (message.isNotBlank() && user.name.isNotBlank()) {
                                    sendMessage(message)
                                    message = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.extraColor2),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Send")
                        }
                    }
                }
            }
        }
    )
}

fun generateConversationId(userId1: String, userId2: String): String {
    return if (userId1 < userId2) {
        "$userId1$userId2"
    } else {
        "$userId2$userId1"
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MessageBubble(
    message: Message,
    isUser: Boolean,
    isAdmin: Boolean,
    context: Context,
) {
    val alignment = if (isUser) Alignment.TopEnd else Alignment.TopStart
    val backgroundColor = if (isUser) GlobalColors.extraColor1 else GlobalColors.extraColor2
    val bubbleShape = RoundedCornerShape(
        bottomStart = 16.dp,
        bottomEnd = 16.dp,
        topStart = if (isUser) 16.dp else 0.dp,
        topEnd = if (isUser) 0.dp else 16.dp
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor, bubbleShape)
                .padding(8.dp)
                .align(alignment)
        ) {
            Column {
                if (!isUser) {
                    Row(
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = if (isAdmin) "${message.senderName} (Admin)" else message.senderName,
                            style = CC.descriptionTextStyle(context),
                            fontWeight = FontWeight.Bold,
                            color = GlobalColors.primaryColor
                        )
                    }
                }
                Text(
                    text = message.message,
                    style = CC.descriptionTextStyle(context)
                )
                Text(
                    text = message.time,
                    style = CC.descriptionTextStyle(context),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
        if (!isUser) {
            // Icon with first letter of sender's name, positioned outside the bubble
            Box(
                modifier = Modifier
                    .offset(x = (-16).dp, y = (-16).dp)
                    .size(24.dp)
                    .background(GlobalColors.primaryColor, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.senderName.first().toString(),
                    style = CC.descriptionTextStyle(context),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
