package cn.cleartv.icu

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import cn.cleartv.icu.utils.AdaptScreenUtils
import cn.cleartv.icu.utils.OnKeyDownHandlerHelper
import cn.cleartv.icu.utils.Utils
import timber.log.Timber

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(contentLayoutRes())
        afterSetContentView()
    }

    @LayoutRes
    protected abstract fun contentLayoutRes(): Int

    protected abstract fun afterSetContentView()


    override fun getResources(): Resources {
        return AdaptScreenUtils.adaptWidth(super.getResources(), App.adapterWidth)
    }

    protected open fun toast(msg: String) {
        Utils.showToast(msg)
    }

    protected open fun startActivity(activityCls: Class<out Activity>) {
        startActivity(Intent(this, activityCls))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Timber.d(event.toString())
        return OnKeyDownHandlerHelper.handleKeyDown(
            this,
            keyCode,
            event
        ) || super.onKeyDown(keyCode, event)
    }

}