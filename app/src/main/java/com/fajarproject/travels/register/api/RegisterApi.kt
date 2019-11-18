package com.fajarproject.travels.register.api

import com.fajarproject.travels.register.model.RegisterModel
import com.fajarproject.travels.register.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

interface RegisterApi {

    @POST("auth/register")
    fun registerWisata(@Body registerModel: RegisterModel) : Call<RegisterResponse?>

}