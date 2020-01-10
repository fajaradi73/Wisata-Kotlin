package com.fajarproject.travels.feature.home

import android.content.Intent
import com.fajarproject.travels.models.LookupDetailModel

interface HomeView {
    fun showLoading()

    fun hideLoading()

    fun getDataSuccess(model: List<LookupDetailModel>?)

    fun getDataFail(message: String?)

    fun setBanner()

    fun setRecycleView()

    fun showBanner()

    fun moveToDetail(intent: Intent?)
}