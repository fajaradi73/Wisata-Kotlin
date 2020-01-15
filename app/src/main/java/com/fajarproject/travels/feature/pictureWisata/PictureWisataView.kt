package com.fajarproject.travels.feature.pictureWisata

import android.content.Intent
import com.fajarproject.travels.models.PictureItem

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
interface PictureWisataView {
    fun showLoading()

    fun hideLoading()

    fun getDataSucces(list: List<PictureItem>)

    fun getDataFail(message : String)

    fun setToolbar()

    fun setUI()

    fun changeActivity(intent: Intent)

    fun setAction()

    fun getStoragePermission()

    fun checkPermission()

    fun openFile()

    fun successUpload(title : String,message : String)

    fun failedUpload(message: String)
}