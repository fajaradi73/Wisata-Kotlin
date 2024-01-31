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
import com.fajarproject.travels.databinding.AdapterWisataBinding
import com.fajarproject.travels.databinding.ProgressLoadingBinding
import com.fajarproject.travels.models.WisataModel
import com.fajarproject.travels.ui.wisata.WisataPresenter
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.like.LikeButton
import com.like.OnLikeListener

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

class WisataAdapter(
	private val list: MutableList<WisataModel>,
	private val context: Context,
	private val presenter: WisataPresenter?
) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var onItemClickListener: OnItemClickListener? = null
	private var isLoadingAdded = false

	fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
		this.onItemClickListener = onItemClickListener
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return if (viewType == Constant.VIEW_TYPE_ITEM) {
			AdapterHolder(
				AdapterWisataBinding.inflate(LayoutInflater.from(parent.context), parent, false),
				this.onItemClickListener
			)
		} else {
			LoadingViewHolder(
				ProgressLoadingBinding.inflate(
					LayoutInflater.from(parent.context),
					parent,
					false
				)
			)
		}
	}

	override fun getItemCount(): Int {
		return list.size
	}

	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
			val binding = AdapterWisataBinding.bind(holder.itemView)
			val tour = list[position]
			binding.titleWisata.text = tour.namaWisata
			binding.alamatWisata.text = tour.alamatWisata

			Glide.with(context)
				.load(FlavorConfig.baseImage() + tour.imageWisata)
				.error(R.drawable.image_dieng)
				.placeholder(Util.circleLoading(context))
				.into(binding.imageWisata)

			binding.whitelist.isLiked = tour.favorite ?: false
			binding.whitelist.setOnLikeListener(object : OnLikeListener {
				override fun liked(likeButton: LikeButton) {
					presenter?.saveFavorite(tour.id, likeButton)
				}

				override fun unLiked(likeButton: LikeButton) {
					presenter?.saveFavorite(tour.id, likeButton)
				}

			})
		} else {
			Log.d("Loading", ".....")
		}
	}

	fun addLoadingFooter() {
		isLoadingAdded = true
		list.add(WisataModel())
		notifyItemInserted(list.size - 1)
	}

	fun removeLoadingFooter() {
		isLoadingAdded = false
		val position: Int = list.size - 1
		list.removeAt(position)
		notifyItemRemoved(position)
	}

	fun getItem(position: Int): WisataModel {
		return list[position]
	}

	fun addData(data: MutableList<WisataModel>) {
		this.list.addAll(data)
		notifyDataSetChanged()
	}

	fun getDataList(): MutableList<WisataModel> {
		return list
	}

	override fun getItemViewType(position: Int): Int {
		return if (position == list.size - 1 && isLoadingAdded) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
	}

}