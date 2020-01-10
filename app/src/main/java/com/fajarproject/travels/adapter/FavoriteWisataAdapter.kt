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
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.models.FavoriteModel
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.adapter_favorite.view.*

class FavoriteWisataAdapter(private val list: List<FavoriteModel>, private val context : Context) :
    RecyclerView.Adapter<FavoriteWisataAdapter.AdapterHolder>() {

    private var onItemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?){
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        return AdapterHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_favorite, parent, false)
            , this.onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val tour = list[position]
        holder.itemView.title_wisata.text   = tour.namaWisata
        holder.itemView.alamat_wisata.text  = tour.alamatWisata

        Glide.with(context)
            .load(App.BASE_IMAGE + tour.imageWisata)
            .error(R.drawable.image_dieng)
            .placeholder(Util.circleLoading(context))
            .into(holder.itemView.image_wisata)
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
}