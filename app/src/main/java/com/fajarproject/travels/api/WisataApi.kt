package com.fajarproject.travels.api

import com.fajarproject.travels.models.*
import com.fajarproject.travels.nearbyTour.model.NearbyModel
import retrofit2.Call
import retrofit2.http.*
import rx.Observable

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

interface WisataApi {

    @GET("auth/nearby_wisata")
    fun get_nearby_wisata(
        @Header("Authorization") token : String?,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Call<List<NearbyModel>?>?

    @FormUrlEncoded
    @POST("auth/favorite")
    fun saveFavorite(@Header("Authorization") token : String?,
                     @Field("id_wisata") id_wisata: Int?) : Call<SaveFavoriteModel?>?

    @FormUrlEncoded
    @POST("auth/favorite")
    fun saveFavorites(@Header("Authorization") token : String?,
                     @Field("id_wisata") id_wisata: Int?) : Observable<SaveFavoriteModel>

    @GET("auth/find_wisata")
    fun getFindWisata(@Header("Authorization") token : String?,
                      @Query("type_id") type_id: String?): Observable<List<WisataModel>>

    @GET("auth/detail_wisata")
    fun getDetailWisata(@Header("Authorization") token : String?,
                        @Query("id_wisata") id_wisata: Int?): Observable<DetailWisataModel>

    @GET("auth/favorite_wisata")
    fun getFavoriteWisata(@Header("Authorization") token : String?): Observable<List<FavoriteModel>>

    @GET("auth/popular_wisata")
    fun getPopularWisata(@Header("Authorization") token : String?): Observable<List<PopularWisataModel>>

}