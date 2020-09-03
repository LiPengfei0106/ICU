package cn.cleartv.icu.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import cn.cleartv.icu.CallType

/**
 * <pre>
 * author : Lee
 * e-mail : lipengfei@cleartv.cn
 * time   : 2020/09/03
 * desc   :
 * version: 1.0
</pre> *
 */
@Entity
data class Call(
    @CallType
    val callType: String,
    val callNumber: String,
    var amCaller: Boolean = true,
    @Ignore
    @Embedded(prefix = "device_")
    val device: Device? = null,
    var mark: String? = null,
    var callTime: Long = System.currentTimeMillis(),
    var connectTime: Long = -1L,
    var endTime: Long = -1L
)