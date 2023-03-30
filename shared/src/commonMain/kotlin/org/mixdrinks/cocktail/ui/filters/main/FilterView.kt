package org.mixdrinks.cocktail.ui.filters.main

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mixdrinks.cocktail.ui.widgets.CustomButton
import org.mixdrinks.cocktail.ui.widgets.undomain.ContentHolder
import org.mixdrinks.cocktail.ui.widgets.undomain.FlowRow
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.styles.MixDrinksColors
import org.mixdrinks.styles.MixDrinksTextStyles
import org.mixdrinks.utils.ResString

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
              .clickable { filterComponent.clear() }
              .padding(start = 12.dp, bottom = 12.dp),
          color = MixDrinksColors.Black,
          text = "Очистити",
          style = MixDrinksTextStyles.H1,
      )
    }

    Box(
        modifier = Modifier.weight(1F)
    ) {
      ContentHolder(
          stateflow = filterComponent.state,
      ) { groups ->
        FilterContent(groups, filterComponent)
      }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
    ) {
      CustomButton(Modifier.align(Alignment.Center), ResString.apply, filterComponent::close)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilterContent(groups: List<FilterComponent.FilterScreenElement>, filterComponent: FilterComponent) {
  LazyColumn {
    items(items = groups, key = { it.key }) { filterGroupUi ->
      when (filterGroupUi) {
        is FilterComponent.FilterScreenElement.FilterGroupUi -> {
          FlowRow(
              modifier = Modifier.animateItemPlacement(tween(300)),
              mainAxisSpacing = 4.dp,
              crossAxisSpacing = 4.dp
          ) {
            filterGroupUi.filterItems.forEach { filterUi ->
              FilterItem(
                  modifier = Modifier.animateItemPlacement(tween(300)),
                  filterGroupId = filterGroupUi.filterGroupId,
                  filterUi = filterUi,
                  filterComponent = filterComponent,
              )
            }
          }
        }
        is FilterComponent.FilterScreenElement.Title -> {
          Text(
              modifier = Modifier
                  .animateItemPlacement(tween(300))
                  .padding(bottom = 12.dp),
              color = MixDrinksColors.Black,
              text = filterGroupUi.name,
              style = MixDrinksTextStyles.H2,
          )
        }
        is FilterComponent.FilterScreenElement.FilterOpenSearch -> {
          AddMoreFilterButton(
              modifier = Modifier
                  .animateItemPlacement(tween(300))
                  .fillMaxWidth(),
              filterGroupId = filterGroupUi.filterGroupId,
              text = filterGroupUi.text,
              filterComponent = filterComponent,
          )
        }
      }
    }
    item {
      Spacer(modifier = Modifier.fillMaxWidth().height(32.dp))
    }
  }
}

@Composable
fun AddMoreFilterButton(
    modifier: Modifier = Modifier,
    filterGroupId: FilterGroupId,
    text: String,
    filterComponent: FilterComponent,
) {
  Button(
      modifier = modifier
          .fillMaxWidth()
          .padding(top = 4.dp)
          .height(32.dp),
      onClick = { filterComponent.openDetailSearch(filterGroupId) },
      colors = ButtonDefaults.buttonColors(MixDrinksColors.White),
      shape = RoundedCornerShape(16.dp),
      border = BorderStroke(1.dp, MixDrinksColors.Main)
  ) {
    Text(
        text = text,
        color = MixDrinksColors.Main,
        style = MixDrinksTextStyles.H6,
    )
  }
}


@Composable
fun FilterItem(
    modifier: Modifier = Modifier,
    filterGroupId: FilterGroupId,
    filterUi: FilterComponent.FilterUi,
    filterComponent: FilterComponent,
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
