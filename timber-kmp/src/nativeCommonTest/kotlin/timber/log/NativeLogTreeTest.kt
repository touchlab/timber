package timber.log

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import kotlin.system.getTimeMillis

import kotlin.native.*
import kotlin.native.concurrent.*
import platform.Foundation.*

class NativeLogTreeTest{
    @BeforeTest @AfterTest fun after() {
        Timber.uprootAll()
        LogSaver.logs.clear()
    }

    @Test
    fun basicTest(){
        Timber.plant(SaveLogTree(Timber.INFO))
        Timber.warn {"Hello"}

        assertEquals(LogSaver.logs, listOf("Hello"))
    }

    @Test
    fun priorityTest(){
        Timber.plant(SaveLogTree(Timber.WARNING))
        Timber.info {"Hello"}

        assertEquals(LogSaver.logs, emptyArray<String>().toMutableList())
    }

    @Test
    fun exceptionTest(){
        Timber.plant(SaveLogTree(Timber.WARNING))
        Timber.warn(NullPointerException()){"Hello"}
        assertTrue(LogSaver.logs.any {it.contains("kfun:timber.log.NativeLogTreeTest.exceptionTest()")})
    }
}

class SaveLogTree(minPriority: Int) : NativeLogTree(minPriority) {
    override fun writeLog(s:String){
        LogSaver.write(s)
    }
}

@ThreadLocal
object LogSaver{

    fun printAll(){
        for (log in logs) {
            println(log)
        }
    }
    fun write(s:String){
        logs.add(s)
    }
    val logs = mutableListOf<String>()
}