package com.fajarproject.travels.api

import com.fajarproject.travels.models.RegisterSubmitModel
import com.fajarproject.travels.models.RegisterModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

interface RegisterApi {

    @POST("auth/register")
    fun registerWisata(@Body registerSubmitModel: RegisterSubmitModel) : Call<RegisterModel?>

    @POST("auth/register")
    fun registerWisatas(@Body registerSubmitModel: RegisterSubmitModel) : Observable<RegisterModel>

}