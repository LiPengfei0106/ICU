package cn.cleartv.icu

import androidx.annotation.StringDef

@StringDef(DeviceType.UNKNOWN, DeviceType.HOST, DeviceType.BED, DeviceType.DOOR, DeviceType.GUEST)
@Retention(AnnotationRetention.SOURCE)
annotation class DeviceType {
    companion object {

        const val UNKNOWN = "unknown"
        const val HOST = "host"
        const val BED = "bed"
        const val DOOR = "door"
        const val GUEST = "guest"

    }
}