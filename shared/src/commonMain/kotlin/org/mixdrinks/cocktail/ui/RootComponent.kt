package org.mixdrinks.cocktail.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.mixdrinks.cocktail.data.CocktailsRepository
import org.mixdrinks.cocktail.data.MixDrinksService
import org.mixdrinks.cocktail.ui.details.DetailsComponent
import org.mixdrinks.cocktail.ui.list.ListComponent
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


  val cocktailsRepository: CocktailsRepository = CocktailsRepository(Graph.ktorfit)
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
      }

  private fun listScreen(componentContext: ComponentContext): ListComponent =
      ListComponent(
          componentContext = componentContext,
          cocktailsRepository = Graph.cocktailsRepository,
          openCocktail = ::openCocktail,
      )

  private fun detailsScreen(componentContext: ComponentContext, config: Config.DetailsConfig): DetailsComponent {
    return DetailsComponent(componentContext, Graph.cocktailsRepository, CocktailId(config.id), navigation::pop)
  }

  private fun openCocktail(id: CocktailId) {
    navigation.push(Config.DetailsConfig(id = id.id))
  }

  fun close() {
    navigation.pop()
  }

  sealed class Child {
    class List(val component: ListComponent) : Child()
    class Details(val component: DetailsComponent) : Child()
  }

  sealed class Config : Parcelable {
    @Parcelize
    object ListConfig : Config()

    @Parcelize
    data class DetailsConfig(val id: Int) : Config()
  }
}
