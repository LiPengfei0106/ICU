package cn.cleartv.icu

import androidx.annotation.StringDef

@StringDef(DeviceStatus.IDLE, DeviceStatus.CALLING, DeviceStatus.IN_CALL)
@Retention(AnnotationRetention.SOURCE)
annotation class DeviceStatus {
    companion object {

        // 连接断开
        const val DISCONNECT = "DISCONNECT"
        // 空闲
        const val IDLE = "IDLE"
        // 呼叫中
        const val CALLING = "CALLING"
        // 通话中
        const val IN_CALL = "IN_CALL"

    }
}