package com.fajarproject.travels.Api

import com.fajarproject.travels.ResponseApi.DetailPenginapan
import com.fajarproject.travels.ResponseApi.DetailWisataResponse
import com.fajarproject.travels.ResponseApi.GetPenginapan
import com.fajarproject.travels.ResponseApi.TourModel
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

interface WisataApi {

    @FormUrlEncoded
    @POST("auth/nearby_wisata")
    fun get_nearby_wisata(
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Call<TourModel?>?

    @GET("auth/find_wisata")
    fun get_find_wisata(@Query("type_id") type_id: String?): Call<TourModel?>?

    @GET("auth/detail_wisata")
    fun get_detail_wisata(@Query("id_wisata") id_wisata: String?): Call<DetailWisataResponse?>?

    @GET("auth/table_penginapan")
    fun get_penginapan(): Call<GetPenginapan?>?

    @GET("auth/detail_penginapan")
    fun get_detail_penginapan(@Query("id_penginapan") id_penginapan: String?): Call<DetailPenginapan?>?
}