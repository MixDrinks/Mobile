package org.mixdrinks.cocktail.ui.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mixdrinks.cocktail.data.CocktailsRepository
import org.mixdrinks.dto.CocktailId

class ListComponent(
    private val componentContext: ComponentContext,
    private val cocktailsRepository: CocktailsRepository,
    private val openCocktail: (CocktailId) -> Unit,
) : ComponentContext by componentContext {

  private val _state = mutableStateOf<UiState>(UiState.Loading)
  val state: State<UiState> = _state

  init {
    _state.value = UiState.Loading
    CoroutineScope(Dispatchers.Default).launch {
      _state.value = UiState.Data(
          cocktailsRepository.getCocktails()
              .map {
                Cocktail(
                    it.id,
                    "https://image.mixdrinks.org/cocktails/${it.id.id}/400/${it.id.id}.webp",
                    it.name
                )
              }
      )
    }
  }

  fun onCocktailClick(cocktailId: CocktailId) {
    openCocktail(cocktailId)
  }

  sealed class UiState {
    object Loading : UiState()
    data class Data(val cocktails: List<Cocktail>) : UiState()
  }

  data class Cocktail(
      val id: CocktailId,
      val url: String,
      val name: String,
  )
}
