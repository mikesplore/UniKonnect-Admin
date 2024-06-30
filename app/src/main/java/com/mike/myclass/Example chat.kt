package com.mike.myclass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.launch

data class ChatMessage(val sender: String, val content: String, val isMine: Boolean)

@ExperimentalComposeUiApi
@Composable
fun SimpleChatScreen() {
    val scaffoldState = rememberScaffoldState()
    val title = "Chat with John"

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { SimpleChatTopBar(title) },
        content = { SimpleChatBody() }
    )
}

@Composable
fun SimpleChatTopBar(title: String) {
    TopAppBar(
        title = {
            Row(
                Modifier.width(310.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = title, style = MaterialTheme.typography.h6)
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* Handle back navigation */ }) {
                Icon(Icons.Filled.ArrowBack, "Back")
            }
        }
    )
}

@ExperimentalComposeUiApi
@Composable
private fun SimpleChatBody() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (allMessagesPanel, messageField) = createRefs()
        SimpleAllMessagesList(Modifier.constrainAs(allMessagesPanel) {
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
            top.linkTo(parent.top, 10.dp)
            start.linkTo(parent.start, 10.dp)
            end.linkTo(parent.end, 10.dp)
            bottom.linkTo(messageField.top)
        })

        SimpleMessageArea(Modifier.constrainAs(messageField) {
            width = Dimension.fillToConstraints
            top.linkTo(allMessagesPanel.bottom)
            start.linkTo(parent.start, 10.dp)
            end.linkTo(parent.end, 10.dp)
            bottom.linkTo(parent.bottom, 10.dp)
        })
    }
}

@Composable
private fun SimpleAllMessagesList(modifier: Modifier) {
    val messages = listOf(
        ChatMessage("John", "Hello!", false),
        ChatMessage("You", "Hi there!", true),
        ChatMessage("John", "How are you?", false)
    )

    Box(
        modifier.border(
            width = 1.dp,
            brush = SolidColor(Color.Transparent),
            shape = RectangleShape
        )
    ) {
        if (messages.isEmpty()) {
            Text(text = "No messages yet.", modifier = Modifier.align(Alignment.Center))
        } else {
            val scrollState = rememberLazyListState()
            LazyColumn(state = scrollState, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(messages) { message ->
                    SimpleMessageRow(message)
                }
            }
            val scope = rememberCoroutineScope()
            SideEffect {
                scope.launch { scrollState.animateScrollToItem(messages.size - 1) }
            }
        }
    }
}

@Composable
private fun SimpleMessageRow(chatMessage: ChatMessage) {
    val modifierMyMessage = Modifier
        .background(
            color = MaterialTheme.colors.secondary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(10.dp)
        )
        .padding(10.dp)

    val modifierForeignMessage = Modifier
        .background(
            color = MaterialTheme.colors.primary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(10.dp)
        )
        .padding(10.dp)

    if (chatMessage.isMine) {
        Column(
            modifier = Modifier
                .padding(start = 25.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top
        ) {
            Row(modifierMyMessage) {
                Text(text = chatMessage.content)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .padding(end = 25.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Row(modifierForeignMessage) {
                Text(text = chatMessage.content)
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun SimpleMessageArea(modifier: Modifier) {
    Box(modifier = modifier) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text(text = "Type a message") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
    }
}
