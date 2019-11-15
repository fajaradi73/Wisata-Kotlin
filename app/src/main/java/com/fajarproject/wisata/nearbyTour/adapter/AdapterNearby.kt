package com.fajarproject.wisata.nearbyTour.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajarproject.wisata.App
import com.fajarproject.wisata.R
import com.fajarproject.wisata.ResponseApi.DataTour
import com.fajarproject.wisata.view.OnItemClickListener
import com.fajarproject.wisata.widget.ImageLoader
import kotlinx.android.synthetic.main.adapter_nearby.view.*

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class AdapterNearby(private val list:List<DataTour>, val context : Context) : RecyclerView.Adapter<AdapterNearby.NearbyHolder>() {


    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyHolder {
        return NearbyHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_nearby, parent, false)
        , this.onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NearbyHolder, position: Int) {
        val nearby = list[position]
        holder.itemView.title_wisata.text = nearby.nama_wisata
        holder.itemView.jarak_wisata.text = "Jarak > " + String.format("%.2f", nearby.distance.toDouble()) + "Km"
        ImageLoader.with(context).load(holder.itemView.image_wisata,App.BASE_IMAGE + nearby.image_wisata)
//        Glide.with(context).load(App.BASE_IMAGE + nearby.image_wisata).into(holder.itemView.image_wisata)
    }

    class NearbyHolder(itemView: View,onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        private var onItemClickListener : OnItemClickListener? = null

        override fun onClick(v: View?) {
            onItemClickListener?.onItemClick(v,adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
            this.onItemClickListener = onItemClickListener
        }
    }

}