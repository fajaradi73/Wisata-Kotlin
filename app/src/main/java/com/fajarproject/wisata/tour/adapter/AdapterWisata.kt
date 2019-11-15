package com.fajarproject.wisata.tour.adapter

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
import kotlinx.android.synthetic.main.adapter_wisata.view.*

class AdapterWisata(private val list: List<DataTour>, private val context : Context) :
    RecyclerView.Adapter<AdapterWisata.WisataHolder>() {

    private var onItemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?){
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WisataHolder {
        return WisataHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_wisata, parent, false)
        ,this.onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WisataHolder, position: Int) {
        val tour = list[position]
        holder.itemView.title_wisata.text   = tour.nama_wisata
        holder.itemView.jam_wisata.text     = tour.jam_buka + " - " + tour.jam_tutup
        holder.itemView.alamat_wisata.text  = tour.alamat_wisata

        ImageLoader.with(context).load(holder.itemView.image_wisata,App.BASE_IMAGE + tour.image_wisata)
//        Glide.with(context).load(App.BASE_IMAGE + tour.image_wisata).into(holder.itemView.image_wisata)
    }

    class WisataHolder(itemView: View,onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
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