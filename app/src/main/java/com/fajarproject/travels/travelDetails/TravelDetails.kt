package com.fajarproject.travels.travelDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.ResponseApi.DataTour
import com.fajarproject.travels.mapsTravels.MapsTravels
import com.fajarproject.travels.travelDetails.presenter.TravelPresenter
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_travel_details.*
import kotlinx.android.synthetic.main.travel_details.*


class TravelDetails : AppCompatActivity(),OnMapReadyCallback{

    private var travelPresenter : TravelPresenter? = null
    private var maps : GoogleMap? = null
    private var wisataMarker: Marker? = null
    private var id_wisata : String? = ""
    private var menu : Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_details)
        setToolbar()
        init()
        travelPresenter = TravelPresenter(this)
        id_wisata       = intent.getStringExtra(Constant.IdWisata)
        setNativeAds()

    }

    private fun setNativeAds(){
        val builder: AdLoader.Builder = AdLoader.Builder(
            this, getString(R.string.ads_native_advance)
        )

        builder.forUnifiedNativeAd { unifiedNativeAd ->
            val styles: NativeTemplateStyle? =
                NativeTemplateStyle.Builder()
                    .withMainBackgroundColor(ColorDrawable(ContextCompat.getColor(this,R.color.white))).build()
            adsNative.setStyles(styles)
            adsNative.setNativeAd(unifiedNativeAd)
        }
        builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d("errorCode","$errorCode ")
                adsNative.visibility = View.GONE
            }
        })
        val adLoader: AdLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().addTestDevice("828CDDB6B368965E6B6BB3B6C5321FD6").build())
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar_wisata)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#00FFFFFF")
            window.setFlags(
                LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            )
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

    }

    private fun init(){
        htab_appbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    setColorIcon(isShow)
                } else if (isShow) {
                    isShow = false
                    setColorIcon(isShow)
                }
            }
        })
        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps_wisata) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

    }

    private fun setColorIcon(isShow : Boolean){
        if (isShow){
            toolbar_wisata.navigationIcon?.setColorFilter(
                resources.getColor(R.color.black),
                Mode.SRC_ATOP
            )
            whitelist.setUnlikeDrawableRes(R.drawable.ic_unlike)
            share.setImageResource(R.drawable.ic_share)
        }else{
            toolbar_wisata.navigationIcon?.setColorFilter(
                resources.getColor(R.color.white),
                Mode.SRC_ATOP
            )
            whitelist.setUnlikeDrawableRes(R.drawable.ic_unlike_white)
            share.setImageResource(R.drawable.ic_share_white)
        }

    }

    override fun onStart() {
        super.onStart()
        travelPresenter?.getTravelDetails(id_wisata)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.travels_menu,menu)
        this.menu   = menu
        return true
    }

    @SuppressLint("SetTextI18n")
    fun setDetailWisata(dataTour: DataTour){
        alamat_wisata.text          = dataTour.alamat_wisata
        jam_wisata.text             = dataTour.jam_buka + " - " + dataTour.jam_tutup
        title_wisata.text           = dataTour.nama_wisata
        Glide.with(this).load(App.BASE_IMAGE + dataTour.image_wisata).into(image_wisata)
        val latLng = LatLng(dataTour.latitude.toDouble(),dataTour.longitude.toDouble())
        val cameraPosition : CameraPosition = CameraPosition.builder().target(latLng).zoom(15F).build()
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_marker_48dp))
        maps!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        maps!!.addMarker(markerOptions)
        maps!!.setOnMapClickListener {
            val intent = Intent(this,MapsTravels::class.java)
            intent.putExtra(Constant.latitudeTravels,dataTour.latitude.toDouble())
            intent.putExtra(Constant.longitudeTravels,dataTour.longitude.toDouble())
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.maps   = googleMap
        maps!!.uiSettings.isScrollGesturesEnabled   = false
        maps!!.uiSettings.isRotateGesturesEnabled   = false
        maps!!.uiSettings.setAllGesturesEnabled(false)
        maps!!.uiSettings.isMapToolbarEnabled       = false
        Util.setStyleMaps(maps,this)

    }
}
