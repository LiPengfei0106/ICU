package cn.cleartv.icu

import androidx.annotation.StringDef

@StringDef(
    DeviceStatus.DISCONNECT,
    DeviceStatus.IDLE,
    DeviceStatus.CALLING,
    DeviceStatus.MONITOR,
    DeviceStatus.IN_CALL_CALLER,
    DeviceStatus.IN_CALL_CALLEE
)
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
        const val IN_CALL_CALLER = "IN_CALL_CALLER"
        const val IN_CALL_CALLEE = "IN_CALL_CALLEE"

        const val MONITOR = "MONITOR"

    }
}