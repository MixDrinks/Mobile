package org.mixdrinks.cocktail.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.mixdrinks.dto.TagId
import org.mixdrinks.styles.MixDrinksColors
import org.mixdrinks.styles.MixDrinksTextStyles

@Composable
fun DetailView(component: DetailsComponent) {
  val state by component.state.collectAsState()

  Box(
      modifier = Modifier
          .fillMaxSize()
          .background(MixDrinksColors.White),
  ) {
    when (state) {
      is DetailsComponent.UiState.Data -> {
        DetailViewContent((state as DetailsComponent.UiState.Data).data, component)
      }

      else -> Text("Loading / Error")
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
    LazyRow(
        modifier = Modifier.padding(start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      items(cocktail.tags) {
        Tag(it, component::onTagClick)
      }
    }
    Image(
        painter = rememberAsyncImagePainter(cocktail.url),
        contentDescription = "Коктейль ${cocktail.name}",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp),
    )
    Box(modifier = Modifier.fillMaxWidth()) {
      Text(
          modifier = Modifier
              .padding(start = 12.dp, bottom = 12.dp)
              .align(Alignment.CenterStart),
          color = MixDrinksColors.Black,
          text = "Інгрідієнти",
          style = MixDrinksTextStyles.H1,
      )
      Box(
          modifier = Modifier
              .padding(horizontal = 12.dp)
              .align(Alignment.CenterEnd)
      ) {
        Counter(
            count = cocktail.goods.count,
            onPlus = component::onPlusClick,
            onMinus = component::onMinusClick,
        )
      }
    }

    cocktail.goods.goods.forEach {
      Good(it)
    }

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
  }
}

@Composable
fun Good(good: FullCocktailUiModel.GoodUi) {
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
  ) {
    Row {
      Image(
          painter = rememberAsyncImagePainter(good.url),
          contentDescription = good.name,
          contentScale = ContentScale.FillWidth,
          modifier = Modifier.size(64.dp)
      )
      Text(
          modifier = Modifier.align(Alignment.CenterVertically)
              .padding(horizontal = 8.dp),
          text = good.name,
          style = MixDrinksTextStyles.H5,
      )
      Spacer(Modifier.weight(1f).fillMaxHeight())
      Text(
          modifier = Modifier.align(Alignment.CenterVertically)
              .padding(horizontal = 8.dp),
          text = good.amount,
          style = MixDrinksTextStyles.H5,
      )
    }
  }
}

@Composable
fun Counter(
    count: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {
  val counterHeight = 40.dp
  Row {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = MixDrinksColors.Main),
        onClick = onMinus,
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .size(counterHeight)
    ) {
      Text("-", color = MixDrinksColors.White)
    }

    Spacer(
        modifier = Modifier.width(4.dp)
    )

    Box(
        modifier = Modifier
            .size(counterHeight)
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
    ) {
      Text(
          modifier = Modifier.align(Alignment.Center),
          text = count.toString(),
          style = MixDrinksTextStyles.H4,
          color = MixDrinksColors.Black,
      )
    }

    Spacer(
        modifier = Modifier.width(4.dp)
    )

    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = MixDrinksColors.Main),
        onClick = onPlus,
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .size(counterHeight)
    ) {
      Text("+", color = MixDrinksColors.White)
    }

    Spacer(
        modifier = Modifier.width(4.dp)
    )
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
fun Tag(tag: FullCocktailUiModel.TagUi, onTagClick: (TagId) -> Unit) {
  Box(
      modifier = Modifier
          .height(32.dp)
          .padding(horizontal = 4.dp)
          .clickable { onTagClick(tag.id) }
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
