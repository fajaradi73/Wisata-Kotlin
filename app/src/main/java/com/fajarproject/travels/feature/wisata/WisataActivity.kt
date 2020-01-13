package com.fajarproject.travels.feature.wisata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.adapter.WisataAdapter
import com.fajarproject.travels.models.WisataModel
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.base.view.OnItemClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_wisata.*

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

class WisataActivity : MvpActivity<WisataPresenter>(),WisataView{

    var list : List<WisataModel>? = arrayListOf()
    private var typeId : String? = ""
    private var titleWisata : String? = ""
    private var adapter : WisataAdapter? = null

    override fun createPresenter(): WisataPresenter {
        val wisataApi : WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
        return WisataPresenter(this,this,wisataApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wisata)
        setRecycleView()
        typeId      = intent.getStringExtra(Constant.typeID)
        titleWisata = intent.getStringExtra(Constant.titleWisata)
        setToolbar()
        Util.setAds(adView)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            presenter?.getWisata(typeId!!)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter?.getWisata(typeId!!)
    }

    override fun onResume() {
        super.onResume()
        if (adView != null) {
            adView.resume()
        }
    }

    override fun onPause() {
        if (adView != null) {
            adView.pause()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (adView != null) {
            adView.destroy()
        }
        super.onDestroy()
    }

    override fun setToolbar(){
        setSupportActionBar(toolbar_wisata)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = titleWisata
    }

    override fun setRecycleView(){
        val linearLayoutManager         = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_wisata.layoutManager         = linearLayoutManager
        rv_wisata.itemAnimator = DefaultItemAnimator()

    }

    override fun showLoading() {
//        loadingOverlay.visibility = View.VISIBLE
        shimmerView.visibility  = View.VISIBLE
        shimmerView.duration    = 1150
        shimmerView.startShimmerAnimation()
        swipeRefresh.visibility = View.GONE
    }

    override fun hideLoading() {
//        loadingOverlay.visibility = View.GONE
        shimmerView.stopShimmerAnimation()
        shimmerView.visibility  = View.GONE
        swipeRefresh.visibility = View.VISIBLE
    }

    override fun getDataSuccess(model: List<WisataModel>) {
        if (model.isNotEmpty()){
            showData(true)
            this.list = model
            adapter = WisataAdapter(
                model,
                this,
                presenter
            )
            rv_wisata.adapter = adapter
            adapter?.setOnItemClickListener(object : OnItemClickListener{
                override fun onItemClick(view: View?, position: Int) {
                    presenter?.getItem(list!![position])
                }
            })

        }else{
            showData(false)
        }
    }

    override fun getDataFail(message: String?) {
        showData(false)
        Log.d("ErrorWisata",message!!)
    }

    override fun moveToDetail(intent: Intent?) {
        startActivity(intent)
    }

    override fun showData(isShow: Boolean) {
        if (isShow){
            rv_wisata.visibility = View.VISIBLE
            ll_nodata.visibility = View.GONE
        }else{
            rv_wisata.visibility = View.GONE
            ll_nodata.visibility = View.VISIBLE
        }
    }

    override fun getDataFailLike(message: String?) {
        Log.d("ErrorWisataLike",message!!)
    }

    override fun showMessageFavorite(message: String?,view: View) {
        Snackbar.make(view,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.travels_menu,menu)
        return true
    }
}