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
import com.fajarproject.travels.databinding.AdapterFotoBinding
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.ui.previewPictureWisata.PreviewPictureActivity
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import org.parceler.Parcels

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
class PictureAdapter(
	private val list: List<PictureItem>, private val context: Activity
) : RecyclerView.Adapter<AdapterHolder<AdapterFotoBinding>>() {

	private var onItemClickListener: OnItemClickListener? = null

	fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
		this.onItemClickListener = onItemClickListener
	}

	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): AdapterHolder<AdapterFotoBinding> {
		return AdapterHolder(
			AdapterFotoBinding.inflate(
				LayoutInflater.from(parent.context), parent, false
			), this.onItemClickListener
		)
	}

	override fun getItemCount(): Int {
		return list.size
	}

	override fun onBindViewHolder(holder: AdapterHolder<AdapterFotoBinding>, position: Int) {
		val binding = AdapterFotoBinding.bind(holder.itemView)
        val data = list[position]
		Glide.with(context)
			.load(FlavorConfig.baseImage() + data.picture)
			.error(R.drawable.image_dieng)
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.thumbnail(0.1f)
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