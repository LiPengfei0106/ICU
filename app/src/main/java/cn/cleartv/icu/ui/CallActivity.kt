package cn.cleartv.icu.ui

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.utils.JsonUtils
import kotlinx.android.synthetic.main.activity_call.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CallActivity : BaseActivity() {

    private val callViewModel: CallViewModel by viewModels()
    private var startCallTime = System.currentTimeMillis() / 1000
    private var callDurationString = ""
    override fun contentLayoutRes(): Int {
        return R.layout.activity_call
    }

    override fun afterSetContentView() {
        App.dateTime.observe(this, Observer {
            tv_date.text = it
        })
        val callNumber = intent.getStringExtra("number")
        val amCaller = intent.getBooleanExtra("amCaller", false)
        if (callNumber.isNullOrBlank()) {
            toast("缺少参数")
            finish()
        }

        btn_hangup.setOnClickListener { finish() }
        btn_transfer.setOnClickListener {
            TransferFragment().show(
                supportFragmentManager,
                "transfer"
            )
        }
        video_local.setMirror(true)
        if (App.deviceType == DeviceType.HOST) {
            btn_transfer.visibility = View.VISIBLE
        } else {
            btn_transfer.visibility = View.GONE
        }
        callViewModel.launchUI {
            while (!isDestroyed) {
                val hasCallTime = (System.currentTimeMillis() / 1000 - startCallTime).toInt()
                val hasCallTimeMin = hasCallTime / 60
                val hasCallTimeSec = hasCallTime % 60
                if (callDurationString.isNotBlank()) {
                    tv_call_time.text =
                        "${hasCallTimeMin}:${if(hasCallTimeSec < 10) "0" else ""}${hasCallTimeSec}/$callDurationString"
                } else {
                    tv_call_time.text = "${hasCallTimeMin}:${if(hasCallTimeSec < 10) "0" else ""}${hasCallTimeSec}"
                    if(callViewModel.remoteInfoData.value == null){
                        toast("对方未应答")
                        finish()
                    }
                }
                if (callViewModel.callDuration > 0) {
                    val leftSec = (callViewModel.callDuration - hasCallTime).toInt()
                    if (leftSec == 300) {
                        toast("通话时长还剩5分钟")
                    } else if (leftSec == 60) {
                        toast("通话时长还剩1分钟")
                    } else if (leftSec == 5 || leftSec == 10 || leftSec == 30) {
                        toast("通话时长还剩${leftSec}秒")
                    } else if (leftSec <= 0) {
                        finish()
                    }
                }
                delay(1000)
            }
        }

        callViewModel.exitData.observe(this, Observer {
            it?.let {
                toast(it)
                finish()
            }
        })

        callViewModel.tipData.observe(this, Observer {
            it?.let {
                toast(it)
            }
        })
        callViewModel.localInfoData.observe(this, Observer {
            video_local.member = it
            startCallTime = System.currentTimeMillis() / 1000
            val durationMin = callViewModel.callDuration / 60
            val durationSec = callViewModel.callDuration % 60
            callDurationString = if (callViewModel.callDuration > 0) {
                "${durationMin}:${if (durationSec < 10) "0" else 0}${durationSec}"
            } else {
                ""
            }
        })
        callViewModel.remoteInfoData.observe(this, Observer {
            video_remote.member = it
            if (it == null) {
                video_remote.clearImage()
            } else {
                callViewModel.launchUI {
                    tv_name.text =
                        withContext(Dispatchers.IO) { DeviceRepository.getDevice(it.userNum)?.name }
                }
            }
        })
        callViewModel.textData.observe(this, Observer {
            try {
                it?.let {
                    val data = JSONObject(it)
                    when (data.optString("action")) {
                        "transfer" -> {
                            val device =
                                JsonUtils.fromJson(data.optString("device"), Device::class.java)
                            device?.let {
                                // 更新界面
                                tv_name.text = "正在转接中..."
                                callViewModel.transfer(device.number, data.optInt("duration", 0))
                            }
                        }
                        else -> {

                        }
                    }
                }
            } catch (e: Exception) {
                toast(e.toString())
            }

        })
        callViewModel.startCall(callNumber!!, amCaller)

    }

    override fun onDestroy() {
        callViewModel.hangup()
        super.onDestroy()
    }
}