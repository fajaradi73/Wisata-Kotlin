package com.fajarproject.travels.nearbyTour.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.ResponseApi.DataTour
import com.fajarproject.travels.nearbyTour.adapter.AdapterNearby
import com.fajarproject.travels.nearbyTour.presenter.NearbyPresenter
import com.fajarproject.travels.travelDetails.TravelDetails
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.view.OnItemClickListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_nearby.*


/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

class NearbyActivity : AppCompatActivity(),LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    override fun onMapReady(p0: GoogleMap?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
            } else {
                buildGoogleApiClient()
            }
        }
    }


    private var nearbyPresenter : NearbyPresenter? = null
    private var kodeGps : String? = ""
    private var latitude : Double? = 0.0
    private var longitude : Double? = 0.0
    private var mIntent : IntentFilter? = null
    private var statusReceiver : BroadcastReceiver? = null

    private var mGoogleApiClient : GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)
        setToolbar()
        nearbyPresenter = NearbyPresenter(this)
        shimmer_view_container.startShimmerAnimation()
        init()
    }

    private fun init(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps_nearby) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Util.checkLocationPermission(this)
        }

        //show error dialog if Google Play Services not available
        if (!Util.checkGooglePlayServicesAvailable(this)){
            Log.d("onCreate", "Google Play Services not available. Ending Test case.")
            finish()
        }else{
            Log.d("onCreate", "Google Play Services available. Continuing.")
        }

    }

    private fun setToolbar(){
        setSupportActionBar(toolbar_nearby)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun showShimmer(isStart : Boolean){
        if (isStart){
            shimmer_view_container.visibility = View.VISIBLE
            shimmer_view_container.startShimmerAnimation()
        }else{
            shimmer_view_container.stopShimmerAnimation()
            shimmer_view_container.visibility = View.GONE
        }
    }

    fun setRecycleView(list: List<DataTour>){
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_nearby.layoutManager = linearLayoutManager
        val nearbyAdapter = AdapterNearby(list, this)
        rv_nearby.adapter = nearbyAdapter
        nearbyAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(this@NearbyActivity,TravelDetails::class.java)
                intent.putExtra(Constant.IdWisata, list[position].id_wisata)
                startActivity(intent)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }
    override fun onLocationChanged(location: Location?) {
        latitude    = location!!.latitude
        longitude   = location.longitude


        if (mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient!!.connect()
        }
        nearbyPresenter!!.getNearby(latitude.toString(),longitude.toString())
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest,this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("connectionSuspended","$p0 ")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("connectionFailed","$p0 ")
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            App.My_Permissions_Request_Location -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    val permission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                    if (permission == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
