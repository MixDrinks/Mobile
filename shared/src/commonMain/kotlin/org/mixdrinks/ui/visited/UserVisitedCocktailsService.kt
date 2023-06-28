package org.mixdrinks.ui.visited

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

internal interface UserVisitedCocktailsService {

    @GET("/user-api/cocktail/visit/list")
    suspend fun getVisitedCocktails(): List<VisitedCocktail>

    @POST("/user-api/cocktail/visit")
    suspend fun visitCocktail(@Query("id") id: Int)

}
