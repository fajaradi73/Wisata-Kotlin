package com.fajarproject.travels.ui.previewPictureProfile

/**
 * Create by Fajar Adi Prasetyo on 18/01/2020.
 */
interface PreviewPictureProfileView {
    fun showLoading()
    fun hideLoading()
    fun setToolbar()

    fun successUpload(title : String,message : String)

    fun failedUpload(message: String)

}