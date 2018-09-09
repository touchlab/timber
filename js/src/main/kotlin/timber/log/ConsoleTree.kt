package timber.log

import kotlin.js.Console

class ConsoleTree(private val console: Console = kotlin.js.console) : Tree() {
  override fun isLoggable(priority: Int, tag: String?) = priority != VERBOSE

  override fun performLog(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
    when (priority) {
      ERROR, ASSERT -> console.error(message)
      WARNING -> console.warn(message)
      INFO -> console.info(message)
      DEBUG -> console.log(message)
      VERBOSE -> {} // TODO use console.debug here?
      else -> error("Unknown priority level: $priority")
    }
  }
}
