package cn.cleartv.icu.ui

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseDialogFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.TTSOutputManager
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import cn.cleartv.icu.ui.viewmodel.CallViewModel
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_call_list.*
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CallListFragment : BaseDialogFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var speakText = ""

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_call_list
    }

    override fun afterInflateView() {
        isCancelable = false
        rv_devices.adapter = DeviceAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                Intent(requireContext(),CallActivity::class.java).apply {
                    putExtra("number",(adapter as DeviceAdapter).getItem(position).number)
                    putExtra("amCaller",false)
                    startActivity(this)
                }
            }
        }
        mainViewModel.callDevices.observe(viewLifecycleOwner, Observer {
            ArrayList(it.values).apply {
                (rv_devices.adapter as? DeviceAdapter)?.setDiffNewData(this)
                if(size > 0){
                    lastOrNull()?.let { device ->
                        speakText = device.name + "呼叫"
                    }
                }else{
                    speakText = ""
                }
            }

        })
    }

    var speakFlag = false
    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        speakFlag = true
        mainViewModel.launchUI {
            while (speakFlag){
                if(!TTSOutputManager.instance.isSpeaking()){
                    TTSOutputManager.instance.speak(speakText)
                }else{
                    Timber.d("isSpeaking wait...")
                }
                delay(1000)
            }
        }
    }

    override fun onPause() {
        Timber.d("onPause")
        super.onPause()
        speakFlag = false
    }
}