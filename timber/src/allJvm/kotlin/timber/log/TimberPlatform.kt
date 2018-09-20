package timber.log

internal actual class NativeBox<T> actual constructor(t:T){
    actual var value:T = t
}

internal actual inline fun <R> synchronized2(lock: Any, block: () -> R): R = synchronized(lock, block)