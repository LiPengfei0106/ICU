package cn.cleartv.icu.ui

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.ui.adapter.BedDeviceAdapter
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import kotlinx.android.synthetic.main.fragment_bed_main.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class BedMainFragment : BaseFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_bed_main
    }

    override fun afterInflateView() {
        btn_request.setOnClickListener {
            viewModel.callHost()
        }
    }
}