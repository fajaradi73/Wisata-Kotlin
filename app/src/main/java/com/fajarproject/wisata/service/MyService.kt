package com.fajarproject.wisata.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.model.LatLng


/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class MyService : Service() {
    private var mLocationManager: LocationManager? = null
    internal var latLng: LatLng? = null

    private inner class LocationListener(provider: String?) :
        android.location.LocationListener {
        internal var mLastLocation: Location = Location(provider)
        override fun onLocationChanged(location: Location) {
            val `in` = Intent()
            `in`.putExtra("latitude", location.latitude)
            `in`.putExtra("longitude", location.longitude)
            `in`.action = "NOW"
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(`in`)
            mLastLocation.set(location)
            latLng = LatLng(
                location.latitude,
                location.longitude
            )
        }

        override fun onProviderDisabled(provider: String?) {
            val `in` = Intent()
            `in`.putExtra("kode_gps", "false")
            `in`.action = "NOW"
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(`in`)
        }

        override fun onProviderEnabled(provider: String?) {
            val `in` = Intent()
            `in`.putExtra("kode_gps", "true")
            if (latLng != null) {
                `in`.putExtra("latitude", latLng!!.latitude)
                `in`.putExtra("longitude", latLng!!.longitude)
            }
            `in`.action = "NOW"
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(`in`)
        }

        override fun onStatusChanged(
            provider: String?,
            status: Int,
            extras: Bundle?
        ) {
        }

    }

    private var mLocationListeners =
        arrayOf(
            LocationListener(LocationManager.GPS_PROVIDER),
            LocationListener(LocationManager.NETWORK_PROVIDER)
        )

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        initializeLocationManager()
        try {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                mLocationListeners[1]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }
        try {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                mLocationListeners[0]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "gps provider does not exist " + ex.message)
        }
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        if (mLocationManager != null) {
            for (mLocationListener in mLocationListeners) {
                try {
                    mLocationManager!!.removeUpdates(mLocationListener)
                } catch (ex: Exception) {
                    Log.i(
                        TAG,
                        "fail to remove location listners, ignore",
                        ex
                    )
                }
            }
        }
    }

    private fun initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    companion object {
        private const val TAG = "Fajar"
        private const val LOCATION_INTERVAL = 1000
        private const val LOCATION_DISTANCE = 10f
    }
}