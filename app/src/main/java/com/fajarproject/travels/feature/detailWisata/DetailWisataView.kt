package com.fajarproject.travels.feature.detailWisata

import android.content.Intent
import android.view.View
import com.fajarproject.travels.models.DetailWisataModel
import com.fajarproject.travels.models.UlasanItem

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

interface DetailWisataView {

    fun setNativeAds()

    fun setToolbar()

    fun init()

    fun setColorIcon(isShow : Boolean)

    fun setUlasan(list: List<UlasanItem>)

    fun getDataSuccess(data: DetailWisataModel)

    fun getDataFail(message : String)

    fun showLoading()

    fun hideLoading()

    fun changeActivity(intent: Intent)

    fun showMessageLike(message: String,view : View)
}