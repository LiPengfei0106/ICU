package cn.cleartv.icu.repository

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.paging.toLiveData
import cn.cleartv.icu.*
import cn.cleartv.icu.db.ICUDatabase
import cn.cleartv.icu.db.entity.Call
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.ui.CallActivity
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.icu.utils.TimeUtils
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.annotation.CallRecordStatus
import cn.cleartv.voip.entity.VoIPMember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object CallRepository : VoIPClient.VoIPCallListener,
    VoIPClient.VoIPReceivedCallListener {

    val localInfoData = MutableLiveData<VoIPMember?>()
    val remoteInfoData = MutableLiveData<VoIPMember?>()
    val exitData = MutableLiveData<String?>()
    val textData = MutableLiveData<String?>()
    val tipData = MutableLiveData<String?>()

    private var monitorNumber = ""


    val callDevices = MutableLiveData<HashMap<String, Device>>().apply {
        value = HashMap()
    }

    var isTransfer = false

    val callDao = ICUDatabase.instance.callDao()

    val callRecordList = callDao.getCallList().toLiveData(pageSize = 20)

    fun resetData(isTransfer: Boolean = false) {
        Timber.i("resetData")
        monitorNumber = ""
        this.isTransfer = isTransfer
        tipData.postValue(null)
        textData.postValue(null)
        exitData.postValue(null)
        remoteInfoData.postValue(null)
        localInfoData.postValue(null)
    }

    override fun onCallConnected(member: VoIPMember) {
        Timber.d("onCallConnected: \n ${member.toJsonString()}")
        if (DeviceStatus.CALLING == App.deviceInfo.status) {
            App.deviceInfo.status = DeviceStatus.IN_CALL_CALLER
        }
        App.deviceInfo.lastOnLineTime = TimeUtils.nowMills
        VoIPClient.sendMessage(
            App.hostDevice.number,
            "heartbeat",
            JsonUtils.toJson(App.deviceInfo)
        )
        remoteInfoData.postValue(member)
        isTransfer = false

        callDao.updateCallConnect(member.userNum)
    }

    override fun onCallDisconnected(msg: String) {
        // 1v1通话异常断开
        Timber.d("onCallDisconnected: \n $msg")
        exitData.postValue(msg)

        remoteInfoData.value?.let {
            callDao.updateCallFinished(it.userNum, msg)
        }
    }

    override fun onCallRefuse(reason: String) {
        // 对方拒绝接受通话
        Timber.d("onCallRefuse: \n $reason")
        exitData.postValue(reason)

        remoteInfoData.value?.let {
            callDao.updateCallFinished(it.userNum, reason)
        }
    }

    override fun onCallStart(myInfo: VoIPMember) {
        // 通话开始
        Timber.d("onCallStart: \n ${myInfo.toJsonString()}")
        localInfoData.postValue(myInfo)
    }

    override fun onHangup(message: String) {
        // 当前通话挂断
        Timber.d("onHangup: $message")
        if (isTransfer) {
            Timber.d("isTransfer")
            resetData(isTransfer)
        } else {
            exitData.postValue(if (message.isNotBlank()) message else "对方已挂断")
        }
    }

    override fun onMgtCancel(mangerNum: String, message: String) {
        // 管理员强拆当前通话
        Timber.d("onMgtCancel: \n mangerNum-$mangerNum; message-$message")
        remoteInfoData.value?.let {
            callDao.updateCallFinished(it.userNum, message)
        }
        exitData.postValue(message)
    }

    override fun onMgtInterCut(mangerNum: String, message: String) {
        // 管理员开始插话
        Timber.d("onMgtInterCut: \n mangerNum-$mangerNum; message-$message")
        when (message) {
            "all" -> {
                tipData.postValue("护士站插话")
            }
            App.deviceInfo.number -> {
                tipData.postValue("护士站插话")
            }
            else -> {
                remoteInfoData.value?.mediaStream?.audioTracks?.firstOrNull()?.setEnabled(false)
                TTSOutputManager.instance.speak("护士正在与对方沟通中，请稍候")
            }
        }
    }

    override fun onMgtInterCutStop() {
        // 管理员插话结束
        Timber.d("onMgtInterCutStop")
        remoteInfoData.value?.mediaStream?.audioTracks?.firstOrNull()?.setEnabled(true)
        tipData.postValue("插话结束")
    }

    override fun onText(text: String) {
        // 通话对方发送的Text消息
        Timber.d("onText: \n $text")
        textData.postValue(text)
    }

    override fun onCall(member: VoIPMember) {
        // 收到来电请求
        Timber.d("onCall VoIPMember: \n ${member.toJsonString()}")
        member.tag?.let {
            val device = JsonUtils.fromJson(it, Device::class.java) ?: Device(
                member.userNum,
                name = member.userNum
            )
            Timber.d("onCall Device: \n $device")
            DeviceRepository.addDevice(device)
            insertCallRecord(member.userNum, false)

            if (device.status == DeviceStatus.MONITOR) {
                if (App.deviceInfo.status == DeviceStatus.IDLE) {
                    monitorNumber = member.userNum
                    VoIPClient.acceptCall(
                        member.userNum,
                        true,
                        true,
                        JsonUtils.toJson(App.deviceInfo)
                    )
                } else {
                    monitorNumber = ""
                    VoIPClient.hangupCall(member.userNum, App.deviceInfo.number, "对方正忙")
                    callDao.updateCallFinished(member.userNum, "未接听")
                }
            } else {
                monitorNumber = ""
                callDevices.value?.let { devices ->
                    devices[device.number] = device
                    callDevices.postValue(devices)
                    if (App.deviceType != DeviceType.HOST) {
                        // 这里非主机自动打开通话界面
                        Intent(App.instance, CallActivity::class.java).apply {
                            putExtra("device", JsonUtils.toJson(device))
                            putExtra("amCaller", false)
                            App.instance.startActivity(this)
                        }
                    }
                }
            }
        }

    }

    override fun onHangup(memberNumber: String, message: String) {
        // 收到挂断消息
        Timber.d("onHangup: \n $memberNumber, \n $message, \n${App.deviceInfo.status}, \n$monitorNumber")
        if(memberNumber == monitorNumber){
            callDevices.value?.let {
                it.remove(memberNumber)?.let {_ ->
                    callDevices.postValue(it)
                }
            }
            resetData(false)
        }

        callDao.updateCallFinished(memberNumber, message)

        callDevices.value?.let {
            if (it.remove(memberNumber) != null) {
                callDevices.postValue(it)
            }
        }
    }

    fun updateCallFinished(memberNumber: String, message: String) {
        Timber.d("updateCallFinished: \n $memberNumber, \n $message")
        callDao.updateCallFinished(memberNumber, message)
    }

    fun insertCallRecord(deviceNumber: String, amCaller: Boolean) {
        Timber.d("insertCallRecord: \n $deviceNumber, \n $amCaller")
        callDao.insert(
            Call(
                callNumber = deviceNumber,
                amCaller = amCaller,
                callStatus = CallStatus.RINGING
            )
        )
    }

    suspend fun updateCallRecord(call: Call): Call {
        // 查询这个通话的录像地址并更新，成功的或者没有开启录制的就不查了
        if (call.callStatus != CallRecordStatus.COMPLETE) {
            val from = if (call.amCaller) App.deviceInfo.number else call.callNumber
            val to = if (!call.amCaller) App.deviceInfo.number else call.callNumber
            VoIPClient.getCallRecordList(
                from,
                to,
                TimeUtils.nowDate,
                if (call.endTime > 0) Date(call.endTime + TimeUtils.timeInterval) else TimeUtils.nowDate
            ).firstOrNull().let {
                when (it?.status) {
                    CallRecordStatus.COMPLETE -> {
                        call.recordStatus = it.status
                        call.recordUrl = it.downUri
                    }
                    null -> {
                        call.recordStatus = "NONE"
                    }
                    else -> {
                        call.recordStatus = it.status
                    }
                }
                callDao.insert(call)
            }
        }
        return call
    }

    suspend fun deleteCallRecord() {
        callDao.deleteAll()
    }
}