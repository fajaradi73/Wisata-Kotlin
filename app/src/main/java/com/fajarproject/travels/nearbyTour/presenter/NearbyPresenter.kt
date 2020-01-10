package com.fajarproject.travels.nearbyTour.presenter

import android.view.View
import android.widget.Toast
import com.fajarproject.travels.models.SaveFavoriteModel
import com.fajarproject.travels.repository.WisataRepository
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.nearbyTour.activity.NearbyActivity
import com.fajarproject.travels.nearbyTour.model.NearbyModel
import com.fajarproject.travels.util.Util
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class NearbyPresenter(val context: NearbyActivity) {
    private val wisataRepository: WisataRepository = WisataRepository()
    private val user : UserModel = Util.getUserToken(context)

    fun getNearby(latitude : Double?, longitude : Double?){
        context.showShimmer(true)
        wisataRepository.getNearbyWisata(user,latitude,longitude)?.enqueue(object : Callback<List<NearbyModel>?>{
            override fun onResponse(call: Call<List<NearbyModel>?>, response: Response<List<NearbyModel>?>) {
                context.showShimmer(false)
                if (response.isSuccessful && response.code() == 200 ){
                    if (response.body()!!.isNotEmpty()){
                        context.setRecycleView(response.body()!!)
                    }
                }else if (response.code() == 401){
                    Util.sessionExpired(context)
                }
            }

            override fun onFailure(call: Call<List<NearbyModel>?>, t: Throwable) {
                t.printStackTrace()
                context.showShimmer(false)
            }
        })
    }

    fun saveFavorite(id_wisata : Int?,view: View?){
        wisataRepository.postFavorite(user,id_wisata)?.enqueue(object : Callback<SaveFavoriteModel?>{
            override fun onResponse(call: Call<SaveFavoriteModel?>, response: Response<SaveFavoriteModel?>) {
                if (response.isSuccessful && response.code() == 200){
                    val message = response.body()!!.message
                    Snackbar.make(view!!,message, Snackbar.LENGTH_LONG).show()
                }else if (response.code() == 500){
                    val jsonObject = JSONObject(response.errorBody()?.string()!!)
                    val message = jsonObject.getString("message")
                    Snackbar.make(view!!,message, Snackbar.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,"Response code ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SaveFavoriteModel?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}