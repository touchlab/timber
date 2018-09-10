package timber.log

import kotlin.native.*
import kotlin.native.concurrent.*
import platform.Foundation.*

internal actual class NativeBox<T> actual constructor(t:T){
    val atom = AtomicReference<T>(t.freeze())
    actual var value:T get() = atom.value
    set(value) {
        atom.value = value.freeze()
    }
}

internal actual inline fun <R> synchronized2(lock: Any, block: () -> R): R {
    ThreadAccess.updateLock.lock()
    try {
        return block()
    } finally {
        ThreadAccess.updateLock.unlock()
    }
}

internal object ThreadAccess{
    val updateLock = NSLock()
}
