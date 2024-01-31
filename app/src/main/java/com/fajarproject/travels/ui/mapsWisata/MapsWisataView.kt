package com.fajarproject.travels.ui.mapsWisata

import android.location.Location
import com.akexorcist.googledirection.model.Direction

interface MapsWisataView {
    fun init()

    fun loadingDirection(isShow : Boolean)

    fun showIntAds()

    fun showSnackbar(message: String)

    fun showSnackbarWithAction(message: String)

    fun showDirection(direction: Direction?)

    fun updateLocationUI()

    fun showDeviceLocation(location: Location)

    fun getLocationPermission()
}