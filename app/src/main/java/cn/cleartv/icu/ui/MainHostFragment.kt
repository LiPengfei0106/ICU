package cn.cleartv.icu.ui

import android.Manifest
import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.ui.adapter.BedDeviceAdapter
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import cn.cleartv.icu.ui.viewmodel.MonitorViewModel
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
class MainHostFragment : BaseFragment() {

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
//                                addFragment(MonitorFragment())
                            }
                            R.id.tv_call -> {
                                Intent(requireContext(),CallActivity::class.java).apply {
                                    putExtra("number",(adapter as BedDeviceAdapter).getItem(position).number)
                                    putExtra("amCaller",true)
                                    startActivity(this)
                                }
                            }
                        }
                    })
            }
        }
        rv_door.adapter = DeviceAdapter(ArrayList()).apply {
            setOnItemClickListener { adapter, _, position ->
                PermissionUtils.checkPermission(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    Runnable {
                        Intent(requireContext(),CallActivity::class.java).apply {
                            putExtra("number",(adapter as DeviceAdapter).getItem(position).number)
                            putExtra("amCaller",true)
                            startActivity(this)
                        }
                    })
            }
        }
        rv_guest.adapter = DeviceAdapter(ArrayList()).apply {
            setOnItemClickListener { adapter, _, position ->
                PermissionUtils.checkPermission(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    Runnable {
                        Intent(requireContext(),CallActivity::class.java).apply {
                            putExtra("number",(adapter as DeviceAdapter).getItem(position).number)
                            putExtra("amCaller",true)
                            startActivity(this)
                        }
                    })
            }
        }
        mainViewModel.bedDevices.observe(viewLifecycleOwner, Observer {
            (rv_bed.adapter as BedDeviceAdapter).setDiffNewData(ArrayList(it))
        })
        mainViewModel.guestDevices.observe(viewLifecycleOwner, Observer {
            (rv_guest.adapter as DeviceAdapter).setDiffNewData(ArrayList(it))
        })
        mainViewModel.doorDevices.observe(viewLifecycleOwner, Observer {
            (rv_door.adapter as DeviceAdapter).setDiffNewData(ArrayList(it))
        })

        val callListFragment = CallListFragment()
        mainViewModel.callDevices.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrEmpty()){
                if(!callListFragment.isVisible){
                    callListFragment.show(parentFragmentManager,"callList")
                }
            }else{
                if(callListFragment.isVisible)
                    callListFragment.dismiss()
            }
        })
    }
}