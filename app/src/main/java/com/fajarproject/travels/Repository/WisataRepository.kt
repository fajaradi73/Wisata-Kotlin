package com.fajarproject.travels.Repository

import com.fajarproject.travels.Api.WisataApi
import com.fajarproject.travels.ResponseApi.DetailPenginapan
import com.fajarproject.travels.ResponseApi.DetailWisataResponse
import com.fajarproject.travels.ResponseApi.GetPenginapan
import com.fajarproject.travels.ResponseApi.TourModel
import com.fajarproject.travels.util.Util
import retrofit2.Call

class WisataRepository {

    fun getNearbyWisata(latitude: String?, longitude: String?): Call<TourModel?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_nearby_wisata(latitude,longitude)
    }
    fun getFindWisata(type_id: String?): Call<TourModel?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_find_wisata(type_id)
    }
    fun getDetailWisata(id_wisata: String?): Call<DetailWisataResponse?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_detail_wisata(id_wisata)
    }
    fun getPenginapan(): Call<GetPenginapan?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_penginapan()
    }
    fun getDetailPenginapan(id_penginapan: String?): Call<DetailPenginapan?>? {
        val retrofit = Util.getDefaultRetrofit()
        val wisataApi: WisataApi = retrofit!!.create(WisataApi::class.java)
        return wisataApi.get_detail_penginapan(id_penginapan)
    }
}