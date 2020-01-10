package com.fajarproject.travels.feature.register

import android.content.Intent
import com.fajarproject.travels.models.RegisterModel
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

interface RegisterView {

    fun setToolbar()

    fun setUI()

    fun showLoading()

    fun hideLoading()

    fun getDataSuccess(data : RegisterModel)

    fun getDataFail(jsonObject: JSONObject)

    fun checkValidation() : Boolean

    fun changeActivity(intent: Intent)

    fun showErrorPassword(isError : Boolean)
}