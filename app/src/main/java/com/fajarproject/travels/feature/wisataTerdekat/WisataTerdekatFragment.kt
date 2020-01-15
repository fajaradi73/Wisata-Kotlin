package com.fajarproject.travels.feature.wisataTerdekat


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.feature.main.MainActivity
import com.fajarproject.travels.adapter.WisataTerdekatAdapter
import com.fajarproject.travels.models.NearbyModel
import com.fajarproject.travels.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_wisata_terdekat.*

/**
 * Created by Fajar Adi Prasetyo on 12/01/20.
 */

class WisataTerdekatFragment : MvpFragment<WisataTerdekatPresenter>(),WisataTerdekatView {

    private var mFusedLocationProviderClient : FusedLocationProviderClient? = null
    private var currentLatitude     : Double = 0.0
    private var currentLongitude    : Double = 0.0
    private var mLocationPermissionGranted  = false
    private var requestCodeLocations        = 123
    private var mLastKnownLocation : Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wisata_terdekat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
        updateLocationUI()
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            presenter?.getNearbyWisata(currentLatitude,currentLongitude)
        }
    }

    override fun createPresenter(): WisataTerdekatPresenter {
        val wisataApi : WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
        activity = context as MainActivity
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        return WisataTerdekatPresenter(this,activity!!,mFusedLocationProviderClient!!,wisataApi)
    }

    override fun setRecycleView() {
        val linearLayoutManager         = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listNearby.layoutManager        = linearLayoutManager
    }

    override fun showLoading() {
        shimmerView.visibility  = View.VISIBLE
        shimmerView.duration    = 1150
        shimmerView.startShimmerAnimation()
        swipeRefresh.visibility = View.GONE
    }

    override fun hideLoading() {
        shimmerView.stopShimmerAnimation()
        shimmerView.visibility  = View.GONE
        swipeRefresh.visibility = View.VISIBLE
    }

    override fun getLocationPermission() {
        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessCoarseLocationApproved) {
            mLocationPermissionGranted = true
            presenter!!.setDeviceLocation(mLocationPermissionGranted)
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                requestCodeLocations
            )
        }
    }

    override fun updateLocationUI() {
        try {
            if (mLocationPermissionGranted) {
                presenter!!.setDeviceLocation(mLocationPermissionGranted)
            } else {
                mLastKnownLocation = null
                getLocationPermission()
            }
        } catch (e : SecurityException)  {
            Log.e("Exception: %s", e.message!!)
        }
    }

    override fun showDeviceLocation(location: Location) {
        mLastKnownLocation  = location
        currentLatitude     = mLastKnownLocation!!.latitude
        currentLongitude    = mLastKnownLocation!!.longitude
        presenter?.getNearbyWisata(currentLatitude,currentLongitude)
    }

    override fun getDataSuccess(list: List<NearbyModel>) {
        if (list.isNotEmpty()){
            showData(true)
            val adapter = WisataTerdekatAdapter(
                list,
                activity!!,
                presenter!!
            )
            listNearby.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    presenter?.getItem(list[position])
                }
            })
        }else{
            showData(false)
        }
    }

    override fun getDataFail(message: String) {
        Log.d("ErrorFavorite",message)
        showData(false)
    }

    override fun showData(isShow: Boolean) {
        if (isShow){
            listNearby.visibility   = View.VISIBLE
            ll_nodata.visibility    = View.GONE
        }else{
            listNearby.visibility   = View.GONE
            ll_nodata.visibility    = View.VISIBLE
        }
    }

    override fun moveToDetail(intent: Intent) {
        startActivity(intent)
    }

    override fun getDataFailLike(message: String?) {
        Log.d("ErrorWisataLike",message!!)
    }

    override fun showMessageFavorite(message: String?, view: View) {
        Snackbar.make(activity!!.findViewById(R.id.clContainer),message!!, Snackbar.LENGTH_LONG).show()
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
