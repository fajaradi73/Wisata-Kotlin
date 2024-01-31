package com.fajarproject.travels.ui.createUlasan


/**
 * Create by Fajar Adi Prasetyo on 28/01/2020.
 */

interface CreateUlasanView {
	fun setToolbar()
	fun setUI()
	fun showLoading()
	fun hideLoading()
	fun failedSubmit(msg: String)
	fun successSubmit(title: String,msg: String)
}