package cn.cleartv.icu.ui

import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.R
import cn.cleartv.icu.repository.CallRepository

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
        CallRepository.callRecordList.observe(this, Observer {

        })
    }
}