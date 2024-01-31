package com.fajarproject.travels.api

import com.fajarproject.travels.models.*
import com.fajarproject.travels.models.request.SaveFavoriteRequest
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
		@Header("Authorization") token: String?,
		@Path("limit") limit: Int?,
		@Path("page") page: Int?,
		@Query("latitude") latitude: Double?,
		@Query("longitude") longitude: Double?
	): Observable<MutableList<NearbyModel>>

	@POST("user/save_favorite")
	fun saveFavorites(
		@Header("Authorization") token: String?,
		@Body request: SaveFavoriteRequest
	): Observable<SaveFavoriteModel>

	@GET("wisata/findWisata/{page}/{limit}")
	fun getFindWisata(
		@Header("Authorization") token: String?,
		@Path("limit") limit: Int?,
		@Path("page") page: Int?,
		@Query("type_id") type_id: String?
	): Observable<MutableList<WisataModel>>

	@GET("wisata/detailWisata")
	fun getDetailWisata(
		@Header("Authorization") token: String?,
		@Query("id_wisata") id_wisata: String?
	): Observable<WisataDetailModel>

	@GET("wisata/favoriteWisata/{page}/{limit}")
	fun getFavoriteWisata(
		@Header("Authorization") token: String?,
		@Path("limit") limit: Int?,
		@Path("page") page: Int?
	): Observable<MutableList<FavoriteModel>>

	@GET("wisata/popularWisata/{page}/{limit}")
	fun getPopularWisata(
		@Header("Authorization") token: String?,
		@Path("limit") limit: Int?,
		@Path("page") page: Int?
	): Observable<MutableList<PopularWisataModel>>

	@Multipart
	@POST("wisata/saveMultiplePicture/{id_wisata}")
	fun uploadPictureWisata(
		@Header("Authorization") token: String?,
		@Path("id_wisata") idWisata: String?,
		@Part files: Array<MultipartBody.Part?>
	): Observable<PictureModel>

	@GET("wisata/nearbyMapsWisata")
	fun getNearbyMapWisata(
		@Header("Authorization") token: String?,
		@Query("radius") radius: Double?,
		@Query("latitude") latitude: Double?,
		@Query("longitude") longitude: Double?
	): Observable<MutableList<NearbyModel>>

}