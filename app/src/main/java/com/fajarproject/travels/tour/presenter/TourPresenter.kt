package com.fajarproject.travels.tour.presenter

import android.util.Log
import android.view.View
import android.widget.Toast
import com.fajarproject.travels.ResponseApi.FavoriteModel
import com.fajarproject.travels.repository.WisataRepository
import com.fajarproject.travels.login.model.User
import com.fajarproject.travels.tour.activity.WisataActivity
import com.fajarproject.travels.tour.model.WisataModel
import com.fajarproject.travels.util.Util
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TourPresenter(val context : WisataActivity) {
    private val wisataRepository : WisataRepository = WisataRepository()
    private val user : User = Util.getUserToken(context)

    fun getTour(type_id : Int?){
        context.showShimmer(true)
        wisataRepository.getFindWisata(user,type_id)?.enqueue(object : Callback<List<WisataModel>?>{
            override fun onResponse(call: Call<List<WisataModel>?>, response: Response<List<WisataModel>?>) {
                context.showShimmer(false)
                if (response.isSuccessful && response.code() == 200){
                    if (response.body()!!.isNotEmpty()){
                        context.showData(true)
                        context.setRecycleView(response.body()!!)
                    }else{
                        context.showData(false)
                    }
                }else if (response.code() == 401){
                    Util.sessionExpired(context)
                }else{
                    context.showData(false)
                }
            }

            override fun onFailure(call: Call<List<WisataModel>?>, t: Throwable) {
                t.printStackTrace()
                context.showShimmer(false)
                context.showData(false)
            }
        })
    }

    fun saveFavorite(id_wisata : Int?,view: View?){
        wisataRepository.postFavorite(user,id_wisata)?.enqueue(object : Callback<FavoriteModel?>{
            override fun onResponse(call: Call<FavoriteModel?>, response: Response<FavoriteModel?>) {
                if (response.isSuccessful && response.code() == 200){
                    val message = response.body()!!.message
                    Snackbar.make(view!!,message,Snackbar.LENGTH_LONG).show()
                }else if (response.code() == 500){
                    val jsonObject = JSONObject(response.errorBody()?.string()!!)
                    val message = jsonObject.getString("message")
                    Snackbar.make(view!!,message,Snackbar.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,"Response code ${response.code()}",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<FavoriteModel?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}