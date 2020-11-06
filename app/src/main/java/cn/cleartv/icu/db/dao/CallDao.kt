package cn.cleartv.icu.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.cleartv.icu.CallStatus
import cn.cleartv.icu.db.entity.Call
import cn.cleartv.icu.db.entity.CallDetail
import cn.cleartv.icu.utils.TimeUtils


/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Dao
interface CallDao {
    // suspend keyword can not be used with Dao function return type is LiveData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(call: Call)


    @Query(
        """
        SELECT 
        id,
        callType,
        callStatus,
        call_table.callNumber,
        amCaller,
        mark,
        recordUrl,
        recordStatus,
        callTime,
        connectTime,
        endTime,
        number as device_number,
        name as device_name,
        type as device_type,
        status as device_status,
        lastOnLineTime as device_lastOnLineTime,
        devices_table.callNumber as device_callNumber,
        description as device_description
        FROM 
        call_table,devices_table 
        WHERE 
        call_table.callNumber=number
        ORDER BY callTime DESC
    """
    )
//    fun getCallList(): LiveData<List<CallDetail>>
    fun getCallList(): DataSource.Factory<Int, CallDetail>

    @Query("UPDATE call_table SET callStatus='${CallStatus.CALLING}', connectTime=:time WHERE callNumber=:callNumber AND callStatus='${CallStatus.RINGING}'")
    fun updateCallConnect(callNumber: String, time: Long = TimeUtils.nowMills)

    @Query("UPDATE call_table SET callStatus='${CallStatus.FINISHED}', endTime=:time, mark=:message WHERE callNumber=:callNumber AND (callStatus='${CallStatus.RINGING}' OR callStatus='${CallStatus.CALLING}')")
    fun updateCallFinished(
        callNumber: String,
        message: String?,
        time: Long = TimeUtils.nowMills
    )

    @Query("DELETE FROM call_table")
    suspend fun deleteAll()
}