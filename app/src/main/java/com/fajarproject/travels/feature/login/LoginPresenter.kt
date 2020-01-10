package com.fajarproject.travels.feature.login

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.fajarproject.travels.api.LoginAPI
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.feature.main.MainActivity
import com.fajarproject.travels.models.LoginModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

class LoginPresenter(view : LoginView, val context: Activity, override var apiStores: LoginAPI) : BasePresenter<LoginView,LoginAPI>() {

    init {
        super.attachView(view)
    }

    fun loginWisata(loginModel: LoginModel){
        view!!.showLoading()
        addSubscribe(apiStores.loginPosts(loginModel),object : NetworkCallback<UserModel>(){
            override fun onSuccess(model: UserModel) {
                Util.saveUser(model,context)
                view!!.changeActivity(Intent(context, MainActivity::class.java))
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (jsonObject != null){
                    when (jsonObject.getString("code")) {
                        "LP_ERR_0001" -> {
                            view!!.errorEmail()
                        }
                        "LP_ERR_0002" -> {
                            view!!.errorPassword()
                        }
                        else -> {
                            Log.d("ErrorLogin","${message}/${code}/${jsonObject}")
                        }
                    }
                }else{
                    Log.d("ErrorLogin","${message}/${code}/${jsonObject}")
                }
            }

            override fun onFinish() {
                view!!.hideLoading()
            }

        })
    }
}