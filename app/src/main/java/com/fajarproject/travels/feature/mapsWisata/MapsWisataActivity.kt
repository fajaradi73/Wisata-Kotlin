package com.fajarproject.travels.feature.mapsWisata

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Leg
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.fajarproject.travels.R
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_maps_travels.*

/**
 * Create by Fajar Adi Prasetyo on 06/02/2020.
 */

class MapsWisataActivity : MvpActivity<MapsWisataPresenter>(),MapsWisataView, OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    private var mapsTravels : GoogleMap? = null

    private var currentLatitude     : Double? = null
    private var currentLongitude    : Double? = null
    private var latitudeTravels     : Double? = null
    private var longitudeTravels    : Double? = null
    private var latLngTravels       : LatLng? = null
    private var latLngCurrent       : LatLng? = null
    private var interstitialAd      : InterstitialAd? = null
    private var mFusedLocationProviderClient : FusedLocationProviderClient? = null
    private var mLocationPermissionGranted  = false
    private var requestCodeLocations        = 123
    private var mLastKnownLocation : Location? = null

    override fun createPresenter(): MapsWisataPresenter {
        val wisataApi : WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        return MapsWisataPresenter(this,this,mFusedLocationProviderClient!!,wisataApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_travels)
        init()
        latitudeTravels     = intent.getDoubleExtra(Constant.latitudeTravels,0.0)
        longitudeTravels    = intent.getDoubleExtra(Constant.longitudeTravels,0.0)
    }

    override fun init() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps_travels) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        val statusBarHeight = Util.getStatusBarHeight(this)
        val size = Util.convertDpToPixel(24F)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.setFlags(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            )
            window.statusBarColor = ContextCompat.getColor(this, R.color.greyTransparent)

            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (statusBarHeight > size) {
                val decor = window.decorView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
        btn_close.setOnClickListener { finish() }
        btn_current.setOnClickListener{
            if (mLastKnownLocation != null){
                val location : Location = mLastKnownLocation!!
                latLngCurrent = LatLng(location.latitude,location.longitude)
                val builder = CameraPosition.builder()
                builder.zoom(15f)
                builder.target(latLngCurrent)
                mapsTravels?.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()))
            }
        }
        interstitialAd  = createNewIntAd()
        Util.setAds(interstitialAd!!)
        btn_direction.setOnClickListener { showIntAds() }
    }

    private fun createNewIntAd() : InterstitialAd{
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.ads_institial)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("Ads","Ads loaded")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d("Ads","Ads failed loaded")
            }

            override fun onAdClosed() {
                // Proceed to the next level.
                presenter?.setDirection(latLngCurrent!!,latLngTravels!!)
            }
        }
        return interstitialAd
    }

    override fun loadingDirection(isShow: Boolean) {
        if (isShow){
            progress_direction.visibility = View.VISIBLE
            ll_direction.visibility = View.GONE
        }else{
            progress_direction.visibility = View.GONE
            ll_direction.visibility = View.VISIBLE
        }
    }

    override fun showIntAds() {
        if (interstitialAd != null && interstitialAd!!.isLoaded){
            interstitialAd?.show()
        }else{
            presenter?.setDirection(latLngCurrent!!,latLngTravels!!)
        }
    }

    override fun showSnackbar(message: String) {
        Snackbar.make(
            btn_direction,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun showSnackbarWithAction(message: String) {
        Snackbar.make(
            btn_direction,
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Ok") { startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }.show()
    }

    override fun showDirection(direction: Direction?) {
        val route : Route? = direction?.routeList?.get(0)
        val leg : Leg? = route?.legList?.get(0)
        val directionPositionList = leg?.directionPoint as ArrayList<LatLng>
        val polylineOptions : PolylineOptions = DirectionConverter.createPolyline(application,directionPositionList,3,Color.BLUE)
        mapsTravels?.addPolyline(polylineOptions)
        Util.setCameraWithCoordinationBounds(route,mapsTravels)
    }

    override fun updateLocationUI() {
        if (mapsTravels == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                mapsTravels?.isMyLocationEnabled = true
                presenter?.setDeviceLocation(mLocationPermissionGranted)
                createLocationRequest()
            } else {
                mLastKnownLocation = null
                mapsTravels?.isMyLocationEnabled = false
                getLocationPermission()
            }
        } catch (e : SecurityException)  {
            Log.e("Exception: %s", e.message!!)
        }
    }

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private fun startLocationUpdates(){
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient?.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
            } else {
                getLocationPermission()
            }
        } catch (e : SecurityException)  {
            Log.e("Exception: %s", e.message!!)
        }
    }

    private fun createLocationRequest() {
        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = 10000
        // 3
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this,
                        123)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }

    override fun getLocationPermission() {
        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessCoarseLocationApproved) {
            mLocationPermissionGranted = true
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                requestCodeLocations
            )
        }
    }

    override fun showDeviceLocation(location: Location) {
        mLastKnownLocation  = location
        currentLatitude     = mLastKnownLocation?.latitude
        currentLongitude    = mLastKnownLocation?.longitude
        latLngCurrent       = LatLng(mLastKnownLocation!!.latitude,mLastKnownLocation!!.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLngCurrent!!)
        markerOptions.title("Lokasi anda")
        markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_location))
        mapsTravels?.addMarker(markerOptions)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mapsTravels = googleMap
        mapsTravels?.uiSettings?.isMyLocationButtonEnabled = false
        mapsTravels?.uiSettings?.isMapToolbarEnabled = false
        latLngTravels = LatLng(latitudeTravels!!,longitudeTravels!!)
        val cameraPosition = CameraPosition.builder().target(latLngTravels).zoom(13f).build()
        val markerOptions = MarkerOptions()
        markerOptions.position(latLngTravels!!)
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_location))
        markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_location_wisata))

        //// move camera
        mapsTravels?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mapsTravels?.animateCamera(CameraUpdateFactory.zoomTo(14f))
        mapsTravels?.addMarker(markerOptions)
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        presenter?.setDeviceLocation(mLocationPermissionGranted)
        mapsTravels?.setOnCameraMoveListener(this)
        mapsTravels?.setOnCameraIdleListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionGranted = false
        when (requestCode) {
            123 -> {
                startLocationUpdates()
            }requestCode -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                }
            }

        }
        updateLocationUI()
    }

    override fun onCameraMove() {
	    val latLng = mapsTravels?.projection?.visibleRegion?.latLngBounds?.center
        if (latLng?.latitude != mLastKnownLocation?.latitude &&
                latLng?.longitude != mLastKnownLocation?.longitude){
            btn_current.show()
        }
    }

    override fun onCameraIdle() {
        val latLng = mapsTravels?.projection?.visibleRegion?.latLngBounds?.center
        if (latLng?.latitude == mLastKnownLocation?.latitude &&
            latLng?.longitude == mLastKnownLocation?.longitude){
            btn_current.hide()
        }
    }
}