package com.fajarproject.travels.login.repository

import com.fajarproject.travels.login.api.LoginAPI
import com.fajarproject.travels.login.model.LoginModel
import com.fajarproject.travels.login.model.RegisterSosmedModel
import com.fajarproject.travels.login.model.User
import com.fajarproject.travels.util.Util
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