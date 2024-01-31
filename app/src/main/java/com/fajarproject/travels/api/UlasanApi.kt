package com.fajarproject.travels.api

import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.models.RattingModel
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.models.request.CreateUlasanRequest
import retrofit2.http.*
import rx.Observable

/**
 * Create by Fajar Adi Prasetyo on 14/01/2020.
 */
interface UlasanApi {

    @GET("wisata/ulasanWisata/{page}/{limit}")
    fun getUlasan(@Header("Authorization") token : String?,
                  @Path("limit") limit : Int?,
                  @Path("page") page : Int?,
                  @Query("id_wisata") id_wisata: String?): Observable<MutableList<UlasanItem>>

    @GET("wisata/rattingWisata")
    fun getRatting(@Header("Authorization") token : String?,
                  @Query("id_wisata") id_wisata: String?): Observable<RattingModel>

    @POST("user/saveUlasan")
    fun saveUlasan(@Header("Authorization") token : String?,@Body request: CreateUlasanRequest) : Observable<SaveModel>
}