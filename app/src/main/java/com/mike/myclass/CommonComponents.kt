package com.mike.myclass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mike.myclass.ui.theme.Zeyada

object CommonComponents {
    @Composable
    fun PasswordTextField(
        modifier: Modifier = Modifier,
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        enabled: Boolean = true,
        isError: Boolean = false,
        singleLine: Boolean,
        fontViewModel: FontViewModel
    ) {
        var passwordVisibility by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontFamily = fontViewModel.selectedFontFamily.value ?: Zeyada),
            label = { Text(text = label, fontFamily = fontViewModel.selectedFontFamily.value ?: Zeyada) },
            singleLine = true,
            enabled = enabled,
            isError = isError,
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        tint = GlobalColors.textColor,
                        contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(10.dp),
            modifier = modifier
                .width(300.dp)
                .shadow(
                    elevation = 10.dp, shape = RoundedCornerShape(20.dp)
                )
        )
    }

    @Composable
    fun SingleLinedTextField(
        modifier: Modifier = Modifier,
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        enabled: Boolean = true,
        isError: Boolean = false,
        singleLine: Boolean,
        fontViewModel: FontViewModel
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontFamily = fontViewModel.selectedFontFamily.value ?: Zeyada),
            label = { Text(text = label, fontFamily = fontViewModel.selectedFontFamily.value ?: Zeyada, fontSize = 14.sp) },
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(10.dp),
            modifier = modifier
                .width(300.dp)
                .shadow(
                    elevation = 10.dp, shape = RoundedCornerShape(20.dp)
                )
        )
    }

    @Composable
    fun BasicTextField(title: String, onTitleChange: (String) -> Unit, singleLine: Boolean, fontViewModel: FontViewModel) {
        androidx.compose.foundation.text.BasicTextField(
            value = title,
            singleLine = singleLine,
            onValueChange = { onTitleChange(it) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = fontViewModel.selectedFontFamily.value ?: FontFamily.Default),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(
                            GlobalColors.tertiaryColor, shape = MaterialTheme.shapes.small
                        )
                        .padding(8.dp)
                ) {
                    if (title.isEmpty()) {
                        Text("Title", style = descriptionTextStyle(fontViewModel))
                    }
                    innerTextField()
                }
            }
        )
    }

    @Composable
    fun descriptionTextStyle(fontViewModel: FontViewModel): TextStyle {
        return TextStyle(
            fontFamily = fontViewModel.selectedFontFamily.value ?: Zeyada,
            color = GlobalColors.textColor,
            fontSize = 15.sp
        )
    }

    @Composable
    fun backbrush(fontViewModel: FontViewModel): Brush {
        return Brush.verticalGradient(
            listOf(
                GlobalColors.primaryColor,
                GlobalColors.textColor,
                GlobalColors.secondaryColor,
            )
        )

    }

    @Composable
    fun titleTextStyle(fontViewModel: FontViewModel): TextStyle {
        return TextStyle(
            fontFamily = fontViewModel.selectedFontFamily.value ?: FontFamily.Default,
            color = GlobalColors.textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
    }

    @Composable
    fun appTextFieldColors(): TextFieldColors {
        return TextFieldDefaults.colors(
            focusedContainerColor = GlobalColors.primaryColor,
            unfocusedContainerColor = GlobalColors.primaryColor,
            focusedIndicatorColor = GlobalColors.tertiaryColor,
            unfocusedIndicatorColor = GlobalColors.primaryColor,
            focusedLabelColor = GlobalColors.textColor,
            cursorColor = GlobalColors.textColor,
            unfocusedLabelColor = GlobalColors.textColor,
            focusedTextColor = GlobalColors.textColor,
            unfocusedTextColor = GlobalColors.textColor
        )
    }
}
