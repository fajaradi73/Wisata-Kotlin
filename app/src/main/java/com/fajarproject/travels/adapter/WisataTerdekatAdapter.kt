package com.fajarproject.travels.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.models.NearbyModel
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.feature.wisataTerdekat.WisataTerdekatPresenter
import com.fajarproject.travels.util.Util
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.adapter_nearby.view.*
import kotlinx.android.synthetic.main.adapter_nearby.view.image_wisata
import kotlinx.android.synthetic.main.adapter_nearby.view.title_wisata

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class WisataTerdekatAdapter(private val list:List<NearbyModel>, val context : Context, val presenter: WisataTerdekatPresenter)
    : RecyclerView.Adapter<WisataTerdekatAdapter.NearbyHolder>() {


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
        holder.itemView.title_wisata.text = nearby.namaWisata
        holder.itemView.jarak_wisata.text = "Jarak > " + String.format("%.2f", nearby.distance!!) + "Km"
        Glide.with(context)
            .load(App.BASE_IMAGE + nearby.imageWisata)
            .error(R.drawable.image_dieng)
            .placeholder(Util.circleLoading(context))
            .into(holder.itemView.image_wisata)

        holder.itemView.whitelist.isLiked = nearby.favorite!!
        holder.itemView.whitelist.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                presenter.saveFavorite(nearby.idWisata,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                presenter.saveFavorite(nearby.idWisata,likeButton)
            }

        })
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