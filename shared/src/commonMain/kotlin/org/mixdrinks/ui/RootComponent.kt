package org.mixdrinks.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import org.mixdrinks.di.ComponentsFactory
import org.mixdrinks.di.Graph
import org.mixdrinks.ui.main.MainComponent
import org.mixdrinks.ui.profile.ProfileComponent
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

    init {
        stack.subscribe {
            if (it.active.configuration == Config.Main) {
                _selectedTab.tryEmit(listOf(
                    TabUiModel(BottomNavigationTab.Main, true),
                    TabUiModel(BottomNavigationTab.Profile, false)
                ))
            }
        }

        graph.authBus.registerLogoutNotifier {
            openTab(BottomNavigationTab.Main)
        }
    }

    val selectedTab: StateFlow<List<TabUiModel>> = _selectedTab

    val showAuthDialog: StateFlow<Boolean> = graph.authBus.showAuthDialog

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
        return ProfileComponent(
            componentContext,
            ComponentsFactory(graph),
        )
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
        graph.authBus.tryEmit(false)
    }

    private val loginDialogScope = scope + SupervisorJob()

    private fun openProfile() {
        if (graph.tokenStorage.getToken() != null) {
            openTab(BottomNavigationTab.Profile)
            return
        }
        loginDialogScope.launch {
            graph.authBus.emit(true)
            graph.tokenStorage.tokenFlow
                .collectLatest {
                    if (it != null) {
                        graph.authBus.emit(false)
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

        val config = when (newTab) {
            BottomNavigationTab.Main -> Config.Main
            BottomNavigationTab.Profile -> Config.Profile
        }

        navigation.bringToFront(config)
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
