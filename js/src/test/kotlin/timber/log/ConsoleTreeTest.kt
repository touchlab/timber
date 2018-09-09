package timber.log

import kotlin.js.Console
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConsoleLogTest {
  private val fakeConsole = FakeConsole()
  private val tree = ConsoleTree(fakeConsole)

  @Test fun isLoggable() {
    assertTrue(tree.isLoggable(ASSERT))
    assertTrue(tree.isLoggable(ERROR))
    assertTrue(tree.isLoggable(WARNING))
    assertTrue(tree.isLoggable(INFO))
    assertTrue(tree.isLoggable(DEBUG))
    assertFalse(tree.isLoggable(VERBOSE))
  }

  @Test fun logs() {
    tree.log(ASSERT, null, null, "assert")
    tree.log(ERROR, null, null, "error")
    tree.log(WARNING, null, null, "warning")
    tree.log(INFO, null, null, "info")
    tree.log(DEBUG, null, null, "debug")
    tree.log(VERBOSE, null, null, "verbose")

    // TODO fix messages https://youtrack.jetbrains.com/issue/KT-15223
    val expected = listOf(
        "ERROR [a, s, s, e, r, t]",
        "ERROR [e, r, r, o, r]",
        "WARN [w, a, r, n, i, n, g]",
        "INFO [i, n, f, o]",
        "LOG [d, e, b, u, g]"
    )
    assertEquals(expected, fakeConsole.messages)
  }
}

class FakeConsole : Console {
  private val _messages = mutableListOf<String>()
  val messages get() = _messages.toList()

  override fun dir(o: Any) = throw UnsupportedOperationException()

  override fun error(vararg o: Any?) {
    _messages.add("ERROR " + o.map(Any?::toString))
  }

  override fun warn(vararg o: Any?) {
    _messages.add("WARN " + o.map(Any?::toString))
  }

  override fun info(vararg o: Any?) {
    _messages.add("INFO " + o.map(Any?::toString))
  }

  override fun log(vararg o: Any?) {
    _messages.add("LOG " + o.map(Any?::toString))
  }
}
