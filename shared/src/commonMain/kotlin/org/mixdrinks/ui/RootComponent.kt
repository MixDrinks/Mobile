package org.mixdrinks.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import org.mixdrinks.data.TagsRepository
import org.mixdrinks.di.ComponentsFactory
import org.mixdrinks.di.Graph
import org.mixdrinks.ui.list.CocktailListMapper
import org.mixdrinks.ui.main.MainComponent
import org.mixdrinks.ui.navigation.DeepLinkParser
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.profile.ProfileComponent
import org.mixdrinks.ui.widgets.undomain.launch
import org.mixdrinks.ui.widgets.undomain.scope

internal class RootComponent(
    private val componentContext: ComponentContext,
    private val graph: Graph,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val _stack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Main,
        handleBackButton = true,
        childFactory = ::createChild
    )

    val stack: Value<ChildStack<Config, Child>> = _stack

    private val _selectedTab = MutableStateFlow(listOf(
        TabUiModel(BottomNavigationTab.Main, true),
        TabUiModel(BottomNavigationTab.Profile, false)
    ))

    val selectedTab: StateFlow<List<TabUiModel>> = _selectedTab

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog

    private fun createChild(config: Config, componentContext: ComponentContext): Child {
        return when (config) {
            is Config.Main -> Child.Main(createMainComponent(componentContext))
            is Config.Profile -> Child.Profile(createProfileComponent(componentContext))
        }
    }

    private fun createMainComponent(componentContext: ComponentContext): MainComponent {
        return MainComponent(componentContext, graph, ComponentsFactory(graph))
    }

    private fun createProfileComponent(componentContext: ComponentContext): ProfileComponent {
        return ProfileComponent(componentContext, graph.visitedCocktailsService, graph.snapshotRepository,
            CocktailListMapper(), TagsRepository(graph.snapshotRepository))
    }

    fun open(tab: BottomNavigationTab) {
        when (tab) {
            BottomNavigationTab.Main -> {
                openTab(BottomNavigationTab.Main)
            }

            BottomNavigationTab.Profile -> {
                openProfile()
            }
        }
    }

    fun authFlowCancel() {
        _showAuthDialog.tryEmit(false)
    }

    private val loginDialogScope = scope + SupervisorJob()

    private fun openProfile() {
        if (graph.tokenStorage.getToken() != null) {
            openTab(BottomNavigationTab.Profile)
            return
        }
        loginDialogScope.launch {
            _showAuthDialog.emit(true)
            graph.tokenStorage.tokenFlow
                .collectLatest {
                    if (it != null) {
                        _showAuthDialog.emit(false)
                        withContext(Dispatchers.Main) {
                            openTab(BottomNavigationTab.Profile)
                        }
                    }
                }
        }
    }

    private fun openTab(newTab: BottomNavigationTab) {
        scope.launch {
            _selectedTab.emit(BottomNavigationTab.values()
                .map { tab ->
                    TabUiModel(tab, tab == newTab)
                })
        }

        when (newTab) {
            BottomNavigationTab.Main -> navigation.replaceCurrent(Config.Main)
            BottomNavigationTab.Profile -> navigation.replaceCurrent(Config.Profile)
        }
    }

    data class TabUiModel(
        val tab: BottomNavigationTab,
        val isSelected: Boolean,
    )

    enum class BottomNavigationTab(
        val icon: String,
        val title: String,
    ) {
        Main(
            icon = "ic_home.xml",
            title = "Головна"
        ),
        Profile(
            icon = "ic_profile.xml",
            title = "Профіль"
        )
    }

    sealed class Config : Parcelable {

        @Parcelize
        object Main : Config()

        @Parcelize
        object Profile : Config()
    }

    sealed class Child {
        data class Main(val component: MainComponent) : Child()
        data class Profile(val component: ProfileComponent) : Child()
    }
}
