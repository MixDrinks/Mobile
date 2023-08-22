package org.mixdrinks.ui.profile

import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import org.mixdrinks.data.ItemsType
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteId
import org.mixdrinks.ui.details.CocktailsDetailNavigation
import org.mixdrinks.ui.items.ItemDetailsNavigation
import org.mixdrinks.ui.profile.root.ProfileRootNavigation
import org.mixdrinks.ui.tag.CommonTag
import org.mixdrinks.ui.tag.CommonTagNavigation
import org.mixdrinks.ui.visited.VisitedCocktailsNavigation

internal class ProfileNavigator(
    private val stackNavigation: StackNavigation<ProfileComponent.ProfileContentConfig>,
) : VisitedCocktailsNavigation,
    CommonTagNavigation,
    CocktailsDetailNavigation,
    ItemDetailsNavigation,
    ProfileRootNavigation {

    override fun navigateToDetails(cocktailId: CocktailId) {
        stackNavigation.push(ProfileComponent.ProfileContentConfig.DetailsConfig(cocktailId.id))
    }

    override fun navigateToTagCocktails(tagId: TagId) {
        stackNavigation.push(ProfileComponent.ProfileContentConfig.CommonTagConfig(tagId.id, CommonTag.Type.TAG))
    }

    override fun navigationToTasteCocktails(tasteId: TasteId) {
        stackNavigation.push(ProfileComponent.ProfileContentConfig.CommonTagConfig(tasteId.id, CommonTag.Type.TASTE))
    }

    override fun navigateToItem(itemsType: ItemsType.Type, id: Int) {
        stackNavigation.push(ProfileComponent.ProfileContentConfig.ItemConfig(id, itemsType.name))
    }

    override fun back() {
        stackNavigation.pop()
    }

    override fun navigateToVisitedCocktails() {
        stackNavigation.push(ProfileComponent.ProfileContentConfig.VisitedCocktailsConfig())
    }
}
