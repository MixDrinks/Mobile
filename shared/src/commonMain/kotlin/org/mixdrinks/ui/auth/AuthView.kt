package org.mixdrinks.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun AuthView(component: AuthComponent) {
    Column {
        Button(
            onClick = {
                AuthCallbacks.googleAuthStart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(4.dp)
                    .fillMaxSize(),
                painter = painterResource("ic_google.xml"),
                contentDescription = "Sign in by Google",
                contentScale = ContentScale.Fit,
            )
            Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
        }
        Button(
            onClick = {
                AuthCallbacks.googleAuthStart()
            },
            content = {
                Text("Google login")
            }
        )

        Button(
            onClick = {
                AuthCallbacks.appleAuthStart()
            },
            content = {
                Text("Email login")
            }
        )
    }

}
