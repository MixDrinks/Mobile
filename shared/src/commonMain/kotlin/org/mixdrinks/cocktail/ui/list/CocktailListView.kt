package org.mixdrinks.cocktail.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.mixdrinks.cocktail.ui.widgets.undomain.ContentHolder
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.styles.MixDrinksColors
import org.mixdrinks.styles.MixDrinksTextStyles

@Composable
fun CocktailListView(component: ListComponent) {
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
            painter = rememberAsyncImagePainter("https://image.mixdrinks.org/icons%2Ffilter.svg"),
            contentDescription = "Filter",
            colorFilter = ColorFilter.tint(Color.White)
        )
      }
    }

    ContentHolder(
        stateflow = component.state,
    ) {
      LazyColumn {
        items(it, key = { it.id.id }) {
          Box(modifier = Modifier.padding(4.dp)) {
            Cocktail(it, component::onCocktailClick)
          }
        }
      }
    }
  }
}

@Composable
fun Cocktail(cocktail: ListComponent.Cocktail, onClick: (CocktailId) -> Unit) {
  Card(
      modifier = Modifier
          .clickable { onClick(cocktail.id) }
          .height(80.dp)
          .fillMaxWidth()
          .padding(2.dp)
          .border(
              1.dp,
              MixDrinksColors.Main,
              shape = RoundedCornerShape(8.dp),
          ),
      shape = RoundedCornerShape(8.dp),
  ) {
    Box {
      Row {
        Image(
            painter = rememberAsyncImagePainter(cocktail.url),
            contentDescription = "Коктейль ${cocktail.name}",
            contentScale = ContentScale.None,
            modifier = Modifier.width(80.dp),
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = cocktail.name,
            color = MixDrinksColors.Main,
            style = MixDrinksTextStyles.H5,
        )
      }
    }
  }
}
