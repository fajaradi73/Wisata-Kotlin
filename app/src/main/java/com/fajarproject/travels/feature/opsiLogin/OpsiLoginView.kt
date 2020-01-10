package com.fajarproject.travels.feature.opsiLogin

import android.content.Intent

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

interface OpsiLoginView {

    fun init()

    fun setUI()

    fun loadingFacebook(isShow : Boolean)

    fun loadingGoogle(isShow : Boolean )

    fun loginGoogle()

    fun loginFacebook()

    fun showLoading()

    fun hideLoading()

    fun changeActivity(intent: Intent?,isFinish : Boolean)
}