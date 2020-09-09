package cn.cleartv.icu.ui.adapter

import android.icu.util.TimeUnit
import androidx.recyclerview.widget.DiffUtil
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Call
import cn.cleartv.icu.db.entity.CallDetail
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.utils.TimeUtils
import cn.cleartv.voip.annotation.CallRecordStatus
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
class CallRecordAdapter(data: MutableList<CallDetail>? = null) :
    BaseQuickAdapter<CallDetail, BaseViewHolder>(R.layout.item_call_record, data) {

    init {
        setDiffCallback(object: DiffUtil.ItemCallback<CallDetail>(){
            override fun areItemsTheSame(oldItem: CallDetail, newItem: CallDetail): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CallDetail, newItem: CallDetail): Boolean {
                return oldItem.callStatus == newItem.callStatus
                        && oldItem.connectTime == newItem.connectTime
                        && oldItem.endTime == newItem.endTime
                        && oldItem.recordStatus == newItem.recordStatus
            }

        })

    }

    override fun convert(holder: BaseViewHolder, item: CallDetail) {
        holder.setText(R.id.tv_name,item.device?.name?:item.callNumber)
//            .setText(R.id.tv_message,item.mark)
            .setText(R.id.tv_call_time,TimeUtils.getFriendlyTimeSpanByNow(item.callTime))

        if(item.recordStatus == CallRecordStatus.COMPLETE){
            holder.setText(R.id.tv_record_status,"观看录像")
        }else if(item.recordStatus == "NONE"){
            holder.setText(R.id.tv_record_status,"无录像")
        }

        if(item.connectTime > 0){
            if(item.endTime > 0){
                val time = (item.endTime - item.connectTime) / 1000
                val min = time / 60
                val sec = time % 60
                holder.setText(R.id.tv_message,"通话时长${if(min>0) (min.toString() + "分") else ""}${sec}秒")
            }else{
                holder.setText(R.id.tv_message,"正在通话中")
            }
            if(item.amCaller){
                holder.setImageResource(R.id.iv_status,R.drawable.ic_call_out)
            }else{
                holder.setImageResource(R.id.iv_status,R.drawable.ic_call_in )
            }
        }else{
            if(item.endTime > 0){
                val time = (item.endTime - item.callTime) / 1000
                val min = time / 60
                val sec = time % 60
                holder.setText(R.id.tv_message,"响铃时长${if(min>0) (min.toString() + "分") else ""}${sec}秒")
            }else{
                holder.setText(R.id.tv_message,"正在响铃中")
            }
            if(item.amCaller){
                holder.setImageResource(R.id.iv_status,R.drawable.ic_call_out_failed)
            }else{
                holder.setImageResource(R.id.iv_status,R.drawable.ic_call_in_failed)
            }
        }
    }

}