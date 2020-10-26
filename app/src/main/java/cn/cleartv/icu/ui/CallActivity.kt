package cn.cleartv.icu.ui

import android.content.Context
import android.media.AudioManager
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.voip.VoIPClient
import kotlinx.android.synthetic.main.activity_call.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject

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
    private var startCallTime = System.currentTimeMillis() / 1000
    private var callDurationString = ""
    override fun contentLayoutRes(): Int {
        return R.layout.activity_call
    }

    override fun afterSetContentView() {
        App.dateTime.observe(this, Observer {
            tv_date.text = it
        })
        val callDevice = JsonUtils.fromJson(intent.getStringExtra("device")?:"", Device::class.java)
        val amCaller = intent.getBooleanExtra("amCaller", false)
        if (callDevice == null) {
            toast("缺少参数")
            finish()
        }

        btn_hangup.setOnClickListener { finish() }
        btn_transfer.setOnClickListener {
            TransferFragment().show(
                supportFragmentManager,
                "transfer"
            )
        }
        video_local.setMirror(true)
        if (App.deviceType == DeviceType.HOST) {
            btn_transfer.visibility = View.VISIBLE
        } else {
            btn_transfer.visibility = View.GONE
        }
        callViewModel.launchUI {
            while (!isDestroyed) {
                val hasCallTime = (System.currentTimeMillis() / 1000 - startCallTime).toInt()
                val hasCallTimeMin = hasCallTime / 60
                val hasCallTimeSec = hasCallTime % 60
                if (callDurationString.isNotBlank()) {
                    tv_call_time.text =
                        "${hasCallTimeMin}:${if (hasCallTimeSec < 10) "0" else ""}${hasCallTimeSec}/$callDurationString"
                } else {
                    tv_call_time.text =
                        "${hasCallTimeMin}:${if (hasCallTimeSec < 10) "0" else ""}${hasCallTimeSec}"
                    if (callViewModel.remoteInfoData.value == null && hasCallTime >= 60) {
                        toast("对方未应答")
                        finish()
                    }
                }
                if (callViewModel.callDuration > 0) {
                    val leftSec = (callViewModel.callDuration - hasCallTime).toInt()
                    if (leftSec == 300) {
                        toast("通话时长还剩5分钟")
                    } else if (leftSec == 60) {
                        toast("通话时长还剩1分钟")
                    } else if (leftSec == 5 || leftSec == 10 || leftSec == 30) {
                        toast("通话时长还剩${leftSec}秒")
                    } else if (leftSec <= 0) {
                        toast("通话时长已耗尽")
                        finish()
                    }
                }
                delay(1000)
            }
        }

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
            startCallTime = System.currentTimeMillis() / 1000
            val durationMin = callViewModel.callDuration / 60
            val durationSec = callViewModel.callDuration % 60
            callDurationString = if (callViewModel.callDuration > 0) {
                "${durationMin}:${if (durationSec < 10) "0" else 0}${durationSec}"
            } else {
                ""
            }
        })
        callViewModel.remoteInfoData.observe(this, Observer {
            video_remote.member = it
            startCallTime = System.currentTimeMillis() / 1000
            if (it == null) {
                video_remote.clearImage()
            } else {
                callViewModel.launchUI {
                    btn_call_host.visibility =
                        if (App.deviceType == DeviceType.BED && it.userNum != App.hostDevice.number) View.VISIBLE else View.GONE
                    tv_name.text = callViewModel.callDevice?.name?:
                        withContext(Dispatchers.IO) { DeviceRepository.getDevice(it.userNum)?.name }
                }
            }
        })
        callViewModel.textData.observe(this, Observer {
            try {
                it?.let {
                    val data = JSONObject(it)
                    when (data.optString("action")) {
                        "transfer" -> {
                            val device =
                                JsonUtils.fromJson(data.optString("device"), Device::class.java)
                            device?.let {
                                // 更新界面
                                tv_name.text = "正在转接中..."
                                callViewModel.transfer(device, data.optInt("duration", 0))
                            }
                        }
                        else -> {

                        }
                    }
                }
            } catch (e: Exception) {
                toast(e.toString())
            }

        })
        callViewModel.startCall(callDevice!!, amCaller)
        tv_name.text = "正在呼叫${callDevice.name}..."

        val manager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        manager.mode = AudioManager.MODE_IN_COMMUNICATION
        manager.isSpeakerphoneOn = true
        iv_speaker.setImageResource(if(manager.isSpeakerphoneOn) R.drawable.ic_speaker_on else R.drawable.ic_speaker)
        btn_speaker.setOnClickListener {
            if (manager.isSpeakerphoneOn) {
                manager.isSpeakerphoneOn = false
                if(!manager.isSpeakerphoneOn){
                    iv_speaker.setImageResource(R.drawable.ic_speaker)
                    toast("切换到听筒")
                }else{
                    toast("切换扬声器失败")
                }
            } else {
                manager.isSpeakerphoneOn = true
                if(manager.isSpeakerphoneOn){
                    iv_speaker.setImageResource(R.drawable.ic_speaker_on)
                    toast("切换到扬声器")
                }else{
                    toast("切换扬声器失败")
                }
            }
            video_remote.member?.mediaStream?.audioTracks?.firstOrNull()?.let {
            }
        }

        iv_mic.setImageResource(R.drawable.ic_mic)
        btn_mic.setOnClickListener {
            if (video_local.member?.audioOn == true) {
                VoIPClient.setAudioOn(false)
                video_local.member?.audioOn = false
                iv_mic.setImageResource(R.drawable.ic_mic_on)
                toast("关闭麦克风")
            } else {
                VoIPClient.setAudioOn(true)
                video_local.member?.audioOn = true
                iv_mic.setImageResource(R.drawable.ic_mic)
                toast("打开麦克风")
            }
        }

        sb_voice.max = manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        sb_voice.progress = manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val curProgress = sb_voice.progress * 100 / sb_voice.max
        when {
            curProgress > 66 -> {
                iv_volume.setImageResource(R.drawable.ic_volume_2)
            }
            curProgress > 33 -> {
                iv_volume.setImageResource(R.drawable.ic_volume_1)
            }
            curProgress  > 0 -> {
                iv_volume.setImageResource(R.drawable.ic_volume)
            }
            else -> {
                iv_volume.setImageResource(R.drawable.ic_volume_mute)
            }
        }
        sb_voice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val p = progress * 100 / sb_voice.max
                when {
                    p > 66 -> {
                        iv_volume.setImageResource(R.drawable.ic_volume_2)
                    }
                    p > 33 -> {
                        iv_volume.setImageResource(R.drawable.ic_volume_1)
                    }
                    p  > 0 -> {
                        iv_volume.setImageResource(R.drawable.ic_volume)
                    }
                    else -> {
                        iv_volume.setImageResource(R.drawable.ic_volume_mute)
                    }
                }
                manager.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    progress,
                    AudioManager.FLAG_PLAY_SOUND
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        btn_call_host.visibility =
            if (App.deviceType == DeviceType.BED && callViewModel.callDevice?.number != App.hostDevice.number) View.VISIBLE else View.GONE
        btn_call_host.setOnClickListener {
            callViewModel.transfer(App.hostDevice, 0)
        }

    }

    override fun onDestroy() {
        callViewModel.hangup()
        super.onDestroy()
    }
}