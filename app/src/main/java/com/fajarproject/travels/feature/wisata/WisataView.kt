package com.fajarproject.travels.feature.wisata

import android.content.Intent
import android.view.View
import com.fajarproject.travels.models.WisataModel

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

interface WisataView {
    fun showLoading()

    fun hideLoading()

    fun getDataSuccess(model: List<WisataModel>)

    fun getDataFail(message: String?)

    fun moveToDetail(intent: Intent?)

    fun showData(isShow : Boolean)

    fun getDataFailLike(message: String?)

    fun showMessageFavorite(message: String?,view: View)

    fun setToolbar()

    fun setRecycleView()
}