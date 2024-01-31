package com.fajarproject.travels.ui.wisataFavorite

import android.content.Intent
import com.fajarproject.travels.models.FavoriteModel

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

interface FavoriteWisataView {

    fun setToolbar()

    fun showLoading()

    fun hideLoading()

    fun moveToDetail(intent : Intent)

    fun setRecycleView()

    fun getDataSucces(model: MutableList<FavoriteModel>)

    fun getDataFail(message : String)

    fun showData(isShow: Boolean)

}