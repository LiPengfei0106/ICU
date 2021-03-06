package cn.cleartv.icu.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.entity.Device
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
interface DeviceDao {
    // suspend keyword can not be used with Dao function return type is LiveData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: Device)

    @Query("SELECT * FROM devices_table where number=:number limit 1")
    fun get(number: String): Device?

    @Update
    suspend fun update(device: Device)

    @Delete
    suspend fun delete(device: Device)

    @Query("SELECT * FROM devices_table ORDER BY number ASC")
    fun getAllDevices(): LiveData<List<Device>>

    @Query("SELECT * FROM devices_table WHERE type=:type ORDER BY number ASC")
    fun getDevicesByType(@DeviceType type: String): LiveData<List<Device>>

    @Query("DELETE FROM devices_table")
    suspend fun deleteAll()

    @Query("DELETE FROM devices_table")
    suspend fun updateDeviceStatus()

    @Query("UPDATE devices_table SET status='DISCONNECT' WHERE status!='DISCONNECT' AND lastOnLineTime+:timeoutMillis<:currentTimeMillis")
    suspend fun updateOfflineStatus(timeoutMillis: Int,currentTimeMillis: Long = TimeUtils.nowMills)
}