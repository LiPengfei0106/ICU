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
class BedDeviceAdapter(data: MutableList<Device>?) :
    BaseQuickAdapter<Device, BaseViewHolder>(R.layout.item_bed_device, data) {

    init {
        setDiffCallback(object : DiffUtil.ItemCallback<Device>() {
            override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem.number == newItem.number
                        && oldItem.name == newItem.name
                        && oldItem.description == newItem.description
            }

        })
        addChildClickViewIds(R.id.tv_monitor, R.id.tv_call)
    }

    override fun convert(holder: BaseViewHolder, item: Device) {
        holder.setText(R.id.tv_name, item.name)
        when (item.status) {
            DeviceStatus.DISCONNECT -> {
                holder.setBackgroundResource(R.id.tv_status, R.drawable.shape_rectangle_gray_top_left_radius16)
                    .setText(R.id.tv_status, "离线")
            }
            DeviceStatus.IDLE -> {
                holder.setBackgroundResource(R.id.tv_status, R.drawable.shape_rectangle_blue_top_left_radius16)
                    .setText(R.id.tv_status, "空闲")
            }
            DeviceStatus.CALLING -> {
                holder.setBackgroundResource(R.id.tv_status, R.drawable.shape_rectangle_red_top_left_radius16)
                    .setText(R.id.tv_status, "呼叫中")
            }
            DeviceStatus.IN_CALL -> {
                holder.setBackgroundResource(R.id.tv_status, R.drawable.shape_rectangle_orange_top_left_radius16)
                    .setText(R.id.tv_status, "通话中")
            }
        }
    }

}