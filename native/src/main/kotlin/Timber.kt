package timber.log

import kotlin.native.*
import kotlin.native.concurrent.*
import platform.Foundation.*

@ThreadLocal
actual object Timber {
    private var forestArray: Array<Tree> = emptyArray()
    private var version = -1

    actual fun uprootAll() {
        TimberConfig.uprootAll()
        checkVersion()
    }

    actual fun uproot(tree: Tree) {
        TimberConfig.uproot(tree)
        checkVersion()
    }

    actual fun plant(tree: Tree) {
        TimberConfig.plant(tree)
        checkVersion()
    }

    actual fun plant(vararg trees: Tree) {
        TimberConfig.plant(trees)
        checkVersion()
    }

    actual fun plantAll(trees: Iterable<Tree>) {
        TimberConfig.plantAll(trees)
        checkVersion()
    }

    actual val trees
        get():List<Tree> {
            checkVersion()
            return forestArray.toList()
        }

    actual val size
        get():Int {
            checkVersion()
            return forestArray.size
        }

    actual fun isLoggable(priority: Int, tag: String?): Boolean {
        checkVersion()
        return forestArray.any { it.isLoggable(priority, tag) }
    }

    actual fun log(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
        checkVersion()
        forestArray.forEach { it.log(priority, tag, throwable, message) }
    }

    @PublishedApi
    internal actual fun rawLog(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
        checkVersion()
        forestArray.forEach { it.rawLog(priority, tag, throwable, message) }
    }

    fun checkVersion() {
        val configVersion: Int = TimberConfig.version.value
        if (configVersion != version) {
            println("checkVersion not equal")
            forestArray = TimberConfig.treeArray
            version = configVersion
        }
    }
}

/**
 * Shared, frozen state. We keep a 'version' int here as checking an integer reference
 * is presumably faster than accessing the AtomicReference List instance.
 */
private object TimberConfig {
    val forestList: AtomicReference<MutableList<Tree>> = AtomicReference<MutableList<Tree>>(mutableListOf<Tree>().freeze())
    val version = AtomicInt(0)
    private val updateLock = NSLock()

    private inline fun <T> withLock(proc: () -> T): T {
        updateLock.lock()
        try {
            return proc.invoke()
        } finally {
            updateLock.unlock()
        }
    }

    val treeArray: Array<Tree>
        get() = forestList.value.toTypedArray()

    fun uprootAll() {
        withLock {
            forestList.value = mutableListOf<Tree>().freeze()
            version.increment()
        }
    }

    fun uproot(tree: Tree) {
        withLock {
            val newList = mutableListOf<Tree>()
            var treeFound = false
            for (ltree in forestList.value) {
                if (ltree == tree) {
                    treeFound = true
                } else {
                    newList.add(ltree)
                }
            }
            require(treeFound) { "Cannot uproot tree which is not planted: $tree" }
            forestList.value = newList.freeze()
            version.increment()
        }
    }

    fun plant(tree: Tree) {
        withLock {
            val newList = mutableListOf<Tree>()
            newList.addAll(forestList.value)
            newList.add(tree)
            forestList.value = newList.freeze()
            version.increment()
        }
    }

    fun plant(trees: Array<out Tree>) {
        withLock {
            val newList = mutableListOf<Tree>()
            newList.addAll(forestList.value)
            newList.addAll(trees)
            forestList.value = newList.freeze()
            version.increment()
        }
    }

    fun plantAll(trees: Iterable<Tree>) {
        withLock {
            val newList = mutableListOf<Tree>()
            newList.addAll(forestList.value)
            newList.addAll(trees)
            forestList.value = newList.freeze()
            version.increment()
        }
    }
}