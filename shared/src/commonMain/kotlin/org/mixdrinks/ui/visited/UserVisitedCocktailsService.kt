package org.mixdrinks.ui.visited

import de.jensklingenberg.ktorfit.http.GET

internal interface UserVisitedCocktailsService {

    @GET("/user-api/cocktail/visit/list")
    suspend fun getVisitedCocktails(): List<VisitedCocktail>

}
