package cn.cleartv.icu.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.entity.Device

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
    suspend fun insert(device: Device): Long

    @Query("SELECT * FROM devices_table where number=:number limit 1")
    fun get(number: String): LiveData<Device?>

    @Update
    suspend fun update(device: Device)

    @Delete
    suspend fun delete(device: Device)

    @Query("SELECT * FROM devices_table")
    fun getAllDevices(): LiveData<List<Device>>

    @Query("SELECT * FROM devices_table WHERE type=:type")
    fun getDevicesByType(@DeviceType type: String): LiveData<List<Device>>

    @Query("DELETE FROM devices_table")
    suspend fun deleteAll()
}