package com.fajarproject.travels.feature.password

import android.app.Activity
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.models.request.PasswordRequest
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Create by Fajar Adi Prasetyo on 27/01/2020.
 */
class PasswordPresenter(view: PasswordView, val context: Activity, override var apiStores: UserApi): BasePresenter<PasswordView,UserApi>() {
    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun changePassword(request: PasswordRequest){
        view?.showLoading()
        addSubscribe(apiStores.changePassword(user.token,request),object : NetworkCallback<SaveModel>(){
            override fun onSuccess(model: SaveModel) {
                view?.successSubmit(model.title,model.message)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                when (code) {
                    401 -> {
                        Util.sessionExpired(context)
                    }
                    500 -> {
                        val title           = jsonObject?.getString("title")
                        val errorMessage    = jsonObject?.getString("message")
                        view?.failedDialog(title!!,errorMessage!!,false)
                    }
                    501 -> {
                        val title           = jsonObject?.getString("title")
                        val errorMessage    = jsonObject?.getString("message")
                        view?.failedDialog(title!!,errorMessage!!,true)
                    }
                    else -> {
                        view?.failedSubmit(message!!)
                    }
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }

        })
    }
}