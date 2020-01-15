package com.fajarproject.travels.api

import com.fajarproject.travels.models.UlasanModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import rx.Observable

/**
 * Create by Fajar Adi Prasetyo on 14/01/2020.
 */
interface UlasanApi {

    @GET("auth/ulasan")
    fun getUlasan(@Header("Authorization") token : String?,
                  @Query("id_wisata") id_wisata: Int?): Observable<UlasanModel>
}