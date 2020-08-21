package cn.cleartv.icu.ui

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.R
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.voip.VoIPClient
import kotlinx.android.synthetic.main.fragment_call.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CallFragment : BaseFragment() {

    private val callViewModel: CallViewModel by activityViewModels()

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_call
    }

    override fun afterInflateView() {
        btn_hangup.setOnClickListener { removeFragment(this) }

        callViewModel.exitInfo.observe(this, Observer {
            it?.let {
                toast(it)
                removeFragment(this@CallFragment)
            }
        })
        callViewModel.myInfoData.observe(this, Observer {
            video_local.member = it
        })
        callViewModel.remoteInfoData.observe(this, Observer {
            video_remote.member = it
        })
        callViewModel.startCall()
    }

    override fun onDestroyView() {
        VoIPClient.hangupCall()
        App.deviceInfo.callNumber = ""
        App.deviceInfo.status = DeviceStatus.IDLE
        App.deviceInfo.lastOnLineTime = System.currentTimeMillis()
        VoIPClient.sendMessage(
            App.hostNumber,
            "heartbeat",
            JsonUtils.toJson(App.deviceInfo)
        )
        super.onDestroyView()
    }

}