package com.axioo.feed.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.axioo.feed.R

private val provider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs,
    )

private val Inter = GoogleFont("Inter")
private val JetBrainsMono = GoogleFont("JetBrains Mono")

private val InterFamily: FontFamily by lazy {
    FontFamily(
        Font(googleFont = Inter, fontProvider = provider, weight = FontWeight.Light),
        Font(googleFont = Inter, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = Inter, fontProvider = provider, weight = FontWeight.Medium),
        Font(googleFont = Inter, fontProvider = provider, weight = FontWeight.SemiBold),
        Font(googleFont = Inter, fontProvider = provider, weight = FontWeight.Bold),
    )
}

private val JetBrainsMonoFamily: FontFamily by lazy {
    FontFamily(
        Font(googleFont = JetBrainsMono, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = JetBrainsMono, fontProvider = provider, weight = FontWeight.Medium),
    )
}

data class AxiooTypeTokens(
    val sans: FontFamily,
    val mono: FontFamily,
    val typography: Typography,
    val monoLabel: TextStyle,
    val monoCount: TextStyle,
    val monoCategory: TextStyle,
)

@Composable
fun rememberAxiooTypeTokens(): AxiooTypeTokens =
    remember {
        val sans = InterFamily
        val mono = JetBrainsMonoFamily

        val typography =
            Typography(
                displayLarge =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 48.sp,
                        lineHeight = 52.sp,
                        letterSpacing = (-0.5).sp,
                    ),
                displayMedium =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 36.sp,
                        lineHeight = 40.sp,
                        letterSpacing = (-0.4).sp,
                    ),
                headlineLarge =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 28.sp,
                        lineHeight = 32.sp,
                    ),
                headlineMedium =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                    ),
                titleLarge =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                    ),
                titleMedium =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                    ),
                titleSmall =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                bodyLarge =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                    ),
                bodyMedium =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                bodySmall =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    ),
                labelLarge =
                    TextStyle(
                        fontFamily = sans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                labelMedium =
                    TextStyle(
                        fontFamily = mono,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 1.5.sp,
                    ),
                labelSmall =
                    TextStyle(
                        fontFamily = mono,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        letterSpacing = 1.6.sp,
                    ),
            )

        val monoLabel =
            TextStyle(
                fontFamily = mono,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 1.6.sp,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Normal,
            )

        val monoCount =
            TextStyle(
                fontFamily = mono,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.5.sp,
            )

        val monoCategory =
            TextStyle(
                fontFamily = mono,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                letterSpacing = 1.8.sp,
            )

        AxiooTypeTokens(
            sans = sans,
            mono = mono,
            typography = typography,
            monoLabel = monoLabel,
            monoCount = monoCount,
            monoCategory = monoCategory,
        )
    }
