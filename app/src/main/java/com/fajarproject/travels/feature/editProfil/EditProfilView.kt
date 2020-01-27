package com.fajarproject.travels.feature.editProfil

import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.models.request.EditProfilRequest

/**
 * Create by Fajar Adi Prasetyo on 23/01/2020.
 */
interface EditProfilView {
    fun showLoading()
    fun hideLoading()
    fun successEdit(model: SaveModel)
    fun failedEdit(msg: String)
    fun getDataInten()
    fun setToolbar()
    fun setUI()
    fun setAction()
    fun getDataProfil(data : ProfileModel)
    fun confirmedDialog(request: EditProfilRequest)
}