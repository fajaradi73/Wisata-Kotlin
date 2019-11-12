package com.fajarproject.wisata.mapsTravels

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration
import com.akexorcist.googledirection.constant.TransitMode
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.constant.Unit
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Leg
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.fajarproject.wisata.R
import com.fajarproject.wisata.mapsTravels.adapter.IconMenuAdapter
import com.fajarproject.wisata.mapsTravels.model.IconPowerMenuItem
import com.fajarproject.wisata.util.Constant
import com.fajarproject.wisata.util.PowerMenuUtil
import com.fajarproject.wisata.util.Util
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.*
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powermenu.*
import kotlinx.android.synthetic.main.activity_maps_travels.*
import com.akexorcist.googledirection.DirectionCallback as DirectionCallback1

class MapsTravels : AppCompatActivity(),OnMapReadyCallback,LocationListener,
    ConnectionCallbacks,
    OnConnectionFailedListener {

    private var mapsTravels : GoogleMap? = null

    private var mGoogleApiClient : GoogleApiClient? = null

    private var marker              : Marker? = null
    private var line              : Polyline? = null
    private var currentLatitude     : Double? = null
    private var currentLongitude    : Double? = null
    private var latitudeTravels     : Double? = null
    private var longitudeTravels    : Double? = null
    private var latLngTravels       : LatLng? = null
    private var latLngCurrent       : LatLng? = null
    private var interstitialAd      : InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_travels)
        init()
        latitudeTravels     = intent.getDoubleExtra(Constant.latitudeTravels,0.0)
        longitudeTravels    = intent.getDoubleExtra(Constant.longitudeTravels,0.0)
    }

    private fun init(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps_travels) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#00FFFFFF")

            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                window.setFlags(
                    LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                    LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                )
            }
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        //show error dialog if Google Play Services not available
        if (!Util.checkGooglePlayServicesAvailable(this)){
            Log.d("onCreate", "Google Play Services not available. Ending Test case.")
            finish()
        }else{
            Log.d("onCreate", "Google Play Services available. Continuing.")
        }
        btn_close.setOnClickListener { finish() }
        btn_current.setOnClickListener{
            val location : Location = mapsTravels!!.myLocation
            latLngCurrent = LatLng(location.latitude,location.longitude)
            val builder = CameraPosition.builder()
            builder.zoom(15f)
            builder.target(latLngCurrent)
            mapsTravels!!.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()))
        }
        interstitialAd  = createNewIntAd()
        Util.setAds(interstitialAd!!)
        btn_direction.setOnClickListener { showIntAds() }
    }

    private fun setDirection(view: View){
        GoogleDirectionConfiguration.getInstance().isLogEnabled = true
        val key : String? = resources.getString(R.string.google_maps_key)
        loadingDirection(true)
        GoogleDirection.withServerKey(key)
            .from(latLngCurrent)
            .to(latLngTravels)
            .transportMode(TransportMode.DRIVING)
            .transitMode(TransitMode.BUS)
            .unit(Unit.METRIC)
            .execute(object : DirectionCallback1 {
                override fun onDirectionSuccess(direction: Direction?, rawBody: String?) {
                    loadingDirection(false)
                    if (direction!!.isOK){
                        val route : Route? = direction.routeList[0]
                        val leg : Leg? = route!!.legList[0]
                        val directionPositionList : ArrayList<LatLng> = leg!!.directionPoint
                        val polylineOptions : PolylineOptions = DirectionConverter.createPolyline(application,directionPositionList,3,Color.BLUE)
                        mapsTravels!!.addPolyline(polylineOptions)
                        Util.setCameraWithCoordinationBounds(route,mapsTravels)
                    }else if (direction.status == "ZERO_RESULTS"){
                        Log.e("gagal", direction.status + "")
                        Snackbar.make(
                            view,
                            "Harap untuk mengaktifkan lokasi untuk menggunakan direction",
                            Snackbar.LENGTH_LONG
                        ).setAction("Ok", OnClickListener {startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }).show()
                    }
                }

                override fun onDirectionFailure(t: Throwable?) {
                    loadingDirection(false)
                    Snackbar.make(
                        view,
                        "Gagal menampilkan route ke lokasi",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun loadingDirection(isShow : Boolean){
        if (isShow){
            progress_direction.visibility = View.VISIBLE
            ll_direction.visibility = View.GONE
        }else{
            progress_direction.visibility = View.GONE
            ll_direction.visibility = View.VISIBLE
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mapsTravels = googleMap
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                mapsTravels!!.isMyLocationEnabled = true
            } else {
                buildGoogleApiClient()
                mapsTravels!!.isMyLocationEnabled = true
            }
        }
//        Util.setStyleMaps(mapsTravels,this)
        mapsTravels!!.uiSettings.isMyLocationButtonEnabled = false
        mapsTravels!!.uiSettings.isMapToolbarEnabled = false
        latLngTravels = LatLng(latitudeTravels!!,longitudeTravels!!)
        val cameraPosition = CameraPosition.builder().target(latLngTravels).zoom(13f).build()
        val markerOptions = MarkerOptions()
        markerOptions.position(latLngTravels!!)
        markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_marker_48dp))

        //// move camera
        mapsTravels!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mapsTravels!!.animateCamera(CameraUpdateFactory.zoomTo(14f))
        mapsTravels!!.addMarker(markerOptions)
    }

    override fun onLocationChanged(location: Location?) {
        currentLatitude     = location!!.latitude
        currentLongitude    = location.longitude
        latLngCurrent       = LatLng(location.latitude,location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLngCurrent!!)
        markerOptions.title("Lokasi anda")
        markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_location))
        mapsTravels!!.addMarker(markerOptions)
        if (mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient!!.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
            )
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun onConnected(p0: Bundle?) {
        val locationRequest             = LocationRequest()
        locationRequest.interval        = 1000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest,this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()

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
                setDirection(btn_direction)
            }
        }
        return interstitialAd
    }


    private fun showIntAds(){
        if (interstitialAd != null && interstitialAd!!.isLoaded){
            interstitialAd!!.show()
        }else{
            setDirection(btn_direction)
        }

    }
}
