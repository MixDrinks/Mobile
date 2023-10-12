package org.mixdrinks.data

import trackEvent

object Tracking {

    fun track(
        action: String,
        screen: String,
        data: Map<String, String> = emptyMap(),
    ) {
        val mapToSend = data.toMutableMap()
        mapToSend["screen"] = screen

        trackEvent(action, mapToSend)
    }
}
