package cn.cleartv.icu

import androidx.annotation.StringDef

@StringDef(CallStatus.RINGING, CallStatus.CALLING, CallStatus.FINISHED, CallStatus.FAILED)
@Retention(AnnotationRetention.SOURCE)
annotation class CallStatus {
    companion object {

        const val RINGING = "RINGING"
        const val CALLING = "CALLING"
        const val FINISHED = "FINISHED"
        const val FAILED = "FAILED"

    }
}