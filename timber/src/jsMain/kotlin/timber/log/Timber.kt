package timber.log

actual object Timber {
    private val forestList = mutableListOf<Tree>()
    private var forestArray: Array<Tree> = emptyArray()

    actual val trees get() = forestArray.toList()

    actual val size get() = forestArray.size

    actual fun uprootAll() {
        synchronized(forestList) {
            forestList.clear()
            forestArray = emptyArray()
        }
    }

    actual fun uproot(tree: Tree) {
        synchronized(forestList) {
            require(forestList.remove(tree)) { "Cannot uproot tree which is not planted: $tree" }
            forestArray = forestList.toTypedArray()
        }
    }

    actual fun plant(tree: Tree) {
        synchronized(forestList) {
            forestList.add(tree)
            forestArray = forestList.toTypedArray()
        }
    }

    actual fun plant(vararg trees: Tree) {
        synchronized(forestList) {
            forestList.addAll(trees)
            forestArray = forestList.toTypedArray()
        }
    }

    actual fun plantAll(trees: Iterable<Tree>) {
        synchronized(forestList) {
            forestList.addAll(trees)
            forestArray = forestList.toTypedArray()
        }
    }

    actual fun isLoggable(priority: Int, tag: String?) = forestArray.any { it.isLoggable(priority, tag) }

    actual fun log(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
        forestArray.forEach { it.log(priority, tag, throwable, message) }
    }

    @PublishedApi
    internal actual fun rawLog(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
        forestArray.forEach { it.rawLog(priority, tag, throwable, message) }
    }
}