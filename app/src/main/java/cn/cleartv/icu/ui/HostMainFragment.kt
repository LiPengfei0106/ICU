package cn.cleartv.icu.ui

import android.Manifest
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.ui.adapter.BedDeviceAdapter
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import cn.cleartv.icu.utils.PermissionUtils
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

    private val mainViewModel: MainViewModel by activityViewModels()
    private val callViewModel: CallViewModel by activityViewModels()
    private val monitorViewModel: MonitorViewModel by activityViewModels()

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_host_main
    }

    override fun afterInflateView() {
        rv_bed.adapter = BedDeviceAdapter(ArrayList()).apply {
            setOnItemChildClickListener { adapter, view, position ->
                PermissionUtils.checkPermission(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    Runnable {
                        when(view.id){
                            R.id.tv_monitor -> {
                                addFragment(MonitorFragment())
                            }
                            R.id.tv_call -> {
                                callViewModel.callDevice = (adapter as BedDeviceAdapter).getItem(position)
                                callViewModel.amCaller = true
                                addFragment(CallFragment())
                            }
                        }
                    })
            }
        }
        rv_door.adapter = DeviceAdapter(ArrayList()).apply {
            setOnItemClickListener { adapter, view, position ->
                PermissionUtils.checkPermission(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    Runnable {
                        callViewModel.callDevice = (adapter as DeviceAdapter).getItem(position)
                        callViewModel.amCaller = true
                        addFragment(CallFragment())
                    })
            }
        }
        rv_guest.adapter = DeviceAdapter(ArrayList()).apply {
            setOnItemClickListener { adapter, view, position ->
                PermissionUtils.checkPermission(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    Runnable {
                        callViewModel.callDevice = (adapter as DeviceAdapter).getItem(position)
                        callViewModel.amCaller = true
                        addFragment(CallFragment())
                    })
            }
        }
        mainViewModel.bedDevices.observe(this, Observer {
            (rv_bed.adapter as BedDeviceAdapter).setDiffNewData(ArrayList(it))
        })
        mainViewModel.guestDevices.observe(this, Observer {
            (rv_guest.adapter as DeviceAdapter).setDiffNewData(ArrayList(it))
        })
        mainViewModel.doorDevices.observe(this, Observer {
            (rv_door.adapter as DeviceAdapter).setDiffNewData(ArrayList(it))
        })

        mainViewModel.callDevices.observe(this, Observer {
            it.keys.firstOrNull()?.let {key->
                callViewModel.callDevice = it[key]
                callViewModel.amCaller = false
                addFragment(CallFragment())
            }
        })
    }
}