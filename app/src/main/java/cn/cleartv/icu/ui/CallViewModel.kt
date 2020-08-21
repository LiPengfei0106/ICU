package cn.cleartv.icu.ui

import androidx.lifecycle.MutableLiveData
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseViewModel
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.db.entity.Device
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
class CallViewModel: BaseViewModel(), VoIPClient.VoIPCallListener {

    val myInfoData = MutableLiveData<VoIPMember?>()
    val remoteInfoData = MutableLiveData<VoIPMember?>()
    val exitInfo = MutableLiveData<String?>()

    var callDevice: Device? = null
    var amCaller = false

    init {
        VoIPClient.callListener = this
    }

    fun startCall(){
        callDevice?.let {
            App.deviceInfo.callNumber = it.number
            App.deviceInfo.status = if(amCaller) DeviceStatus.IN_CALL_CALLER else DeviceStatus.IN_CALL_CALLEE
            App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
            VoIPClient.sendMessage(
                App.hostNumber,
                "heartbeat",
                JsonUtils.toJson(App.deviceInfo)
            )
            if(amCaller){
                Timber.d("startCall")
                VoIPClient.startCall(it.number, true,true, JsonUtils.toJson(App.deviceInfo))
            }else{
                Timber.d("acceptCall")
                VoIPClient.acceptCall(it.number, true,true, JsonUtils.toJson(App.deviceInfo))
            }
        }
    }

    override fun onCallConnected(member: VoIPMember) {
        Timber.d("onCallConnected: \n ${member.toJsonString()}")
        remoteInfoData.postValue(member)
    }

    override fun onCallDisconnected(msg: String) {
        // 1v1通话异常断开
        Timber.d("onCallDisconnected: \n $msg")
        exitInfo.postValue(msg)
    }

    override fun onCallRefuse(reason: String) {
        // 对方拒绝接受通话
        Timber.d("onCallRefuse: \n $reason")
        exitInfo.postValue(reason)
    }

    override fun onCallStart(myInfo: VoIPMember) {
        // 通话开始
        Timber.d("onCallStart: \n ${myInfo.toJsonString()}")
        myInfoData.postValue(myInfo)
    }

    override fun onHangup() {
        // 当前通话挂断
        Timber.d("onHangup")
        exitInfo.postValue("对方已挂断")
    }

    override fun onMgtCancel(mangerNum: String, message: String) {
        // 管理员强拆当前通话
        Timber.d("onMgtCancel: \n mangerNum-$mangerNum; message-$message")
        toast(message)
    }

    override fun onMgtInterCut(mangerNum: String, message: String) {
        // 管理员开始插话
        Timber.d("onMgtInterCut: \n mangerNum-$mangerNum; message-$message")
        toast(message)
    }

    override fun onMgtInterCutStop() {
        // 管理员插话结束
        Timber.d("onMgtInterCutStop")
        toast("onMgtInterCutStop")
    }

    override fun onText(text: String) {
        // 通话对方发送的Text消息
        Timber.d("onText: \n $text")
        toast(text)
    }

}