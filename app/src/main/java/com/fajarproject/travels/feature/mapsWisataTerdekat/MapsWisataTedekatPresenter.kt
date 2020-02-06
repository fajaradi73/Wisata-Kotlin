package com.fajarproject.travels.feature.mapsWisataTerdekat

import android.app.Activity
import android.location.Location
import android.util.Log
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task


/**
 * Create by Fajar Adi Prasetyo on 06/02/2020.
 */
class MapsWisataTedekatPresenter(
	view:MapsWisataTerdekatView,
	private val mFusedLocationProviderClient : FusedLocationProviderClient,
	val context : Activity,
	override var apiStores: WisataApi
):BasePresenter<MapsWisataTerdekatView,WisataApi>() {
	init {
		super.attachView(view)
	}

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
			}
		} catch (e: SecurityException) {
			Log.e("Exception: %s", e.message!!)
		}
	}
}