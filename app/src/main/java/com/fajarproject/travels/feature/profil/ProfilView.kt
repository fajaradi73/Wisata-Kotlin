package com.fajarproject.travels.feature.profil

import android.content.Intent
import com.fajarproject.travels.models.ProfileModel

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

interface ProfilView {
    fun showLoading()

    fun hideLoading()

    fun getDataFail(message : String)

    fun getDataSuccess(model : ProfileModel)

    fun changeActivity(intent: Intent)

    fun showDialogLogout()

    fun setImage(imageUrl : String)

    fun setAction()
}