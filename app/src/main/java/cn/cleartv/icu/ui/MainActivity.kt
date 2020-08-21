package cn.cleartv.icu.ui

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.entity.VoIPMember
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import timber.log.Timber

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun contentLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun afterSetContentView() {
        viewModel.dateTime.observe(this, Observer {
            tv_date.text = it
        })

        when (App.deviceType) {
            DeviceType.HOST -> supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, HostMainFragment()).commit()
            DeviceType.BED -> supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, BedMainFragment()).commit()
            DeviceType.GUEST -> supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, GuestMainFragment()).commit()
        }
        if (App.deviceType != DeviceType.HOST) {
            viewModel.startHeartBeat()
        } else {
            viewModel.startCheckDeviceOnLine()
        }
    }

}
