package timber.log

actual object Timber {
    private var forestArray: Array<Tree> = emptyArray()

    actual val trees get() = forestArray.toList()

    actual val size get() = forestArray.size

    actual fun plant(config:() -> Array<Tree>) {
        forestArray = config()
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