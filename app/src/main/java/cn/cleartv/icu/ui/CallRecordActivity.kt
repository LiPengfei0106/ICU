package cn.cleartv.icu.ui

import android.content.Intent
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.R
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.ui.adapter.CallRecordAdapter
import cn.cleartv.voip.annotation.CallRecordStatus
import kotlinx.android.synthetic.main.activity_call_record.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/09/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CallRecordActivity : BaseActivity() {

    override fun contentLayoutRes(): Int {
        return R.layout.activity_call_record
    }

    override fun afterSetContentView() {
        App.dateTime.observe(this, Observer {
            tv_date.text = it
        })
        btn_back.setOnClickListener {
            finish()
        }
        recycler_view.adapter = CallRecordAdapter().apply {

            setOnItemClickListener { adapter, view, position ->
                (adapter as CallRecordAdapter).let {
                    CoroutineScope(Dispatchers.Main).launch {
                        val call = withContext(Dispatchers.IO) {
                            CallRepository.updateCallRecord(it.getItem(position))
                        }
                        when (call.recordStatus) {
                            "NONE" -> toast("无录制记录")
                            CallRecordStatus.RECORDING -> toast("录制中")
                            CallRecordStatus.CREATE -> toast("录制创建中")
                            CallRecordStatus.FAIL -> toast("录制失败")
                            CallRecordStatus.UPLOADING -> toast("录制上传中")
                            CallRecordStatus.WAITUPLOAD -> toast("录制等待上传中")
                            CallRecordStatus.COMPLETE -> {
                                Intent(this@CallRecordActivity,VideoPlayerActivity::class.java).apply {
                                    putExtra("url",call.recordUrl)
                                    startActivity(this)
                                }
//                                toast("录制成功：\n${call.recordUrl}")
                            }
                        }
                    }
                }
            }
        }

        CallRepository.callRecordList.observe(this, Observer {
//            ArrayList(it).apply {
                (recycler_view.adapter as? CallRecordAdapter)?.setDiffNewData(it)
                if(it.size == 0){
                    toast("无通话记录")
                }
//            }
        })
    }
}