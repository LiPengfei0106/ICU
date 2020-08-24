package cn.cleartv.icu.repository

import androidx.lifecycle.MutableLiveData
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.entity.VoIPMember
import timber.log.Timber

/**
 * <pre>
 * author : Lee
 * e-mail : lipengfei@cleartv.cn
 * time   : 2020/08/18
 * desc   :
 * version: 1.0
</pre> *
 */
object MonitorRepository: VoIPClient.VoIPMonitorListener {

    var monitorDevice: Device? = null
    val localMember = MutableLiveData<VoIPMember?>()
    val currentMonitorMember = MutableLiveData<VoIPMember?>()
    val otherMonitorMember = MutableLiveData<VoIPMember?>()
    val exitData = MutableLiveData<String?>()
    val tipData = MutableLiveData<String?>()
    val isInterCut = MutableLiveData(false)

    fun resetData(){
        localMember.value = null
        currentMonitorMember.value = null
        otherMonitorMember.value = null
        exitData.value = null
        tipData.value = null
        isInterCut.value = false
    }

    override fun onInterCutFailed(msg: String) {
        Timber.d("onInterCutFailed: \n $msg")
        tipData.postValue(msg)
        isInterCut.postValue(false)
    }

    override fun onInterCutSuccess() {
        Timber.d("onInterCutSuccess")
        tipData.postValue("正在插话")
        isInterCut.postValue(true)
    }

    override fun onMgtInterCutStop() {
        Timber.d("onMgtInterCutStop")
        tipData.postValue("插话结束")
        isInterCut.postValue(false)
    }

    override fun onMonitorConnected(member: VoIPMember) {
        Timber.d("onMonitorConnected: \n ${member.toJsonString()}")
        if(member.userNum == monitorDevice?.number){
            currentMonitorMember.postValue(member)
        }else{
            otherMonitorMember.postValue(member)
        }
    }

    override fun onMonitorDisconnected(msg: String) {
        Timber.d("onMonitorDisconnected: \n $msg")
        exitData.postValue(msg)
        isInterCut.postValue(false)
    }

    override fun onMonitorStart(myInfo: VoIPMember) {
        Timber.d("onMonitorDisconnected: \n ${myInfo.toJsonString()}")
        localMember.postValue(myInfo)
    }

    override fun onMonitorStop() {
        Timber.d("onMonitorStop")
        exitData.postValue("监听结束")
        isInterCut.postValue(false)
    }
}