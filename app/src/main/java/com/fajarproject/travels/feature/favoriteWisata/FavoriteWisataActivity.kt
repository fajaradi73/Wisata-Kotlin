package com.fajarproject.travels.feature.favoriteWisata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.FavoriteWisataAdapter
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.models.FavoriteModel
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.fragment_favorite.*

/**
 * Created by Fajar Adi Prasetyo on 10/01/20.
 */

class FavoriteWisataActivity : MvpActivity<FavoriteWisataPresenter>(),FavoriteWisataView {

    var list : List<FavoriteModel>? = null

    override fun createPresenter(): FavoriteWisataPresenter {
        val wisataApi : WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
        return FavoriteWisataPresenter(this,this,wisataApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_favorite)
        setToolbar()
        setRecycleView()
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            presenter?.getFavorite()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter?.getFavorite()
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Favorite Wisata"
    }

    override fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingOverlay.visibility = View.GONE
    }

    override fun moveToDetail(intent: Intent) {
        startActivity(intent)
    }

    override fun setRecycleView() {
        val linearLayoutManager         = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listFavorite.layoutManager      = linearLayoutManager
    }

    override fun getDataSucces(model: List<FavoriteModel>) {
        if (model.isNotEmpty()){
            showData(true)
            this.list = model
            val adapter = FavoriteWisataAdapter(
                model,
                this
            )
            listFavorite.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    presenter!!.getItem(list!![position])
                }
            })
        }else{
            showData(false)
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
}