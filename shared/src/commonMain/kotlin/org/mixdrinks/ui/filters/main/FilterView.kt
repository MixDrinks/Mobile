package org.mixdrinks.ui.filters.main

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.app.utils.ResString
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.ui.filters.FilterItem
import org.mixdrinks.ui.widgets.CustomButton
import org.mixdrinks.ui.widgets.undomain.ContentHolder
import org.mixdrinks.ui.widgets.undomain.FlowRow

@Composable
internal fun FilterView(filterComponent: FilterComponent) {
  Surface(
      modifier = Modifier.fillMaxSize(),
      color = MixDrinksColors.White,
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      TopAppBar(
          backgroundColor = Color.White,
          title = {
            Text(
                color = MixDrinksColors.Black,
                text = ResString.filters,
                style = MixDrinksTextStyles.H1,
            )
          },
          actions = {
            Text(
                modifier = Modifier
                    .clickable { filterComponent.clear() },
                color = MixDrinksColors.Black,
                text = ResString.clear,
                style = MixDrinksTextStyles.H1,
            )
          }
      )

      Box(
          modifier = Modifier.weight(1F)
              .padding(horizontal = 8.dp)
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
        CustomButton(
            Modifier.align(Alignment.Center)
                .padding(horizontal = 8.dp),
            ResString.apply,
            filterComponent::close
        )
      }
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
              mainAxisSpacing = 4.dp,
              crossAxisSpacing = 4.dp
          ) {
            filterGroupUi.filterItems.forEach { filterItem ->
              FilterItem(
                  modifier = Modifier.animateItemPlacement(tween(300)),
                  filterUi = filterItem,
                  onValue = filterComponent::onFilterStateChange,
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
internal fun AddMoreFilterButton(
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
