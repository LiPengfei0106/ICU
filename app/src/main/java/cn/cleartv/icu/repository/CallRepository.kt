package cn.cleartv.icu.repository

import androidx.lifecycle.MutableLiveData
import cn.cleartv.icu.App
import cn.cleartv.icu.CallStatus
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.TTSOutputManager
import cn.cleartv.icu.db.ICUDatabase
import cn.cleartv.icu.db.entity.Call
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.annotation.CallRecordStatus
import cn.cleartv.voip.entity.VoIPMember
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


    val callDevices = MutableLiveData<HashMap<String, Device>>().apply {
        value = HashMap()
    }

    var isTransfer = false

    val callDao = ICUDatabase.instance.callDao()

    val callRecordList = callDao.getCallList()

    fun resetData(isTransfer: Boolean = false) {
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
        App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
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
        Timber.d("onHangup")
        if (isTransfer) {
            Timber.d("isTransfer")
        } else {
            exitData.postValue(if (message.isNotBlank()) message else "对方已挂断")
        }
        resetData(isTransfer)
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
        Timber.d("onCall: \n ${member.toJsonString()}")
        member.tag?.let {
            val device = JsonUtils.fromJson(it, Device::class.java) ?: Device(
                member.userNum,
                name = member.userNum
            )
            DeviceRepository.addDevice(device)
            newCall(device, false)

            if (device.status == DeviceStatus.MONITOR) {
                if (App.deviceInfo.status == DeviceStatus.IDLE) {
                    VoIPClient.acceptCall(
                        member.userNum,
                        true,
                        true,
                        JsonUtils.toJson(App.deviceInfo)
                    )
                } else {
                    VoIPClient.hangupCall(member.userNum, App.deviceInfo.number, "对方正忙")
                    callDao.updateCallFinished(member.userNum, "未接听")
                }
            } else {
                callDevices.value?.let { devices ->
                    devices[device.number] = device
                    callDevices.postValue(devices)
                }
            }
        }

    }

    override fun onHangup(memberNumber: String, message: String) {
        // 收到挂断消息
        Timber.d("onHangup: \n $memberNumber, \n $message")

        callDao.updateCallFinished(memberNumber, message)

        callDevices.value?.let {
            if (it.remove(memberNumber) != null) {
                callDevices.postValue(it)
            }
        }
    }

    fun newCall(device: Device, amCaller: Boolean) {
        callDao.insert(
            Call(
                callNumber = device.callNumber,
                amCaller = amCaller,
                callStatus = CallStatus.RINGING
            )
        )
    }

    suspend fun updateCallRecord(call: Call): Call {
        // 查询这个通话的录像地址并更新，成功的或者没有开启录制的就不查了
        if (call.callStatus != CallRecordStatus.COMPLETE && call.recordStatus != "NONE") {
            val from = if (call.amCaller) App.deviceInfo.number else call.callNumber
            val to = if (!call.amCaller) App.deviceInfo.number else call.callNumber
            VoIPClient.getCallRecordList(
                from,
                to,
                Date(call.callTime),
                if (call.endTime > 0) Date(call.endTime) else Date()
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
}