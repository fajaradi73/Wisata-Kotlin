package com.fajarproject.travels.feature.ulasan

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.UlasanAdapter
import com.fajarproject.travels.api.UlasanApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.models.RattingItem
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.models.UlasanModel
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.activity_ulasan.*

/**
 * Created by Fajar Adi Prasetyo on 14/01/20.
 */

class UlasanActivity : MvpActivity<UlasanPresenter>(),UlasanView {

    private var idWisata = 0
    private var totalUlasan = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ulasan)
        setToolbar()
        setUI()
        idWisata    = intent.getIntExtra(Constant.IdWisata,0)
        presenter?.getUlasan(idWisata)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing   = false
            presenter?.getUlasan(idWisata)
        }
    }

    override fun createPresenter(): UlasanPresenter {
        val ulasanApi = Util.getRetrofitRxJava2()!!.create(UlasanApi::class.java)
        return UlasanPresenter(this,this,ulasanApi)
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
        listUlasan.layoutManager    = LinearLayoutManager(this)
//        listRatting.layoutManager   = LinearLayoutManager(this)
    }

    override fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingOverlay.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    override fun getDataSuccess(model: UlasanModel) {
        totalUlasan = model.jumlahUlasan!!
        totalRatting.text   = model.rattingWisata.toString()
        jumlahUlasan.text   = model.jumlahUlasan.toString() + " Ulasan"
        setDataRatting(model.ratting)
        setDataUlasan(model.ulasan)
    }

    override fun getDataFail(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    override fun setDataRatting(model: List<RattingItem>) {
        ////Ratting 5
        totalRatting5.text = model[4].jumlahRatting.toString()
        progressRatting5.progress = getProgressRatting(model[4].jumlahRatting!!)
        ////Ratting 4
        totalRatting4.text = model[3].jumlahRatting.toString()
        progressRatting4.progress = getProgressRatting(model[3].jumlahRatting!!)
        ////Ratting 3
        totalRatting3.text = model[2].jumlahRatting.toString()
        progressRatting3.progress = getProgressRatting(model[2].jumlahRatting!!)
        ////Ratting 2
        totalRatting2.text = model[1].jumlahRatting.toString()
        progressRatting2.progress = getProgressRatting(model[1].jumlahRatting!!)
        ////Ratting 1
        totalRatting1.text = model[0].jumlahRatting.toString()
        progressRatting1.progress = getProgressRatting(model[0].jumlahRatting!!)
    }

    override fun setDataUlasan(model: List<UlasanItem>) {
        listUlasan.adapter = UlasanAdapter(model,this)
    }

    override fun getProgressRatting(ratting : Int) : Float{
        return ((ratting.toFloat() / totalUlasan.toFloat()) * 100)
    }
}
