package com.fajarproject.travels.ui.password

import com.fajarproject.travels.models.request.PasswordRequest

/**
 * Create by Fajar Adi Prasetyo on 27/01/2020.
 */
interface PasswordView {
    fun setToolbar()
    fun getDataIntent()
    fun setUI()
    fun showLoading()
    fun hideLoading()
    fun confirmSubmit(request: PasswordRequest)
    fun failedDialog(title: String, msg: String,isNew : Boolean)
    fun failedSubmit(msg : String)
    fun successSubmit(title: String, msg: String)
}