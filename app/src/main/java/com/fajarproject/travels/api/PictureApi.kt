package com.fajarproject.travels.api

import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.models.SavePictureWisataModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import rx.Observable

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
interface PictureApi {

    @GET("auth/picture")
    fun getPictureWisata(@Header("Authorization") token : String?,
                        @Query("id_wisata") id_wisata: Int?): Observable<List<PictureItem>>

    @Multipart
    @POST("auth/saveMultiplePicture")
    fun uploadPictureWisata(@Header("Authorization") token : String?,
                            @Part("id_wisata") idWisata : RequestBody,
                            @Part files : Array<MultipartBody.Part?> ) : Observable<SavePictureWisataModel>
}