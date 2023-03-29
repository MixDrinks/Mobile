package org.mixdrinks.cocktail.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.mixdrinks.cocktail.ui.details.goods.GoodsView
import org.mixdrinks.cocktail.ui.widgets.undomain.ContentHolder
import org.mixdrinks.styles.MixDrinksColors
import org.mixdrinks.styles.MixDrinksTextStyles

@Composable
fun DetailView(component: DetailsComponent) {
  Box(
      modifier = Modifier
          .fillMaxSize()
          .background(MixDrinksColors.White),
  ) {
    ContentHolder(
        stateflow = component.state
    ) {
      DetailViewContent(it, component)
    }
  }
}

@Composable
fun DetailViewContent(cocktail: FullCocktailUiModel, component: DetailsComponent) {
  Column {
    Row(
        modifier = Modifier
            .background(MixDrinksColors.Main)
            .fillMaxWidth()
            .height(52.dp),
    ) {
      Box(
          modifier = Modifier.size(52.dp)
              .clickable {
                component.close()
              }
      ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp)
                .padding(start = 12.dp),
            painter = rememberAsyncImagePainter("https://image.mixdrinks.org/icons%2Fback.png"),
            contentDescription = "Test"
        )
      }
      Text(
          modifier = Modifier.padding(start = 4.dp)
              .align(Alignment.CenterVertically),
          color = MixDrinksColors.White,
          text = cocktail.name,
          style = MixDrinksTextStyles.H2,
          softWrap = false,
          maxLines = 1,
      )
    }
    Spacer(modifier = Modifier.height(4.dp).fillMaxWidth())
    DetailsScrollContent(cocktail, component)
  }
}

@Composable
fun DetailsScrollContent(cocktail: FullCocktailUiModel, component: DetailsComponent) {
  Column(
      modifier = Modifier.verticalScroll(rememberScrollState())
  ) {
    Tags(cocktail, component)
    Image(
        painter = rememberAsyncImagePainter(cocktail.url),
        contentDescription = "Коктейль ${cocktail.name}",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp),
    )

    GoodsView(component.goodsSubComponent)

    Text(
        modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
        color = MixDrinksColors.Black,
        text = "Рецепт",
        style = MixDrinksTextStyles.H1,
    )
    cocktail.receipt.forEachIndexed { index, text ->
      Receipt(index + 1, text)
      if (index != cocktail.receipt.lastIndex) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .height(1.dp)
                .background(MixDrinksColors.Main)
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp).fillMaxWidth())
    Tools(cocktail)
    Spacer(modifier = Modifier.height(12.dp).fillMaxWidth())
  }
}

@Composable
fun Tools(cocktail: FullCocktailUiModel) {
  LazyRow(
      modifier = Modifier
          .padding(start = 12.dp)
  ) {
    items(cocktail.tools) {
      Tool(it)
    }
  }
}

@Composable
fun Tool(toolUi: FullCocktailUiModel.ToolUi) {
  Card(
      modifier = Modifier
          .width(100.dp)
          .height(124.dp)
          .fillMaxWidth()
          .padding(horizontal = 2.dp)
          .border(
              1.dp,
              MixDrinksColors.Main,
              shape = RoundedCornerShape(8.dp),
          ),
      shape = RoundedCornerShape(8.dp),
  ) {
    Column {
      Image(
          painter = rememberAsyncImagePainter(toolUi.url),
          contentDescription = toolUi.name,
          contentScale = ContentScale.Fit,
          modifier = Modifier.size(100.dp)
      )
      Text(
          modifier = Modifier.align(Alignment.CenterHorizontally)
              .padding(start = 2.dp, end = 2.dp, top = 4.dp),
          text = toolUi.name,
          style = MixDrinksTextStyles.H5,
          textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
fun Receipt(number: Int, text: String) {
  Row(
      modifier = Modifier
          .padding(horizontal = 12.dp)
          .defaultMinSize(minHeight = 32.dp)
  ) {
    Text(
        modifier = Modifier
            .size(32.dp)
            .background(MixDrinksColors.Main, shape = RoundedCornerShape(4.dp))
            .wrapContentHeight(),
        text = number.toString(),
        color = MixDrinksColors.White,
        style = MixDrinksTextStyles.H4,
        textAlign = TextAlign.Center,
    )
    Text(
        modifier = Modifier.padding(start = 8.dp),
        text = text,
        color = MixDrinksColors.Main,
        style = MixDrinksTextStyles.H4,
    )
  }
}

@Composable
fun Tags(cocktail: FullCocktailUiModel, component: DetailsComponent) {
  LazyRow(
      modifier = Modifier.padding(start = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    items(cocktail.tags) {
      Tag(it) {
        when (it) {
          is FullCocktailUiModel.TagUi.Tag -> component.onTagClick(it.id)
          is FullCocktailUiModel.TagUi.Taste -> component.onTasteClick(it.id)
        }
      }
    }
  }
}

@Composable
fun Tag(tag: FullCocktailUiModel.TagUi, onTagClick: () -> Unit) {
  Box(
      modifier = Modifier
          .height(32.dp)
          .padding(horizontal = 4.dp)
          .clickable { onTagClick() }
          .background(MixDrinksColors.Main, shape = RoundedCornerShape(4.dp))
  ) {
    Text(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = 8.dp),
        text = tag.name,
        color = MixDrinksColors.White,
        style = MixDrinksTextStyles.H5,
    )
  }
}
