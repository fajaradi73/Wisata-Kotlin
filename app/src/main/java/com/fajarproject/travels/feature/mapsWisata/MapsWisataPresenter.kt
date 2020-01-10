package com.fajarproject.travels.feature.mapsWisata

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration
import com.akexorcist.googledirection.constant.TransitMode
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.constant.Unit
import com.akexorcist.googledirection.model.Direction
import com.fajarproject.travels.R
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MapsWisataPresenter(
    view: MapsWisataView,
    val context: Activity,
    private val mFusedLocationProviderClient : FusedLocationProviderClient,
    override var apiStores: WisataApi)
    : BasePresenter<MapsWisataView,WisataApi>() {

    init {
        super.attachView(view)
    }
    fun setDirection(latLngCurrent : LatLng,latLngTravels : LatLng){
        GoogleDirectionConfiguration.getInstance().isLogEnabled = true
        val key : String? = context.resources.getString(R.string.google_maps_key)
        view!!.loadingDirection(true)
        GoogleDirection.withServerKey(key)
            .from(latLngCurrent)
            .to(latLngTravels)
            .transportMode(TransportMode.DRIVING)
            .transitMode(TransitMode.BUS)
            .unit(Unit.METRIC)
            .execute(object : DirectionCallback {
                override fun onDirectionSuccess(direction: Direction?, rawBody: String?) {
                    view!!.loadingDirection(false)
                    if (direction!!.isOK) {
                        view!!.showDirection(direction)
                    } else if (direction.status == "ZERO_RESULTS") {
                        Log.e("gagal", direction.status + "")
                        view!!.showSnackbar(
                            "Harap untuk mengaktifkan lokasi untuk menggunakan direction"
                        )
                    }
                }
                override fun onDirectionFailure(t: Throwable?) {
                    view!!.loadingDirection(false)
                    view!!.showSnackbar("Gagal menampilkan route ke lokasi")
                }
            })
    }

    fun setDeviceLocation(mLocationPermissionGranted : Boolean){
        try {
            if (mLocationPermissionGranted) {
                val locationResult: Task<Location> = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(context
                ) { task ->
                    if (task.isSuccessful) { // Set the map's camera position to the current location of the device.
                        view!!.showDeviceLocation(task.result!!)
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