package cn.cleartv.icu.ui.adapter

import android.icu.util.TimeUnit
import androidx.recyclerview.widget.DiffUtil
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Call
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.utils.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/09/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CallRecordAdapter(data: MutableList<Call>? = null) :
    BaseQuickAdapter<Call, BaseViewHolder>(R.layout.item_call_record, data) {

    init {
        setDiffCallback(object: DiffUtil.ItemCallback<Call>(){
            override fun areItemsTheSame(oldItem: Call, newItem: Call): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Call, newItem: Call): Boolean {
                return oldItem.callStatus == newItem.callStatus
                        && oldItem.connectTime == newItem.connectTime
                        && oldItem.endTime == newItem.endTime
                        && oldItem.recordStatus == newItem.recordStatus
            }

        })

    }

    override fun convert(holder: BaseViewHolder, item: Call) {
        holder.setText(R.id.tv_name,item.device?.name)
            .setText(R.id.tv_message,item.mark)
            .setText(R.id.tv_call_time,TimeUtils.getFriendlyTimeSpanByNow(item.callTime))
            .setText(R.id.tv_record_status,item.callStatus)
        if(item.amCaller){
            holder.setImageResource(R.id.iv_status,if(item.connectTime > 0) R.drawable.ic_call_in else R.drawable.ic_call_in_failed)
        }else{
            holder.setImageResource(R.id.iv_status,if(item.connectTime > 0) R.drawable.ic_call_out else R.drawable.ic_call_out_failed)
        }
    }

}