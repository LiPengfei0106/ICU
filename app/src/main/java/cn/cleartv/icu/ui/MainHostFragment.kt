package cn.cleartv.icu.ui

import android.Manifest
import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.ui.adapter.BedDeviceAdapter
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import cn.cleartv.icu.ui.viewmodel.MonitorViewModel
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.icu.utils.PermissionUtils
import kotlinx.android.synthetic.main.fragment_host_main.*
import java.util.*
import kotlin.collections.ArrayList

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

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_host_main
    }

    override fun afterInflateView() {
        rv_bed.adapter = BedDeviceAdapter(ArrayList()).apply {
            setOnItemChildClickListener { adapter, view, position ->
                PermissionUtils.checkPermission(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    Runnable {
                        val device = (adapter as BedDeviceAdapter).getItem(position)
                        when(view.id){
                            R.id.tv_monitor -> {
                                if(device.status == DeviceStatus.IN_CALL_CALLER || device.status == DeviceStatus.IN_CALL_CALLEE){
                                    Intent(requireContext(),MonitorActivity::class.java).apply {
                                        putExtra("device",JsonUtils.toJson(device))
                                        startActivity(this)
                                    }
                                }else{
                                    toast("该设备未在通话中")
                                }
                            }
                            R.id.tv_call -> {
                                when(device.status){
                                    DeviceStatus.IDLE -> {
                                        Intent(requireContext(),CallActivity::class.java).apply {
                                            putExtra("number",device.number)
                                            putExtra("amCaller",true)
                                            startActivity(this)
                                        }
                                    }
                                    DeviceStatus.IN_CALL_CALLEE,DeviceStatus.IN_CALL_CALLER,DeviceStatus.CALLING -> {
                                        toast("正在通话中")
                                    }
                                    DeviceStatus.DISCONNECT -> {
                                        toast("设备不在线")
                                    }
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
                        val device = (adapter as DeviceAdapter).getItem(position)
                        when(device.status){
                            DeviceStatus.IDLE -> {
                                Intent(requireContext(),CallActivity::class.java).apply {
                                    putExtra("number",device.number)
                                    putExtra("amCaller",true)
                                    startActivity(this)
                                }
                            }
                            DeviceStatus.IN_CALL_CALLEE,DeviceStatus.IN_CALL_CALLER,DeviceStatus.CALLING -> {
                                toast("正在通话中")
                            }
                            DeviceStatus.DISCONNECT -> {
                                toast("设备不在线")
                            }
                        }
                    })
            }
        }
        rv_guest.adapter = DeviceAdapter(ArrayList()).apply {
            setOnItemClickListener { adapter, _, position ->
                PermissionUtils.checkPermission(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    Runnable {
                        val device = (adapter as DeviceAdapter).getItem(position)
                        when(device.status){
                            DeviceStatus.IDLE -> {
                                Intent(requireContext(),CallActivity::class.java).apply {
                                    putExtra("number",device.number)
                                    putExtra("amCaller",true)
                                    startActivity(this)
                                }
                            }
                            DeviceStatus.IN_CALL_CALLEE,DeviceStatus.IN_CALL_CALLER,DeviceStatus.CALLING -> {
                                toast("正在通话中")
                            }
                            DeviceStatus.DISCONNECT -> {
                                toast("设备不在线")
                            }
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