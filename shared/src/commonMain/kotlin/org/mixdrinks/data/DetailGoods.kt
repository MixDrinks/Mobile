package org.mixdrinks.data

data class DetailGoodsUiModel(
    val id: Int,
    val name: String,
    val about: String,
    val url: String
)

data class GoodsType(
    val id: Int,
    val type: Type
) {
    enum class Type {
        GOODS, GLASSWARE, TOOL;

        companion object {
            fun fromString(value: String) = Type.values().first { it.toString() == value }
        }
    }
}

