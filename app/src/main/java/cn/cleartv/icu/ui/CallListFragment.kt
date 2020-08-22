package cn.cleartv.icu.ui

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseDialogFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_call_list.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CallListFragment : BaseDialogFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val callViewModel: CallViewModel by activityViewModels()

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_call_list
    }

    override fun afterInflateView() {
        rv_devices
        rv_devices.adapter = DeviceAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                Intent(requireContext(),CallActivity::class.java).apply {
                    putExtra("number",(adapter as DeviceAdapter).getItem(position).number)
                    putExtra("amCaller",false)
                    startActivity(this)
                }
            }
        }
        mainViewModel.callDevices.observe(viewLifecycleOwner, Observer {
            (rv_devices.adapter as? DeviceAdapter)?.setDiffNewData(ArrayList(it.values))
        })
    }
}