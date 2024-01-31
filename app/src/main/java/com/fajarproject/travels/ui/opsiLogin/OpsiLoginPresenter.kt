package com.fajarproject.travels.ui.opsiLogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.api.LoginAPI
import com.fajarproject.travels.ui.main.MainActivity
import com.fajarproject.travels.models.RegisterSosmedModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

class OpsiLoginPresenter(view: OpsiLoginView, val context: Activity,
                         override var apiStores: LoginAPI
) :
    BasePresenter<OpsiLoginView, LoginAPI>() {

    init {
        super.attachView(view)
    }

    fun logoutSosmed(login : String){
        if (login.toLowerCase(Locale.getDefault()) == "facebook"){
            /////// Logout Facebook
            LoginManager.getInstance().logOut()
            view?.loadingFacebook(false)
        }else{
            ///// Google Logout
            FirebaseAuth.getInstance().signOut()
            view?.loadingFacebook(false)
        }
    }

    fun setUserGoogle(user: FirebaseUser){
        val email       : String?  = user.email
        val fullname    : String?  = user.displayName
        val id          : String?  = user.uid
        val image       : String?  = user.photoUrl.toString()
        loginSosmed(id!!,email!!,fullname!!,image!!,"google")
    }

    fun setUserFacebook(loginResult: LoginResult){
        val request: GraphRequest = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObject: JSONObject, _: GraphResponse ->
            val accessToken1: AccessToken? =
                AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken1 != null && !accessToken1.isExpired
            if (isLoggedIn) {
                try {
                    val id          : String = jsonObject.getString("id")
                    val email       : String = jsonObject.getString("email")
                    val fullname    : String = jsonObject.getString("name")
                    val imageUrl    = "https://graph.facebook.com/$id/picture?type=large"
                    loginSosmed(id,email,fullname,imageUrl,"Facebook")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }else{
                Toast.makeText(context,"$isLoggedIn/", Toast.LENGTH_LONG).show()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,first_name,last_name,email,link,gender, birthday")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun loginSosmed(fgCode : String, email: String, fullname : String, imageUrl : String, login: String){
        view?.showLoading()
        addSubscribe(apiStores.loginSosmedPosts(fgCode),object : NetworkCallback<UserModel>(){
            override fun onSuccess(model: UserModel) {
                Util.saveUser(model,context)
                view?.changeActivity(Intent(context, MainActivity::class.java),true)
            }

            override fun onFailure(message: String?,code : Int?,jsonObject: JSONObject?) {
                val codeError = jsonObject!!.getString("code")
                if (codeError == "LS_ERR_0002"){
                    val registerModel =
                        RegisterSosmedModel(
                            email,
                            fullname,
                            fgCode,
                            imageUrl,
                            login
                        )
                    registerSosmed(registerModel)
                }else{
                    logoutSosmed(login)
                    Log.d("ErrorLogin","${message}/${code}/${jsonObject}")
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }
        })
    }

    fun registerSosmed(registerSosmedModel: RegisterSosmedModel){
        view?.showLoading()
        addSubscribe(apiStores.registerSosmeds(registerSosmedModel),object : NetworkCallback<UserModel>(){
            override fun onSuccess(model: UserModel) {
                Util.saveUser(model,context)
                view?.changeActivity(Intent(context, MainActivity::class.java),true)
            }

            override fun onFailure(message: String?, code: Int?,jsonObject: JSONObject?) {
                if (code == 500){
                    logoutSosmed(registerSosmedModel.login_with)
                    val messageError = jsonObject!!.getString("message")
                    Util.showRoundedDialog(context,messageError,"",false)
                }else{
                    logoutSosmed(registerSosmedModel.login_with)
                    Log.d("ErrorRegister","${message}/${code}")
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }

        })
    }

}