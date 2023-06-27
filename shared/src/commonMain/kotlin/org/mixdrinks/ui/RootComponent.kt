package org.mixdrinks.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mixdrinks.di.ComponentsFactory
import org.mixdrinks.di.Graph
import org.mixdrinks.ui.main.MainComponent
import org.mixdrinks.ui.profile.ProfileComponent

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

    private val _selectedTab = MutableStateFlow(BottomNavigationTab.Main)
    val selectedTab: StateFlow<BottomNavigationTab> = _selectedTab

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
        return ProfileComponent(componentContext)
    }

    fun open(tab: BottomNavigationTab) {
        _selectedTab.value = tab
        val config = when (tab) {
            BottomNavigationTab.Main -> Config.Main
            BottomNavigationTab.Profile -> Config.Profile
        }
        navigation.replaceCurrent(config)
    }

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
