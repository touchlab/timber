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
import kotlin.system.getTimeMillis

class ConcurrencyTest {
    @BeforeTest @AfterTest fun after() {
        Timber.uprootAll()
    }

    @Test
    fun multipleThreads(){
        val countLogTree = CountLogTree(Timber.INFO)
        Timber.plant(countLogTree)

        val COUNT = 10
        val LOG_RUNS = 100000
        val workers = Array(COUNT) { Worker.start() }

        Array(workers.size) { workerIndex ->
            workers[workerIndex].execute(TransferMode.SAFE, {LOG_RUNS}) {
                for(i in 0 until it){
                    Timber.info {"Loggin run $i"}
                }
                return@execute it
            }
        }.forEach { it.result }

        workers.forEach {
            it.requestTermination().consume { _ -> }
        }

        assertEquals(COUNT * LOG_RUNS, countLogTree.writeCount.value)
    }
}

class CountLogTree(minPriority: Int) : NativeLogTree(minPriority) {
    val writeCount = AtomicInt(0)
    override fun writeLog(s:String){
        writeCount.increment()
    }
}