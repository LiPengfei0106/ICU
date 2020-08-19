package cn.cleartv.icu

import androidx.annotation.StringDef

@StringDef(DeviceType.BED, DeviceType.DOOR, DeviceType.GUEST)
@Retention(AnnotationRetention.SOURCE)
annotation class DeviceType {
    companion object {

        const val BED = "BED"
        const val DOOR = "DOOR"
        const val GUEST = "GUEST"

    }
}