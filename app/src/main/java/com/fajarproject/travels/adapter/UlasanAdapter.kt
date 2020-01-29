package com.fajarproject.travels.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fajarproject.travels.R
import com.fajarproject.travels.base.view.Callback
import com.fajarproject.travels.base.widget.UnixToHuman
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.adapter_ulasan.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Fajar Adi Prasetyo on 03/12/19.
 */

class UlasanAdapter (private val list: MutableList<UlasanItem>, val context : Context?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class AdapterHolder(itemView : View): RecyclerView.ViewHolder(itemView)
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            AdapterHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.adapter_ulasan, parent, false)
            )
        }else {
            LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.progress_loading,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val ulasan = list[position]
            holder.itemView.nama_pengirim.text = ulasan.fullname
            holder.itemView.tvStar.text = ulasan.rattingUlasan.toString()
            val prettyTime = PrettyTime()
            val date = Util.longToDate(ulasan.createDate!!)
            holder.itemView.tanggal_ulasan.text = UnixToHuman.getTimeAgo(ulasan.createDate)
            var expand = false
            holder.itemView.ulasan.setUndelineSpan(false)
            holder.itemView.ulasan.setText(ulasan.ulasan!!,expand,object : Callback{
                override fun onExpand() {
                }

                override fun onCollapse() {
                }

                override fun onLoss() {
                }

                override fun onExpandClick() {
                    expand = !expand
                    holder.itemView.ulasan.setChanged(expand)
                }

                override fun onCollapseClick() {
                }

            })
        }else{
            Log.d("Loading",".....")
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        list.add(UlasanItem())
        notifyItemInserted(list.size - 1)
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = list.size - 1
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int) : UlasanItem{
        return list[position]
    }

    fun addData(data : MutableList<UlasanItem>){
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    fun getDataList() : MutableList<UlasanItem>{
        return list
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size - 1 && isLoadingAdded) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
    }
}