package com.fajarproject.travels.feature.login

import android.content.Intent

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

interface LoginView {

    fun setToolbar()

    fun setUI()

    fun enableButton()

    fun showLoading()

    fun hideLoading()

    fun changeActivity(intent: Intent?)

    fun errorEmail()

    fun isEmailValid(isValid : Boolean)

    fun errorPassword()

    fun failedLogin(title: String, msg: String,isFinish: Boolean)

}