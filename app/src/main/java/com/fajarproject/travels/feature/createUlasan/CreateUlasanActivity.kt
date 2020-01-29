package com.fajarproject.travels.feature.createUlasan

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.fajarproject.travels.R
import com.fajarproject.travels.api.UlasanApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.models.request.CreateUlasanRequest
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.activity_create_ulasan.*

/**
 * Create by Fajar Adi Prasetyo on 28/01/2020.
 */

class CreateUlasanActivity : MvpActivity<CreateUlasanPresenter>(),CreateUlasanView {

	var idWisata : Int? = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_create_ulasan)
		idWisata = intent.getIntExtra(Constant.IdWisata,0)
		setToolbar()
		setUI()
	}

	override fun createPresenter(): CreateUlasanPresenter {
		val ulasanApi = Util.getRetrofitRxJava2()!!.create(UlasanApi::class.java)
		return CreateUlasanPresenter(this,this,ulasanApi)
	}

	override fun setToolbar() {
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

	override fun setUI() {
		cvEdit.setOnClickListener {
			val request = CreateUlasanRequest()
			request.idWisata    = idWisata.toString()
			request.ratting     = simpleRatingBar.rating.toInt().toString()
			request.ulasan      = ulasan.text.toString()
			presenter?.saveUlasan(request)
		}
	}

	override fun showLoading() {
		loadingOverlay.visibility = View.VISIBLE
	}

	override fun hideLoading() {
		loadingOverlay.visibility = View.GONE
	}

	override fun failedSubmit(msg: String) {
		Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
	}

	override fun successSubmit(title: String, msg: String) {
		Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
		setResult(Activity.RESULT_OK)
		finish()
	}
}
