package cn.cleartv.icu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseViewModel
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.icu.utils.TimeUtils
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.entity.VoIPMember
import kotlinx.coroutines.delay
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

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

    private val repository = DeviceRepository
    val devices = repository.getAllDevices()
    val bedDevices = repository.getDevicesByType(DeviceType.BED)
    val guestDevices = repository.getDevicesByType(DeviceType.GUEST)
    val doorDevices = repository.getDevicesByType(DeviceType.DOOR)


    val callDevices: LiveData<HashMap<String,Device>> = CallRepository.callDevices

    fun startHeartBeat() {
        launchUI {
            while (true) {
                App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
                VoIPClient.sendMessage(
                    App.hostDevice.number,
                    "heartbeat",
                    JsonUtils.toJson(App.deviceInfo)
                )
                delay(10000)
            }
        }
    }

    fun startCheckDeviceOnLine() {
        launchUI {
            while (true) {
                delay(15000)
                repository.updateOfflineStatus()
            }
        }
    }

}