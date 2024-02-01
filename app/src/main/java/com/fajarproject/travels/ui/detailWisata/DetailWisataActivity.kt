package com.fajarproject.travels.ui.detailWisata

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fajarproject.travels.FlavorConfig
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.PictureDetailAdapter
import com.fajarproject.travels.adapter.UlasanAdapter
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.databinding.ActivityDetailWisataBinding
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.models.WisataDetailModel
import com.fajarproject.travels.ui.createUlasan.CreateUlasanActivity
import com.fajarproject.travels.ui.mapsWisata.MapsWisataActivity
import com.fajarproject.travels.ui.pictureWisata.PictureWisataActivity
import com.fajarproject.travels.ui.ulasan.UlasanActivity
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.util.fileUtil.FileUtil
import com.fajarproject.travels.util.fileUtil.FileUtilCallbacks
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
import lv.chi.photopicker.PhotoPickerFragment
import java.util.*

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

@SuppressLint("SetTextI18n")
@RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)

class DetailWisataActivity : MvpActivity<DetailWisataPresenter>(), DetailWisataView,
	FileUtilCallbacks, PhotoPickerFragment.Callback,
	OnMapReadyCallback {

	private var maps: GoogleMap? = null
	private var idWisata: String? = ""
	private var menu: Menu? = null
	private var requestCodePicture = 132
	private var fileUtil: FileUtil? = null
	private var list: MutableList<String> = ArrayList()
	private var isSafeBackPressed = true
	private var namaWisata = ""
	private lateinit var binding: ActivityDetailWisataBinding

	override fun createPresenter(): DetailWisataPresenter {
		val wisataApi: WisataApi = Util.getRetrofitRxJava2(this)!!.create(WisataApi::class.java)
		return DetailWisataPresenter(this, this, wisataApi)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityDetailWisataBinding.inflate(layoutInflater)
		setContentView(binding.root)
		setToolbar()
		init()
		idWisata = intent.getStringExtra(Constant.IdWisata)
		binding.swipeRefresh.setOnRefreshListener {
			binding.swipeRefresh.isRefreshing = false
			presenter?.getDetailWisata(idWisata, true)
		}
		setAction()
	}

	override fun onStart() {
		super.onStart()
		if (isConnection) {
			presenter?.getDetailWisata(idWisata, true)
		}
	}

	override fun setToolbar() {
		setSupportActionBar(binding.toolbarWisata)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		Util.setColorFilter(
			binding.toolbarWisata.navigationIcon!!,
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
		binding.lihatSemua.setOnClickListener {
			val intent = Intent(this@DetailWisataActivity, PictureWisataActivity::class.java)
			intent.putExtra(Constant.IdWisata, idWisata)
			intent.putExtra(Constant.NamaWisata, namaWisata)
			startActivityForResult(intent, requestCodePicture)
		}
		binding.tambahFoto.setOnClickListener {
			openPicker()
		}
		binding.semuaUlasan.setOnClickListener {
			val intent = Intent(this, UlasanActivity::class.java)
			intent.putExtra(Constant.IdWisata, idWisata)
			changeActivity(intent)
		}
		binding.createUlasan.setOnClickListener {
			val intent = Intent(this, CreateUlasanActivity::class.java)
			intent.putExtra(Constant.IdWisata, idWisata)
			startActivityForResult(intent, 123)
		}
		binding.linkCreate.setOnClickListener {
			val intent = Intent(this, CreateUlasanActivity::class.java)
			intent.putExtra(Constant.IdWisata, idWisata)
			startActivityForResult(intent, 123)
		}
	}


	private fun openPicker() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
			intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 5);
			intent.type = "image/*"

			startActivityForResult(intent, 888)
		}else{
			PhotoPickerFragment.newInstance(
				multiple = true,
				maxSelection = 5,
				allowCamera = true,
				theme = R.style.ChiliPhotoPicker_Dark
			).show(supportFragmentManager, "picker")
		}
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
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 123 || requestCode == requestCodePicture) {
				presenter?.getDetailWisata(idWisata, true)
			} else if (requestCode == 888) {
				if(data?.clipData != null) {
					val images = data.clipData!!
					list.clear()
					for (i in 0 until images.itemCount){
						val uri = images.getItemAt(i).uri
						fileUtil?.getPath(
							uri,
							Build.VERSION.SDK_INT
						)
					}
					if (list.size > 0) {
						presenter?.uploadPicture(idWisata!!, list)
					}
				}

			}
		}
	}

	override fun setUlasan(list: MutableList<UlasanItem>) {
		val linearLayoutManager = LinearLayoutManager(this)
		linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
		binding.listUlasan.layoutManager = linearLayoutManager
		val adapter = UlasanAdapter(list, this)
		binding.listUlasan.adapter = adapter
	}

	override fun getDataSuccess(data: WisataDetailModel) {
		namaWisata = data.namaWisata!!
		binding.alamatWisata.text = data.alamatWisata
		binding.jamWisata.text =
			Util.milisecondTotimes(data.jamBuka!!) + " - " + Util.milisecondTotimes(data.jamTutup!!)
		binding.titleWisata.text = data.namaWisata
		binding.textKota.text = data.kotaWisata.toString().replace(
			"  ",
			" "
		) + ", " + data.provinsiWisata.toString().replace("  ", " ")
		binding.textRating.text = data.ratting.toString()
		binding.textRatingUlasan.text = data.ratting.toString()
		binding.textUlasan.text = "( " + data.jumlahRatting.toString() + " ulasan )"
		binding.textUlasanView.text = "( " + data.jumlahRatting.toString() + " ulasan )"
		binding.textAkses.text = data.aksesWisata
		Glide.with(this)
			.load(FlavorConfig.baseImage() + data.imageWisata)
			.error(R.drawable.image_dieng)
			.placeholder(Util.circleLoading(this)).into(binding.imageWisata)
		setDataMaps(data.latitude!!, data.longitude!!)
		setDataFavorite(data.favorite!!, data.idWisata)
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
		binding.cvView.visibility = View.VISIBLE
		if (time > data.jamBuka && time < data.jamTutup || data.jamBuka == 0L && data.jamTutup == 0L) {
			binding.cvView.setCardBackgroundColor(
				ContextCompat.getColor(
					this,
					R.color.colorPrimary
				)
			)
			binding.tvView.text = "Buka"
		} else {
			binding.cvView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red))
			binding.tvView.text = "Tutup"
		}
	}

	override fun getDataFail(message: String) {
		Log.d("Error", message)
	}

	override fun showLoading() {
		isSafeBackPressed = false
		binding.loadingOverlay.visibility = View.VISIBLE
	}

	override fun hideLoading() {
		isSafeBackPressed = true
		binding.loadingOverlay.visibility = View.GONE
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
				binding.semuaUlasan.visibility = View.GONE
			}

			jumlah < 3 -> {
				showDataUlasan(true)
				binding.semuaUlasan.visibility = View.GONE
			}

			else -> {
				showDataUlasan(true)
				binding.semuaUlasan.visibility = View.VISIBLE
			}
		}
	}

	override fun checkCreateUlasan(isShow: Boolean) {
		if (isShow) {
			if (binding.clNoUlasan.visibility == View.GONE) {
				binding.createUlasan.visibility = View.VISIBLE
			}
		} else {
			binding.createUlasan.visibility = View.GONE
		}
	}

	override fun showDataUlasan(isShow: Boolean) {
		if (isShow) {
			binding.listUlasan.visibility = View.VISIBLE
			binding.clNoUlasan.visibility = View.GONE
		} else {
			binding.listUlasan.visibility = View.GONE
			binding.clNoUlasan.visibility = View.VISIBLE
		}
	}

	override fun showDataFoto(isShow: Boolean) {
		if (isShow) {
			binding.clNoFoto.visibility = View.GONE
			binding.listFoto.visibility = View.VISIBLE
			binding.lihatSemua.visibility = View.VISIBLE
		} else {
			binding.clNoFoto.visibility = View.VISIBLE
			binding.listFoto.visibility = View.GONE
			binding.lihatSemua.visibility = View.GONE
		}
	}

	override fun setDataMaps(latitude: Double, longitude: Double) {
		val latLng = LatLng(latitude, longitude)
		val cameraPosition: CameraPosition =
			CameraPosition.builder().target(latLng).zoom(15F).build()
		val markerOptions = MarkerOptions()
		markerOptions.position(latLng)
		markerOptions.icon(Util.bitmapDescriptorFromVector(this, R.drawable.ic_location_wisata))
		maps?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
		maps?.addMarker(markerOptions)
		maps?.setOnMapClickListener {
			val intent = Intent(this, MapsWisataActivity::class.java)
			intent.putExtra(Constant.latitudeTravels, latitude)
			intent.putExtra(Constant.longitudeTravels, longitude)
			changeActivity(intent)
		}
	}

	override fun setDataFavorite(isFav: Boolean, idWisata: String?) {
		binding.whitelist.isLiked = isFav
		binding.whitelist.setOnLikeListener(object : OnLikeListener {
			override fun liked(likeButton: LikeButton?) {
				presenter?.saveFavorite(idWisata, likeButton!!)
			}

			override fun unLiked(likeButton: LikeButton?) {
				presenter?.saveFavorite(idWisata, likeButton!!)
			}
		})

	}

	override fun setDataFoto(data: List<PictureItem>, idWisata: String?) {
		val linearLayoutManager = LinearLayoutManager(this)
		linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
		binding.listFoto.layoutManager = linearLayoutManager
		binding.listFoto.adapter = PictureDetailAdapter(data, this)
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