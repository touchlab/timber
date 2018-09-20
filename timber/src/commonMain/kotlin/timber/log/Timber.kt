package timber.log

object Timber{
  private val forestArray: NativeBox<Array<Tree>> = NativeBox(emptyArray())

  val trees:List<Tree>
  get() = forestArray.value.toList()

  val size:Int
  get() = forestArray.value.size

  fun uprootAll() {
    synchronized2(forestArray) {
      forestArray.value = emptyArray()
    }
  }

  fun uproot(tree: Tree) {
    synchronized2(forestArray) {
      val newList = mutableListOf<Tree>()
      var treeFound = false
      for (ltree in forestArray.value) {
        if (ltree == tree) {
          treeFound = true
        } else {
          newList.add(ltree)
        }
      }
      require(treeFound) { "Cannot uproot tree which is not planted: $tree" }
      forestArray.value = newList.toTypedArray()
    }
  }

  fun plant(tree: Tree) {
    synchronized2(forestArray) {
      val newList = mutableListOf<Tree>()
      newList.addAll(forestArray.value)
      newList.add(tree)
      forestArray.value = newList.toTypedArray()
    }
  }

  fun plant(vararg trees: Tree) {
    synchronized2(forestArray) {
      val newList = mutableListOf<Tree>()
      newList.addAll(forestArray.value)
      newList.addAll(trees)
      forestArray.value = newList.toTypedArray()
    }
  }

  fun plantAll(trees: Iterable<Tree>) {
    synchronized2(forestArray) {
      val newList = mutableListOf<Tree>()
      newList.addAll(forestArray.value)
      newList.addAll(trees)
      forestArray.value = newList.toTypedArray()
    }
  }

  fun isLoggable(priority: Int, tag: String? = null):Boolean =
          forestArray.value.any { it.isLoggable(priority, tag) }

  fun log(priority: Int, tag: String?, throwable: Throwable?, message: String?){
    forestArray.value.forEach { it.log(priority, tag, throwable, message) }
  }

  @PublishedApi
  internal fun rawLog(priority: Int, tag: String?, throwable: Throwable?, message: String?){
    forestArray.value.forEach { it.rawLog(priority, tag, throwable, message) }
  }


  fun tagged(tag: String): Tree {
    val taggedTag = tag
    return object : Tree() {
      override fun isLoggable(priority: Int, tag: String?): Boolean {
        return Timber.isLoggable(priority, tag ?: taggedTag)
      }

      override fun performLog(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
        Timber.log(priority, tag ?: taggedTag, throwable, message)
      }
    }
  }

  const val VERBOSE = 2
  const val DEBUG = 3
  const val INFO = 4
  const val WARNING = 5
  const val ERROR = 6
  const val ASSERT = 7
}

internal expect class NativeBox<T>(t:T){
  var value:T
}

internal expect inline fun <R> synchronized2(lock: Any, block: () -> R): R

inline fun Timber.log(priority: Int, throwable: Throwable? = null, message: () -> String) {
  if (isLoggable(priority, null)) {
    rawLog(priority, null, throwable, message())
  }
}

inline fun Timber.assert(throwable: Throwable? = null, message: () -> String) {
  log(ASSERT, throwable, message)
}

inline fun Timber.error(throwable: Throwable? = null, message: () -> String) {
  log(ERROR, throwable, message)
}

inline fun Timber.warn(throwable: Throwable? = null, message: () -> String) {
  log(WARNING, throwable, message)
}

inline fun Timber.info(throwable: Throwable? = null, message: () -> String) {
  log(INFO, throwable, message)
}

inline fun Timber.debug(throwable: Throwable? = null, message: () -> String) {
  log(DEBUG, throwable, message)
}

inline fun Timber.verbose(throwable: Throwable? = null, message: () -> String) {
  log(VERBOSE, throwable, message)
}
