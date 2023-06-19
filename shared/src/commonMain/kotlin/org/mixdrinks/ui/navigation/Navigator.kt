package org.mixdrinks.ui.navigation

import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import org.mixdrinks.data.ItemsType
import org.mixdrinks.ui.filters.search.SearchItemComponent
import kotlin.native.concurrent.ThreadLocal

internal class Navigator(
    private val stackNavigation: StackNavigation<Config>
) {

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

        @ThreadLocal
        companion object {
            private var operation: Int = 0
        }
    }

    fun back() {
        stackNavigation.pop()
    }

    fun navigateToItem(itemsType: ItemsType.Type, id: Int) {
        stackNavigation.push(
            Config.ItemConfig(id, itemsType.toString())
        )
    }

    fun navigateToDetails(id: Int) {
        stackNavigation.push(Config.DetailsConfig(id))
    }

    fun navigateToSearchItem(searchItemType: SearchItemComponent.SearchItemType) {
        stackNavigation.push(Config.SearchItemConfig(searchItemType))
    }

    fun navigateToFilters() {
        stackNavigation.push(Config.FilterConfig())
    }

    fun openFromDeepLink(config: Config) {
        stackNavigation.push(config)
    }

}
