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
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.feature.previewPictureWisata.PreviewPictureActivity
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.adapter_foto.view.*
import org.parceler.Parcels

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
class PictureAdapter(private val list: List<PictureItem>, private val context : Activity) : RecyclerView.Adapter<PictureAdapter.AdapterHolder>() {

    private var onItemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?){
        this.onItemClickListener = onItemClickListener
    }

    class AdapterHolder(itemView: View, onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var onItemClickListener : OnItemClickListener? = null

        override fun onClick(v: View?) {
            onItemClickListener?.onItemClick(v,adapterPosition)
        }
        init {
            itemView.setOnClickListener(this)
            this.onItemClickListener = onItemClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        return AdapterHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_foto, parent, false)
            , this.onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val data = list[position]
        Glide.with(context)
            .load(App.BASE_IMAGE + data.picture)
            .error(R.drawable.image_dieng)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(0.1f)
            .placeholder(Util.circleLoading(context))
            .into(holder.itemView.picture)
        ViewCompat.setTransitionName(holder.itemView.picture,data.namaWisata)

        holder.itemView.picture.setOnClickListener {
            previewImageWisata(holder.itemView.picture,position,list)
        }
    }

    private fun previewImageWisata(view : View, position: Int, data: List<PictureItem>){
        // Ordinary Intent for launching a new activity
        val intent = Intent(context, PreviewPictureActivity::class.java)
        intent.putExtra(Constant.position,position)
        intent.putExtra(Constant.dataFoto, Parcels.wrap(data))

        val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            context,
            view,  // Starting view
            ViewCompat.getTransitionName(view)!! // The String
        )
        ActivityCompat.startActivity(context, intent,options.toBundle())
    }
}