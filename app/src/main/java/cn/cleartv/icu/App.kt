package cn.cleartv.icu

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import cn.cleartv.icu.utils.LogTree
import cn.cleartv.voip.VoIPClient
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
        @DeviceType
        lateinit var deviceType: String
        lateinit var hostNumber: String
        lateinit var connectStatus: LiveData<String>
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Timber.plant(LogTree())

        deviceType = BuildConfig.FLAVOR
        Timber.i("DeviceType: $deviceType")
        hostNumber = "10001000"

        VoIPClient.hostUrl = "http://cmcdev.cleartv.cn"
        when(deviceType){
            DeviceType.HOST -> VoIPClient.init(this, "10001000", "123456", BuildConfig.DEBUG)
            DeviceType.BED -> VoIPClient.init(this, "10001001", "123456", BuildConfig.DEBUG)
            DeviceType.GUEST -> VoIPClient.init(this, "10002001", "123456", BuildConfig.DEBUG)
        }
        connectStatus = VoIPClient.statusData

    }
}