package timber.log

import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

internal actual class NativeBox<T> actual constructor(t:T){
    val atom = AtomicReference<T>(t.freeze())
    actual var value:T get() = atom.value
    set(value) {
        atom.value = value.freeze()
    }
}

internal actual inline fun <R> synchronized2(lock: Any, block: () -> R): R = ThreadAccess.updateLock.withLock{
    block()
}

internal object ThreadAccess{
    val updateLock = Lock()
}
