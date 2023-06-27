package org.mixdrinks.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles


@Composable
internal fun AuthView(modifier: Modifier, onClose: () -> Unit) {
    Column(modifier = modifier
        .wrapContentSize()
        .background(shape = RoundedCornerShape(6.dp), color = MixDrinksColors.Grey)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Авторизуся",
            style = MixDrinksTextStyles.H2,
            textAlign = TextAlign.Center,
            color = MixDrinksColors.White,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SocialButton(
            socialButtonType = SocialButtonType.Google,
            onClick = {
                AuthCallbacks.googleAuthStart()
            })

        SocialButton(
            socialButtonType = SocialButtonType.Apple,
            onClick = {
                AuthCallbacks.appleAuthStart()
            }
        )

        Button(
            modifier = Modifier.padding(8.dp).height(48.dp).fillMaxWidth(),
            onClick = { onClose() },
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
            )
        ) {
            Text(text = "Закрити", modifier = Modifier.padding(6.dp))
        }
    }
}

internal enum class SocialButtonType(
    val icon: String,
    val text: String,
) {
    Google("ic_google.xml", "Sign in with Google"),
    Apple("ic_apple.xml", "Sign in with Apple"),
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun SocialButton(socialButtonType: SocialButtonType, onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(8.dp).height(48.dp).fillMaxWidth(),
        onClick = { onClick() },
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
        )
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(48.dp),
            painter = painterResource(socialButtonType.icon),
            contentDescription = socialButtonType.text,
            contentScale = ContentScale.Fit,
        )
        Text(text = socialButtonType.text, modifier = Modifier.padding(6.dp))
    }
}
