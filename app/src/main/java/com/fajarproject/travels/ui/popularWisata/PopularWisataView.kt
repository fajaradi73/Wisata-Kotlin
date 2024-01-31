package com.fajarproject.travels.ui.popularWisata

import android.content.Intent
import android.view.View
import com.fajarproject.travels.models.PopularWisataModel

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

interface PopularWisataView {

    fun showLoading()

    fun hideLoading()

    fun moveToDetail(intent : Intent)

    fun setRecycleView()

    fun getDataSuccess(model: MutableList<PopularWisataModel>)

    fun getDataFail(message : String)

    fun showData(isShow: Boolean)

    fun getDataFailLike(message: String?)

    fun showMessageFavorite(message: String?,view: View)
}