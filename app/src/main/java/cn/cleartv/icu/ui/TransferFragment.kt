package cn.cleartv.icu.ui

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseDialogFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.ui.adapter.DeviceAdapter
import cn.cleartv.icu.ui.viewmodel.MainViewModel
import cn.cleartv.icu.utils.JsonUtils
import cn.cleartv.voip.VoIPClient
import kotlinx.android.synthetic.main.fragment_transfer.*
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
class TransferFragment : BaseDialogFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var transferDevice: Device? = null
    private var transferDuration = -1

    override fun viewLayoutRes(): Int {
        return R.layout.fragment_transfer
    }

    override fun afterInflateView() {
        rv_devices.adapter = DeviceAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                transferDevice = (adapter as DeviceAdapter).getItem(position)
                tv_transfer_name.text = transferDevice?.name
            }
        }
        mainViewModel.bedDevices.observe(viewLifecycleOwner, Observer {
            (rv_devices.adapter as? DeviceAdapter)?.setDiffNewData(ArrayList(it))
        })

        spinner_time.adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_transfer_time,
            resources.getStringArray(R.array.transfer_time)
        )
        spinner_time.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                transferDuration = position * 300000
            }

        }
        btn_transfer.setOnClickListener {

            transferDevice?.let {
                JSONObject().apply {
                    put("action","transfer")
                    put("device",JsonUtils.toJson(it))
                    put("duration",transferDuration)
                    VoIPClient.sendTextMsg(this.toString())
                    dismiss()
                }
                return@setOnClickListener
            }
            toast("请选择转接目标")
        }

        iv_close.setOnClickListener {
            dismiss()
        }
    }
}