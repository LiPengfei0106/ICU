package cn.cleartv.icu.db.entity

import androidx.room.Embedded
import androidx.room.Ignore

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/09/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CallDetail : Call() {
    @Embedded(prefix = "device_")
    var device: Device? = null


    override fun toString(): String {
        return "CallDetail(" +
                "id=$id, " +
                "callType='$callType', " +
                "callNumber='$callNumber', " +
                "amCaller=$amCaller, " +
                "mark=$mark, " +
                "recordUrl=$recordUrl, " +
                "callTime=$callTime, " +
                "connectTime=$connectTime, " +
                "endTime=$endTime, " +
                "device=$device" +
                ")"
    }
}