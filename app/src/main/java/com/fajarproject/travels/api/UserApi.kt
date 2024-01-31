package com.fajarproject.travels.api

import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.models.PictureModel
import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.models.request.EditProfilRequest
import com.fajarproject.travels.models.request.PasswordRequest
import okhttp3.MultipartBody
import retrofit2.http.*
import rx.Observable

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

interface UserApi {

    @GET("user/")
    fun getProfile(@Header("Authorization") token : String?): Observable<ProfileModel>

    @Multipart
    @POST("user/update_picture")
    fun uploadPicture(@Header("Authorization") token : String?,@Part picture : MultipartBody.Part) : Observable<PictureModel>

    @Multipart
    @POST("user/update_picture_background")
    fun uploadPictureBackground(@Header("Authorization") token : String?,@Part picture : MultipartBody.Part) : Observable<PictureModel>

    @PUT("user/update_profile")
    fun updateProfil(@Header("Authorization") token : String?,@Body request : EditProfilRequest) : Observable<SaveModel>

    @POST("user/update_password")
    fun changePassword(@Header("Authorization") token : String?,@Body request : PasswordRequest) : Observable<SaveModel>
}