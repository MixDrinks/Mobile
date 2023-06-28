package org.mixdrinks.ui.visited

import org.mixdrinks.di.GraphHolder

suspend fun <T> authExecutor(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        GraphHolder.graph.authBus.logout()
        println("Error: $e")
        Result.failure(e)
    }
}
