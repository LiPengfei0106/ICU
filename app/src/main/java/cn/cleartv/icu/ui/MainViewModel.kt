package cn.cleartv.icu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseViewModel
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.entity.Device
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
class MainViewModel : BaseViewModel(), VoIPClient.VoIPListener,
    VoIPClient.VoIPReceivedCallListener {

    init {
        VoIPClient.voipListener = this
        VoIPClient.receivedCallListener = this
    }

    private val repository = DeviceRepository()
    val devices = repository.getAllDevices()
    val bedDevices = repository.getDevicesByType(DeviceType.BED)
    val guestDevices = repository.getDevicesByType(DeviceType.GUEST)
    val doorDevices = repository.getDevicesByType(DeviceType.DOOR)


    val callDevices = MutableLiveData<HashMap<String, Device>>().apply {
        value = HashMap()
    }

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

    fun startHeartBeat() {
        launchUI {
            while (true) {
                App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
                VoIPClient.sendMessage(
                    App.hostNumber,
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
                repository.updateOfflineStatus()
                delay(10000)
            }
        }
    }

    fun updateDevice(device: Device) {
        launchUI {
            repository.addDevice(device)
        }
    }


    override fun onMessage(memberNumber: String, type: String, message: String) {
        // 处理自定义的消息
        Timber.d("onMessage: \n memberNumber-$memberNumber; type-$type; message-$message")
        when (type) {
            "heartbeat" -> {
                // 分机像主机发送心跳，用于同步分机状态
                try {
                    JsonUtils.fromJson(
                        message,
                        Device::class.java
                    )?.let {
                        updateDevice(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onPong(data: JSONObject) {
        // TODO 更新本地时间
        Timber.d("onPong: \n $data")
    }

    override fun onCall(member: VoIPMember) {
        // 收到来电请求
        Timber.d("onCall: \n ${member.toJsonString()}")
        member.tag?.let {
            val device = JsonUtils.fromJson(it, Device::class.java) ?: Device(
                member.userNum,
                name = member.userNum
            )
            callDevices.value?.let { devices ->
                devices[device.number] = device
                callDevices.postValue(devices)
            }
        }

    }

    override fun onHangup(memberNumber: String) {
        // 收到挂断消息
        Timber.d("onHangup: \n $memberNumber")
        callDevices.value?.let {
            if(it.remove(memberNumber) != null){
                callDevices.postValue(it)
            }
        }
    }

}