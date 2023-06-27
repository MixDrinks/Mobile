package org.mixdrinks.ui.navigation

import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import org.mixdrinks.data.ItemsType
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteId
import org.mixdrinks.ui.filters.search.SearchItemComponent
import org.mixdrinks.ui.tag.CommonTag
import kotlin.native.concurrent.ThreadLocal

internal class Navigator(
    private val stackNavigation: StackNavigation<Config>
) : INavigator {

    sealed class Config(open val operationIndex: Int) : Parcelable {
        @Parcelize
        data class ListConfig(
            override val operationIndex: Int
        ) : Config(operationIndex) {
            constructor() : this(operation++)
        }

        @Parcelize
        data class FilterConfig(
            override val operationIndex: Int
        ) : Config(operationIndex) {
            constructor() : this(operation++)
        }

        @Parcelize
        data class DetailsConfig(
            val id: Int,
            override val operationIndex: Int
        ) : Config(operationIndex) {
            constructor(id: Int) : this(id, operation++)
        }

        @Parcelize
        data class ItemConfig(
            val id: Int,
            val typeGoods: String,
            override val operationIndex: Int
        ) : Config(operationIndex) {
            constructor(id: Int, itemType: String) : this(id, itemType, operation++)
        }

        @Parcelize
        data class SearchItemConfig(
            val searchItemType: SearchItemComponent.SearchItemType,
            override val operationIndex: Int
        ) : Config(operationIndex) {

            constructor(searchItemType: SearchItemComponent.SearchItemType) : this(
                searchItemType,
                operation++
            )
        }

        @Parcelize
        data class CommonTagConfig(
            val id: Int,
            val type: CommonTag.Type,
            override val operationIndex: Int
        ) : Config(operationIndex) {
            constructor(id: Int, type: CommonTag.Type) : this(id, type, operation++)
        }

        @ThreadLocal
        companion object {
            private var operation: Int = 0
        }
    }

    override fun back() {
        stackNavigation.pop()
    }

    override fun navigateToItem(itemsType: ItemsType.Type, id: Int) {
        stackNavigation.push(
            Config.ItemConfig(id, itemsType.name)
        )
    }

    override fun navigateToDetails(id: Int) {
        stackNavigation.push(Config.DetailsConfig(id))
    }

    override fun navigateToDetails(id: CocktailId) {
        stackNavigation.push(Config.DetailsConfig(id.id))
    }

    override fun navigateToSearchItem(searchItemType: SearchItemComponent.SearchItemType) {
        stackNavigation.push(Config.SearchItemConfig(searchItemType))
    }

    override fun navigateToFilters() {
        stackNavigation.push(Config.FilterConfig())
    }

    override fun navigateToTagCocktails(tagId: TagId) {
        stackNavigation.push(Config.CommonTagConfig(tagId.id, CommonTag.Type.TAG))
    }

    override fun navigationToTasteCocktails(tasteId: TasteId) {
        stackNavigation.push(Config.CommonTagConfig(tasteId.id, CommonTag.Type.TASTE))
    }

    override fun openFromDeepLink(config: Config) {
        stackNavigation.push(config)
    }
}
