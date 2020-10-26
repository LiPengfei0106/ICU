package cn.cleartv.icu.ui

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.R
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.icu.utils.ShellUtil
import cn.cleartv.voip.VoIPClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun contentLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun afterSetContentView() {
        viewModel.launchUI {
            withContext(Dispatchers.IO) {
                Timber.i(ShellUtil.execCommand(
                    arrayOf("setprop service.adb.tcp.port 5555", "stop adbd", "start adbd"),
                    true,
                    false
                ).toString())

//                VoIPClient.getCallRecordList(Date(0),Date(),"10001000",null).forEach {
//                    Timber.i(JsonUtils.toJson(it))
//                }
            }
        }
        App.dateTime.observe(this, Observer {
            tv_date.text = it
        })

        btn_setting.setOnClickListener {
            PasswordFragment().show(supportFragmentManager,"password")
        }

        when (App.deviceType) {
            DeviceType.HOST -> {
                btn_call_record.visibility = View.VISIBLE
                btn_call_record.setOnClickListener {
                    startActivity(CallRecordActivity::class.java)
                }
                supportFragmentManager.beginTransaction()
                    .add(R.id.fl_content, MainHostFragment(), MainHostFragment::class.java.simpleName)
                    .addToBackStack(MainHostFragment::class.java.simpleName)
                    .commitAllowingStateLoss()
            }
            DeviceType.BED -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fl_content, MainBedFragment(), MainBedFragment::class.java.simpleName)
                    .addToBackStack(MainBedFragment::class.java.simpleName)
                    .commitAllowingStateLoss()
            }
            DeviceType.GUEST -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fl_content, MainGuestFragment(), MainGuestFragment::class.java.simpleName)
                    .addToBackStack(MainGuestFragment::class.java.simpleName)
                    .commitAllowingStateLoss()
            }
        }
        if (App.deviceType != DeviceType.HOST) {
            // 挪到了App里面，后台应用也需要
//            viewModel.startHeartBeat()
        } else {
            viewModel.startCheckDeviceOnLine()
        }

    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()

    }

}
