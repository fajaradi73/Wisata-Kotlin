package com.fajarproject.travels.feature.wisata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.Shimmer
import com.fajarproject.travels.R
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.adapter.WisataAdapter
import com.fajarproject.travels.models.WisataModel
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.base.widget.PaginationScrollListener
import com.fajarproject.travels.preference.AppPreference
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_wisata.*

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

class WisataActivity : MvpActivity<WisataPresenter>(),WisataView{

    private var typeId : String? = ""
    private var titleWisata : String? = ""
    private var adapter : WisataAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var limit :Int? = 10
    private var currentPage = 0
    private var countData = 0

    private var isLoading = false
    private var isLastPage = false

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
        limit       = AppPreference.getIntPreferenceByName(activity!!,"sizePerPage")

        setToolbar()
        Util.setAds(adView)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            isLoading = false
            isLastPage = false
            currentPage = 0
            presenter?.getWisata(typeId!!,limit!!,currentPage)
        }
    }

    override fun onStart() {
        super.onStart()
        currentPage = 0
        if (isConnection) {
            presenter?.getWisata(typeId!!, limit!!, currentPage)
        }
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
        linearLayoutManager         = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_wisata.layoutManager         = linearLayoutManager
        rv_wisata.itemAnimator = DefaultItemAnimator()
    }

    override fun showLoading() {
        shimmerView.visibility  = View.VISIBLE
        shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        shimmerView.startShimmer()
        swipeRefresh.visibility     = View.GONE
    }

    override fun hideLoading() {
        shimmerView.stopShimmer()
        shimmerView.visibility      = View.GONE
        swipeRefresh.visibility     = View.VISIBLE
    }

    private fun setScrollRecycleView(){
        rv_wisata.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager){
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                presenter?.getWisata(typeId!!,limit!!,currentPage)
            }

            override fun getTotalPageCount(): Int {
                return limit!!
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }

    override fun getDataSuccess(model: MutableList<WisataModel>) {
        countData = model.size
        if (currentPage == 0){
            adapter = WisataAdapter(
                model,this,presenter
            )
            rv_wisata.adapter = adapter
            setScrollRecycleView()
        }else{
            adapter?.removeLoadingFooter()
            isLoading = false
            adapter?.addData(model)
            adapter?.notifyDataSetChanged()
        }

        adapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val list = adapter?.getDataList()
                presenter?.getItem(list!![position])
            }
        })
        checkLastData()
        checkData()
    }

    private fun checkLastData(){
        if (countData == limit){
            adapter?.addLoadingFooter()
        }else{
            isLastPage = true
        }
    }

    private fun checkData(){
        if (adapter?.itemCount == 0){
            showData(false)
        }else{
            showData(true)
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