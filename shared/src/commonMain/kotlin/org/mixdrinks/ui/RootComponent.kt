package org.mixdrinks.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import org.mixdrinks.data.CocktailListRepository
import org.mixdrinks.data.FilterRepository
import org.mixdrinks.data.FutureCocktailSelector
import org.mixdrinks.data.MixDrinksService
import org.mixdrinks.data.SnapshotRepository
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.details.DetailsComponent
import org.mixdrinks.ui.details.FullCocktailRepository
import org.mixdrinks.ui.details.goods.GoodsRepository
import org.mixdrinks.ui.filters.main.FilterComponent
import org.mixdrinks.ui.filters.search.ItemRepository
import org.mixdrinks.ui.filters.search.SearchItemComponent
import org.mixdrinks.ui.list.ListComponent
import org.mixdrinks.ui.list.SelectedFilterProvider

internal object Graph {
  private val ktorfit = Ktorfit.Builder()
      .httpClient(HttpClient {
        install(ContentNegotiation) {
          json(Json {
            isLenient = true
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

internal class RootComponent(
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

  val stack: StateFlow<ChildStack<*, Child>?> = callbackFlow {
    val listener: (ChildStack<*, Child>) -> Unit = {
      this.trySend(it)
    }

    _stack.subscribe(listener)
    awaitClose {
      _stack.unsubscribe(listener)
    }
  }
      .stateIn(
          CoroutineScope(Dispatchers.Main),
          SharingStarted.WhileSubscribed(),
          null
      )


  private fun createChild(config: Config, componentContext: ComponentContext): Child =
      when (config) {
        Config.ListConfig -> Child.List(listScreen(componentContext))
        is Config.DetailsConfig -> Child.Details(detailsScreen(componentContext, config))
        Config.FilterConfig -> Child.Filters(filterScreen(componentContext))
        is Config.SearchItemConfig -> Child.ItemSearch(searchItemScreen(componentContext, config.searchItemType))
        else -> throw IllegalStateException("Unknown config: $config")
      }

  private fun listScreen(componentContext: ComponentContext): ListComponent =
      ListComponent(
          componentContext = componentContext,
          cocktailListRepository = CocktailListRepository(
              suspend { Graph.snapshotRepository.get() },
              Graph.filterRepository,
              suspend { CocktailSelector(Graph.filterRepository.getFilterGroups().map { it.toFilterGroup() }) },
          ),
          selectedFilterProvider = SelectedFilterProvider(
              suspend { Graph.snapshotRepository.get() },
              suspend { Graph.filterRepository }
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
        getFutureCocktail(),
        navigation,
    )
  }

  private fun getFutureCocktail(): FutureCocktailSelector {
    return FutureCocktailSelector(
        snapshot = { Graph.snapshotRepository.get() },
        cocktailSelector = {
          CocktailSelector(Graph.filterRepository.getFilterGroups().map { it.toFilterGroup() })
        },
        filterRepository = { Graph.filterRepository },
    )
  }

  private fun searchItemScreen(
      component: ComponentContext,
      searchItemType: SearchItemComponent.SearchItemType,
  ): SearchItemComponent {
    val itemRepository = ItemRepository(
        suspend { Graph.snapshotRepository.get() },
        getFutureCocktail()
    )
    return SearchItemComponent(
        component,
        searchItemType,
        Graph.filterRepository,
        itemRepository,
        navigation,
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
