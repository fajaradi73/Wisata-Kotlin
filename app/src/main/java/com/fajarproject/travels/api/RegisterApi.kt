package com.fajarproject.travels.api

import com.fajarproject.travels.models.RegisterSubmitModel
import com.fajarproject.travels.models.SaveModel
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

interface RegisterApi {

    @POST("register")
    fun registerWisata(@Body registerSubmitModel: RegisterSubmitModel) : Observable<SaveModel>

}