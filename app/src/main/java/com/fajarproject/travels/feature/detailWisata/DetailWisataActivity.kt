package com.fajarproject.travels.feature.detailWisata

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.feature.mapsWisata.MapsWisataActivity
import com.fajarproject.travels.adapter.UlasanAdapter
import com.fajarproject.travels.models.DetailWisataModel
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.base.widget.AppBarStateChangeListener
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.activity_wisata_detail.*
import kotlinx.android.synthetic.main.travel_details.*

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

@SuppressLint("SetTextI18n")
class DetailWisataActivity : MvpActivity<DetailWisataPresenter>(),DetailWisataView,
    OnMapReadyCallback {

    private var maps        : GoogleMap?    = null
    private var idWisata    : Int?          = 0
    private var menu        : Menu?         = null


    override fun createPresenter(): DetailWisataPresenter {
        val wisataApi : WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
        return DetailWisataPresenter(this,this,wisataApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wisata_detail)
        setToolbar()
        init()
        idWisata       = intent.getIntExtra(Constant.IdWisata,0)
        setNativeAds()
    }

    override fun setNativeAds() {
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

    override fun setToolbar() {
        setSupportActionBar(toolbar_wisata)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#00FFFFFF")
            window.setFlags(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            )
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    override fun init() {
        htab_appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener(){
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                if(state?.name == "COLLAPSED"){
                    setColorIcon(true)
                }else if (state?.name == "EXPANDED"){
                    setColorIcon(false)
                }
            }

        })
        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps_wisata) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun setColorIcon(isShow : Boolean) {
        if (isShow){
            Util.setColorFilter(toolbar_wisata.navigationIcon!!,ContextCompat.getColor(this,R.color.black))
            whitelist.setUnlikeDrawableRes(R.drawable.ic_unlike)
            share.setImageResource(R.drawable.ic_share)
        }else{
            Util.setColorFilter(toolbar_wisata.navigationIcon!!,ContextCompat.getColor(this,R.color.white))
            whitelist.setUnlikeDrawableRes(R.drawable.ic_unlike_white)
            share.setImageResource(R.drawable.ic_share_white)
        }
    }

    override fun setUlasan(list: List<UlasanItem>) {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        list_ulasan.layoutManager = linearLayoutManager
        val adapter = UlasanAdapter(list, this)
        list_ulasan.adapter = adapter
    }

    override fun getDataSuccess(data: DetailWisataModel) {
        alamat_wisata.text          = data.alamatWisata
        jam_wisata.text             = Util.milisecondTotimes(data.jamBuka!!) + " - " + Util.milisecondTotimes(data.jamTutup!!)
        title_wisata.text           = data.namaWisata
        Glide.with(this)
            .load(App.BASE_IMAGE + data.imageWisata)
            .error(R.drawable.image_dieng)
            .placeholder(Util.circleLoading(this)).into(image_wisata)
        val latLng = LatLng(data.latitude!!,data.longitude!!)
        val cameraPosition : CameraPosition = CameraPosition.builder().target(latLng).zoom(15F).build()
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(Util.bitmapDescriptorFromVector(this,R.drawable.ic_marker_48dp))
        maps?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        maps?.addMarker(markerOptions)
        maps?.setOnMapClickListener {
            val intent = Intent(this, MapsWisataActivity::class.java)
            intent.putExtra(Constant.latitudeTravels,data.latitude)
            intent.putExtra(Constant.longitudeTravels,data.longitude)
            changeActivity(intent)
        }
        whitelist.isLiked = data.favorite!!
        whitelist.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                presenter?.saveFavorite(data.idWisata!!,likeButton!!)
            }

            override fun unLiked(likeButton: LikeButton?) {
                presenter?.saveFavorite(data.idWisata!!,likeButton!!)
            }
        })
        text_rating.text        = data.ratting.toString()
        text_rating_ulasan.text = data.ratting.toString()
        text_ulasan.text        = "( " + data.jumlahRatting.toString() + " ulasan )"
        text_ulasan_view.text   = "( " + data.jumlahRatting.toString() + " ulasan )"
        setUlasan(data.ulasan)
    }

    override fun getDataFail(message: String) {
        Log.d("Error",message)
    }

    override fun showLoading() {
        loadingOverlay.visibility   = View.VISIBLE
        htab_appbar.visibility      = View.GONE
        layoutID.visibility         = View.GONE
    }

    override fun hideLoading() {
        loadingOverlay.visibility   = View.GONE
        htab_appbar.visibility      = View.VISIBLE
        layoutID.visibility         = View.VISIBLE
    }

    override fun changeActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun showMessageLike(message: String, view: View) {
        Snackbar.make(view,message, Snackbar.LENGTH_LONG).show()
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        this.maps   = googleMap
        maps?.uiSettings?.isScrollGesturesEnabled   = false
        maps?.uiSettings?.isRotateGesturesEnabled   = false
        maps?.uiSettings?.setAllGesturesEnabled(false)
        maps?.uiSettings?.isMapToolbarEnabled       = false
    }

    override fun onStart() {
        super.onStart()
        presenter?.getDetailWisata(idWisata)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.travels_menu,menu)
        this.menu   = menu
        return true
    }
}