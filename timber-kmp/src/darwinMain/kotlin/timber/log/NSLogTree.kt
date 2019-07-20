package timber.log

import platform.Foundation.*

class NSLogTree(minPriority: Int) : NativeLogTree(minPriority) {
    override fun writeLog(s:String){
        NSLog(s)
    }
}

