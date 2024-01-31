package com.fajarproject.travels.ui.mapsWisataTerdekat

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fajarproject.travels.R
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.databinding.ActivityMapsWisataTerdekatBinding
import com.fajarproject.travels.models.NearbyModel
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
import com.google.android.gms.maps.model.VisibleRegion

/**
 * Create by Fajar Adi Prasetyo on 06/02/2020.
 */

class MapsWisataTerdekatActivity : MvpActivity<MapsWisataTerdekatPresenter>(),MapsWisataTerdekatView,OnMapReadyCallback {


	private var mFusedLocationProviderClient : FusedLocationProviderClient? = null
	private var mLocationPermissionGranted  = false
	private var requestCodeLocations        = 123
	private var mLastKnownLocation : Location? = null
	private var mapsTravels : GoogleMap? = null
	private var currentLatitude     : Double? = null
	private var currentLongitude    : Double? = null
	private var latLngCurrent       : LatLng? = null
	private lateinit var binding : ActivityMapsWisataTerdekatBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMapsWisataTerdekatBinding.inflate(layoutInflater)
		setContentView(binding.root)
		init()
	}

	override fun createPresenter(): MapsWisataTerdekatPresenter {
		val wisataApi : WisataApi = Util.getRetrofitRxJava2(this)!!.create(WisataApi::class.java)
		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
		return MapsWisataTerdekatPresenter(this,mFusedLocationProviderClient!!,this,wisataApi)
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
		binding.loadingOverlay.visibility = View.VISIBLE
	}

	override fun hideLoading() {
		binding.loadingOverlay.visibility = View.GONE
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

	override fun getDataSuccess(model: MutableList<NearbyModel>) {
		mapsTravels?.clear()
		if (model.size > 0){
			for (data in model) {
				val markerOptions = MarkerOptions()
				markerOptions.position(LatLng(data.latitude!!,data.longitude!!))
				markerOptions.title(data.namaWisata)
				markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_location_wisata))
				mapsTravels?.addMarker(markerOptions)
			}
		}
	}

	override fun getDataFailed(msg: String) {
		Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
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

		val cameraPosition = CameraPosition.builder().target(latLngCurrent).zoom(13f).build()

		//// move camera
		mapsTravels?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
		mapsTravels?.animateCamera(CameraUpdateFactory.zoomTo(14f))
		calculateRadius(currentLatitude!!,currentLongitude!!)
	}

	override fun calculateRadius(latitude : Double, longitude : Double){
		val vr: VisibleRegion = mapsTravels?.projection?.visibleRegion!!
		val left = vr.latLngBounds.southwest.longitude

		val middleLeftCornerLocation =
			Location("center")
		middleLeftCornerLocation.latitude = vr.latLngBounds.center.latitude
		middleLeftCornerLocation.longitude = left
		val radius = Util.calculationByDistance(vr.latLngBounds.center,LatLng(middleLeftCornerLocation.latitude,middleLeftCornerLocation.longitude))
		presenter?.getNearbyMaps(latitude,longitude, radius)

	}

	override fun onMapReady(googleMap: GoogleMap?) {
		mapsTravels = googleMap
		mapsTravels?.uiSettings?.isMyLocationButtonEnabled = false
		mapsTravels?.uiSettings?.isMapToolbarEnabled = false
		// Turn on the My Location layer and the related control on the map.
		updateLocationUI()

		// Get the current location of the device and set the position of the map.
		presenter?.setDeviceLocation(mLocationPermissionGranted)
		mapsTravels?.setOnCameraIdleListener {
			calculateRadius(mapsTravels?.cameraPosition?.target?.latitude!!,mapsTravels?.cameraPosition?.target?.longitude!!)
		}
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
