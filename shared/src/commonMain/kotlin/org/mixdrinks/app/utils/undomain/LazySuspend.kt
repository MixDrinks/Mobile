package org.mixdrinks.app.utils.undomain

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.jvm.Volatile

internal class LazySuspend<T>(
    private val block: suspend () -> T,
) {

    @Volatile
    private var value: T? = null

    private val mutex = Mutex()

    suspend operator fun invoke(): T {
        return mutex.withLock {
            if (value == null) {
                value = block()
            }
            value!!
        }
    }
}
