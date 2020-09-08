package cn.cleartv.icu.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import cn.cleartv.icu.CallStatus
import cn.cleartv.icu.CallType
import cn.cleartv.icu.db.ICUDatabase
import cn.cleartv.voip.annotation.CallRecordStatus
import com.squareup.moshi.JsonClass

/**
 * <pre>
 * author : Lee
 * e-mail : lipengfei@cleartv.cn
 * time   : 2020/09/03
 * desc   :
 * version: 1.0
</pre> *
 */
@JsonClass(generateAdapter = true)
@Entity(tableName = ICUDatabase.TABLE_CALL)
data class Call(
    @CallType
    var callType: String = CallType.CALL,
    @CallStatus
    var callStatus: String = CallStatus.RINGING,
    var callNumber: String = "",
    var amCaller: Boolean = true,
    var mark: String? = null,
    var recordUrl: String? = null,
    var recordStatus: String? = null,
    var callTime: Long = System.currentTimeMillis(),
    var connectTime: Long = -1L,
    var endTime: Long = -1L
){

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @Ignore
    @Embedded(prefix = "device_")
    val device: Device? = null


    override fun toString(): String {
        return "Call(" +
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