package cn.cleartv.icu.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.cleartv.icu.CallStatus
import cn.cleartv.icu.db.entity.Call


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

    @Query("SELECT * FROM call_table ORDER BY callTime ASC")
    fun getCallList(): LiveData<List<Call>>

    @Query("UPDATE call_table SET callStatus='${CallStatus.CALLING}', connectTime=:time WHERE callNumber=:callNumber AND callStatus='${CallStatus.RINGING}'")
    fun updateCallConnect(callNumber: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE call_table SET callStatus='${CallStatus.FINISHED}', endTime=:time, mark=:message WHERE callNumber=:callNumber AND (callStatus='${CallStatus.RINGING}' OR callStatus='${CallStatus.CALLING}')")
    fun updateCallFinished(callNumber: String, message: String?, time: Long = System.currentTimeMillis())

}