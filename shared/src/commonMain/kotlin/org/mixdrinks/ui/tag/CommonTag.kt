package org.mixdrinks.ui.tag

import org.mixdrinks.domain.FilterGroups

internal data class CommonTag(
    val id: Int,
    val type: Type,
) {

    enum class Type(public val filterGroups: FilterGroups) {
        TAG(FilterGroups.TAGS), TASTE(FilterGroups.TASTE)
    }

}
