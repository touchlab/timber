package timber.log

import kotlin.native.*
import kotlin.native.concurrent.*
import platform.Foundation.*

@ThreadLocal
actual object Timber {
    private val forestArray: Array<Tree> by lazy {
        TimberConfig.forestLambdaRef.value()
    }

    actual fun plant(config:() -> Array<Tree>) {
        TimberConfig.plant(config)
    }

    actual val trees
        get():List<Tree> {
            return forestArray.toList()
        }

    actual val size
        get():Int {
            return forestArray.size
        }

    actual fun isLoggable(priority: Int, tag: String?): Boolean {
        return forestArray.any { it.isLoggable(priority, tag) }
    }

    actual fun log(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
        forestArray.forEach { it.log(priority, tag, throwable, message) }
    }

    @PublishedApi
    internal actual fun rawLog(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
        forestArray.forEach { it.rawLog(priority, tag, throwable, message) }
    }
}

/**
 * Shared, frozen state. We keep a 'version' int here as checking an integer reference
 * is presumably faster than accessing the AtomicReference List instance.
 */
private object TimberConfig {
    val forestLambdaRef : AtomicReference<() -> Array<Tree>> = AtomicReference({ emptyArray<Tree>()}.freeze())

    fun plant(config:() -> Array<Tree>){
        forestLambdaRef.value = config.freeze()
    }

    val treeArray: Array<Tree>
        get() = forestLambdaRef.value()
}