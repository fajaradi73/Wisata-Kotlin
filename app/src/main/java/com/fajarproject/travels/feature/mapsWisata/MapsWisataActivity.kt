package com.fajarproject.travels.feature.mapsWisata

import android.Manifest
import android.content.Intent
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_maps_travels.*

class MapsWisataActivity : MvpActivity<MapsWisataPresenter>(),MapsWisataView, OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener{

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#00FFFFFF")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.setFlags(
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                )
            }
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        btn_close.setOnClickListener { finish() }
        btn_current.setOnClickListener{
            if (mLastKnownLocation != null){
                btn_current.hide()
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
            } else {
                mLastKnownLocation = null
                mapsTravels?.isMyLocationEnabled = false
                getLocationPermission()
            }
        } catch (e : SecurityException)  {
            Log.e("Exception: %s", e.message!!)
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
        markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_marker_48dp))

        //// move camera
        mapsTravels?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mapsTravels?.animateCamera(CameraUpdateFactory.zoomTo(14f))
        mapsTravels?.addMarker(markerOptions)
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        presenter?.setDeviceLocation(mLocationPermissionGranted)
        mapsTravels?.setOnCameraMoveListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionGranted = false
        when (requestCode) {
            requestCode -> {
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
        val cameraPosition = mapsTravels?.cameraPosition
        if (cameraPosition?.target?.latitude != latLngCurrent?.latitude &&
                cameraPosition?.target?.longitude != latLngCurrent?.longitude){
            btn_current.show()
        }
    }
}