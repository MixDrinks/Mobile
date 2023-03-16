package org.mixdrinks.utils

import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.GoodId
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageUrlCreatorsTest {

  @Test
  fun `Verify image url create cocktail`() {
    assertEquals(
        ImageUrlCreators.createUrl(CocktailId(10), ImageUrlCreators.Size.SIZE_560),
        "https://image.mixdrinks.org/cocktails/10/560/10.webp"
    )
  }

  @Test
  fun `Verify image url create good`() {
    assertEquals(
        ImageUrlCreators.createUrl(GoodId(10), ImageUrlCreators.Size.SIZE_560),
        "https://image.mixdrinks.org/goods/10/560/10.webp"
    )
  }

}