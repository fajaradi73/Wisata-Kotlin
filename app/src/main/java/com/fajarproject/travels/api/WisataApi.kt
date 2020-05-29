package com.fajarproject.travels.api

import com.fajarproject.travels.models.*
import com.fajarproject.travels.models.NearbyModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

interface WisataApi {

    @GET("wisata/nearbyWisata/{page}/{limit}")
    fun getNearbyWisata(
        @Header("Authorization") token : String?,
        @Path("limit") limit : Int?,
        @Path("page") page : Int?,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Observable<MutableList<NearbyModel>>

    @FormUrlEncoded
    @POST("user/favorite")
    fun saveFavorites(@Header("Authorization") token : String?,
                     @Field("id_wisata") id_wisata: Int?) : Observable<SaveFavoriteModel>

    @GET("wisata/findWisata/{page}/{limit}")
    fun getFindWisata(@Header("Authorization") token : String?,
                      @Path("limit") limit : Int?,
                      @Path("page") page : Int?,
                      @Query("type_id") type_id: String?): Observable<MutableList<WisataModel>>

    @GET("wisata/detailWisata")
    fun getDetailWisata(@Header("Authorization") token : String?,
                        @Query("id_wisata") id_wisata: Int?): Observable<WisataDetailModel>

    @GET("wisata/favoriteWisata/{page}/{limit}")
    fun getFavoriteWisata(@Header("Authorization") token : String?,
                          @Path("limit") limit : Int?,
                          @Path("page") page : Int?): Observable<MutableList<FavoriteModel>>

    @GET("wisata/popularWisata/{page}/{limit}")
    fun getPopularWisata(@Header("Authorization") token : String?,
                         @Path("limit") limit : Int?,
                         @Path("page") page : Int?): Observable<MutableList<PopularWisataModel>>

    @Multipart
    @POST("wisata/saveMultiplePicture")
    fun uploadPictureWisata(@Header("Authorization") token : String?,
                            @Part("id_wisata") idWisata : RequestBody,
                            @Part files : Array<MultipartBody.Part?> ) : Observable<PictureModel>

    @GET("wisata/nearbyMapsWisata")
    fun getNearbyMapWisata(
        @Header("Authorization") token : String?,
        @Query("radius") radius: Double?,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Observable<MutableList<NearbyModel>>

}