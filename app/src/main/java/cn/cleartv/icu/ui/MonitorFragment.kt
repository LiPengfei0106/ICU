package cn.cleartv.icu.ui

import androidx.fragment.app.activityViewModels
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.R
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
class MonitorFragment : BaseFragment() {

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_monitor
    }

    override fun afterInflateView() {
    }

    override fun onDestroyView() {
        VoIPClient.stopMonitor()
        super.onDestroyView()
    }



}