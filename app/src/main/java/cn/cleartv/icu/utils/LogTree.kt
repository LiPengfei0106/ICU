package cn.cleartv.icu.utils

import android.util.Log
import cn.cleartv.icu.BuildConfig
import timber.log.Timber

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
open class LogTree : Timber.DebugTree() {
    // adb shell setprop log.tag.ICU DEBUG 控制日志输出
    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return BuildConfig.DEBUG || priority > Log.INFO || Log.isLoggable("ICU", Log.DEBUG)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (isLoggable(tag, priority)) {
            super.log(priority, "<${Thread.currentThread().name}> $tag", message, t)
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        return super.createStackElementTag(element) + "(Line " + element.lineNumber + ")";
    }

}