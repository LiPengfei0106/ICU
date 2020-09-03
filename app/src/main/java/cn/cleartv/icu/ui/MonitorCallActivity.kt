package cn.cleartv.icu.ui

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.repository.MonitorRepository
import cn.cleartv.icu.ui.viewmodel.MonitorViewModel
import cn.cleartv.icu.utils.JsonUtils
import kotlinx.android.synthetic.main.activity_monitor_call.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class MonitorCallActivity : BaseActivity() {

    private val monitorViewModel: MonitorViewModel by viewModels()

    override fun contentLayoutRes(): Int {
        return R.layout.activity_monitor_call
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
            monitorViewModel.startMonitorCall()
        }

        monitorViewModel.tipData.observe(this, Observer {
            it?.let {
                toast(it)
            }
        })

        monitorViewModel.currentMonitorCallMember.observe(this, Observer {
            it?.let {
                video_1.member = it
                monitorViewModel.launchUI {
                    tv_name_1.text =
                        withContext(Dispatchers.IO) { DeviceRepository.getDevice(it.userNum)?.name }
                }
            }
        })

        monitorViewModel.otherMonitorCallMember.observe(this, Observer {
            it?.let {
                video_2.member = it
                monitorViewModel.launchUI {
                    tv_name_2.text =
                        withContext(Dispatchers.IO) { DeviceRepository.getDevice(it.userNum)?.name }
                }
            }
        })

        monitorViewModel.exitData.observe(this, Observer {
            it?.let {
                toast(it)
                finish()
            }
        })

        monitorViewModel.isInterCut.observe(this, Observer {
            if (it) {
                btn_intercut.visibility = View.GONE
                btn_stop_intercut.visibility = View.VISIBLE
            } else {
                btn_intercut.isEnabled = true
                btn_intercut.visibility = View.VISIBLE
                btn_stop_intercut.visibility = View.GONE
            }
        })

        btn_stop_intercut.setOnClickListener {
            monitorViewModel.stopInterCut()
        }

        btn_intercut.setOnClickListener {
//            btn_intercut.isEnabled = false
//            monitorViewModel.interCut("${App.deviceInfo.name}开始插话")
            InterCutFragment().show(supportFragmentManager,"InterCut")
        }

        btn_cancel.setOnClickListener {
            monitorViewModel.mgtCancel("${App.deviceInfo.name}切断了通话")
            finish()
        }

        btn_back.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        monitorViewModel.stopMonitorCall()
        super.onDestroy()
    }
}