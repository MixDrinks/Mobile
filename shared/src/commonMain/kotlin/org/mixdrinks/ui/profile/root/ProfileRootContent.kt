package org.mixdrinks.ui.profile.root

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.app.utils.ResString
import org.mixdrinks.ui.widgets.MixDrinksHeader

@Composable
internal fun ProfileRootContent(component: ProfileRootComponent) {
    val showAuthDialog = remember { mutableStateOf(false) }
    Box {
        Column {
            MixDrinksHeader(
                name = ResString.profile,
            )
            ProfileButton(ResString.visitedCocktails, MixDrinksColors.Black) {
                component.navigateToVisitedCocktails()
            }
            ProfileButton(ResString.logout, MixDrinksColors.Black) {
                component.logout()
            }
            ProfileButton(ResString.deleteAccount, MixDrinksColors.Red) {
                showAuthDialog.value = true
            }
        }

        if (showAuthDialog.value) {
            CustomAlertDialog(
                onDismiss = { showAuthDialog.value = false },
                onConfirm = { component.deleteAccount() }
            )
        }
    }
}

@Composable
private fun ProfileButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.background(
            color = MixDrinksColors.White,
            shape = RoundedCornerShape(8.dp)
        )
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(48.dp),
            text = text.uppercase(),
            style = MixDrinksTextStyles.H3.copy(fontWeight = FontWeight.W400),
            color = color,
        )
    }
}

@Composable
private fun CustomAlertDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Black.copy(alpha = 0.5F))
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 8.dp
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Text(
                    text = ResString.deleteAccountDialogMessage,
                    modifier = Modifier.padding(8.dp), fontSize = 20.sp
                )

                Row(Modifier.padding(top = 10.dp)) {
                    OutlinedButton(
                        onClick = { onDismiss() },

                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = ResString.deleteAccountDialogNo,
                            style = MixDrinksTextStyles.H4,
                        )
                    }
                    Button(
                        onClick = { onConfirm() },
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1F)
                    ) {
                        Text(
                            text = ResString.deleteAccountDialogYes,
                            style = MixDrinksTextStyles.H4,
                        )
                    }
                }
            }
        }
    }
}
