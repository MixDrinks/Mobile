package org.mixdrinks.tools

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mixdrinks.utils.LazySuspend
import kotlin.test.Test
import kotlin.test.assertEquals

class LazySuspendTest {

  @Test
  fun `Verify get value from lazy suspend`() {
    var callCount = 0
    val test = LazySuspend {
      callCount++
      delay(1000)
      return@LazySuspend "test"
    }

    runBlocking {
      launch {
        assertEquals("test", test.invoke())
      }
      launch {
        assertEquals("test", test.invoke())
      }
    }

    runBlocking {
      assertEquals("test", test.invoke())
    }

    assertEquals(callCount, 1)

    runBlocking {
      assertEquals("test", test.invoke())
    }

    assertEquals(callCount, 1)
  }
}
