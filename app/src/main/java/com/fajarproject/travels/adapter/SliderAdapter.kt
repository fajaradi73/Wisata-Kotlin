package com.fajarproject.travels.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.fajarproject.travels.R
import com.fajarproject.travels.util.Util
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.image_slider_layout_item.view.*

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

@SuppressLint("SetTextI18n")
class SliderAdapter(val context: Context) : SliderViewAdapter<SliderAdapter.AdapterHolder>() {

    class AdapterHolder(var itemView : View): ViewHolder(itemView)

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {

        when (position) {
            0 -> {
                Glide.with(holder.itemView)
                    .load("https://agnizeeyolo.files.wordpress.com/2018/12/13.jpg")
                    .placeholder(Util.circleLoading(context))
                    .into(holder.itemView.iv_auto_image_slider)
            }
            1 -> {
                Glide.with(holder.itemView)
                    .load("https://content.fortune.com/wp-content/uploads/2019/09/1920x1080-Intrepid-Travel-Egypt-Cairo-Pyramids-group-hug-028.jpeg")
                    .placeholder(Util.circleLoading(context))
                    .into(holder.itemView.iv_auto_image_slider)
            }
            2 -> {
                Glide.with(holder.itemView)
                    .load("https://image.cnbcfm.com/api/v1/image/106294079-1576209090305gettyimages-1086852880.jpeg?v=1576209163&w=1260&h=750")
                    .placeholder(Util.circleLoading(context))
                    .into(holder.itemView.iv_auto_image_slider)
            }
            3 -> {
                Glide.with(holder.itemView)
                    .load("https://image.cnbcfm.com/api/v1/image/106294119-1576210340215gettyimages-763172559.jpeg?v=1576211217&w=1260&h=750")
                    .placeholder(Util.circleLoading(context))
                    .into(holder.itemView.iv_auto_image_slider)
            }
            else -> {
                Glide.with(holder.itemView)
                    .load("https://image.cnbcfm.com/api/v1/image/106294131-1576211589402gettyimages-913987230.jpeg?v=1576211867&w=1260&h=750")
                    .placeholder(Util.circleLoading(context))
                    .into(holder.itemView.iv_auto_image_slider)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?): AdapterHolder {
        return AdapterHolder(
            LayoutInflater.from(
                parent!!.context
            ).inflate(R.layout.image_slider_layout_item, parent, false)
        )
    }

    override fun getCount(): Int {
        return 5
    }
}