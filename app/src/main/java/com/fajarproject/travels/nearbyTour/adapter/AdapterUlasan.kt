package com.fajarproject.travels.nearbyTour.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fajarproject.travels.R
import com.fajarproject.travels.travelDetails.TravelDetails
import com.fajarproject.travels.travelDetails.model.UlasanItem
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.adapter_ulasan.view.*

/**
 * Created by Fajar Adi Prasetyo on 03/12/19.
 */

class AdapterUlasan (private val list: List<UlasanItem?>,val context : TravelDetails?)
    : RecyclerView.Adapter<AdapterUlasan.AdapterHolder>(){

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
        holder.itemView.ulasan.text = ulasan!!.ulasan
        holder.itemView.tanggal_ulasan.text = Util.epochToTime(ulasan.createDate!!)
        holder.itemView.ratting_ulasan.rating = ulasan.rattingUlasan!!.toFloat()
    }
}