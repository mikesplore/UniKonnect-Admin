package com.mike.myclass

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Replay
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.mike.myclass.ui.theme.Amatic
import com.mike.myclass.ui.theme.Crimson
import com.mike.myclass.ui.theme.Lora
import com.mike.myclass.ui.theme.Segoe
import com.mike.myclass.ui.theme.Zeyada
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
    private const val PREFS_NAME = "color_scheme_prefs"
    private const val COLOR_SCHEME_KEY = "color_scheme"

    private val defaultScheme = ColorScheme("164863", "427D9D", "9BBEC8", "DDF2FD")

    var currentScheme by mutableStateOf(defaultScheme)

    fun loadColorScheme(context: Context): ColorScheme {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(COLOR_SCHEME_KEY, null)
        val scheme = if (json != null) {
            Gson().fromJson(json, ColorScheme::class.java)
        } else {
            defaultScheme
        }
        currentScheme = scheme
        return scheme
    }

    fun saveColorScheme(context: Context, scheme: ColorScheme) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(scheme)
        editor.putString(COLOR_SCHEME_KEY, json)
        editor.apply()
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
    var currentFont by remember { mutableStateOf<FontFamily?>(null) }
    var fontUpdated by remember { mutableStateOf(false) }

    // Load color scheme from SharedPreferences
    LaunchedEffect(Unit) {
        val scheme = GlobalColors.loadColorScheme(context)
        primaryColor = scheme.primaryColor
        secondaryColor = scheme.secondaryColor
        tertiaryColor = scheme.tertiaryColor
        textColor = scheme.textColor
    }

    // Listen to changes in global color scheme and update local states
    LaunchedEffect(GlobalColors.currentScheme) {
        primaryColor = GlobalColors.currentScheme.primaryColor
        secondaryColor = GlobalColors.currentScheme.secondaryColor
        tertiaryColor = GlobalColors.currentScheme.tertiaryColor
        textColor = GlobalColors.currentScheme.textColor
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
                    title = { Text(text = "Colors and Font", style = CC.titleTextStyle(context)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("dashboard") }) {
                            Icon(Icons.Default.ArrowBackIosNew, "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                GlobalColors.resetToDefaultColors(context)
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
                                GlobalColors.saveColorScheme(context, newScheme)
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


                Spacer(modifier = Modifier.height(16.dp))
                ColorPallete()
                Spacer(modifier = Modifier.height(16.dp))
                ColorPicker(context)
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextStyle(context = LocalContext.current) { selectedFont ->
                    currentFont = selectedFont
                    fontUpdated = !fontUpdated // Toggle the state to trigger recomposition
                }
            }
        }
    }
}
data class Theme(val name: String, val colors: List<Color>)
@Composable
fun ColorPallete(){
    val oceanTheme = Theme("Ocean", listOf(
        Color(0xFF00BCD4),
        Color(0xFF2196F3),
        Color(0xFF009688),
        Color(0xFFBBDEFB)
    ))
    val sunsetTheme = Theme("Sunset", listOf(
        Color(0xFFFF9800),
        Color(0xFFFF5722),
        Color(0xFFFFEB3B),
        Color(0xFFFFC107)
    ))

    val forestTheme = Theme("Forest", listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFF388E3C), // Dark Green
        Color(0xFF8BC34A), // Light Green
        Color(0xFFCDDC39)  // Lime
    ))

    val berryTheme = Theme("Berry", listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF3F51B5)  // Indigo
    ))

    val coffeeTheme = Theme("Coffee", listOf(
        Color(0xFF795548), // Brown
        Color(0xFFA1887F), // Light Brown
        Color(0xFFD7CCC8), // Grey
        Color(0xFFBCAAA4)  // Light Grey
    ))
    val pastelTheme = Theme("Pastel", listOf(
        Color(0xFFE0BBE4), // Lavender
        Color(0xFF957DAD), // Dusty Rose
        Color(0xFFD291BC), // Pale Pink
        Color(0xFFFEC8D8)  // Peach
    ))

    val neonTheme = Theme("Neon", listOf(
        Color(0xFF00FFFF), // Cyan
        Color(0xFF00FF00), // Green
        Color(0xFFFF00FF), // Magenta
        Color(0xFFFF0000)  // Red
    ))

    val vintageTheme = Theme("Vintage", listOf(
        Color(0xFFD4AC0D), // Golden Yellow
        Color(0xFF856D4D), // Brown
        Color(0xFFF5CBA7), // Beige
        Color(0xFFF4D03F)  // Bright Yellow
    ))

    val monochromeTheme = Theme("Monochrome", listOf(
        Color(0xFF000000), // Black
        Color(0xFF333333), // Dark Grey
        Color(0xFFCCCCCC), // Light Grey
        Color(0xFFFFFFFF)  // White
    ))

    val tropicalTheme = Theme("Tropical", listOf(
        Color(0xFF00FF7F), // Spring Green
        Color(0xFF32CD32), // Lime Green
        Color(0xFFADFF2F), // Green Yellow
        Color(0xFF228B22)  // Forest Green
    ))

    Row(modifier = Modifier
        .border(
            width = 1.dp,
            color = GlobalColors.textColor,
            shape = RoundedCornerShape(10.dp)
        )
        .background(Color.Transparent, RoundedCornerShape(10.dp))
        .clip(RoundedCornerShape(10.dp))
        .horizontalScroll(rememberScrollState())
        .height(200.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center) {
        ThemeColorRow(oceanTheme.colors)
        ThemeColorRow(sunsetTheme.colors)
        ThemeColorRow(forestTheme.colors)
        ThemeColorRow(berryTheme.colors)
        ThemeColorRow(coffeeTheme.colors)
        ThemeColorRow(pastelTheme.colors)
        ThemeColorRow(neonTheme.colors)
        ThemeColorRow(vintageTheme.colors)
        ThemeColorRow(monochromeTheme.colors)
        ThemeColorRow(tropicalTheme.colors)

    }
}
@Composable
fun ThemeColorRow(colors: List<Color>) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxHeight()
            .width(350.dp)
            .background(Color.Transparent, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp)) // Add clip modifier here
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color)
            )
        }
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
            "#FFFFFF", "#F5F5F5", "#DCDCDC", "#C0C0C0", "#A9A9A9", "#808080", "#696969", "#E0E0E0",
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
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(10.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    Column(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        colorCategories.forEach { (category, colors) ->
            Text(
                text = category,
                style = CC.descriptionTextStyle(context),
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
                                    clipboardManager.setText(
                                        AnnotatedString(
                                            colorCode.removePrefix(
                                                "#"
                                            )
                                        )
                                    )
                                }
                                .border(1.dp, Color.Gray, shape = CircleShape)
                        )
                    }
                }

        }
    }
    }
        }
}

class FontPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("font_prefs", Context.MODE_PRIVATE)

    fun saveSelectedFont(fontName: String?) {
        prefs.edit().putString("selected_font", fontName).apply()
    }

    fun getSelectedFont(): String? {
        return prefs.getString("selected_font", null) // Default to null (system font)
    }
    fun resetToSystemFont() {
        prefs.edit().remove("selected_font").apply()
    }
}

@Composable
fun currentFontFamily(context: Context): FontFamily {
    val fontPrefs = remember { FontPreferences(context) }
    val selectedFontName = fontPrefs.getSelectedFont()

    return when (selectedFontName) {
        "Segoe" -> Segoe
        "Lora" -> Lora
        "Amatic" -> Amatic
        "Crimson" -> Crimson
        "Zeyada" -> Zeyada
        else -> FontFamily.Default // Use system font if no preference is saved
    }
}

@Composable
fun CustomTextStyle(context: Context, onFontSelected: (FontFamily) -> Unit) {
    val fontPrefs = remember { FontPreferences(context) }
    var fontUpdated by remember { mutableStateOf(false) }
    var selectedFontFamily by remember { mutableStateOf<FontFamily?>(null) }
    val fontFamilies = mapOf(
        "Segoe" to Segoe,
        "Amatic" to Amatic,
        "Lora" to Lora,
        "Crimson" to Crimson,
        "Zeyada" to Zeyada,
        "System" to FontFamily.Default
    )

    Column(
        modifier = Modifier
            .background(GlobalColors.primaryColor)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Font Styles", style = CC.descriptionTextStyle(context)) // Assuming CC.descriptionTextStyle(context) is defined elsewhere
        }

        fontFamilies.forEach { (fontName, fontFamily) ->
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { selectedFontFamily = fontFamily },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$fontName - Michael Odhiambo",
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Selected Font Preview:",
                style = CC.titleTextStyle(context),
                fontSize = 18.sp
            )
        }

        Row(
            modifier = Modifier
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(10.dp)
                )
                .fillMaxWidth()
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "This is a preview of the selected font.",
                fontFamily = selectedFontFamily,
                fontSize = 16.sp,
                color = GlobalColors.textColor,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                fontPrefs.saveSelectedFont(fontFamilies.entries.find { it.value == selectedFontFamily }?.key)
                selectedFontFamily?.let { onFontSelected(it) }
                fontUpdated = !fontUpdated // Trigger recomposition in parent
            },
            colors = ButtonDefaults.buttonColors(GlobalColors.primaryColor),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        ) {
            Text("Save", style = CC.descriptionTextStyle(context))
        }
    }
}


@Preview
@Composable
fun ColorSettingsPreview() {
    val context = LocalContext.current
//        CustomTextStyle(
//            context = context,
//            onFontSelected = {}
//        )

    // Load the color scheme when the composable is launched
    LaunchedEffect(Unit) {
        GlobalColors.currentScheme = GlobalColors.loadColorScheme(context)
    }
    ColorSettings(rememberNavController(), LocalContext.current)
}