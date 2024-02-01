package com.fajarproject.travels.api

import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.models.PictureModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
interface PictureApi {

    @GET("wisata/pictureWisata")
    fun getPictureWisata(@Header("Authorization") token : String?,
                        @Query("id_wisata") id_wisata: String?): Observable<List<PictureItem>>

    @Multipart
    @POST("wisata/saveMultiplePicture/{id_wisata}")
    fun uploadPictureWisata(
        @Header("Authorization") token: String?,
        @Path("id_wisata") idWisata: String?,
        @Part files: Array<MultipartBody.Part?>
    ): Observable<PictureModel>
}