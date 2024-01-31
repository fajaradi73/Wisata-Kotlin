package com.fajarproject.travels.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fajarproject.travels.FlavorConfig
import com.fajarproject.travels.R
import com.fajarproject.travels.base.ui.AdapterHolder
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.databinding.AdapterFotoDetailBinding
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.ui.previewPictureWisata.PreviewPictureActivity
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import org.parceler.Parcels

/**
 * Create by Fajar Adi Prasetyo on 12/01/2020.
 */
class PictureDetailAdapter(private val list: List<PictureItem>, private val context: Activity) :
	RecyclerView.Adapter<AdapterHolder<AdapterFotoDetailBinding>>() {

	private var onItemClickListener: OnItemClickListener? = null

	fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
		this.onItemClickListener = onItemClickListener
	}

	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): AdapterHolder<AdapterFotoDetailBinding> {
		return AdapterHolder(
			AdapterFotoDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false),
			this.onItemClickListener
		)
	}

	override fun getItemCount(): Int {
		return list.size
	}

	override fun onBindViewHolder(holder: AdapterHolder<AdapterFotoDetailBinding>, position: Int) {
		val binding = AdapterFotoDetailBinding.bind(holder.itemView)
		val data = list[position]
		Glide.with(context)
			.load(FlavorConfig.baseImage() + data.picture)
			.error(R.drawable.image_dieng)
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(Util.circleLoading(context))
			.into(binding.picture)
		ViewCompat.setTransitionName(binding.picture, data.namaWisata)
		binding.picture.setOnClickListener {
			previewImageWisata(binding.picture, position, list)
		}
	}

	private fun previewImageWisata(view: View, position: Int, data: List<PictureItem>) {
		// Ordinary Intent for launching a new activity
		val intent = Intent(context, PreviewPictureActivity::class.java)
		intent.putExtra(Constant.position, position)
		intent.putExtra(Constant.dataFoto, Parcels.wrap(data))
		val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
			context,
			view,  // Starting view
			ViewCompat.getTransitionName(view)!! // The String
		)
		ActivityCompat.startActivity(context, intent, options.toBundle())
	}
}