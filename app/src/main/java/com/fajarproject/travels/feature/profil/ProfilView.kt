package com.fajarproject.travels.feature.profil

import android.content.Intent
import android.net.Uri
import android.view.View
import com.fajarproject.travels.models.ProfileModel

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

interface ProfilView {
    fun showLoading()

    fun hideLoading()

    fun showLoadingShimmer()

    fun hideLoadingShimmer()

    fun getDataFail(message : String)

    fun getDataSuccess(model : ProfileModel)

    fun changeActivity(intent: Intent)

    fun showDialogLogout()

    fun setImage(imageUrl : String)

    fun setImageBackground(imageUrl : String)

    fun setAction()

    fun onImagePicked(uri : Uri)

    fun successUpload(title : String,message : String)

    fun failedUpload(message: String)

    fun previewImageProfil(imageUrl: String,view : View)

    fun previewResult()
}