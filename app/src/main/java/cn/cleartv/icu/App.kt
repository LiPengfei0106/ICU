package cn.cleartv.icu

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.repository.MonitorRepository
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.icu.utils.LogTree
import cn.cleartv.voip.VoIPClient
import org.json.JSONObject
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
class App : Application(), VoIPClient.VoIPListener {

    companion object {
        lateinit var instance: App

        @DeviceType
        lateinit var deviceType: String
        lateinit var hostNumber: String
        lateinit var deviceInfo: Device
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Timber.plant(LogTree())

        deviceType = BuildConfig.FLAVOR
        Timber.i("DeviceType: $deviceType")
        hostNumber = "10001000"

        VoIPClient.hostUrl = "http://cmcdev.cleartv.cn"
        when (deviceType) {
            DeviceType.HOST -> {
                deviceInfo = Device("10001000", "10001000", type = deviceType)
                VoIPClient.init(this, "10001000", "123456", BuildConfig.DEBUG)
            }
            DeviceType.BED -> {
                deviceInfo = Device("10001001", "10001001", type = deviceType)
                VoIPClient.init(this, "10001001", "123456", BuildConfig.DEBUG)
            }
            DeviceType.GUEST -> {
                deviceInfo = Device("10002001", "10002001", type = deviceType)
                VoIPClient.init(this, "10002001", "123456", BuildConfig.DEBUG)
            }
        }
        VoIPClient.statusData.observeForever {
            when (it) {
                VoIPClient.Status.CONNECTED -> {
                    if (deviceInfo.status == DeviceStatus.DISCONNECT) {
                        deviceInfo.status = DeviceStatus.IDLE
                        deviceInfo.name = VoIPClient.myLoginMemberInfo?.name ?: deviceInfo.name
                    }
                }
                VoIPClient.Status.DISCONNECT -> {
                    deviceInfo.status = DeviceStatus.DISCONNECT
                }
            }
        }

        VoIPClient.monitorListener = MonitorRepository
        VoIPClient.callListener = CallRepository
        VoIPClient.receivedCallListener = CallRepository
        VoIPClient.voipListener = this

    }

    override fun onMessage(memberNumber: String, type: String, message: String) {
        // 处理自定义的消息
        Timber.d("onMessage: \n memberNumber-$memberNumber; type-$type; message-$message")
        when (type) {
            "heartbeat" -> {
                // 分机像主机发送心跳，用于同步分机状态
                try {
                    JsonUtils.fromJson(
                        message,
                        Device::class.java
                    )?.let {
                        DeviceRepository.addDevice(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onPong(data: JSONObject) {
        // TODO 更新本地时间
        Timber.d("onPong: \n $data")
    }
}