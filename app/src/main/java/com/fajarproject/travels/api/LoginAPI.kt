package com.fajarproject.travels.api

import com.fajarproject.travels.models.LoginModel
import com.fajarproject.travels.models.RegisterSosmedModel
import com.fajarproject.travels.models.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

interface LoginAPI {

    @POST("auth/login")
    fun loginPosts(@Body login : LoginModel?): Observable<UserModel>

    @FormUrlEncoded
    @POST("auth/login_sosmed")
    fun loginSosmedPosts(@Field ("fg_code") fgCode : String?): Observable<UserModel>

    @POST("auth/register_sosmed")
    fun registerSosmeds(@Body registerSosmedModel: RegisterSosmedModel?) : Observable<UserModel>
}