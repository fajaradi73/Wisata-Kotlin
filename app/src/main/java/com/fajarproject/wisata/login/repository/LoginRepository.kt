package com.fajarproject.wisata.login.repository

import com.fajarproject.wisata.login.api.LoginAPI
import com.fajarproject.wisata.login.model.LoginModel
import com.fajarproject.wisata.login.model.RegisterSosmedModel
import com.fajarproject.wisata.login.model.User
import com.fajarproject.wisata.util.Util
import retrofit2.Call

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

class LoginRepository {

    fun loginWisata(loginModel: LoginModel):Call<User?>{
        val retrofit  = Util.getDefaultRetrofit()
        val loginAPI = retrofit!!.create(LoginAPI::class.java)
        return loginAPI.loginPost(loginModel)
    }
    fun loginSosmed(fgCode : String) : Call<User?>{
        val retrofit = Util.getDefaultRetrofit()
        val loginAPI = retrofit!!.create(LoginAPI::class.java)
        return loginAPI.loginSosmedPost(fgCode)
    }
    fun registerSosmed(registerSosmedModel: RegisterSosmedModel): Call<User?>{
        val retrofit = Util.getDefaultRetrofit()
        val loginAPI = retrofit!!.create(LoginAPI::class.java)
        return loginAPI.registerSosmed(registerSosmedModel)
    }
}