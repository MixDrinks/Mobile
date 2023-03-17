package org.mixdrinks.styles

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Suppress("MagicNumber")
object MixDrinksTextStyles {

  val H1 = TextStyle(
      fontFamily = FontFamily.Default,
      fontSize = 32.sp,
      fontWeight = FontWeight.SemiBold,
      letterSpacing = (-0.07).sp
  )

  val H2 = TextStyle(
      fontFamily = FontFamily.Default,
      fontSize = 28.sp,
      fontWeight = FontWeight.SemiBold,
      letterSpacing = (-0.07).sp
  )

  val H4 = TextStyle(
      fontFamily = FontFamily.Default,
      fontSize = 18.sp,
      fontWeight = FontWeight.W600,
      letterSpacing = (-0.07).sp,
      lineHeight = 20.sp,
  )

  val H5 = TextStyle(
      fontFamily = FontFamily.Default,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = (-0.07).sp,
      lineHeight = 20.sp,
  )

  val H6 = TextStyle(
      fontFamily = FontFamily.Default,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = (-0.07).sp,
      lineHeight = 18.sp,
  )
}
