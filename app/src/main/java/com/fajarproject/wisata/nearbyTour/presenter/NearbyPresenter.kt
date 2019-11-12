package com.fajarproject.wisata.nearbyTour.presenter

import com.fajarproject.wisata.Repository.WisataRepository
import com.fajarproject.wisata.ResponseApi.TourModel
import com.fajarproject.wisata.nearbyTour.activity.NearbyActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class NearbyPresenter(val context: NearbyActivity) {
    private val wisataRepository: WisataRepository = WisataRepository()

    fun getNearby(latitude : String?, longitude : String?){
        context.showShimmer(true)
        wisataRepository.getNearbyWisata(latitude,longitude)?.enqueue(object : Callback<TourModel?>{
            override fun onResponse(call: Call<TourModel?>, response: Response<TourModel?>) {
                context.showShimmer(false)
                if (response.isSuccessful && response.code() == 200 ){
                    if (response.body()!!.data.isNotEmpty()){
                        context.setRecycleView(response.body()!!.data)
                    }
                }
            }

            override fun onFailure(call: Call<TourModel?>, t: Throwable) {
                t.printStackTrace()
                context.showShimmer(false)
            }
        })

    }

}