package com.fajarproject.travels.travelDetails.presenter

import com.fajarproject.travels.Repository.WisataRepository
import com.fajarproject.travels.ResponseApi.DetailWisataResponse
import com.fajarproject.travels.travelDetails.TravelDetails
import com.fajarproject.travels.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelPresenter(private val context: TravelDetails) {
    private val wisataRepository : WisataRepository = WisataRepository()

    fun getTravelDetails(id_wisata : String?){
        Util.showLoading(context)
        wisataRepository.getDetailWisata(id_wisata)?.enqueue(object : Callback<DetailWisataResponse?>{
            override fun onResponse(call: Call<DetailWisataResponse?>, response: Response<DetailWisataResponse?>) {
                Util.hideLoading()
                if (response.isSuccessful && response.code() == 200){
                    if (response.body()?.status == 1){
                        context.setDetailWisata(response.body()?.data!!)
                    }
                }
            }

            override fun onFailure(call: Call<DetailWisataResponse?>, t: Throwable) {
                t.printStackTrace()
                Util.hideLoading()
            }
        })
    }
}