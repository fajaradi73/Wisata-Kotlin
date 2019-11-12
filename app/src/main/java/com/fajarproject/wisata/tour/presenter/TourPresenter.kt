package com.fajarproject.wisata.tour.presenter

import com.fajarproject.wisata.Repository.WisataRepository
import com.fajarproject.wisata.ResponseApi.TourModel
import com.fajarproject.wisata.tour.activity.WisataActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TourPresenter(val context : WisataActivity) {
    private val wisataRepository : WisataRepository = WisataRepository()

    fun getTour(type_id : String?){
        context.showShimmer(true)
        wisataRepository.getFindWisata(type_id)?.enqueue(object : Callback<TourModel?>{
            override fun onResponse(call: Call<TourModel?>, response: Response<TourModel?>) {
                context.showShimmer(false)
                if (response.isSuccessful && response.code() == 200){
                    if (response.body()!!.data.isNotEmpty()){
                        context.showData(true)
                        context.setRecycleView(response.body()!!.data)
                    }else{
                        context.showData(false)
                    }
                }else{
                    context.showData(false)
                }
            }

            override fun onFailure(call: Call<TourModel?>, t: Throwable) {
                t.printStackTrace()
                context.showShimmer(false)
                context.showData(false)
            }


        })
    }
}