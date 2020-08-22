package cn.cleartv.icu.ui

import android.Manifest
import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import cn.cleartv.icu.utils.PermissionUtils
import kotlinx.android.synthetic.main.fragment_guest_main.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class MainGuestFragment : BaseFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val callViewModel: CallViewModel by activityViewModels()

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_guest_main
    }

    override fun afterInflateView() {
        tv_tip.text = "1、病房谢绝家属探视，如特殊情况请您换探视服、口罩及拖鞋。\n\n" +
                "2、患者出现异常情况或有任何疑问请立即通知护士。\n\n" +
                "3、因为ICU病房病危重疾病人多，患者病情可能随时发生变化，医护人" +
                "员将以患者为重，个别时间可能不能及时探视或者介绍病情请您耐心等待，我们谢谢您的配合!"

        btn_request.setOnClickListener {
            PermissionUtils.checkPermission(requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                Runnable {
                    Intent(requireContext(),CallActivity::class.java).apply {
                        putExtra("number",App.hostNumber)
                        putExtra("amCaller",true)
                        startActivity(this)
                    }
                })
        }

        mainViewModel.callDevices.observe(viewLifecycleOwner, Observer {
            it.keys.firstOrNull()?.let {key->
                Intent(requireContext(),CallActivity::class.java).apply {
                    putExtra("number",it[key]?.number)
                    putExtra("amCaller",false)
                    startActivity(this)
                }
            }
        })

    }
}