package cn.cleartv.icu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.entity.VoIPMember
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import timber.log.Timber

class MainActivity : BaseActivity(), VoIPClient.VoIPListener, VoIPClient.VoIPCallListener,
    VoIPClient.VoIPReceivedCallListener, VoIPClient.VoIPMonitorListener {

    private val viewModel: MainViewModel by viewModels()

    override fun contentLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun afterSetContentView() {
        viewModel.dateTime.observe(this, Observer {
            tv_date.text = it
        })

        VoIPClient.voipListener = this
        VoIPClient.callListener = this
        VoIPClient.receivedCallListener = this
        VoIPClient.monitorListener = this
        when(App.deviceType){
            DeviceType.HOST -> supportFragmentManager.beginTransaction().add(R.id.fl_content,HostMainFragment()).commit()
            DeviceType.BED -> supportFragmentManager.beginTransaction().add(R.id.fl_content,BedMainFragment()).commit()
            DeviceType.GUEST -> supportFragmentManager.beginTransaction().add(R.id.fl_content,GuestMainFragment()).commit()
        }
        if(App.deviceType != DeviceType.HOST) viewModel.startHeartBeat()
    }

    override fun onMessage(memberNumber: String, type: String, message: String) {
        // 处理自定义的消息
        Timber.d("onMessage: \n memberNumber-$memberNumber; type-$type; message-$message")
        when(type){
            "heartbeat" -> {
                // 分机像主机发送心跳，用于同步分机状态

            }
        }
    }

    override fun onPong(data: JSONObject) {
        // TODO 更新本地时间
        Timber.d("onPong: \n $data")
    }

    override fun onCall(member: VoIPMember) {
        // TODO 收到来电请求
        Timber.d("onCall: \n ${member.toJsonString()}")
    }

    override fun onHangup(memberNumber: String) {
        // TODO 收到挂断消息
        Timber.d("onHangup: \n $memberNumber")
    }


    override fun onCallConnected(member: VoIPMember) {
        // TODO 1v1通话连接成功
        Timber.d("onCallConnected: \n ${member.toJsonString()}")
    }

    override fun onCallDisconnected(msg: String) {
        // TODO 1v1通话异常断开
        Timber.d("onCallDisconnected: \n $msg")
    }

    override fun onCallRefuse(reason: String) {
        // TODO 对方拒绝接受通话
        Timber.d("onCallRefuse: \n $reason")
    }

    override fun onCallStart(myInfo: VoIPMember) {
        // TODO 通话开始
        Timber.d("onCallStart: \n ${myInfo.toJsonString()}")
    }

    override fun onHangup() {
        // TODO 当前通话挂断
        Timber.d("onHangup")
    }

    override fun onMgtCancel(mangerNum: String, message: String) {
        // TODO 管理员强拆当前通话
        Timber.d("onMgtCancel: \n mangerNum-$mangerNum; message-$message")
    }

    override fun onMgtInterCut(mangerNum: String, message: String) {
        // TODO 管理员开始插话
        Timber.d("onMgtInterCut: \n mangerNum-$mangerNum; message-$message")
    }

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

    override fun onText(text: String) {
        // TODO 通话对方发送的Text消息
        Timber.d("onText: \n $text")
    }

}
