package com.mike.myclass

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
    private const val COLORS_PATH = "color_scheme"

    private val defaultScheme = ColorScheme("164863", "427D9D", "9BBEC8", "DDF2FD")

    var currentScheme by mutableStateOf(defaultScheme)

    fun loadColorScheme(context: Context, onComplete: (ColorScheme) -> Unit) {
        val database = Firebase.database
        val colorSchemeRef = database.getReference(COLORS_PATH)

        colorSchemeRef.get().addOnSuccessListener {
            val colorScheme = it.getValue(ColorScheme::class.java)
            if (colorScheme != null) {
                currentScheme = colorScheme
                onComplete(colorScheme)
            } else {
                currentScheme = defaultScheme
                onComplete(defaultScheme)
            }
        }.addOnFailureListener {
            currentScheme = defaultScheme
            onComplete(defaultScheme)
        }
    }

    fun saveColorScheme(scheme: ColorScheme) {
        val database = Firebase.database
        val colorSchemeRef = database.getReference(COLORS_PATH)
        colorSchemeRef.setValue(scheme)
        currentScheme = scheme
    }

    fun resetToDefaultColors() {
        saveColorScheme(defaultScheme)
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
        GlobalColors.loadColorScheme(context) { scheme ->
            primaryColor = scheme.primaryColor
            secondaryColor = scheme.secondaryColor
            tertiaryColor = scheme.tertiaryColor
            textColor = scheme.textColor
        }
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
            Text("Colors", style = CC.titleTextStyle, color = GlobalColors.textColor)
            OutlinedColorTextField(
                component = "Primary Color",
                colorValue = primaryColor,
                onValueChange = { primaryColor = it }
            )
            OutlinedColorTextField(
                component = "Secondary Color",
                colorValue = secondaryColor,
                onValueChange = { secondaryColor = it }
            )
            OutlinedColorTextField(
                component = "Tertiary Color",
                colorValue = primaryColor,
                onValueChange = { primaryColor = it }
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text("Brush", style = CC.titleTextStyle)



            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newScheme = ColorScheme(
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        tertiaryColor = tertiaryColor,
                        textColor = textColor
                    )
                    GlobalColors.saveColorScheme(newScheme)
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
                    GlobalColors.resetToDefaultColors()
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

@Composable
fun OutlinedColorTextField(
    component: String,
    colorValue: String,
    onValueChange: (String) -> Unit
) {
    var isValidColor by remember { mutableStateOf(true) } // State to track validity

    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp)
            .background(GlobalColors.primaryColor, shape = RoundedCornerShape(8.dp))
    ) {

        Row(modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(component, style = CC.descriptionTextStyle)
        CC.SingleLinedTextField(
            value = colorValue,
            onValueChange = { newValue ->
                isValidColor = isValidHexColor(newValue)
                onValueChange(newValue)
            },
            singleLine = true,
            label = "",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .background(if (isValidColor) Color.Transparent else Color.Red.copy(alpha = 0.3f))
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

@Preview
@Composable
fun ColorSettingsPreview() {
    val context = LocalContext.current

    // Load the color scheme when the composable is launched
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context) { scheme ->
            GlobalColors.currentScheme = scheme
        }
    }
    ColorSettings(rememberNavController(), context)
}
