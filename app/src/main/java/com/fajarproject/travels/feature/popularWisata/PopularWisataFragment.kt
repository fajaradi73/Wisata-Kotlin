package com.fajarproject.travels.feature.popularWisata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.PopularWisataAdapter
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.feature.main.MainActivity
import com.fajarproject.travels.models.PopularWisataModel
import com.fajarproject.travels.util.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_popular.*

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

class PopularWisataFragment : MvpFragment<PopularWisataPresenter>(),PopularWisataView {

    var list : List<PopularWisataModel>? = arrayListOf()

    override fun createPresenter(): PopularWisataPresenter {
        val wisataApi : WisataApi = Util.getRetrofitRxJava2()!!.create(WisataApi::class.java)
        activity = context as MainActivity
        return PopularWisataPresenter(this,activity!!,wisataApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popular, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter!!.getPopularWisata()
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            presenter!!.getPopularWisata()
        }
    }

    override fun showLoading() {
        shimmerView.visibility  = View.VISIBLE
        shimmerView.duration    = 1150
        shimmerView.startShimmerAnimation()
        swipeRefresh.visibility = View.GONE
    }

    override fun hideLoading() {
        shimmerView.stopShimmerAnimation()
        shimmerView.visibility  = View.GONE
        swipeRefresh.visibility = View.VISIBLE
    }

    override fun moveToDetail(intent: Intent) {
        startActivity(intent)
    }

    override fun setRecycleView() {
        val linearLayoutManager         = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listPopular.layoutManager       = linearLayoutManager
    }

    override fun getDataSucces(model: List<PopularWisataModel>) {
        if (model.isNotEmpty()){
            showData(true)
            this.list = model
            val adapter = PopularWisataAdapter(
                model,
                context!!,
                presenter
            )
            listPopular.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    presenter?.getItem(list!![position])
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

    override fun showData(isShow: Boolean) {
        if (isShow){
            listPopular.visibility  = View.VISIBLE
            ll_nodata.visibility    = View.GONE
        }else{
            listPopular.visibility  = View.GONE
            ll_nodata.visibility    = View.VISIBLE
        }
    }

    override fun getDataFailLike(message: String?) {
        Log.d("ErrorWisataLike",message!!)
    }

    override fun showMessageFavorite(message: String?, view: View) {
        Snackbar.make(activity!!.findViewById(R.id.clContainer),message!!, Snackbar.LENGTH_LONG).show()
    }
}