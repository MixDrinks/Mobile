package org.mixdrinks.cocktail.ui.filters.search

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
            .padding(top = 24.dp)
            .background(MixDrinksColors.White)
            .fillMaxSize()
    ) {
      Column {
        val textState by searchItemComponent.textState.collectAsState()
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MixDrinksColors.White,
                textColor = MixDrinksColors.Main,
                cursorColor = MixDrinksColors.Main,
                focusedIndicatorColor = MixDrinksColors.Main,
                unfocusedIndicatorColor = MixDrinksColors.Main,
                focusedLabelColor = MixDrinksColors.Main,
            ),
            value = textState,
            onValueChange = { searchItemComponent.onSearchQueryChanged(it) },
            label = { Text("Пошук") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            trailingIcon = {
              Icon(
                  Icons.Filled.Clear,
                  contentDescription = "Clear search",
                  modifier = Modifier
                      .offset(x = 12.dp)
                      .clickable {
                        searchItemComponent.onSearchQueryChanged("")
                      }
              )
            }
        )
        ItemList(searchItemComponent)
      }
    }
  }
}

@Composable
fun ItemList(searchItemComponent: SearchItemComponent) {
  ContentHolder(
      stateflow = searchItemComponent.state,
  ) {
    LazyColumn {
      items(items = it, key = { it.id.value }) { item ->
        Item(item = item, searchItemComponent)
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.Item(item: SearchItemComponent.ItemUiModel, searchItemComponent: SearchItemComponent) {
  val color = updateTransition(item.isSelected, label = "Checked indicator")

  val backgroundColor by color.animateColor(
      label = "BackgroundColor"
  ) { isSelect ->
    if (isSelect) {
      MixDrinksColors.Main.copy(alpha = 0.5f)
    } else {
      MixDrinksColors.White
    }
  }

  Card(
      modifier = Modifier
          .animateItemPlacement()
          .height(64.dp)
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 2.dp)
          .toggleable(
              value = item.isSelected,
              enabled = true,
              onValueChange = { searchItemComponent.onItemClicked(item.id, it) },
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
          )
          .border(
              1.dp,
              MixDrinksColors.Main,
              shape = RoundedCornerShape(8.dp),
          ),
      shape = RoundedCornerShape(8.dp),
  ) {
    Row {
      Image(
          painter = rememberAsyncImagePainter(item.imageUrl),
          contentDescription = item.name,
          contentScale = ContentScale.FillWidth,
          modifier = Modifier.size(64.dp)
      )
      Box(
          modifier = Modifier
              .weight(1F)
              .fillMaxHeight()
              .background(backgroundColor)
              .padding(end = 8.dp)
      ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp),
            text = item.name,
            style = MixDrinksTextStyles.H5,
        )
        Box(
            modifier = Modifier
                .background(color = MixDrinksColors.Main, shape = RoundedCornerShape(16.dp))
                .defaultMinSize(minWidth = 32.dp)
                .height(32.dp)
                .align(Alignment.CenterEnd)
        ) {
          Text(
              modifier = Modifier
                  .align(Alignment.Center)
                  .padding(horizontal = 8.dp),
              text = item.count.toString(),
              style = MixDrinksTextStyles.H4,
              color = MixDrinksColors.White,
          )
        }
      }
    }
  }
}
