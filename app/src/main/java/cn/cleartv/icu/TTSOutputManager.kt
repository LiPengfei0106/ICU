package cn.cleartv.icu

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import timber.log.Timber
import java.util.*

class TTSOutputManager private constructor() {
    private var mTextToSpeech: TextToSpeech? = null
    var isInitSuccess = false
        private set

    fun init() {
        init(null)
    }

    // com.iflytek.speechsuite  com.iflytek.speechcloud
    // 先尝试使用默认的，不行就尝试使用讯飞
    private fun init(engine: String?) {
        //实例并初始化TTS对象
        try {
            mTextToSpeech?.shutdown()
            isInitSuccess = false
            // 尝试本地默认的语音引擎
            mTextToSpeech = TextToSpeech(App.instance, TextToSpeech.OnInitListener { status: Int ->
                if (status == TextToSpeech.SUCCESS) {
                    //设置中文朗读语言
                    val supported = mTextToSpeech?.setLanguage(Locale.CHINA)
                    if (supported != TextToSpeech.LANG_AVAILABLE && supported != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                        Timber.e( "Can not init TTS : $engine")
                        if (engine == null) {
                            // 尝试科大讯飞语音引擎3.0.apk
                            init("com.iflytek.speechcloud")
                        }
                    } else {
                        isInitSuccess = true
                    }
                } else {
                    Timber.e("Can not init TTS : $engine")
                    if (engine == null) {
                        // 尝试科大讯飞语音引擎3.0.apk
                        init("com.iflytek.speechcloud")
                    }
                }
            }, engine)
        } catch (e: Exception) {
            e.printStackTrace()
            val intent = Intent()
            isInitSuccess = false
            intent.action = "com.android.settings.TTS_SETTINGS"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            App.instance.startActivity(intent)
        }
    }

    fun speak(text: String?) {
        if (isInitSuccess && !text.isNullOrEmpty()) {
            mTextToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    fun stop(){
        mTextToSpeech?.stop()
    }

    fun isSpeaking(): Boolean{
        return mTextToSpeech?.isSpeaking ?:false
    }


    companion object {
        /**
         * 单一实例
         */
        val instance: TTSOutputManager by lazy {
            TTSOutputManager()
        }

    }
}