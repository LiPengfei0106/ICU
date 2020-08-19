package cn.cleartv.icu.ui

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.ui.adapter.BedDeviceAdapter
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import kotlinx.android.synthetic.main.fragment_host_main.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HostMainFragment : BaseFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_host_main
    }

    override fun afterInflateView() {
        rv_bed.adapter = BedDeviceAdapter(ArrayList())
        rv_door.adapter = DeviceAdapter(ArrayList())
        rv_guest.adapter = DeviceAdapter(ArrayList())
        viewModel.bedDevices.observe(this, Observer {
            (rv_bed.adapter as BedDeviceAdapter).setDiffNewData(ArrayList(it))
        })
        viewModel.guestDevices.observe(this, Observer {
            (rv_guest.adapter as DeviceAdapter).setDiffNewData(ArrayList(it))
        })
        viewModel.doorDevices.observe(this, Observer {
            (rv_door.adapter as DeviceAdapter).setDiffNewData(ArrayList(it))
        })

    }
}