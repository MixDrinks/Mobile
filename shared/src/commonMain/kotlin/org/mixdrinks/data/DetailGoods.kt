package org.mixdrinks.data

import org.mixdrinks.domain.FilterGroups

data class DetailGoodsUiModel(
    val id: Int,
    val name: String,
    val about: String,
    val url: String
)

data class ItemsType(
    val id: Int,
    val type: Type
) {
    enum class Type {
        GOODS, GLASSWARE, TOOL;

        fun getFilterGroup(): FilterGroups {
            return when (this) {
                GOODS -> FilterGroups.GOODS
                GLASSWARE -> FilterGroups.GLASSWARE
                TOOL -> FilterGroups.TOOLS
            }
        }

        companion object {
            fun fromString(value: String) = Type.values().first { it.toString() == value }
        }
    }
}

