package com.fajarproject.travels.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fajarproject.travels.R
import com.fajarproject.travels.feature.home.HomePresenter
import com.fajarproject.travels.models.LookupDetailModel
import kotlinx.android.synthetic.main.adapter_menu.view.*

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

class MenuAdapter (private val list: List<LookupDetailModel>?,val context: Context,val presenter: HomePresenter):
    RecyclerView.Adapter<MenuAdapter.AdapterHolder>() {

    class AdapterHolder(itemView : View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        return AdapterHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_menu, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        try {
            val data : LookupDetailModel? = list!![position]
            holder.itemView.txt_menu_title.text = data!!.lookup_meaning
            imageMenu(data,holder)
            holder.itemView.cvMenu.setOnClickListener { presenter.getItem(data) }
        }catch (e : NullPointerException){
            e.printStackTrace()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun imageMenu(data : LookupDetailModel,holder : AdapterHolder){
        when {
            data.lookup_meaning!!.toLowerCase() == "sejarah" -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_museum)
            }
            data.lookup_meaning.toLowerCase() == "alam" -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_waterfall)
            }
            data.lookup_meaning.toLowerCase() == "kuliner" -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_kuliner)
            }
            data.lookup_meaning.toLowerCase() == "pendidikan" -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_school)
            }
            data.lookup_meaning.toLowerCase() == "pertanian" -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_farm)
            }
            data.lookup_meaning.toLowerCase() == "budaya" -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_culture)
            }
            data.lookup_meaning.toLowerCase() == "bahari" -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_bahari)
            }
            data.lookup_meaning.toLowerCase() == "religi" -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_religion)
            }
            else -> {
                holder.itemView.img_menu.background = ContextCompat.getDrawable(context,R.drawable.ic_travel)
            }
        }
    }
}