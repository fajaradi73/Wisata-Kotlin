package com.fajarproject.travels.travelDetails.presenter

import android.view.View
import android.widget.Toast
import com.fajarproject.travels.ResponseApi.FavoriteModel
import com.fajarproject.travels.repository.WisataRepository
import com.fajarproject.travels.login.model.User
import com.fajarproject.travels.travelDetails.TravelDetails
import com.fajarproject.travels.travelDetails.model.DetailWisataModel
import com.fajarproject.travels.util.Util
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelPresenter(private val context: TravelDetails) {
    private val wisataRepository : WisataRepository = WisataRepository()
    private val user : User = Util.getUserToken(context)

    fun getTravelDetails(id_wisata : Int?){
        Util.showLoading(context)
        wisataRepository.getDetailWisata(user,id_wisata)?.enqueue(object : Callback<DetailWisataModel?>{
            override fun onResponse(call: Call<DetailWisataModel?>, response: Response<DetailWisataModel?>) {
                Util.hideLoading()
                if (response.isSuccessful && response.code() == 200){
                    context.setDetailWisata(response.body()!!)
                }else if (response.code() == 401){
                    Util.sessionExpired(context)
                }else{
                    Toast.makeText(context,"Response Code ${response.code()}",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<DetailWisataModel?>, t: Throwable) {
                t.printStackTrace()
                Util.hideLoading()
            }
        })
    }
    fun saveFavorite(id_wisata : Int?,view: View?){
        wisataRepository.postFavorite(user,id_wisata)?.enqueue(object : Callback<FavoriteModel?>{
            override fun onResponse(call: Call<FavoriteModel?>, response: Response<FavoriteModel?>) {
                if (response.isSuccessful && response.code() == 200){
                    val message = response.body()!!.message
                    Snackbar.make(view!!,message, Snackbar.LENGTH_LONG).show()
                }else if (response.code() == 500){
                    val jsonObject = JSONObject(response.errorBody()?.string()!!)
                    val message = jsonObject.getString("message")
                    Snackbar.make(view!!,message, Snackbar.LENGTH_LONG).show()
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