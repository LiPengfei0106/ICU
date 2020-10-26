package cn.cleartv.icu.ui

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.repository.MonitorRepository
import cn.cleartv.icu.ui.viewmodel.MonitorViewModel
import cn.cleartv.icu.utils.JsonUtils
import kotlinx.android.synthetic.main.activity_monitor.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/22
 *     desc   : 用于监听某个设备
 *     version: 1.0
 * </pre>
 */
class MonitorActivity : BaseActivity() {

    private val monitorViewModel: MonitorViewModel by viewModels()

    override fun contentLayoutRes(): Int {
        return R.layout.activity_monitor
    }

    override fun afterSetContentView() {
        App.dateTime.observe(this, Observer {
            tv_date.text = it
        })
        intent.getStringExtra("device")?.let {
            MonitorRepository.monitorDevice = JsonUtils.fromJson(it, Device::class.java)
        }
        if (MonitorRepository.monitorDevice == null) {
            finish()
        } else {
            monitorViewModel.startMonitor()
        }

        monitorViewModel.tipData.observe(this, Observer {
            it?.let {
                toast(it)
            }
        })

        monitorViewModel.monitorMember.observe(this, Observer {
            it?.let {
                video.member = it
                monitorViewModel.launchUI {
                    tv_name.text =
                        withContext(Dispatchers.IO) { DeviceRepository.getDevice(it.userNum)?.name }
                }
            }
        })

        CallRepository.exitData.observe(this, Observer {
            it?.let {
                finish()
            }
        })

        btn_back.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        App.deviceInfo.status = DeviceStatus.IDLE
        monitorViewModel.stopMonitor()
        super.onDestroy()
    }
}