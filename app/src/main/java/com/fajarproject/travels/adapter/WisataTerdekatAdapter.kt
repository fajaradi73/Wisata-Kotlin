package com.fajarproject.travels.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajarproject.travels.FlavorConfig
import com.fajarproject.travels.R
import com.fajarproject.travels.base.ui.AdapterHolder
import com.fajarproject.travels.base.ui.LoadingViewHolder
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.databinding.AdapterNearbyBinding
import com.fajarproject.travels.databinding.ProgressLoadingBinding
import com.fajarproject.travels.models.NearbyModel
import com.fajarproject.travels.ui.wisataTerdekat.WisataTerdekatPresenter
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.like.LikeButton
import com.like.OnLikeListener


/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class WisataTerdekatAdapter(
	private val list: MutableList<NearbyModel>,
	val context: Context,
	val presenter: WisataTerdekatPresenter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var onItemClickListener: OnItemClickListener? = null

	fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
		this.onItemClickListener = onItemClickListener
	}

	private var isLoadingAdded = false

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return if (viewType == Constant.VIEW_TYPE_ITEM) {
			AdapterHolder(
				AdapterNearbyBinding.inflate(LayoutInflater.from(parent.context), parent, false),
				this.onItemClickListener
			)
		} else {
			LoadingViewHolder(
				ProgressLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
			)
		}
	}

	override fun getItemCount(): Int {
		return list.size
	}

	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val binding = AdapterNearbyBinding.bind(holder.itemView)
			val nearby = list[position]
			binding.titleWisata.text = nearby.namaWisata
			binding.jarakWisata.text =
				"Jarak > " + String.format("%.2f", nearby.distance!!) + "Km"
			Glide.with(context)
				.load(FlavorConfig.baseImage() + nearby.imageWisata)
				.error(R.drawable.image_dieng)
				.placeholder(Util.circleLoading(context))
				.into(binding.imageWisata)

			binding.whitelist.isLiked = nearby.favorite!!
			binding.whitelist.setOnLikeListener(object : OnLikeListener {
				override fun liked(likeButton: LikeButton) {
					presenter.saveFavorite(nearby.id, likeButton)
				}

				override fun unLiked(likeButton: LikeButton) {
					presenter.saveFavorite(nearby.id, likeButton)
				}
			})
		} else {
			///do nothing
			Log.d("Loading", "......")
		}
	}

	fun addLoadingFooter() {
		isLoadingAdded = true
		list.add(NearbyModel())
		notifyItemInserted(list.size - 1)
	}

	fun removeLoadingFooter() {
		isLoadingAdded = false
		val position: Int = list.size - 1
		list.removeAt(position)
		notifyItemRemoved(position)
	}

	fun getItem(position: Int): NearbyModel {
		return list[position]
	}

	fun addData(data: MutableList<NearbyModel>) {
		this.list.addAll(data)
		notifyDataSetChanged()
	}

	fun getDataList(): MutableList<NearbyModel> {
		return list
	}

	override fun getItemViewType(position: Int): Int {
		return if (position == list.size - 1 && isLoadingAdded) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
	}

}