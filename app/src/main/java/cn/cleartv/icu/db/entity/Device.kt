package cn.cleartv.icu.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.ICUDatabase

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Entity(tableName = ICUDatabase.TABLE_DEVICE)
data class Device(
    var number: String,
    @DeviceType var type: String = DeviceType.BED,
    @DeviceStatus var status: String = DeviceStatus.DISCONNECT,
    var name: String = "",
    var description: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}