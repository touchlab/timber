package timber.log

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.system.getTimeMillis

import kotlin.native.*
import kotlin.native.concurrent.*
import platform.Foundation.*


class ConcurrencyTest {
    val INCREMENT_COUNT = 10000000

    @Test
    fun logPerformance() {
        Timber.plant(CountTree())

        val messLambda = {"asdf"}
        var start = getTimeMillis()
        for(i in 0 until INCREMENT_COUNT){
//            Timber.rawLog(priority = WARNING, tag = "qwert", throwable = null, message = "asdf")
            Timber.warn(message = messLambda)
        }

        println("logs: "+ (getTimeMillis() - start))
    }

    class CountTree():Tree(){

        override fun isLoggable(priority: Int, tag: String?): Boolean {
            return false
        }

        override fun performLog(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
//            Counter.logCount++
        }
    }

    @ThreadLocal
    object Counter{
        var logCount = 0
    }

    @Test
    fun timings() {

        var raw = 0
        val aInt = AtomicInt(0)
        var locked = 0
        val updateLock = NSLock()
        val aRef = AtomicReference<Int>(0)

        var start = getTimeMillis()
        for(i in 0 until INCREMENT_COUNT){
            raw = i
        }

        println("raw: "+ (getTimeMillis() - start))


        start = getTimeMillis()
        for(i in 0 until INCREMENT_COUNT){
            raw = aInt.value
        }

        println("atom: "+ (getTimeMillis() - start))

        start = getTimeMillis()
        for(i in 0 until INCREMENT_COUNT){
            updateLock.lock()
            raw = locked
            updateLock.unlock()
        }

        println("locked: "+ (getTimeMillis() - start))

        start = getTimeMillis()
        for(i in 0 until INCREMENT_COUNT){
            raw = aRef.value
        }

        println("ref: "+ (getTimeMillis() - start))
    }
}