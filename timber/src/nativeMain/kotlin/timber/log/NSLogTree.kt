package timber.log

import kotlin.text.*
import platform.Foundation.*

class NSLogTree(minPriority: Int) : NativeLogTree(minPriority) {
    override fun writeLog(s:String){
        NSLog(s)
    }
}

