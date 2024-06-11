package com.mike.myclass

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import java.io.File
import com.mike.myclass.CommonComponents as CC

data class ColorScheme(
    val primaryColor: String,
    val secondaryColor: String,
    val tertiaryColor: String,
    val textColor: String
)

fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor("#$hex"))
    } catch (e: IllegalArgumentException) {
        Color.Unspecified
    }
}

object GlobalColors {
    private const val COLORS_FILE_NAME = "color_scheme.json"

    private val defaultScheme = ColorScheme("050C9C", "3572EF", "3ABEF9", "A7E6FF")

    var currentScheme by mutableStateOf(defaultScheme)

    fun loadColorScheme(context: Context): ColorScheme {
        val file = File(context.filesDir, COLORS_FILE_NAME)
        return if (file.exists()) {
            val json = file.readText()
            Gson().fromJson(json, ColorScheme::class.java)
        } else {
            defaultScheme
        }
    }

    fun saveColorScheme(context: Context, scheme: ColorScheme) {
        val file = File(context.filesDir, COLORS_FILE_NAME)
        if (file.exists()) {
            file.delete()  // Delete the old color scheme file
        }
        val json = Gson().toJson(scheme)
        file.writeText(json)
        currentScheme = scheme
    }

    fun resetToDefaultColors(context: Context) {
        saveColorScheme(context, defaultScheme)
    }

    val primaryColor: Color
        get() = parseColor(currentScheme.primaryColor)

    val secondaryColor: Color
        get() = parseColor(currentScheme.secondaryColor)

    val tertiaryColor: Color
        get() = parseColor(currentScheme.tertiaryColor)

    val textColor: Color
        get() = parseColor(currentScheme.textColor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSettings(navController: NavController, context: Context) {
    var primaryColor by remember { mutableStateOf(GlobalColors.currentScheme.primaryColor) }
    var secondaryColor by remember { mutableStateOf(GlobalColors.currentScheme.secondaryColor) }
    var tertiaryColor by remember { mutableStateOf(GlobalColors.currentScheme.tertiaryColor) }
    var textColor by remember { mutableStateOf(GlobalColors.currentScheme.textColor) }

    // Listen to changes in global color scheme and update local states
    LaunchedEffect(GlobalColors.currentScheme) {
        primaryColor = GlobalColors.currentScheme.primaryColor
        secondaryColor = GlobalColors.currentScheme.secondaryColor
        tertiaryColor = GlobalColors.currentScheme.tertiaryColor
        textColor = GlobalColors.currentScheme.textColor
    }

    var refreshTrigger by remember { mutableStateOf(false) } // Trigger to force recomposition

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "COLORS", style = CC.titleTextStyle) },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("dashboard") }
                    ) {
                        Icon(Icons.Filled.ArrowBackIosNew, "Back", tint = GlobalColors.tertiaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor,
                    navigationIconContentColor = GlobalColors.textColor,
                )
            )
        },
        containerColor = GlobalColors.primaryColor,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            ColorInputField(
                label = "Primary Color",
                colorValue = primaryColor,
                onValueChange = { newValue -> primaryColor = newValue }
            )

            ColorInputField(
                label = "Secondary Color",
                colorValue = secondaryColor,
                onValueChange = { newValue -> secondaryColor = newValue }
            )

            ColorInputField(
                label = "Tertiary Color",
                colorValue = tertiaryColor,
                onValueChange = { newValue -> tertiaryColor = newValue }
            )

            ColorInputField(
                label = "Text Color",
                colorValue = textColor,
                onValueChange = { newValue -> textColor = newValue }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newScheme = ColorScheme(
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        tertiaryColor = tertiaryColor,
                        textColor = textColor
                    )
                    GlobalColors.saveColorScheme(context,newScheme)
                    refreshTrigger = !refreshTrigger // Toggle the trigger to force recomposition
                },
                colors = ButtonDefaults.buttonColors(GlobalColors.secondaryColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(250.dp)
            ) {
                Text("Save Colors", style = CC.descriptionTextStyle)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    GlobalColors.resetToDefaultColors(context)
                    refreshTrigger = !refreshTrigger // Toggle the trigger to force recomposition
                },
                colors = ButtonDefaults.buttonColors(GlobalColors.secondaryColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(270.dp)
            ) {
                Text("Revert to Default Colors", style = CC.descriptionTextStyle)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorInputField(
    label: String,
    colorValue: String,
    onValueChange: (String) -> Unit
) {
    var isValidColor by remember { mutableStateOf(true) } // State to track validity

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = CC.descriptionTextStyle,
            color = GlobalColors.textColor
        )
        OutlinedTextField(
            value = colorValue,
            onValueChange = { newValue ->
                isValidColor = isValidHexColor(newValue)
                onValueChange(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isValidColor) GlobalColors.textColor else Color.Red,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
                .background(GlobalColors.primaryColor, shape = RoundedCornerShape(8.dp)),
            textStyle = CC.descriptionTextStyle,
            isError = !isValidColor,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GlobalColors.textColor,
                unfocusedBorderColor = GlobalColors.tertiaryColor,
                cursorColor = GlobalColors.textColor,
                focusedTextColor = GlobalColors.textColor,
                unfocusedTextColor = GlobalColors.textColor,
                errorBorderColor = Color.Red
            ),
            singleLine = true
        )
        if (!isValidColor) {
            Text(
                text = "Invalid color code",
                color = Color.Red,
                style = CC.descriptionTextStyle,
                fontSize = 12.sp
            )
        }
    }
}

// Helper function to check if a string is a valid hex color code
fun isValidHexColor(colorString: String): Boolean {
    return try {
        Color(android.graphics.Color.parseColor("#$colorString")) // Try parsing with #
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}


@Composable
fun OutlinedColorTextField(
    label: String,
    colorValue: String,
    onValueChange: (String) -> Unit
) {
    var isValidColor by remember { mutableStateOf(true) } // State to track validity

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .background(GlobalColors.primaryColor, shape = RoundedCornerShape(8.dp))
    ) {
        Text(label, style = CC.descriptionTextStyle)
        CC.SingleLinedTextField(
            value = colorValue,
            onValueChange = { newValue ->
                isValidColor = isValidHexColor(newValue)
                onValueChange(newValue)
            },
            singleLine = true,
            label = "",
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isValidColor) Color.Transparent else Color.Red.copy(alpha = 0.3f))
        )
    }
}

// Helper function to check if a string is a valid hex color code


@Preview
@Composable
fun ColorSettingsPreview() {
    val context = LocalContext.current

    // Load the color scheme when the composable is launched
    LaunchedEffect(Unit) {
        GlobalColors.currentScheme = GlobalColors.loadColorScheme(context)
    }
    ColorSettings(rememberNavController(), LocalContext.current)
}