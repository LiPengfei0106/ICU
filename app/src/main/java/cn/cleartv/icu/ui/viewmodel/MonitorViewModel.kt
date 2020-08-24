package cn.cleartv.icu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.cleartv.icu.BaseViewModel
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.repository.MonitorRepository
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.entity.VoIPMember

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
    val currentMonitorMember : LiveData<VoIPMember?> = MonitorRepository.currentMonitorMember
    val otherMonitorMember : LiveData<VoIPMember?> = MonitorRepository.otherMonitorMember
    val exitData : LiveData<String?> = MonitorRepository.exitData
    val tipData : LiveData<String?> = MonitorRepository.tipData
    val isInterCut : LiveData<Boolean> = MonitorRepository.isInterCut

    fun startMonitor() {
        MonitorRepository.resetData()
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

    fun interCut(msg: String, videoOn: Boolean = false) {
        VoIPClient.startInterCut(videoOn, msg)
    }

    fun stopInterCut(){
        VoIPClient.stopInterCut()
    }

    fun stopMonitor(){
        VoIPClient.stopMonitor()
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