package com.fajarproject.travels.repository

import com.fajarproject.travels.Api.WisataApi
import com.fajarproject.travels.ResponseApi.FavoriteModel
import com.fajarproject.travels.login.model.User
import com.fajarproject.travels.nearbyTour.model.NearbyModel
import com.fajarproject.travels.tour.model.WisataModel
import com.fajarproject.travels.travelDetails.model.DetailWisataModel
import com.fajarproject.travels.util.Util
import retrofit2.Call

class WisataRepository {

    fun getNearbyWisata(user: User?,latitude: Double?, longitude: Double?): Call<List<NearbyModel>?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_nearby_wisata(user!!.token,latitude,longitude)
    }
    fun getFindWisata(user: User?,type_id: Int?): Call<List<WisataModel>?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_find_wisata(user!!.token,type_id)
    }
    fun getDetailWisata(user: User?,id_wisata: Int?): Call<DetailWisataModel?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_detail_wisata(user!!.token,id_wisata)
    }
    fun postFavorite(user : User?,id_wisata: Int?) : Call<FavoriteModel?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.saveFavorite(user!!.token,id_wisata)
    }
}