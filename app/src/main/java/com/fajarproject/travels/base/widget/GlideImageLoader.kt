package com.fajarproject.travels.base.widget

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.fajarproject.travels.R
import com.fajarproject.travels.util.Util
import lv.chi.photopicker.loader.ImageLoader

/**
 * Create by Fajar Adi Prasetyo on 16/01/2020.
 */
class GlideImageLoader: ImageLoader {

    override fun loadImage(context: Context, view: ImageView, uri: Uri) {
        Glide.with(context)
            .load(uri)
            .error(R.drawable.bg_placeholder)
            .placeholder(Util.circleLoading(context))
            .centerCrop()
            .into(view)
    }
}