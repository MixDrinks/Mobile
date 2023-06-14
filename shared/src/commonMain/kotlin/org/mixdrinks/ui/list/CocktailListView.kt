package org.mixdrinks.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.app.utils.ResString
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.filters.FilterItem
import org.mixdrinks.ui.widgets.undomain.ContentHolder
import org.mixdrinks.ui.widgets.undomain.FlowRow

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun CocktailListView(component: ListComponent) {
  Column(
      modifier = Modifier
          .fillMaxHeight()
          .background(MixDrinksColors.White),
  ) {
    Box(
        modifier = Modifier
            .background(MixDrinksColors.Main)
            .fillMaxWidth()
            .height(52.dp),
    ) {
      Box(
          modifier = Modifier
              .clickable {
                component.openFilters()
              }
              .align(Alignment.CenterEnd)
              .size(52.dp)
      ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp),
            painter = painterResource("ic_filter.xml"),
            contentDescription = ResString.filters,
            colorFilter = ColorFilter.tint(Color.White)
        )
      }
    }

    ContentHolder(
        stateflow = component.state,
    ) {
      when (it) {
        is ListComponent.CocktailsListState.PlaceHolder -> {
          PlaceHolder(it, component)
        }

        is ListComponent.CocktailsListState.Cocktails -> {
          CocktailList(it, component)
        }
      }
    }
  }
}

@Composable
internal fun PlaceHolder(placeHolder: ListComponent.CocktailsListState.PlaceHolder, component: ListComponent) {
  Box(
      modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(),
      contentAlignment = Alignment.Center,
  ) {
    Column(
        modifier = Modifier
            .padding(16.dp),
    ) {
      Text(
          text = ResString.cocktailNotFound,
          style = MixDrinksTextStyles.H1,
          color = MixDrinksColors.Black,
          textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
          text = ResString.clearFilterForFoundSomething,
          style = MixDrinksTextStyles.H4,
          color = MixDrinksColors.Black,
          textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(32.dp))
      FlowRow(
          mainAxisSpacing = 4.dp,
          crossAxisSpacing = 4.dp
      ) {
        placeHolder.filters.forEach {
          FilterItem(filterUi = it, onValue = component::onFilterStateChange)
        }
      }
    }
  }
}

@Composable
internal fun CocktailList(cocktails: ListComponent.CocktailsListState.Cocktails, component: ListComponent) {
  LazyColumn {
    items(cocktails.list, key = { it.id.id }) { cocktail ->
      Cocktail(cocktail, component::onCocktailClick)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LazyItemScope.Cocktail(
    cocktail: ListComponent.CocktailsListState.Cocktails.Cocktail,
    onClick: (CocktailId) -> Unit,
) {
  Card(
      modifier = Modifier
          .animateItemPlacement()
          .clickable { onClick(cocktail.id) }
          .height(96.dp)
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .border(
              1.dp,
              MixDrinksColors.Main.copy(alpha = 0.8F),
              shape = RoundedCornerShape(8.dp),
          ),
      shape = RoundedCornerShape(8.dp),
  ) {
    Box {
      Row {
        Image(
            painter = rememberAsyncImagePainter(cocktail.url),
            contentDescription = "Коктейль ${cocktail.name}",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.width(96.dp),
        )

        Column {
          Text(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              text = cocktail.name,
              color = MixDrinksColors.Main,
              style = MixDrinksTextStyles.H5,
          )

          LazyRow(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(cocktail.tags) {
              Text(
                  modifier = Modifier
                      .background(MixDrinksColors.Grey.copy(alpha = 0.8F), shape = RoundedCornerShape(2.dp))
                      .padding(4.dp),
                  text = it,
                  color = MixDrinksColors.Black,
                  style = MixDrinksTextStyles.H6,
              )
            }
          }
        }
      }
    }
  }
}
