package com.fajarproject.travels.ui.ulasan

import com.fajarproject.travels.models.RattingItem
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.models.RattingModel

/**
 * Create by Fajar Adi Prasetyo on 14/01/2020.
 */
interface UlasanView {

    fun setToolbar()

    fun setUI()

    fun showLoading()

    fun hideLoading()

    fun getDataSuccess(model : RattingModel)

    fun getDataFail(message : String)

    fun setDataRatting(model : List<RattingItem>)

    fun setDataUlasan(model : MutableList<UlasanItem>)

    fun getProgressRatting(ratting : Int) : Float
}