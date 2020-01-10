package com.fajarproject.travels.repository

import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.models.SaveFavoriteModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.nearbyTour.model.NearbyModel
import com.fajarproject.travels.util.Util
import retrofit2.Call

class WisataRepository {

    fun getNearbyWisata(user: UserModel?, latitude: Double?, longitude: Double?): Call<List<NearbyModel>?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_nearby_wisata(user!!.token,latitude,longitude)
    }
    fun postFavorite(user : UserModel?, id_wisata: Int?) : Call<SaveFavoriteModel?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.saveFavorite(user!!.token,id_wisata)
    }
}