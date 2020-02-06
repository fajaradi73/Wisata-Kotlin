package com.fajarproject.travels.feature.mapsWisataTerdekat

import android.location.Location


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

}