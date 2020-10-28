package cn.cleartv.icu

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Process
import android.util.Log
import androidx.lifecycle.liveData
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.repository.MonitorRepository
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.icu.utils.LogTree
import cn.cleartv.icu.utils.TimeUtils
import cn.cleartv.voip.VoIPClient
import cn.cleartv.voip.VoIPParams
import cn.cleartv.voip.VoIPService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.webrtc.MyVideoDecoderFactory
import timber.log.Timber
import java.util.*


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
        lateinit var hostDevice: Device
        lateinit var deviceInfo: Device
        var adapterWidth: Int = 2222

        var isRecord = false
        var userHardwareDecode = false

        lateinit var settingSP: SharedPreferences
        lateinit var updateUrl: String

        var timeInterval = 0L
            private set

        val dateTime = liveData {
            while (true) {
                emit(TimeUtils.nowString + "  " + TimeUtils.getChineseWeek(Date()))
                delay(1000)
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("App", "onCreate")
        if (shouldInit()) {
            init()
        }
    }

    private fun init() {
        Log.i("App", "init")
        instance = this
        Timber.plant(LogTree())

        TTSOutputManager.instance.init()

        settingSP = PreferenceManager.getDefaultSharedPreferences(instance)
        deviceType = BuildConfig.FLAVOR
        Timber.i("DeviceType: $deviceType")

        updateUrl = settingSP.getString("update_url", null)
            ?: resources.getString(R.string.default_update_url)

        adapterWidth = (settingSP.getString("adapter_width", null) ?: resources.getString(R.string.default_adapter_width)).toInt()
        isRecord = settingSP.getBoolean("is_record", true)
        userHardwareDecode = settingSP.getBoolean("use_hardware_decode", false)

        val username = settingSP.getString("username", null) ?: ""
        val password = settingSP.getString("password", null) ?: resources.getString(R.string.default_password)
        hostDevice = Device(settingSP.getString("host_number", null) ?: resources.getString(R.string.default_host_number), "护士站")
        deviceInfo = Device(
            username,
            username,
            type = deviceType
        )
        // 断线后，最长5分钟之内重连，默认是512
        VoIPService.maxReconnectDelaySec = 256
        VoIPClient.hostUrl =
            settingSP.getString("host_url", null) ?: resources.getString(R.string.host_url)
        VoIPClient.voIPParams = VoIPParams.Builder()
            .userHardwareVideoDecoder(userHardwareDecode)
            .videoFormat(720, 540, 15).build()
        VoIPClient.init(this, username, password, BuildConfig.DEBUG)
        if (deviceType != DeviceType.HOST) {
            startHeartBeat()
        }
//        when (deviceType) {
//            DeviceType.HOST -> {
//                deviceInfo = Device("10001000", "10001000", type = deviceType)
//                VoIPClient.init(this, "10001000", "123456", BuildConfig.DEBUG)
//            }
//            DeviceType.BED -> {
//                deviceInfo = Device("10001001", "10001001", type = deviceType)
//                VoIPClient.init(this, "10001001", "123456", BuildConfig.DEBUG)
//            }
//            DeviceType.GUEST -> {
//                deviceInfo = Device("10002001", "10002001", type = deviceType)
//                VoIPClient.init(this, "10002001", "123456", BuildConfig.DEBUG)
//            }
//        }
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

    private fun shouldInit(): Boolean {
        val am =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos =
            am.runningAppProcesses
        val mainProcessName = packageName
        val myPid = Process.myPid()
        for (info in processInfos) {
            if (info.pid == myPid && mainProcessName == info.processName) {
                return true
            }
        }
        return false
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
                        it.lastOnLineTime = System.currentTimeMillis()
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
        timeInterval = data.optLong("date") - System.currentTimeMillis()
    }


    fun startHeartBeat() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                deviceInfo.lastOnLineTime = System.currentTimeMillis()
                VoIPClient.sendMessage(
                    hostDevice.number,
                    "heartbeat",
                    JsonUtils.toJson(deviceInfo)
                )
                delay(10000)
            }
        }
    }
}