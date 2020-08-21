package cn.cleartv.icu.utils

import android.widget.Toast
import cn.cleartv.icu.App

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object Utils {

    private var toast: Toast? = null
    fun showToast(content: String) {
        (toast ?: (Toast.makeText(App.instance,content,Toast.LENGTH_SHORT))).let {
            it.setText(content)
            it.show()
        }
    }

}