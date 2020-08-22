package cn.cleartv.icu.repository

import androidx.lifecycle.LiveData
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.ICUDatabase
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


    override fun onInterCutFailed(msg: String) {
        // TODO 插话失败通知
        Timber.d("onInterCutFailed: \n $msg")
    }

    override fun onInterCutSuccess() {
        // TODO 插话成功通知
        Timber.d("onInterCutSuccess")
    }

    override fun onMgtInterCutStop() {
        // TODO 停止插话通知
        Timber.d("onMgtInterCutStop")
    }

    override fun onMonitorConnected(member: VoIPMember) {
        // TODO 监听成功通知
        Timber.d("onMonitorConnected: \n ${member.toJsonString()}")
    }

    override fun onMonitorDisconnected(msg: String) {
        // TODO 监听断开通知
        Timber.d("onMonitorDisconnected: \n $msg")
    }

    override fun onMonitorStart(myInfo: VoIPMember) {
        // TODO 监听开始
        Timber.d("onMonitorDisconnected: \n ${myInfo.toJsonString()}")
    }

    override fun onMonitorStop() {
        // TODO 监听停止
        Timber.d("onMonitorStop")
    }
}