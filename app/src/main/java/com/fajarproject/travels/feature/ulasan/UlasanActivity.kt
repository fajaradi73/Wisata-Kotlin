package com.fajarproject.travels.feature.ulasan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.UlasanAdapter
import com.fajarproject.travels.api.UlasanApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.feature.createUlasan.CreateUlasanActivity
import com.fajarproject.travels.models.RattingItem
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.models.RattingModel
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.activity_ulasan.*

/**
 * Created by Fajar Adi Prasetyo on 14/01/20.
 */

class UlasanActivity : MvpActivity<UlasanPresenter>(),UlasanView {

    private var idWisata = 0
    private var totalUlasan = 0
    private var adapter : UlasanAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var limit :Int? = 10
    private var currentPage = 0
    private var countData = 0

    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ulasan)
        setToolbar()
        setUI()
        limit       = AppPreference.getIntPreferenceByName(this,"sizePerPage")
        idWisata    = intent.getIntExtra(Constant.IdWisata,0)
        presenter?.getRatting(idWisata)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing   = false
            isLoading = false
            isLastPage = false
            presenter?.getRatting(idWisata)
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
        linearLayoutManager = LinearLayoutManager(this)
        listUlasan.layoutManager    = linearLayoutManager
        listUlasan.itemAnimator = DefaultItemAnimator()
        scrollView.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if(v?.getChildAt(v.childCount - 1) != null) {
                if (!isLoading && !isLastPage) {
                    if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                        scrollY > oldScrollY
                    ) {
                        isLoading = true
                        currentPage += 1
                        presenter?.getUlasan(idWisata, limit!!, currentPage)
                    }
                }
            }
        }
        cvUlasan.setOnClickListener {
            val intent = Intent(this, CreateUlasanActivity::class.java)
            intent.putExtra(Constant.IdWisata, idWisata)
            startActivityForResult(intent,123)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 123){
                currentPage = 0
                presenter?.getUlasan(idWisata,limit!!,currentPage)
            }
        }
    }

    override fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingOverlay.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    override fun getDataSuccess(model: RattingModel) {
        title = "Ulasan " + model.namaWisata
        totalUlasan = model.jumlahUlasan!!
        totalRatting.text   = model.rattingWisata.toString()
        jumlahUlasan.text   = model.jumlahUlasan.toString() + " Ulasan"
        setDataRatting(model.ratting)
        if (model.createUlasan!!){
            cvUlasan.visibility = View.GONE
        }else{
            cvUlasan.visibility = View.GONE
        }
    }

    override fun getDataFail(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        adapter?.removeLoadingFooter()
        isLoading = false
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

    override fun setDataUlasan(model: MutableList<UlasanItem>) {
        countData = model.size
        if (currentPage == 0){
            adapter = UlasanAdapter(
                model,this
            )
            listUlasan.adapter = adapter
//            setScrollRecycleView()
        }else{
            adapter?.removeLoadingFooter()
            isLoading = false
            adapter?.addData(model)
            adapter?.notifyDataSetChanged()
        }
        checkLastData()
    }

    private fun checkLastData(){
        if (countData == limit){
            adapter?.addLoadingFooter()
        }else{
            isLastPage = true
        }
    }

    override fun getProgressRatting(ratting : Int) : Float{
        return ((ratting.toFloat() / totalUlasan.toFloat()) * 100)
    }
}
