package com.fajarproject.travels.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fajarproject.travels.R
import com.fajarproject.travels.base.ui.AdapterHolder
import com.fajarproject.travels.databinding.AdapterMenuBinding
import com.fajarproject.travels.models.LookupDetailModel
import com.fajarproject.travels.ui.home.HomePresenter

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

class MenuAdapter(
	private val list: List<LookupDetailModel>?,
	val context: Context,
	val presenter: HomePresenter
) :
	RecyclerView.Adapter<AdapterHolder<AdapterMenuBinding>>() {

	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): AdapterHolder<AdapterMenuBinding> {
		return AdapterHolder(
			AdapterMenuBinding.inflate(
				LayoutInflater.from(parent.context), parent, false
			), null
		)
	}

	override fun getItemCount(): Int {
		return list?.size!!
	}

	override fun onBindViewHolder(holder: AdapterHolder<AdapterMenuBinding>, position: Int) {
		try {
            val binding = AdapterMenuBinding.bind(holder.itemView)
			val data = list?.get(position)
			binding.txtMenuTitle.text = data?.lookup_meaning
			imageMenu(data, holder)
			binding.cvMenu.setOnClickListener { presenter.getItem(data) }
		} catch (e: NullPointerException) {
			e.printStackTrace()
		}
	}

	@SuppressLint("DefaultLocale")
	private fun imageMenu(data: LookupDetailModel?, holder: AdapterHolder<AdapterMenuBinding>) {
		val binding = AdapterMenuBinding.bind(holder.itemView)
        when {
			data?.lookup_meaning?.toLowerCase() == "sejarah" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_museum)
			}

			data?.lookup_meaning?.toLowerCase() == "alam" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_waterfall)
			}

			data?.lookup_meaning?.toLowerCase() == "kuliner" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_kuliner)
			}

			data?.lookup_meaning?.toLowerCase() == "pendidikan" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_school)
			}

			data?.lookup_meaning?.toLowerCase() == "pertanian" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_farm)
			}

			data?.lookup_meaning?.toLowerCase() == "budaya" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_culture)
			}

			data?.lookup_meaning?.toLowerCase() == "bahari" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_bahari)
			}

			data?.lookup_meaning?.toLowerCase() == "religi" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_religion)
			}

			else -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_travel)
			}
		}
	}
}