package org.mixdrinks.cocktail.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.mixdrinks.cocktail.data.MixDrinksService
import org.mixdrinks.cocktail.data.SnapshotRepository
import org.mixdrinks.cocktail.ui.details.DetailsComponent
import org.mixdrinks.cocktail.ui.details.FullCocktailRepository
import org.mixdrinks.cocktail.ui.details.goods.GoodsRepository
import org.mixdrinks.cocktail.ui.filters.main.FilterComponent
import org.mixdrinks.cocktail.ui.filters.FilterRepository
import org.mixdrinks.cocktail.ui.filters.search.ItemRepository
import org.mixdrinks.cocktail.ui.filters.search.SearchItemComponent
import org.mixdrinks.cocktail.ui.list.CocktailListRepository
import org.mixdrinks.cocktail.ui.list.ListComponent
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailId

object Graph {
  private val ktorfit = Ktorfit.Builder()
      .httpClient(HttpClient {
        install(ContentNegotiation) {
          json(Json {
            isLenient = true;
            ignoreUnknownKeys = true
          })
        }
      })
      .baseUrl("https://api.mixdrinks.org/")
      .build()
      .create<MixDrinksService>()


  val snapshotRepository: SnapshotRepository = SnapshotRepository(ktorfit)

  val filterRepository = FilterRepository { snapshotRepository.get() }
}

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

  private val navigation = StackNavigation<Config>()

  private val _stack =
      childStack(
          source = navigation,
          initialConfiguration = Config.ListConfig,
          handleBackButton = true,
          childFactory = ::createChild
      )

  val stack: Value<ChildStack<*, Child>> = _stack

  private fun createChild(config: Config, componentContext: ComponentContext): Child =
      when (config) {
        Config.ListConfig -> Child.List(listScreen(componentContext))
        is Config.DetailsConfig -> Child.Details(detailsScreen(componentContext, config))
        Config.FilterConfig -> Child.Filters(filterScreen(componentContext))
        is Config.SearchItemConfig -> Child.ItemSearch(searchItemScreen(componentContext, config.searchItemType))
      }

  private fun listScreen(componentContext: ComponentContext): ListComponent =
      ListComponent(
          componentContext = componentContext,
          cocktailListRepository = CocktailListRepository(
              suspend { Graph.snapshotRepository.get() },
              Graph.filterRepository,
              suspend { CocktailSelector(Graph.filterRepository.getFilterGroups().map { it.toFilterGroup() }) },
          ),
          navigation = navigation,
      )

  private fun detailsScreen(componentContext: ComponentContext, config: Config.DetailsConfig): DetailsComponent {
    return DetailsComponent(
        componentContext,
        FullCocktailRepository { Graph.snapshotRepository.get() },
        CocktailId(config.id),
        navigation,
        GoodsRepository { Graph.snapshotRepository.get() }
    )
  }

  private fun filterScreen(componentContext: ComponentContext): FilterComponent {
    return FilterComponent(
        componentContext,
        Graph.filterRepository,
        suspend { CocktailSelector(Graph.filterRepository.getFilterGroups().map { it.toFilterGroup() }) },
        navigation,
    )
  }

  private fun searchItemScreen(
      component: ComponentContext,
      searchItemType: SearchItemComponent.SearchItemType,
  ): SearchItemComponent {
    return SearchItemComponent(
        component,
        searchItemType,
        Graph.filterRepository,
        ItemRepository { Graph.snapshotRepository.get() },
    )
  }

  sealed class Child {
    class List(val component: ListComponent) : Child()
    class Details(val component: DetailsComponent) : Child()
    class Filters(val component: FilterComponent) : Child()

    class ItemSearch(val component: SearchItemComponent) : Child()
  }

  sealed class Config : Parcelable {
    @Parcelize
    object ListConfig : Config()

    @Parcelize
    object FilterConfig : Config()

    @Parcelize
    data class DetailsConfig(val id: Int) : Config()

    @Parcelize
    data class SearchItemConfig(val searchItemType: SearchItemComponent.SearchItemType) : Config()
  }
}
