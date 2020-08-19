package cn.cleartv.icu

import android.app.Application
import cn.cleartv.icu.utils.LogTree
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
class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Timber.plant(LogTree())
    }
}