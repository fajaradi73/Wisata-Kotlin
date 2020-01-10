package com.fajarproject.travels.api

import com.fajarproject.travels.models.ProfileModel
import retrofit2.http.GET
import retrofit2.http.Header
import rx.Observable

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

interface UserApi {

    @GET("auth/profile")
    fun getProfile(@Header("Authorization") token : String?): Observable<ProfileModel>

}