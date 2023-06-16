package org.mixdrinks.ui.filters

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId

@Composable
internal fun FilterItem(
    modifier: Modifier = Modifier,
    filterUi: FilterItemUiModel,
    onValue: (FilterItemUiModel, Boolean) -> Unit,
) {
    val color = updateTransition(filterUi, label = "Checked indicator")

    val backgroundColor by color.animateColor(
        label = "BackgroundColor"
    ) { filter ->
        when {
            filter.isSelect -> MixDrinksColors.Main
            !filter.isEnable -> Color.Transparent
            else -> MixDrinksColors.White
        }
    }

    val textColor by color.animateColor(
        label = "TextColor"
    ) { filter ->
        when {
            filter.isSelect -> MixDrinksColors.White
            !filter.isEnable -> MixDrinksColors.Grey
            else -> MixDrinksColors.Main
        }
    }

    Card(
        modifier = modifier
            .height(32.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = backgroundColor,
        border = BorderStroke(1.dp, MixDrinksColors.Main)
    ) {
        Box(
            modifier = Modifier
                .toggleable(
                    value = filterUi.isSelect,
                    enabled = filterUi.isEnable,
                    onValueChange = { onValue(filterUi, it) },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                )
                .fillMaxHeight()
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center),
                text = filterUi.name,
                color = textColor,
                style = MixDrinksTextStyles.H6,
            )
        }
    }
}

@Immutable
internal data class FilterItemUiModel(
    val groupId: FilterGroupId,
    val id: FilterId,
    val name: String,
    val isSelect: Boolean,
    val isEnable: Boolean,
)
