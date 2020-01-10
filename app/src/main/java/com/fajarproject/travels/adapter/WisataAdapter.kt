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
import com.fajarproject.travels.feature.wisata.WisataPresenter
import com.fajarproject.travels.models.WisataModel
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.base.view.OnItemClickListener
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.adapter_wisata.view.*

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

class WisataAdapter(private val list: List<WisataModel>, private val context : Context, private val presenter: WisataPresenter?) :
    RecyclerView.Adapter<WisataAdapter.WisataHolder>() {

    private var onItemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?){
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WisataHolder {
        return WisataHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_wisata, parent, false)
            , this.onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WisataHolder, position: Int) {
        val tour = list[position]
        holder.itemView.title_wisata.text   = tour.namaWisata
        holder.itemView.alamat_wisata.text  = tour.alamatWisata

        Glide.with(context)
            .load(App.BASE_IMAGE + tour.imageWisata)
            .error(R.drawable.image_dieng)
            .placeholder(Util.circleLoading(context))
            .into(holder.itemView.image_wisata)

        holder.itemView.whitelist.isLiked = tour.favorite!!
        holder.itemView.whitelist.setOnLikeListener(object : OnLikeListener{
            override fun liked(likeButton: LikeButton) {
                presenter?.saveFavorite(tour.idWisata,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                presenter?.saveFavorite(tour.idWisata,likeButton)
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