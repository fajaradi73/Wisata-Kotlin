package com.fajarproject.travels.feature.detailWisata

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.PictureDetailAdapter
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.feature.mapsWisata.MapsWisataActivity
import com.fajarproject.travels.adapter.UlasanAdapter
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.feature.createUlasan.CreateUlasanActivity
import com.fajarproject.travels.feature.pictureWisata.PictureWisataActivity
import com.fajarproject.travels.feature.ulasan.UlasanActivity
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.models.WisataDetailModel
import com.fajarproject.travels.util.fileUtil.FileUtil
import com.fajarproject.travels.util.fileUtil.FileUtilCallbacks
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
import com.google.android.material.snackbar.Snackbar
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.activity_detail_wisata.*
import lv.chi.photopicker.PhotoPickerFragment
import java.util.*

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

@SuppressLint("SetTextI18n")
class DetailWisataActivity : MvpActivity<DetailWisataPresenter>(), DetailWisataView,
	FileUtilCallbacks, PhotoPickerFragment.Callback,
	OnMapReadyCallback {

	private var maps: GoogleMap? = null
	private var idWisata: Int? = 0
	private var menu: Menu? = null
	private var requestCodePicture = 132
	private var fileUtil: FileUtil? = null
	private var list: MutableList<String> = ArrayList()
	private var isSafeBackPressed = true
	private var namaWisata = ""

	override fun createPresenter(): DetailWisataPresenter {
		val wisataApi: WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
		return DetailWisataPresenter(this, this, wisataApi)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_detail_wisata)
		setToolbar()
		init()
		idWisata = intent.getIntExtra(Constant.IdWisata, 0)
		setNativeAds()
		presenter?.getDetailWisata(idWisata, true)
		swipeRefresh.setOnRefreshListener {
			swipeRefresh.isRefreshing = false
			presenter?.getDetailWisata(idWisata, true)
		}
		setAction()
	}

	override fun setNativeAds() {
		val builder: AdLoader.Builder = AdLoader.Builder(
			this, getString(R.string.ads_native_advance)
		)

		builder.forUnifiedNativeAd { unifiedNativeAd ->
			val styles: NativeTemplateStyle? =
				NativeTemplateStyle.Builder()
					.withMainBackgroundColor(
						ColorDrawable(
							ContextCompat.getColor(
								this,
								R.color.white
							)
						)
					).build()
			adsNative.setStyles(styles)
			adsNative.setNativeAd(unifiedNativeAd)
		}
		builder.withAdListener(object : AdListener() {
			override fun onAdFailedToLoad(errorCode: Int) {
				Log.d("errorCode", "$errorCode ")
				adsNative.visibility = View.GONE
			}
		})
		val adLoader: AdLoader = builder.build()
		adLoader.loadAd(AdRequest.Builder().addTestDevice("828CDDB6B368965E6B6BB3B6C5321FD6").build())
	}

	override fun setToolbar() {
		setSupportActionBar(toolbar_wisata)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		Util.setColorFilter(
			toolbar_wisata.navigationIcon!!,
			ContextCompat.getColor(this, R.color.white)
		)

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
		val mapFragment =
			supportFragmentManager.findFragmentById(R.id.maps_wisata) as SupportMapFragment?
		mapFragment?.getMapAsync(this)
		fileUtil = FileUtil(this, this)
	}

	override fun setAction() {
		lihat_semua.setOnClickListener {
			val intent = Intent(this@DetailWisataActivity, PictureWisataActivity::class.java)
			intent.putExtra(Constant.IdWisata, idWisata)
			intent.putExtra(Constant.NamaWisata, namaWisata)
			startActivityForResult(intent, requestCodePicture)
		}
		tambah_foto.setOnClickListener {
			openPicker()
		}
		semuaUlasan.setOnClickListener {
			val intent = Intent(this, UlasanActivity::class.java)
			intent.putExtra(Constant.IdWisata, idWisata)
			changeActivity(intent)
		}
		createUlasan.setOnClickListener {
			val intent = Intent(this, CreateUlasanActivity::class.java)
			intent.putExtra(Constant.IdWisata, idWisata)
			startActivityForResult(intent,123)
		}
		linkCreate.setOnClickListener {
			val intent = Intent(this, CreateUlasanActivity::class.java)
			intent.putExtra(Constant.IdWisata, idWisata)
			startActivityForResult(intent,123)
		}
	}


	private fun openPicker() {
		PhotoPickerFragment.newInstance(
			multiple = true,
			maxSelection = 5,
			allowCamera = true,
			theme = R.style.ChiliPhotoPicker_Dark
		).show(supportFragmentManager, "picker")
	}

	override fun onImagesPicked(photos: ArrayList<Uri>) {
		for (i in 0 until photos.size) {
			fileUtil?.getPath(
				photos[i],
				Build.VERSION.SDK_INT
			)
		}
		if (list.size > 0) {
			presenter?.uploadPicture(idWisata!!, list)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK){
			if (requestCode == 123 || requestCode == requestCodePicture){
				presenter?.getDetailWisata(idWisata,true)
			}
		}
	}

	override fun setUlasan(list: MutableList<UlasanItem>) {
		val linearLayoutManager = LinearLayoutManager(this)
		linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
		list_ulasan.layoutManager = linearLayoutManager
		val adapter = UlasanAdapter(list, this)
		list_ulasan.adapter = adapter
	}

	override fun getDataSuccess(data: WisataDetailModel) {
		namaWisata = data.namaWisata!!
		alamat_wisata.text = data.alamatWisata
		jam_wisata.text =
			Util.milisecondTotimes(data.jamBuka!!) + " - " + Util.milisecondTotimes(data.jamTutup!!)
		title_wisata.text = data.namaWisata
		text_kota.text = data.kotaWisata.toString().replace(
			"  ",
			" "
		) + ", " + data.provinsiWisata.toString().replace("  ", " ")
		text_rating.text = data.ratting.toString()
		text_rating_ulasan.text = data.ratting.toString()
		text_ulasan.text = "( " + data.jumlahRatting.toString() + " ulasan )"
		text_ulasan_view.text = "( " + data.jumlahRatting.toString() + " ulasan )"
		textAkses.text = data.aksesWisata
		Glide.with(this)
			.load(App.BASE_IMAGE + data.imageWisata)
			.error(R.drawable.image_dieng)
			.placeholder(Util.circleLoading(this)).into(image_wisata)
		setDataMaps(data.latitude!!, data.longitude!!)
		setDataFavorite(data.favorite!!, data.idWisata!!)
		setUlasan(data.ulasan!!)
		checkUlasan(data.jumlahRatting!!)
		checkCreateUlasan(data.create_ulasan!!)
		if (data.picture!!.isNotEmpty()) {
			showDataFoto(true)
			setDataFoto(data.picture, data.idWisata)
		} else {
			showDataFoto(false)
		}
		val time = Util.getCurrentMilliSecond()
		cv_view.visibility = View.VISIBLE
		if (time > data.jamBuka && time < data.jamTutup || data.jamBuka == 0L && data.jamTutup == 0L) {
			cv_view.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
			tv_view.text = "Buka"
		} else {
			cv_view.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red))
			tv_view.text = "Tutup"
		}
	}

	override fun getDataFail(message: String) {
		Log.d("Error", message)
	}

	override fun showLoading() {
		isSafeBackPressed = false
		loadingOverlay.visibility = View.VISIBLE
	}

	override fun hideLoading() {
		isSafeBackPressed = true
		loadingOverlay.visibility = View.GONE
	}

	override fun changeActivity(intent: Intent) {
		startActivity(intent)
	}

	override fun showMessageLike(message: String, view: View) {
		Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
	}

	override fun checkUlasan(jumlah: Int) {
		when {
			jumlah == 0 -> {
				showDataUlasan(false)
				semuaUlasan.visibility = View.GONE
			}
			jumlah < 3 -> {
				showDataUlasan(true)
				semuaUlasan.visibility = View.GONE
			}
			else -> {
				showDataUlasan(true)
				semuaUlasan.visibility = View.VISIBLE
			}
		}
	}

	override fun checkCreateUlasan(isShow: Boolean) {
		if (isShow) {
			if (clNoUlasan.visibility == View.GONE) {
				createUlasan.visibility = View.VISIBLE
			}
		} else {
			createUlasan.visibility = View.GONE
		}
	}

	override fun showDataUlasan(isShow: Boolean) {
		if (isShow) {
			list_ulasan.visibility = View.VISIBLE
			clNoUlasan.visibility = View.GONE
		} else {
			list_ulasan.visibility = View.GONE
			clNoUlasan.visibility = View.VISIBLE
		}
	}

	override fun showDataFoto(isShow: Boolean) {
		if (isShow) {
			clNoFoto.visibility = View.GONE
			list_foto.visibility = View.VISIBLE
			lihat_semua.visibility = View.VISIBLE
		} else {
			clNoFoto.visibility = View.VISIBLE
			list_foto.visibility = View.GONE
			lihat_semua.visibility = View.GONE
		}
	}

	override fun setDataMaps(latitude: Double, longitude: Double) {
		val latLng = LatLng(latitude, longitude)
		val cameraPosition: CameraPosition =
			CameraPosition.builder().target(latLng).zoom(15F).build()
		val markerOptions = MarkerOptions()
		markerOptions.position(latLng)
		markerOptions.icon(Util.bitmapDescriptorFromVector(this, R.drawable.ic_marker_48dp))
		maps?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
		maps?.addMarker(markerOptions)
		maps?.setOnMapClickListener {
			val intent = Intent(this, MapsWisataActivity::class.java)
			intent.putExtra(Constant.latitudeTravels, latitude)
			intent.putExtra(Constant.longitudeTravels, longitude)
			changeActivity(intent)
		}
	}

	override fun setDataFavorite(isFav: Boolean, idWisata: Int) {
		whitelist.isLiked = isFav
		whitelist.setOnLikeListener(object : OnLikeListener {
			override fun liked(likeButton: LikeButton?) {
				presenter?.saveFavorite(idWisata, likeButton!!)
			}

			override fun unLiked(likeButton: LikeButton?) {
				presenter?.saveFavorite(idWisata, likeButton!!)
			}
		})

	}

	override fun setDataFoto(data: List<PictureItem>, idWisata: Int) {
		val linearLayoutManager = LinearLayoutManager(this)
		linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
		list_foto.layoutManager = linearLayoutManager
		list_foto.adapter = PictureDetailAdapter(data, this)
	}

	override fun successUpload(title: String, message: String) {
		presenter?.getDetailWisata(idWisata, false)
		Util.showRoundedDialog(this, title, message, false, object : DialogYesListener {
			override fun onYes() {
			}
		}, object : DialogNoListener {
			override fun onNo() {

			}
		})
	}

	override fun failedUpload(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show()
	}


	override fun onMapReady(googleMap: GoogleMap?) {
		this.maps = googleMap
		maps?.uiSettings?.isScrollGesturesEnabled = false
		maps?.uiSettings?.isRotateGesturesEnabled = false
		maps?.uiSettings?.setAllGesturesEnabled(false)
		maps?.uiSettings?.isMapToolbarEnabled = false
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressed()
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.travels_menu,menu)
		this.menu = menu
		return true
	}

	override fun FileUtilonStartListener() {

	}

	override fun FileUtilonProgressUpdate(progress: Int) {
	}

	override fun FileUtilonCompleteListener(
		path: String?,
		wasDriveFile: Boolean,
		wasUnknownProvider: Boolean,
		wasSuccessful: Boolean,
		Reason: String?
	) {
		if (wasSuccessful) {
			list.add(path!!)
		}
	}
}