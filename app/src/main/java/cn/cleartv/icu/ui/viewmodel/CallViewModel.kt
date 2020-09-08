package cn.cleartv.icu.ui.viewmodel

import androidx.lifecycle.LiveData
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseViewModel
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.entity.VoIPMember
import timber.log.Timber

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CallViewModel : BaseViewModel() {

    var callDevice: Device? = null

    val localInfoData: LiveData<VoIPMember?> = CallRepository.localInfoData
    val remoteInfoData: LiveData<VoIPMember?> = CallRepository.remoteInfoData
    val exitData: LiveData<String?> = CallRepository.exitData
    val tipData: LiveData<String?> = CallRepository.tipData
    val textData: LiveData<String?> = CallRepository.textData

    var callDuration = 0
        private set

    fun hangup(isTransfer: Boolean = false) {
        CallRepository.callDevices.value?.let {
            if(it.remove(App.deviceInfo.callNumber) != null){
                CallRepository.callDevices.postValue(it)
            }
        }
        CallRepository.resetData(isTransfer)
        VoIPClient.hangupCall()
        App.deviceInfo.callNumber = ""
        App.deviceInfo.status = DeviceStatus.IDLE
        App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
        VoIPClient.sendMessage(
            App.hostDevice.number,
            "heartbeat",
            JsonUtils.toJson(App.deviceInfo)
        )
    }

    fun startCall(device: Device, amCaller: Boolean, duration: Int = 0) {
        CallRepository.callDevices.value?.let {
            if(it.remove(device.number) != null){
                CallRepository.callDevices.postValue(it)
            }
        }
        CallRepository.resetData(false)
        VoIPClient.hangupCall()
        callDevice = device
        callDuration = duration
        if (amCaller) {
            Timber.d("startCall")
            App.deviceInfo.callNumber = device.number
            App.deviceInfo.status = DeviceStatus.CALLING
            App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
            VoIPClient.startCall(device.number, true, true, JsonUtils.toJson(App.deviceInfo),App.isRecord)
            VoIPClient.sendMessage(
                App.hostDevice.number,
                "heartbeat",
                JsonUtils.toJson(App.deviceInfo)
            )
            // 发起通话在这里新增记录，接受通话和通话状态的修改在CallRepository中
            CallRepository.newCall(App.deviceInfo,true)
        } else {
            Timber.d("acceptCall")
            App.deviceInfo.callNumber = device.number
            App.deviceInfo.status = DeviceStatus.IN_CALL_CALLEE
            App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
            VoIPClient.acceptCall(device.number, true, true, JsonUtils.toJson(App.deviceInfo))
            VoIPClient.sendMessage(
                App.hostDevice.number,
                "heartbeat",
                JsonUtils.toJson(App.deviceInfo)
            )
        }
    }

    fun transfer(device: Device, duration: Int) {
        hangup(true)
        startCall(device,true,duration)
    }

}