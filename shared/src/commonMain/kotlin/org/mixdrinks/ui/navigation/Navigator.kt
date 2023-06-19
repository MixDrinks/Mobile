package org.mixdrinks.ui.navigation

import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import org.mixdrinks.data.ItemsType
import org.mixdrinks.ui.filters.search.SearchItemComponent

internal class Navigator(
    private val stackNavigation: StackNavigation<Config>
) {

    sealed class Config : Parcelable {
        @Parcelize
        object ListConfig : Config()

        @Parcelize
        object FilterConfig : Config()

        @Parcelize
        data class DetailsConfig(val id: Int, val operation: Int) : Config()

        @Parcelize
        data class GoodsConfig(val id: Int, val typeGoods: String) : Config()

        @Parcelize
        data class SearchItemConfig(val searchItemType: SearchItemComponent.SearchItemType) :
            Config()
    }

    private var operation: Int = 0

    fun back() {
        stackNavigation.pop()
    }

    fun navigateToItem(itemsType: ItemsType.Type, id: Int) {
        stackNavigation.push(
            Config.GoodsConfig(id, itemsType.toString())
        )
    }

    fun navigateToDetails(id: Int) {
        stackNavigation.push(Config.DetailsConfig(id, operation++))
    }

    fun navigateToSearchItem(searchItemType: SearchItemComponent.SearchItemType) {
        stackNavigation.push(Config.SearchItemConfig(searchItemType))
    }

    fun navigateToFilters() {
        stackNavigation.push(Config.FilterConfig)
    }

    fun openFromDeepLink(config: Config) {
        stackNavigation.push(config)
    }

}
