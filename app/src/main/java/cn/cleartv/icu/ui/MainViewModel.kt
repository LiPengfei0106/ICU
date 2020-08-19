package cn.cleartv.icu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import cn.cleartv.icu.BaseViewModel
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.utils.TimeUtils
import kotlinx.coroutines.delay
import java.util.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class MainViewModel : BaseViewModel() {

    private val repository = DeviceRepository()
    val devices = repository.getAllDevices()
    val bedDevices = repository.getDevicesByType(DeviceType.BED)
    val guestDevices = repository.getDevicesByType(DeviceType.GUEST)
    val doorDevices = repository.getDevicesByType(DeviceType.DOOR)

    val dateTime = liveData {
        while (true) {
            emit(TimeUtils.nowString + "  " + TimeUtils.getChineseWeek(Date()))
            delay(1000)
        }
    }

    fun getDevice(number: String): LiveData<Device?> {
        return repository.getDevice(number)
    }

    fun addDevice(device: Device) {
        launchUI {
            repository.addDevice(device)
        }
    }

    fun deleteDevice(device: Device) {
        launchUI {
            repository.deleteDevice(device)
        }
    }

    fun deleteAllDevice() {
        launchUI {
            repository.deleteAllDevice()
        }
    }

}