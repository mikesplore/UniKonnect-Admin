package com.mike.myclass

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Replay

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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
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

    private val defaultScheme = ColorScheme("164863", "427D9D", "9BBEC8", "DDF2FD")

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
                navigationIcon = {
                    IconButton(onClick = {navController.navigate("dashboard")}) {
                        Icon(Icons.Default.ArrowBackIosNew,"Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            GlobalColors.resetToDefaultColors(context)
                            refreshTrigger = !refreshTrigger
                        }
                    ) {
                        Icon(Icons.Filled.Replay, "Revert", tint = GlobalColors.tertiaryColor)
                    }
                    IconButton(
                        onClick = {
                            val newScheme = ColorScheme(
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor,
                                tertiaryColor = tertiaryColor,
                                textColor = textColor
                            )
                            GlobalColors.saveColorScheme(context,newScheme)
                            refreshTrigger = !refreshTrigger
                        }
                    ) {
                        Icon(Icons.Filled.Check, "Save", tint = GlobalColors.tertiaryColor)
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
            ColorPicker(context)


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
fun ColorPicker(context: Context) {
    var selectedColor by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    // Define color categories
    val colorCategories = mapOf(
        "Black" to listOf(
            "#000000"
        ),
        "White" to listOf(
            "#FFFFFF", "#F5F5F5", "#DCDCDC", "#C0C0C0", "#A9A9A9", "#808080", "#696969","#E0E0E0",
            "#F8F8FF", "#FFFAFA", "#F0F0F0", "#E8E8E8"
        ),
        "Red" to listOf(
            "#FFFAFA", "#FFC0CB", "#FF69B4", "#FF1493", "#FF6347", "#FF4500", "#FF0000", "#8B0000",
            "#FFA07A", "#FA8072", "#E9967A", "#FF7F50", "#FF6A6A", "#FF4040"
        ),
        "Orange" to listOf(
            "#FFF5EE", "#FFE4B5", "#FFA07A", "#FF7F50", "#FF6347", "#FF4500", "#FF8C00", "#FFA500",
            "#FFD700", "#FFA07A", "#FF8247", "#FF8C69", "#FFD39B", "#FFB90F"
        ),
        "Yellow" to listOf(
            "#FFFFE0", "#FFFACD", "#FAFAD2", "#FFEFD5", "#FFE4B5", "#FFD700", "#FFD700", "#FFD700",
            "#FFEC8B", "#FFDEAD", "#FFF8DC", "#FFFF00", "#FFFACD", "#F0E68C"
        ),
        "Green" to listOf(
            "#F0FFF0", "#98FB98", "#90EE90", "#00FA9A", "#00FF7F", "#3CB371", "#2E8B57", "#006400",
            "#00FF00", "#32CD32", "#7FFF00", "#7CFC00", "#ADFF2F", "#228B22"
        ),
        "Cyan" to listOf(
            "#E0FFFF", "#AFEEEE", "#7FFFD4", "#40E0D0", "#48D1CC", "#00CED1", "#20B2AA", "#008B8B",
            "#00FFFF", "#00BFFF", "#1E90FF", "#87CEEB", "#87CEFA", "#B0E0E6"
        ),
        "Blue" to listOf(
            "#ADD8E6", "#87CEEB", "#87CEFA", "#4682B4", "#4169E1", "#0000FF", "#0000CD", "#00008B",
            "#5F9EA0", "#6495ED", "#00BFFF", "#1E90FF", "#4169E1", "#000080"
        ),
        "Purple" to listOf(
            "#E6E6FA", "#D8BFD8", "#DDA0DD", "#DA70D6", "#BA55D3", "#9932CC", "#9400D3", "#800080",
            "#8A2BE2", "#9370DB", "#7B68EE", "#6A5ACD", "#483D8B", "#4B0082"
        ),
        "Brown" to listOf(
            "#FFF8DC", "#FFEBCD", "#FFE4C4", "#FFDAB9", "#FFDEAD", "#DEB887", "#D2B48C", "#A0522D",
            "#8B4513", "#A52A2A", "#D2691E", "#CD853F", "#8B4513", "#BC8F8F"
        ),
        "Miscellaneous" to listOf(
            "#F5F5DC", "#EEE8AA", "#F0E68C", "#BDB76B", "#8B4513", "#A52A2A", "#D2691E", "#CD5C5C",
            "#F4A460", "#DAA520", "#D2B48C", "#C0C0C0", "#708090", "#778899"
        )
    )


    Column(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        colorCategories.forEach { (category, colors) ->
            Text(
                text = category,
                style = CC.descriptionTextStyle,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            val rows = colors.chunked(5)
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEach { colorCode ->
                        Box(
                            modifier = Modifier
                                .padding(3.dp)
                                .size(50.dp)
                                .background(
                                    Color(android.graphics.Color.parseColor(colorCode)),
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColor = colorCode
                                    clipboardManager.setText(AnnotatedString(colorCode.removePrefix("#")))
                                }
                                .border(1.dp, Color.Gray, shape = CircleShape)
                        )
                    }
                }
            }
        }
    }
}



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