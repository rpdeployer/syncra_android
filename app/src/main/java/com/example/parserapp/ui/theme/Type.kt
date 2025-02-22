package com.example.parserapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.parserapp.R

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.W400, FontStyle.Normal)
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.W400, FontStyle.Normal)
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.W400, FontStyle.Normal)
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.W400, FontStyle.Normal)
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.W400, FontStyle.Normal)
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.25.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.W400, FontStyle.Normal)
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)