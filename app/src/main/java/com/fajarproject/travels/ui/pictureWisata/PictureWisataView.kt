package com.fajarproject.travels.ui.pictureWisata

import android.content.Intent
import com.fajarproject.travels.models.PictureItem

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
interface PictureWisataView {
    fun showLoading()

    fun hideLoading()

    fun getDataSuccess(list: List<PictureItem>)

    fun getDataFail(message : String)

    fun setToolbar()

    fun setUI()

    fun changeActivity(intent: Intent)

    fun setAction()


    fun successUpload(title : String,message : String)

    fun failedUpload(message: String)
}