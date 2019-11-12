package com.fajarproject.wisata.nearbyTour.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.wisata.R
import com.fajarproject.wisata.ResponseApi.DataTour
import com.fajarproject.wisata.nearbyTour.adapter.AdapterNearby
import com.fajarproject.wisata.nearbyTour.presenter.NearbyPresenter
import com.fajarproject.wisata.service.MyService
import com.fajarproject.wisata.travelDetails.TravelDetails
import com.fajarproject.wisata.util.Constant
import com.fajarproject.wisata.util.InternetCheck
import com.fajarproject.wisata.view.OnItemClickListener
import kotlinx.android.synthetic.main.activity_nearby.*


/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

class NearbyActivity : AppCompatActivity() {

    private var nearbyPresenter : NearbyPresenter? = null
    private var kodeGps : String? = ""
    private var latitude : Double? = 0.0
    private var longitude : Double? = 0.0
    private var mIntent : IntentFilter? = null
    private var statusReceiver : BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)
        setToolbar()
        nearbyPresenter = NearbyPresenter(this)
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(baseContext, MyService::class.java)
        startService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        shimmer_view_container.startShimmerAnimation()
        InternetCheck(object : InternetCheck.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet!!){
                    LocalBroadcastManager.getInstance(this@NearbyActivity).registerReceiver(broadcastReceiver, IntentFilter("NOW"))
                }else{
                    shimmer_view_container.stopShimmerAnimation()
                    shimmer_view_container.visibility = View.GONE
                    ll_nodata.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onPause() {
        if (mIntent != null){
            unregisterReceiver(statusReceiver)
            mIntent = null
        }
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(baseContext,MyService::class.java))
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

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                kodeGps     = intent.getStringExtra("kode_gps")
                latitude    = intent.getDoubleExtra("latitude", 0.0)
                longitude   = intent.getDoubleExtra("longitude", 0.0)
                if (kodeGps != null) {
                    if (kodeGps == "false") {
                        shimmer_view_container.stopShimmerAnimation()
                        shimmer_view_container.visibility = View.GONE
                        rv_nearby.visibility = View.GONE
                        ll_gps.visibility = View.VISIBLE
                        btn_ok.setOnClickListener { startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                    } else if (kodeGps == "true") {
                        shimmer_view_container.startShimmerAnimation()
                        shimmer_view_container.visibility = View.VISIBLE
                        rv_nearby.visibility = View.VISIBLE
                        ll_gps.visibility = View.GONE
                    }
                }
                if (latitude != 0.0 && longitude != 0.0) {
                    InternetCheck(object : InternetCheck.Consumer {
                        override fun accept(internet: Boolean?) {
                            if (internet!!){
                                nearbyPresenter!!.getNearby(latitude.toString(),longitude.toString())
                            }
                        }
                    })
                }
            }
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
}
