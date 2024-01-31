package com.fajarproject.travels.ui.mapsWisataTerdekat

import android.app.Activity
import android.location.Location
import android.util.Log
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.NearbyModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import org.json.JSONObject


/**
 * Create by Fajar Adi Prasetyo on 06/02/2020.
 */
class MapsWisataTerdekatPresenter(
	view:MapsWisataTerdekatView,
	private val mFusedLocationProviderClient : FusedLocationProviderClient,
	val context : Activity,
	override var apiStores: WisataApi
):BasePresenter<MapsWisataTerdekatView,WisataApi>() {
	init {
		super.attachView(view)
	}
	private val user : UserModel = Util.getUserToken(context)

	fun setDeviceLocation(mLocationPermissionGranted : Boolean){
		view?.showLoading()
		try {
			if (mLocationPermissionGranted) {
				val locationResult: Task<Location> = mFusedLocationProviderClient.lastLocation
				locationResult.addOnCompleteListener(context
				) { task ->
					view?.hideLoading()
					if (task.isSuccessful) { // Set the map's camera position to the current location of the device.
						view?.showDeviceLocation(task.result!!)
					} else {
						Log.e("Exception: %s", task.exception.toString())
					}
				}
			}
		} catch (e: SecurityException) {
			view?.hideLoading()
			Log.e("Exception: %s", e.message!!)
		}
	}
	fun getNearbyMaps(latitude : Double,longitude : Double,radius : Double){
		view?.showLoading()
		addSubscribe(apiStores.getNearbyMapWisata("Bearer " + user.token,radius,latitude, longitude),object : NetworkCallback<MutableList<NearbyModel>>(){
			override fun onSuccess(model: MutableList<NearbyModel>) {
				view?.getDataSuccess(model)
			}

			override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
				if (code == 401){
					Util.sessionExpired(context)
				}else{
					view?.getDataFailed(message!!)
				}
			}

			override fun onFinish() {
				view?.hideLoading()
			}

		})
	}
}