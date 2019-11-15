package com.fajarproject.wisata.register.api

import com.fajarproject.wisata.register.model.RegisterModel
import com.fajarproject.wisata.register.model.RegisterResponse
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