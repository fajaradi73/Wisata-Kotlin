package com.fajarproject.travels.ui.mapsWisataTerdekat

import android.location.Location
import com.fajarproject.travels.models.NearbyModel


/**
 * Create by Fajar Adi Prasetyo on 06/02/2020.
 */
interface MapsWisataTerdekatView {

	fun init()

	fun showLoading()

	fun hideLoading()

	fun updateLocationUI()

	fun showDeviceLocation(location: Location)

	fun getLocationPermission()

	fun getDataSuccess(model : MutableList<NearbyModel>)

	fun getDataFailed(msg : String)

	fun calculateRadius(latitude : Double, longitude : Double)
}