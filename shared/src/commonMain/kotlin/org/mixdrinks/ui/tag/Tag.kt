package org.mixdrinks.ui.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.dto.TagId
import org.mixdrinks.ui.list.CocktailsListState

@Composable
internal fun Tag(tag: CocktailsListState.TagUIModel, onClick: (TagId) -> Unit) {
    Tag(name = tag.name, onClick = { onClick(tag.id) })
}

@Composable
internal fun Tag(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .padding(horizontal = 2.dp)
            .clickable { onClick() }
            .background(MixDrinksColors.Secondary, shape = RoundedCornerShape(4.dp))
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 8.dp),
            text = name,
            color = MixDrinksColors.DarkGrey,
            style = MixDrinksTextStyles.H5,
        )
    }
}

