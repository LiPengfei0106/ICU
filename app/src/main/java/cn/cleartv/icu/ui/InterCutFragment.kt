package cn.cleartv.icu.ui

import androidx.fragment.app.activityViewModels
import cn.cleartv.icu.BaseDialogFragment
import cn.cleartv.icu.R
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.ui.viewmodel.MonitorViewModel
import kotlinx.android.synthetic.main.fragment_inter_cut.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class InterCutFragment : BaseDialogFragment() {
    private val monitorViewModel: MonitorViewModel by activityViewModels()
    override fun viewLayoutRes(): Int {
        return R.layout.fragment_inter_cut
    }

    override fun afterInflateView() {

        monitorViewModel.launchUI {

            monitorViewModel.currentMonitorCallMember.value?.let {member->
                btn_first.text =
                    withContext(Dispatchers.IO) { DeviceRepository.getDevice(member.userNum)?.name }
                btn_first.setOnClickListener {
                    monitorViewModel.interCut(member.userNum)
                    dismiss()
                }
            }

            monitorViewModel.otherMonitorCallMember.value?.let {member->
                btn_second.text =
                    withContext(Dispatchers.IO) { DeviceRepository.getDevice(member.userNum)?.name }
                btn_second.setOnClickListener {
                    monitorViewModel.interCut(member.userNum)
                    dismiss()
                }
            }
            btn_all.setOnClickListener {
                monitorViewModel.interCut("all")
                dismiss()
            }
        }
    }
}