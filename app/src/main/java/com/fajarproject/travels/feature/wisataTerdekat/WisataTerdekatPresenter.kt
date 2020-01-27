package com.fajarproject.travels.feature.wisataTerdekat

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.View
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.feature.detailWisata.DetailWisataActivity
import com.fajarproject.travels.models.SaveFavoriteModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.models.NearbyModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 12/01/20.
 */

class WisataTerdekatPresenter(view: WisataTerdekatView, val context: Activity,
                              private val mFusedLocationProviderClient : FusedLocationProviderClient,
                              override var apiStores: WisataApi
) :BasePresenter<WisataTerdekatView,WisataApi>() {
    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun setDeviceLocation(mLocationPermissionGranted : Boolean){
        try {
            if (mLocationPermissionGranted) {
                val locationResult: Task<Location> = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(context
                ) { task ->
                    if (task.isSuccessful) { // Set the map's camera position to the current location of the device.
                        view?.showDeviceLocation(task.result!!)
                    } else {
                        Log.e("Exception: %s", task.exception.toString())
                    }
                }
            }else{
                view?.getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }
    fun saveFavorite(id_wisata : Int?,likes: View){
        likes.isEnabled = false
        addSubscribe(apiStores.saveFavorites(user.token,id_wisata),object : NetworkCallback<SaveFavoriteModel>(){
            override fun onSuccess(model: SaveFavoriteModel) {
                view?.showMessageFavorite(model.message,likes)
            }

            override fun onFailure(message: String?,code : Int?,jsonObject: JSONObject?) {
                when (code) {
                    500 -> {
                        val messageError = jsonObject!!.getString("message")
                        view?.showMessageFavorite(messageError,likes)
                    }
                    401 -> {
                        Util.sessionExpired(context)
                    }
                    else -> {
                        view?.getDataFailLike(message!!)
                    }
                }
            }

            override fun onFinish() {
                likes.isEnabled = true
            }

        })
    }
    fun getNearbyWisata(latitude : Double,longitude : Double,limit : Int, page : Int){
        if (page == 0) {
            view?.showLoading()
        }
        addSubscribe(apiStores.getNearbyWisata(user.token,latitude,longitude,limit,page),object : NetworkCallback<MutableList<NearbyModel>>(){
            override fun onSuccess(model: MutableList<NearbyModel>) {
                view?.getDataSuccess(model)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else{
                    view?.getDataFail(message!!)
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }

        })
    }
    fun getItem(data: NearbyModel){
        val intent = Intent(context, DetailWisataActivity::class.java)
        intent.putExtra(Constant.IdWisata,data.idWisata!!)
        view!!.moveToDetail(intent)
    }
}