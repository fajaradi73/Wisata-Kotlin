package com.fajarproject.travels.base.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.fajarproject.travels.base.view.OnItemClickListener


class AdapterHolder<B : ViewBinding>(var binding: B, onItemClickListener: OnItemClickListener?) :
	RecyclerView.ViewHolder(binding.root),
	View.OnClickListener {
	private var onItemClickListener: OnItemClickListener? = null

	override fun onClick(v: View?) {
		onItemClickListener?.onItemClick(v, adapterPosition)
	}

	init {
		binding.root.setOnClickListener(this)
		this.onItemClickListener = onItemClickListener
	}

}
