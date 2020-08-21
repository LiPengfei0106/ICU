package cn.cleartv.icu.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class DeviceAdapter(data: MutableList<Device>?) :
    BaseQuickAdapter<Device, BaseViewHolder>(R.layout.item_device, data) {

    init {
        setDiffCallback(object: DiffUtil.ItemCallback<Device>(){
            override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem.number == newItem.number
            }

            override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem.status == newItem.status
                        && oldItem.name == newItem.name
                        && oldItem.description == newItem.description
            }

        })

    }

    override fun convert(holder: BaseViewHolder, item: Device) {
        holder.setText(R.id.tv_name,item.name)
        when (item.status) {
            DeviceStatus.DISCONNECT -> {
                holder.setBackgroundResource(R.id.tv_status, R.drawable.shape_rectangle_gray_top_radius16)
                    .setText(R.id.tv_status, "离线")
            }
            DeviceStatus.IDLE -> {
                holder.setBackgroundResource(R.id.tv_status, R.drawable.shape_rectangle_blue_top_radius16)
                    .setText(R.id.tv_status, "空闲")
            }
            DeviceStatus.CALLING -> {
                holder.setBackgroundResource(R.id.tv_status, R.drawable.shape_rectangle_red_top_radius16)
                    .setText(R.id.tv_status, "呼叫中")
            }
            DeviceStatus.IN_CALL_CALLEE, DeviceStatus.IN_CALL_CALLER -> {
                holder.setBackgroundResource(R.id.tv_status, R.drawable.shape_rectangle_orange_top_radius16)
                    .setText(R.id.tv_status, "通话中")
            }
        }
    }

}