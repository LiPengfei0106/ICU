package cn.cleartv.icu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseViewModel
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.repository.MonitorRepository
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
class MonitorViewModel : BaseViewModel() {


    val localMember : LiveData<VoIPMember?> = MonitorRepository.localMember
    val currentMonitorCallMember : LiveData<VoIPMember?> = MonitorRepository.currentMonitorMember
    val otherMonitorCallMember : LiveData<VoIPMember?> = MonitorRepository.otherMonitorMember
    val exitData : LiveData<String?> = MonitorRepository.exitData
    val tipData : LiveData<String?> = MonitorRepository.tipData
    val isInterCut : LiveData<Boolean> = MonitorRepository.isInterCut
    val monitorMember : LiveData<VoIPMember?> = CallRepository.remoteInfoData

    fun startMonitorCall() {
        MonitorRepository.resetData()
        Timber.d("startMonitorCall")
        MonitorRepository.monitorDevice?.let {
            when (it.status) {
                DeviceStatus.IN_CALL_CALLEE -> {
                    VoIPClient.startMonitor(it.callNumber, it.number)
                }
                DeviceStatus.IN_CALL_CALLER -> {
                    VoIPClient.startMonitor(it.number, it.callNumber)
                }
            }
        }
    }

    fun startMonitor() {
        CallRepository.resetData(false)
        Timber.d("startMonitor")
        MonitorRepository.monitorDevice?.let {
            when (it.status) {
                DeviceStatus.IDLE -> {
                    App.deviceInfo.status = DeviceStatus.MONITOR
                    VoIPClient.startCall(it.number, false, false, JsonUtils.toJson(App.deviceInfo))
                }
                DeviceStatus.DISCONNECT -> {
                    toast("对方不在线")
                }
                else -> {
                    toast("对方正忙")
                }
            }
        }
    }

    fun interCut(msg: String, videoOn: Boolean = false) {
        VoIPClient.startInterCut(videoOn, msg)
    }

    fun stopInterCut(){
        VoIPClient.stopInterCut()
    }

    fun stopMonitorCall(){
        if(isInterCut.value == true){
            VoIPClient.stopInterCut()
        }
        VoIPClient.stopMonitor()
    }

    fun stopMonitor(){
        CallRepository.resetData(false)
        VoIPClient.hangupCall()
    }

    fun mgtCancel(message: String){
        MonitorRepository.monitorDevice?.let {
            when (it.status) {
                DeviceStatus.IN_CALL_CALLEE -> {
                    VoIPClient.mgtCancelCall(it.callNumber, it.number, message)
                }
                DeviceStatus.IN_CALL_CALLER -> {
                    VoIPClient.mgtCancelCall(it.number, it.callNumber,message)
                }
            }
        }
    }
}