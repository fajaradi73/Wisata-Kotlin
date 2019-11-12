package com.fajarproject.wisata.login.presenter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginResult
import com.fajarproject.wisata.createAccount.CreateAccount
import com.fajarproject.wisata.login.activity.OpsiLogin
import com.fajarproject.wisata.util.Constant
import com.google.firebase.auth.FirebaseUser
import org.json.JSONException
import org.json.JSONObject

class LoginPresenter(val context : OpsiLogin) {

    fun setUserGoogle(user: FirebaseUser){
        val email       : String?  = user.email
        val fullname    : String?  = user.displayName
        val id          : String?  = user.uid
        val image       : String?  = user.photoUrl.toString()
        Log.d("usergoogle", "$email/$fullname/$id/$image")
        val intent = Intent(context, CreateAccount::class.java)
        intent.putExtra(Constant.typeLogin,"Sosmed")
        intent.putExtra(Constant.Name,fullname)
        intent.putExtra(Constant.Email,email)
        context.startActivity(intent)
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
                    Log.d("userfacebook", "$email/$fullname/$id/")
                    val intent = Intent(context, CreateAccount::class.java)
                    intent.putExtra(Constant.typeLogin,"Sosmed")
                    intent.putExtra(Constant.Name,fullname)
                    intent.putExtra(Constant.Email,email)
                    context.startActivity(intent)
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
}