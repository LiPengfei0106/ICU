package cn.cleartv.icu

import androidx.annotation.StringDef

@StringDef(CallType.CALL, CallType.MONITOR, CallType.TRANSFER)
@Retention(AnnotationRetention.SOURCE)
annotation class CallType {
    companion object {

        const val CALL = "call"
        const val MONITOR = "monitor"
        const val TRANSFER = "transfer"

    }
}