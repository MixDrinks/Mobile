package org.mixdrinks.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlin.native.concurrent.ThreadLocal
import org.mixdrinks.di.ComponentsFactory
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.details.DetailsComponent
import org.mixdrinks.ui.items.ItemDetailComponent
import org.mixdrinks.ui.profile.root.ProfileRootComponent
import org.mixdrinks.ui.tag.CommonTag
import org.mixdrinks.ui.tag.CommonTagCocktailsComponent
import org.mixdrinks.ui.visited.VisitedCocktailsComponent

internal class ProfileComponent(
    private val componentContext: ComponentContext,
    private val componentsFactory: ComponentsFactory,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<ProfileContentConfig>()

    private val profileTabNavigator: ProfileNavigator = ProfileNavigator(navigation)

    private val _stack: Value<ChildStack<ProfileContentConfig, ProfileChild>> = childStack(
        source = navigation,
        initialConfiguration = ProfileContentConfig.ProfileRoot,
        handleBackButton = true,
        childFactory = ::createChild
    )

    val stack: Value<ChildStack<ProfileContentConfig, ProfileChild>> = _stack

    private fun createChild(config: ProfileContentConfig, componentContext: ComponentContext): ProfileChild {
        return when (config) {
            is ProfileContentConfig.ProfileRoot -> ProfileChild.ProfileRoot(
                componentsFactory.profileRootComponent(
                    componentContext,
                    profileTabNavigator,
                )
            )
            is ProfileContentConfig.CommonTagConfig -> ProfileChild.CommonTag(
                componentsFactory.commonTagCocktailsComponent(
                    componentContext, CommonTag(config.id, config.type), profileTabNavigator,
                )
            )

            is ProfileContentConfig.DetailsConfig -> ProfileChild.Details(
                componentsFactory.cocktailDetailsComponent(componentContext, CocktailId(config.id), profileTabNavigator)
            )

            is ProfileContentConfig.ItemConfig -> ProfileChild.Item(
                componentsFactory.detailGoodsScreen(
                    componentContext = componentContext,
                    itemDetailsNavigation = profileTabNavigator,
                    id = config.id,
                    type = config.typeGoods
                )
            )

            is ProfileContentConfig.VisitedCocktailsConfig -> ProfileChild.VisitedCocktails(
                componentsFactory.visitedCocktailsComponent(componentContext, profileTabNavigator)
            )
        }
    }

    sealed class ProfileContentConfig(open val operationIndex: Int) : Parcelable {

        @Parcelize
        data object ProfileRoot: ProfileContentConfig(0)

        @Parcelize
        data class VisitedCocktailsConfig(
            override val operationIndex: Int,
        ) : ProfileContentConfig(operationIndex) {
            constructor(): this(Companion.operation++)
        }


        @Parcelize
        data class DetailsConfig(
            val id: Int,
            override val operationIndex: Int,
        ) : ProfileContentConfig(operationIndex) {
            constructor(id: Int) : this(id, Companion.operation++)
        }

        @Parcelize
        data class ItemConfig(
            val id: Int,
            val typeGoods: String,
            override val operationIndex: Int,
        ) : ProfileContentConfig(operationIndex) {
            constructor(id: Int, itemType: String) : this(id, itemType, Companion.operation++)
        }

        @Parcelize
        data class CommonTagConfig(
            val id: Int,
            val type: CommonTag.Type,
            override val operationIndex: Int,
        ) : ProfileContentConfig(operationIndex) {
            constructor(id: Int, type: CommonTag.Type) : this(id, type, operation++)
        }

        @ThreadLocal
        companion object {
            private var operation: Int = 0
        }
    }

    sealed class ProfileChild {
        class ProfileRoot(val component: ProfileRootComponent) : ProfileChild()
        class VisitedCocktails(val component: VisitedCocktailsComponent) : ProfileChild()
        class Details(val component: DetailsComponent) : ProfileChild()
        class Item(val component: ItemDetailComponent) : ProfileChild()
        class CommonTag(val component: CommonTagCocktailsComponent) : ProfileChild()
    }
}
