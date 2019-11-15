package com.fajarproject.wisata.login.api

import com.fajarproject.wisata.login.model.LoginModel
import com.fajarproject.wisata.login.model.RegisterSosmedModel
import com.fajarproject.wisata.login.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

interface LoginAPI {

    @POST("auth/login")
    fun loginPost(@Body login : LoginModel?): Call<User?>

    @FormUrlEncoded
    @POST("auth/login_sosmed")
    fun loginSosmedPost(@Field ("fg_code") fgCode : String?): Call<User?>

    @POST("auth/register_sosmed")
    fun registerSosmed(@Body registerSosmedModel: RegisterSosmedModel?) : Call<User?>
}