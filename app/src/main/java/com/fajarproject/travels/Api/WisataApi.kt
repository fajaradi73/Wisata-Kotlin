package com.fajarproject.travels.Api

import com.fajarproject.travels.ResponseApi.*
import com.fajarproject.travels.nearbyTour.model.NearbyModel
import com.fajarproject.travels.tour.model.WisataModel
import com.fajarproject.travels.travelDetails.model.DetailWisataModel
import retrofit2.Call
import retrofit2.http.*

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

    @GET("auth/find_wisata")
    fun get_find_wisata(@Header("Authorization") token : String?,@Query("type_id") type_id: Int?): Call<List<WisataModel>?>?

    @GET("auth/detail_wisata")
    fun get_detail_wisata(@Header("Authorization") token : String?,@Query("id_wisata") id_wisata: Int?): Call<DetailWisataModel?>?

    @FormUrlEncoded
    @POST("auth/favorite")
    fun saveFavorite(@Header("Authorization") token : String?,
                     @Field("id_wisata") id_wisata: Int?) : Call<FavoriteModel?>?

    @GET("auth/table_penginapan")
    fun get_penginapan(): Call<GetPenginapan?>?

    @GET("auth/detail_penginapan")
    fun get_detail_penginapan(@Header("Authorization") token : String?,@Query("id_penginapan") id_penginapan: String?): Call<DetailPenginapan?>?
}