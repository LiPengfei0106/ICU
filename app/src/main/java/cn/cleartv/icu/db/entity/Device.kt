package cn.cleartv.icu.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.ICUDatabase
import com.squareup.moshi.JsonClass

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@JsonClass(generateAdapter = true)
@Entity(tableName = ICUDatabase.TABLE_DEVICE)
data class Device(
    @PrimaryKey
    var number: String,
    var name: String = "",
    @DeviceType var type: String = DeviceType.BED
) {

    @DeviceStatus
    var status: String = DeviceStatus.DISCONNECT
        @Synchronized set

    var description: String = ""
    var callNumber: String = ""
    var lastOnLineTime: Long = -1L

    override fun toString(): String {
        return "Device(" +
                "number='$number', " +
                "type='$type', " +
                "status='$status', " +
                "name='$name', " +
                "description='$description', " +
                "callNumber='$callNumber', " +
                "lastOnLineTime=$lastOnLineTime" +
                ")"
    }
}