package com.fajarproject.travels.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpanned
import androidx.recyclerview.widget.RecyclerView
import com.fajarproject.travels.R
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.adapter_ulasan.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

/**
 * Created by Fajar Adi Prasetyo on 03/12/19.
 */

class UlasanAdapter (private val list: List<UlasanItem>, val context : Context?)
    : RecyclerView.Adapter<UlasanAdapter.AdapterHolder>(){

    class AdapterHolder(itemView : View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        return AdapterHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_ulasan, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val ulasan = list[position]
        holder.itemView.nama_pengirim.text  = ulasan.fullname
        holder.itemView.ulasan.text         = ulasan.ulasan
        Util.makeTextViewResizable(holder.itemView.ulasan,2,"Selengkapnya",true)
        holder.itemView.tvStar.text         = ulasan.rattingUlasan.toString()
        val prettyTime = PrettyTime()
        val date = Util.longToDate(ulasan.createDate!!)
        holder.itemView.tanggal_ulasan.text = prettyTime.format(date)
    }
}