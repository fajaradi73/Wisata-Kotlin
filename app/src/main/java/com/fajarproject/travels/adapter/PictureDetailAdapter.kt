package com.fajarproject.travels.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.adapter_foto_detail.view.*

/**
 * Create by Fajar Adi Prasetyo on 12/01/2020.
 */
class PictureDetailAdapter(private val list: List<PictureItem>, private val context : Activity) : RecyclerView.Adapter<PictureDetailAdapter.AdapterHolder>() {

    private var onItemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?){
        this.onItemClickListener = onItemClickListener
    }

    class AdapterHolder(itemView: View, onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var onItemClickListener : OnItemClickListener? = null

        override fun onClick(v: View?) {
            onItemClickListener?.onItemClick(v,adapterPosition)
        }
        init {
            itemView.setOnClickListener(this)
            this.onItemClickListener = onItemClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        return AdapterHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_foto_detail, parent, false)
            , this.onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val data = list[position]
        Glide.with(context)
            .load(App.BASE_IMAGE + data.picture)
            .error(R.drawable.image_dieng)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(Util.circleLoading(context))
            .into(holder.itemView.picture)
    }
}