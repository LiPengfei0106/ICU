package cn.cleartv.icu.repository

import androidx.lifecycle.MutableLiveData
import cn.cleartv.icu.App
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.entity.VoIPMember
import org.json.JSONObject
import timber.log.Timber

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object CallRepository: VoIPClient.VoIPCallListener,
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

    fun resetData(isTransfer: Boolean){
        this.isTransfer = isTransfer
        tipData.postValue(null)
        textData.postValue(null)
        exitData.postValue(null)
        remoteInfoData.postValue(null)
        localInfoData.postValue(null)
    }

    override fun onCallConnected(member: VoIPMember) {
        Timber.d("onCallConnected: \n ${member.toJsonString()}")
        App.deviceInfo.status = if (member.userNum == App.deviceInfo.callNumber) DeviceStatus.IN_CALL_CALLER else DeviceStatus.IN_CALL_CALLEE
        App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
        VoIPClient.sendMessage(
            App.hostNumber,
            "heartbeat",
            JsonUtils.toJson(App.deviceInfo)
        )
        remoteInfoData.postValue(member)
        isTransfer = false
    }

    override fun onCallDisconnected(msg: String) {
        // 1v1通话异常断开
        Timber.d("onCallDisconnected: \n $msg")
        exitData.postValue(msg)
    }

    override fun onCallRefuse(reason: String) {
        // 对方拒绝接受通话
        Timber.d("onCallRefuse: \n $reason")
        exitData.postValue(reason)
    }

    override fun onCallStart(myInfo: VoIPMember) {
        // 通话开始
        Timber.d("onCallStart: \n ${myInfo.toJsonString()}")
        localInfoData.postValue(myInfo)
    }

    override fun onHangup() {
        // 当前通话挂断
        Timber.d("onHangup")
        if(isTransfer){
            Timber.d("isTransfer")
        }else{
            exitData.postValue("对方已挂断")
        }
    }

    override fun onMgtCancel(mangerNum: String, message: String) {
        // 管理员强拆当前通话
        Timber.d("onMgtCancel: \n mangerNum-$mangerNum; message-$message")
        exitData.postValue(message)
    }

    override fun onMgtInterCut(mangerNum: String, message: String) {
        // 管理员开始插话
        Timber.d("onMgtInterCut: \n mangerNum-$mangerNum; message-$message")
        tipData.postValue(message)
    }

    override fun onMgtInterCutStop() {
        // 管理员插话结束
        Timber.d("onMgtInterCutStop")
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