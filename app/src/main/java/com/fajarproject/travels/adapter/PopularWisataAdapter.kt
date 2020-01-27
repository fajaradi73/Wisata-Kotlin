package com.fajarproject.travels.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.feature.popularWisata.PopularWisataPresenter
import com.fajarproject.travels.models.PopularWisataModel
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.adapter_populer.view.*

class PopularWisataAdapter(private val list: MutableList<PopularWisataModel>, private val context : Context, private val presenter: PopularWisataPresenter?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener : OnItemClickListener? = null
    private var isLoadingAdded = false

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?){
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.VIEW_TYPE_ITEM){
            AdapterHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.adapter_populer, parent, false)
                , this.onItemClickListener
            )
        }else {
            LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM){
            val tour = list[position]
            holder.itemView.title_wisata.text   = tour.namaWisata
            holder.itemView.alamat_wisata.text  = tour.alamatWisata

            Glide.with(context)
                .load(App.BASE_IMAGE + tour.imageWisata)
                .error(R.drawable.image_dieng)
                .placeholder(Util.circleLoading(context))
                .into(holder.itemView.image_wisata)

            holder.itemView.txt_ratting.text = tour.ratting.toString()
            holder.itemView.whitelist.isLiked = tour.favorite!!
            holder.itemView.whitelist.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {
                    presenter?.saveFavorite(tour.idWisata,likeButton)
                }
                override fun unLiked(likeButton: LikeButton) {
                    presenter?.saveFavorite(tour.idWisata,likeButton)
                }
            })
        }else{
            Log.d("Loading","......")
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        list.add(PopularWisataModel())
        notifyItemInserted(list.size - 1)
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = list.size - 1
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int) : PopularWisataModel{
        return list[position]
    }

    fun addData(data : MutableList<PopularWisataModel>){
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    fun getDataList() : MutableList<PopularWisataModel>{
        return list
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size - 1 && isLoadingAdded) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
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
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}