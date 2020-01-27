package com.fajarproject.travels.feature.wisataFavorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.Shimmer
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.FavoriteWisataAdapter
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.base.widget.PaginationScrollListener
import com.fajarproject.travels.models.FavoriteModel
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.activity_favorite.*

/**
 * Created by Fajar Adi Prasetyo on 10/01/20.
 */

class FavoriteWisataActivity : MvpActivity<FavoriteWisataPresenter>(),FavoriteWisataView {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var limit :Int? = 10
    private var currentPage = 0
    private var adapter : FavoriteWisataAdapter? = null
    private var countData = 0

    private var isLoading = false
    private var isLastPage = false
    override fun createPresenter(): FavoriteWisataPresenter {
        val wisataApi : WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
        return FavoriteWisataPresenter(this,this,wisataApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setToolbar()
        limit = AppPreference.getIntPreferenceByName(activity!!,"sizePerPage")
        setRecycleView()
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            isLoading = false
            isLastPage = false
            currentPage = 0
            presenter?.getFavorite(limit!!,currentPage)
        }
    }

    override fun onStart() {
        super.onStart()
        currentPage = 0
        presenter?.getFavorite(limit!!,currentPage)
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Favorite Wisata"
    }


    override fun showLoading() {
        shimmerView.visibility  = View.VISIBLE
        shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().build())
        shimmerView.startShimmer()
        swipeRefresh.visibility     = View.GONE
    }

    override fun hideLoading() {
        shimmerView.stopShimmer()
        shimmerView.visibility      = View.GONE
        swipeRefresh.visibility     = View.VISIBLE
    }

    override fun moveToDetail(intent: Intent) {
        startActivity(intent)
    }

    private fun setScrollRecycleView(){
        listFavorite.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager){
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                presenter?.getFavorite(limit!!,currentPage)
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
    override fun setRecycleView() {
        linearLayoutManager         = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listFavorite.layoutManager      = linearLayoutManager
        listFavorite.setHasFixedSize(true)
    }

    override fun getDataSucces(model: MutableList<FavoriteModel>) {
        countData = model.size
        if (currentPage == 0){
            adapter = FavoriteWisataAdapter(
                model,this
            )
            listFavorite.adapter = adapter
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

    override fun getDataFail(message: String) {
        Log.d("ErrorFavorite",message)
        showData(false)
    }

    override fun showData(isShow : Boolean) {
        if (isShow){
            listFavorite.visibility = View.VISIBLE
            ll_nodata.visibility    = View.GONE
        }else{
            listFavorite.visibility = View.GONE
            ll_nodata.visibility    = View.VISIBLE
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