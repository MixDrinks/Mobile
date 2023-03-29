package org.mixdrinks.cocktail.ui.filters.search

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.mixdrinks.cocktail.ui.widgets.undomain.ContentHolder
import org.mixdrinks.styles.MixDrinksColors
import org.mixdrinks.styles.MixDrinksTextStyles

@Composable
fun SearchItemView(searchItemComponent: SearchItemComponent) {
  Box(
      modifier = Modifier
          .background(MixDrinksColors.White)
          .fillMaxSize()
  ) {
    Card(
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        backgroundColor = MixDrinksColors.White,
        elevation = 4.dp,
        modifier = Modifier
            .background(MixDrinksColors.White)
            .fillMaxSize()
    ) {
      ItemList(searchItemComponent)
    }
  }
}

@Composable
fun ItemList(searchItemComponent: SearchItemComponent) {
  ContentHolder(
      stateflow = searchItemComponent.state,
  ) {
    LazyColumn {
      items(items = it) { item ->
        Item(item = item)
      }
    }
  }
}

@Composable
fun Item(item: SearchItemComponent.ItemUiModel) {
  val color = updateTransition(item.isSelected, label = "Checked indicator")

  val backgroundColor by color.animateColor(
      label = "BackgroundColor"
  ) { isSelect ->
    if (isSelect) {
      MixDrinksColors.Main
    } else {
      MixDrinksColors.White
    }
  }

  Card(
      modifier = Modifier
          .height(64.dp)
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 2.dp)
          .border(
              1.dp,
              MixDrinksColors.Main,
              shape = RoundedCornerShape(8.dp),
          ),
      shape = RoundedCornerShape(8.dp),
      backgroundColor = backgroundColor,
  ) {
    Row {
      Image(
          painter = rememberAsyncImagePainter(item.imageUrl),
          contentDescription = item.name,
          contentScale = ContentScale.FillWidth,
          modifier = Modifier.size(64.dp)
      )
      Text(
          modifier = Modifier.align(Alignment.CenterVertically)
              .padding(horizontal = 8.dp),
          text = item.name,
          style = MixDrinksTextStyles.H5,
      )
    }
  }
}
