package cn.cleartv.icu.repository

import androidx.lifecycle.LiveData
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.ICUDatabase
import cn.cleartv.icu.db.entity.Device

/**
 * <pre>
 * author : Lee
 * e-mail : lipengfei@cleartv.cn
 * time   : 2020/08/18
 * desc   :
 * version: 1.0
</pre> *
 */
object DeviceRepository {

    private val deviceDao = ICUDatabase.instance.deviceDao()

    fun getDevicesByType(@DeviceType type: String): LiveData<List<Device>>{
        return deviceDao.getDevicesByType(type)
    }

    fun getAllDevices(): LiveData<List<Device>>{
        return deviceDao.getAllDevices()
    }

    suspend fun getDevice(number: String): Device?{
        return deviceDao.get(number)
    }

    fun addDevice(device: Device){
        deviceDao.insert(device)
    }

    suspend fun deleteDevice(device: Device){
        deviceDao.delete(device)
    }

    suspend fun deleteAllDevice(){
        deviceDao.deleteAll()
    }

    suspend fun updateOfflineStatus(timeoutMillis: Int = 30000){
        deviceDao.updateOfflineStatus(timeoutMillis)
    }
}