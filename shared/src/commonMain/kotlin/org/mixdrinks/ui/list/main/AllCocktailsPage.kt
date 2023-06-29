package org.mixdrinks.ui.list.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.app.utils.ResString
import org.mixdrinks.ui.filters.FilterItem
import org.mixdrinks.ui.list.CocktailList
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.widgets.undomain.ContentHolder
import org.mixdrinks.ui.widgets.undomain.FlowRow

@Composable
internal fun AllCocktailsPage(component: ListComponent) {
    Scaffold(
        topBar = {
            TopBar(component)
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Content(component)
            }
        }
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun TopBar(component: ListComponent) {
    val isSearchActive by component.isSearchActive.collectAsState()

    Box(
        modifier = Modifier
            .background(MixDrinksColors.Main)
            .fillMaxWidth()
            .height(60.dp),
    ) {
        SearchField(component, isSearchActive)

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterEnd),
            visible = !isSearchActive
        ) {
            Row(modifier = Modifier
                .padding(horizontal = 4.dp)
            ) {
                Icon(
                    painter = painterResource("ic_search.xml"),
                    contentDescription = "Фільтри",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { component.openSearch() }
                        .size(36.dp),
                )

                Spacer(modifier = Modifier.width(8.dp))

                OpenFilterButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxHeight(),
                    component = component
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SearchField(component: ListComponent, isSearchActive: Boolean) {
    val searchQuery by component.searchQuery.collectAsState()
    val focusRequester = remember { FocusRequester() }
    AnimatedVisibility(
        exit = shrinkVertically(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        visible = isSearchActive
    ) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        TextField(
            modifier = Modifier.focusRequester(focusRequester),
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable { component.closeSearch() },
                    painter = painterResource("ic_clear.xml"),
                    contentDescription = "Закрити"
                )
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            value = searchQuery,
            onValueChange = {
                component.onSearchQueryChange(it)
            })
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun OpenFilterButton(modifier: Modifier, component: ListComponent) {
    Box(
        modifier = modifier
            .clickable {
                component.navigateToFilters()
            }
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .size(36.dp),
            painter = painterResource("ic_filter.xml"),
            contentDescription = ResString.filters,
            colorFilter = ColorFilter.tint(Color.White)
        )
        val counter by component.filterCountState.collectAsState()
        if (counter != null) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .padding(4.dp)
                    .background(MixDrinksColors.White, shape = RoundedCornerShape(16.dp)),
                text = counter.toString(),
                color = MixDrinksColors.Main,
                textAlign = TextAlign.Center,
                style = MixDrinksTextStyles.H4,
            )
        }
    }
}


@Composable
internal fun Content(component: ListComponent) {
    ContentHolder(
        stateflow = component.state,
    ) {
        when (it) {
            is CocktailsListState.PlaceHolder -> {
                PlaceHolder(it, component)
            }

            is CocktailsListState.Cocktails -> {
                CocktailList(
                    it,
                    component::navigateToDetails,
                    component::navigateToTagCocktails
                )
            }
        }
    }
}

@Composable
internal fun PlaceHolder(
    placeHolder: CocktailsListState.PlaceHolder,
    component: ListComponent,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
        ) {
            Text(
                text = ResString.cocktailNotFound,
                style = MixDrinksTextStyles.H1,
                color = MixDrinksColors.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = ResString.clearFilterForFoundSomething,
                style = MixDrinksTextStyles.H4,
                color = MixDrinksColors.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(32.dp))
            FlowRow(
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 4.dp
            ) {
                placeHolder.filters.forEach {
                    FilterItem(filterUi = it, onValue = component::onFilterStateChange)
                }
            }
        }
    }
}
