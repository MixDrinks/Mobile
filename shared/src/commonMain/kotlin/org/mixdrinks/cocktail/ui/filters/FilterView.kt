package org.mixdrinks.cocktail.ui.filters

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mixdrinks.cocktail.ui.widgets.undomain.ContentHolder
import org.mixdrinks.cocktail.ui.widgets.undomain.FlowRow
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.styles.MixDrinksColors
import org.mixdrinks.styles.MixDrinksTextStyles

@Composable
fun FilterView(filterComponent: FilterComponent) {
  Column(
      modifier = Modifier.fillMaxSize()
          .padding(horizontal = 12.dp)
  ) {
    Row {
      Text(
          modifier = Modifier
              .padding(start = 12.dp, bottom = 12.dp),
          color = MixDrinksColors.Black,
          text = "Фільтри",
          style = MixDrinksTextStyles.H1,
      )
      Spacer(Modifier.weight(1f))
      Text(
          modifier = Modifier
              .clickable { filterComponent.close() }
              .padding(start = 12.dp, bottom = 12.dp),
          color = MixDrinksColors.Black,
          text = "Закрити",
          style = MixDrinksTextStyles.H1,
      )
    }

    ContentHolder(
        stateflow = filterComponent.state,
    ) { groups ->
      LazyColumn {
        items(groups) {
          FilterGroup(it, filterComponent)
        }
      }
    }
  }
}

@Composable
fun FilterGroup(filterGroupUi: FilterComponent.FilterScreenElement, filterComponent: FilterComponent) {
  when (filterGroupUi) {
    is FilterComponent.FilterScreenElement.Title -> {
      Text(
          modifier = Modifier
              .padding(bottom = 12.dp),
          color = MixDrinksColors.Black,
          text = filterGroupUi.name,
          style = MixDrinksTextStyles.H2,
      )
    }
    is FilterComponent.FilterScreenElement.FilterGroupUi -> {
      FlowRow(mainAxisSpacing = 4.dp, crossAxisSpacing = 4.dp) {
        filterGroupUi.filterItems.forEach { filterUi ->
          FilterItem(filterGroupUi.filterGroupId, filterUi, filterComponent)
        }
      }
    }
  }
}

@Composable
fun FilterItem(filterGroupId: FilterGroupId, filterUi: FilterComponent.FilterUi, filterComponent: FilterComponent) {
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
      modifier = Modifier
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
                onValueChange = { filterComponent.onValueChange(filterGroupId, filterUi.id, it) },
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
