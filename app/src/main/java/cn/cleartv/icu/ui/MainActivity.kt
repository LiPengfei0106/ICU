package cn.cleartv.icu.ui

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.R
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun contentLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun afterSetContentView() {
        viewModel.dateTime.observe(this, Observer {
            tv_date.text = it
        })

        /**
         *
        parentFragmentManager.beginTransaction()
        .hide(this)
        .add(R.id.fl_content, fragment, fragment.javaClass.simpleName)
        .addToBackStack(fragment.javaClass.simpleName)
        .commitAllowingStateLoss()
         */

        when (App.deviceType) {
            DeviceType.HOST -> supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, MainHostFragment(),MainHostFragment::class.java.simpleName)
                .addToBackStack(MainHostFragment::class.java.simpleName)
                .commitAllowingStateLoss()
            DeviceType.BED -> supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, MainBedFragment(),MainBedFragment::class.java.simpleName)
                .addToBackStack(MainBedFragment::class.java.simpleName)
                .commitAllowingStateLoss()
            DeviceType.GUEST -> supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, MainGuestFragment(),MainGuestFragment::class.java.simpleName)
                .addToBackStack(MainGuestFragment::class.java.simpleName)
                .commitAllowingStateLoss()
        }
        if (App.deviceType != DeviceType.HOST) {
            viewModel.startHeartBeat()
        } else {
            viewModel.startCheckDeviceOnLine()
        }
    }

    override fun onBackPressed() {

    }

}
