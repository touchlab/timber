package timber.log

class ListTree(allowedLevel: Int = VERBOSE, allowedTags: Set<String> = setOf()) : Tree() {
  val allowedTags = allowedTags.toMutableSet()
  var allowedLevel = allowedLevel
    set(value) {
      when (value) {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, ASSERT -> {
          field = value
        }
        else -> throw IllegalArgumentException("Unknown log level: $value")
      }
    }

  private val _messages = mutableListOf<String>()
  val messages get() = _messages

  override fun isLoggable(priority: Int, tag: String?): Boolean {
    return priority <= allowedLevel && (allowedTags.isEmpty() || allowedTags.contains(tag))
  }

  override fun performLog(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
    if (isLoggable(priority, tag)) {
      _messages.add(buildString {
        append(priority.toPriorityString())
        if (tag != null) {
          append(' ')
          append(tag)
        }
        if (message != null) {
          append(' ' )
          append(message)
        }
        if (throwable != null) {
          append(" [")
          append(throwable::class)
          append(": ")
          append(throwable.message)
          append(']')
        }
      })
    }
  }

  private fun Int.toPriorityString() = when(this) {
    VERBOSE -> "VERBOSE"
    DEBUG -> "DEBUG"
    INFO -> "INFO"
    WARNING -> "WARNING"
    ERROR -> "ERROR"
    ASSERT -> "ASSERT"
    else -> throw IllegalArgumentException("Unknown priority: $this")
  }
}
