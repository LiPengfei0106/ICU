package cn.cleartv.icu.ui

import android.Manifest
import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.icu.utils.PermissionUtils
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
class MainBedFragment : BaseFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val callViewModel: CallViewModel by activityViewModels()

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_bed_main
    }

    override fun afterInflateView() {
        btn_request.setOnClickListener {
            PermissionUtils.checkPermission(requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                Runnable {
                    Intent(requireContext(),CallActivity::class.java).apply {
                        putExtra("device", JsonUtils.toJson(App.hostDevice))
                        putExtra("amCaller",true)
                        startActivity(this)
                    }
                })
        }

//        mainViewModel.callDevices.observe(viewLifecycleOwner, Observer {
//            it.keys.firstOrNull()?.let {key->
//                Intent(requireContext(),CallActivity::class.java).apply {
//                    putExtra("device", JsonUtils.toJson(it[key]?:Device(key)))
//                    putExtra("amCaller",false)
//                    startActivity(this)
//                }
//            }
//        })
    }
}