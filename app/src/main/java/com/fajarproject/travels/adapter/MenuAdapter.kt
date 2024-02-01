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
import java.util.*

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
		val meaning = data?.lookup_meaning?.replace("Wisata ","")
		when {
			meaning?.lowercase(Locale.getDefault()) == "sejarah" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_museum)
			}

			meaning?.lowercase(Locale.getDefault()) == "alam" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_waterfall)
			}

			meaning?.lowercase(Locale.getDefault()) == "kuliner" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_kuliner)
			}

			meaning?.lowercase(Locale.getDefault()) == "pendidikan" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_school)
			}

			meaning?.lowercase(Locale.getDefault()) == "pertanian" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_farm)
			}

			meaning?.lowercase(Locale.getDefault()) == "budaya" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_culture)
			}

			meaning?.lowercase(Locale.getDefault()) == "bahari" -> {
				binding.imgMenu.background =
					ContextCompat.getDrawable(context, R.drawable.ic_bahari)
			}

			meaning?.lowercase(Locale.getDefault()) == "religi" -> {
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