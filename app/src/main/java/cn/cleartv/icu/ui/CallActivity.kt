package cn.cleartv.icu.ui

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.utils.JsonUtils
import kotlinx.android.synthetic.main.fragment_call.*
import org.json.JSONObject
import java.lang.Exception

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

    override fun contentLayoutRes(): Int {
        return R.layout.fragment_call
    }

    override fun afterSetContentView() {
        val callNumber = intent.getStringExtra("number")
        val amCaller = intent.getBooleanExtra("amCaller",false)
        if(callNumber.isNullOrBlank()) {
            toast("缺少参数")
            finish()
        }

        btn_hangup.setOnClickListener { finish() }
        btn_transfer.setOnClickListener { TransferFragment().show(supportFragmentManager,"transfer") }

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
        })
        callViewModel.remoteInfoData.observe(this, Observer {
            video_remote.member = it
        })
        callViewModel.textData.observe(this, Observer {
            try {
                it?.let {
                    val data = JSONObject(it)
                    when(data.optString("action")){
                        "transfer" -> {
                            val device = JsonUtils.fromJson(data.optString("device"),Device::class.java)
                            device?.let {
                                // 更新界面
                                callViewModel.transfer(device.number,data.optInt("duration",0))
                            }
                        }
                        else -> {

                        }
                    }
                }
            }catch (e: Exception){
                toast(e.toString())
            }

        })
        callViewModel.startCall(callNumber!!,amCaller)

    }

    override fun onDestroy() {
        callViewModel.hangup()
        super.onDestroy()
    }
}