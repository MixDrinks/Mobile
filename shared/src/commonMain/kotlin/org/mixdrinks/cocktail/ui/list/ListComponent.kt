package org.mixdrinks.cocktail.ui.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mixdrinks.cocktail.ui.RootComponent
import org.mixdrinks.cocktail.ui.widgets.undomain.UiState
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.utils.ImageUrlCreators
import kotlin.coroutines.EmptyCoroutineContext

class ListComponent(
    private val componentContext: ComponentContext,
    private val cocktailListRepository: CocktailListRepository,
    private val navigation: StackNavigation<RootComponent.Config>,
) : ComponentContext by componentContext {

  val state: StateFlow<UiState<List<Cocktail>>> = flow {
    emitAll(cocktailListRepository.getCocktails()
        .map { cocktails ->
          UiState.Data(cocktails.map { cocktail ->
            Cocktail(
                cocktail.id,
                ImageUrlCreators.createUrl(cocktail.id, ImageUrlCreators.Size.SIZE_400),
                cocktail.name
            )
          })
        })
  }.stateIn(CoroutineScope(EmptyCoroutineContext), SharingStarted.Eagerly, UiState.Loading)

  fun openFilters() {
    navigation.push(RootComponent.Config.FilterConfig)
  }

  fun onCocktailClick(cocktailId: CocktailId) {
    navigation.push(RootComponent.Config.DetailsConfig(id = cocktailId.id))
  }

  data class Cocktail(
      val id: CocktailId,
      val url: String,
      val name: String,
  )
}
