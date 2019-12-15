package com.fajarproject.travels.tour.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.ResponseApi.DataTour
import com.fajarproject.travels.tour.model.WisataModel
import com.fajarproject.travels.tour.presenter.TourPresenter
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.view.OnItemClickListener
import com.fajarproject.travels.widget.ImageLoader
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.adapter_wisata.view.*

class AdapterWisata(private val list: List<WisataModel>, private val context : Context, private val presenter: TourPresenter) :
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
        holder.itemView.title_wisata.text   = tour.namaWisata
        holder.itemView.jam_wisata.text     = Util.milisecondTotimes(tour.jamBuka!!) + " - " + Util.milisecondTotimes(tour.jamTutup!!)
        holder.itemView.alamat_wisata.text  = tour.alamatWisata

//        ImageLoader.with(context).load(holder.itemView.image_wisata,App.BASE_IMAGE + tour.imageWisata)
        Glide.with(context).load(App.BASE_IMAGE + tour.imageWisata).into(holder.itemView.image_wisata)
        holder.itemView.whitelist.isLiked = tour.favorite!!
        holder.itemView.whitelist.setOnLikeListener(object : OnLikeListener{
            override fun liked(likeButton: LikeButton?) {
                presenter.saveFavorite(tour.idWisata,likeButton)
            }

            override fun unLiked(likeButton: LikeButton?) {
                presenter.saveFavorite(tour.idWisata,likeButton)
            }

        })
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