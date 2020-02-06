package com.fajarproject.travels.feature.mapsWisataTerdekat

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fajarproject.travels.R
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Create by Fajar Adi Prasetyo on 06/02/2020.
 */

class MapsWisataTerdekatActivity : MvpActivity<MapsWisataTedekatPresenter>(),MapsWisataTerdekatView,OnMapReadyCallback {


	private var mFusedLocationProviderClient : FusedLocationProviderClient? = null
	private var mLocationPermissionGranted  = false
	private var requestCodeLocations        = 123
	private var mLastKnownLocation : Location? = null
	private var mapsTravels : GoogleMap? = null
	private var currentLatitude     : Double? = null
	private var currentLongitude    : Double? = null
	private var latLngCurrent       : LatLng? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_maps_wisata_terdekat)
		init()
	}

	override fun createPresenter(): MapsWisataTedekatPresenter {
		val wisataApi : WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
		return MapsWisataTedekatPresenter(this,mFusedLocationProviderClient!!,this,wisataApi)
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
	}

	override fun showLoading() {
	}

	override fun hideLoading() {
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
//		latLngTravels = LatLng(latitudeTravels!!,longitudeTravels!!)
//		val cameraPosition = CameraPosition.builder().target(latLngTravels).zoom(13f).build()
//		val markerOptions = MarkerOptions()
//		markerOptions.position(latLngTravels!!)
//		markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_marker_48dp))
//
//		//// move camera
//		mapsTravels?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//		mapsTravels?.animateCamera(CameraUpdateFactory.zoomTo(14f))
//		mapsTravels?.addMarker(markerOptions)
		// Turn on the My Location layer and the related control on the map.
		updateLocationUI()

		// Get the current location of the device and set the position of the map.
		presenter?.setDeviceLocation(mLocationPermissionGranted)
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
}
