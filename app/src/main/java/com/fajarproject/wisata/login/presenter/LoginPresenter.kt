package com.fajarproject.wisata.login.presenter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fajarproject.wisata.MainActivity
import com.fajarproject.wisata.login.activity.Login
import com.fajarproject.wisata.login.activity.OpsiLogin
import com.fajarproject.wisata.login.model.LoginModel
import com.fajarproject.wisata.login.model.RegisterSosmedModel
import com.fajarproject.wisata.login.model.User
import com.fajarproject.wisata.login.repository.LoginRepository
import com.fajarproject.wisata.preference.AppPreference
import com.fajarproject.wisata.util.Util
import com.fajarproject.wisata.view.DialogNoListener
import com.fajarproject.wisata.view.DialogYesListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

class LoginPresenter(val context : Activity) {
    private val loginRepository : LoginRepository = LoginRepository()

    fun setUserGoogle(user: FirebaseUser){
        val email       : String?  = user.email
        val fullname    : String?  = user.displayName
        val id          : String?  = user.uid
        val image       : String?  = user.photoUrl.toString()
        Log.d("usergoogle", "$email/$fullname/$id/$image")
        loginSosmed(id!!,email!!,fullname!!,image!!,"google")
    }

    fun setUserFacebook(loginResult: LoginResult){
        val request: GraphRequest = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObject: JSONObject, response: GraphResponse ->
            Log.v("LoginActivity", response.toString())
            Log.i("Succes", "LoginButton FacebookCallback onSuccess")
            val accessToken1: AccessToken? =
                AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken1 != null && !accessToken1.isExpired
            if (isLoggedIn) {
                try {
                    val id          : String = jsonObject.getString("id")
                    val email       : String = jsonObject.getString("email")
                    val fullname    : String = jsonObject.getString("name")
                    val firstName   = jsonObject.getString("first_name")
                    val lastName    = jsonObject.getString("last_name")
                    val imageUrl    = "https://graph.facebook.com/$id/picture?type=large"
                    Log.d("userfacebook", "$email/$fullname/$id/")
                    loginSosmed(id,email,fullname,imageUrl,"Facebook")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }else{
                Toast.makeText(context,"$isLoggedIn/",Toast.LENGTH_LONG).show()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,first_name,last_name,email,link,gender, birthday")
        request.parameters = parameters
        request.executeAsync()
    }

    fun loginWisata(loginModel: LoginModel){
        Util.showLoading(context)
        loginRepository.loginWisata(loginModel).enqueue(object : Callback<User?>{
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                Util.hideLoading()
                if (response.code() == 200){
                    saveUser(response.body()!!)
                    context.startActivity(Intent(context,MainActivity::class.java))
                    context.finish()
                }else{
                    val jsonObject = JSONObject(response.errorBody()?.string()!!)
                    val code = jsonObject.getString("code")
                    if (code == "LP_ERR_0001"){
                        if (context is Login) {
                            context.error_email.visibility = View.VISIBLE
                            context.error_email.text = "Email yang anda masukkan belum terdaftar"
                        }
                    }else if (code == "LP_ERR_0002"){
                        if (context is Login) {
                            context.error_password.visibility = View.VISIBLE
                            context.error_password.text = "Password yang anda masukkan salah"
                        }
                    }else {
                        Toast.makeText(
                            context,
                            "Response code ${response.code()}/${code}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Util.hideLoading()
                t.printStackTrace()
            }
        })
    }

    fun saveUser(user: User){
        val type: Type? = object : TypeToken<User?>() {}.type
        val json: String? = Gson().toJson(user, type)
        AppPreference.writePreference(context,"user",json!!)
    }

    fun loginSosmed(fgCode : String,email: String,fullname : String,imageUrl : String, login: String){
        Util.showLoading(context)
        loginRepository.loginSosmed(fgCode).enqueue(object : Callback<User?>{
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                Util.hideLoading()
                if (response.code() == 200){
                    saveUser(response.body()!!)
                    context.startActivity(Intent(context,MainActivity::class.java))
                    context.finish()
                }else{
                    val jsonObject = JSONObject(response.errorBody()?.string()!!)
                    val code = jsonObject.getString("code")
                    if (code == "LS_ERR_0002"){
                        val registerModel = RegisterSosmedModel(email,fullname,fgCode,imageUrl,login)
                        registerSosmed(registerModel)
                    }else {
                        logoutSosmed(login)
                        Toast.makeText(
                            context,
                            "Response code ${response.code()}/${code}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            override fun onFailure(call: Call<User?>, t: Throwable) {
                Util.hideLoading()
                t.printStackTrace()
                logoutSosmed(login)
            }
        })
    }

    fun logoutSosmed(login : String){
        if (login.toLowerCase(Locale.getDefault()) == "facebook"){
            /////// Logout Facebook
            LoginManager.getInstance().logOut()
            if (context is OpsiLogin){
                context.loadingFacebook(false)
            }
        }else{
            ///// Google Logout
            FirebaseAuth.getInstance().signOut()
            if (context is OpsiLogin){
                context.loadingGoogle(false)
            }
        }
    }

    fun registerSosmed(registerSosmedModel: RegisterSosmedModel){
        Util.showLoading(context)
        loginRepository.registerSosmed(registerSosmedModel).enqueue(object : Callback<User?>{
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                Util.hideLoading()
                when {
                    response.code() == 200 -> {
                        saveUser(response.body()!!)
                        context.startActivity(Intent(context,MainActivity::class.java))
                        context.finish()
                    }
                    response.code() == 500 -> {
                        logoutSosmed(registerSosmedModel.login_with)
                        val jsonObject = JSONObject(response.errorBody()?.string()!!)
                        val code = jsonObject.getString("code")
                        val message = jsonObject.getString("message")
                        Util.showRoundedDialog(context,message,"",false)
                    }
                    else -> {
                        logoutSosmed(registerSosmedModel.login_with)
                        Toast.makeText(
                            context,
                            "Response code ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Util.hideLoading()
                t.printStackTrace()
                logoutSosmed(registerSosmedModel.login_with)
            }
        })
    }
}