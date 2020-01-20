package com.fajarproject.travels.api

import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.models.SavePictureWisataModel
import okhttp3.MultipartBody
import retrofit2.http.*
import rx.Observable

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

interface UserApi {

    @GET("auth/profile")
    fun getProfile(@Header("Authorization") token : String?): Observable<ProfileModel>

    @Multipart
    @POST("auth/update_picture")
    fun uploadPicture(@Header("Authorization") token : String?,@Part picture : MultipartBody.Part) : Observable<SavePictureWisataModel>
}