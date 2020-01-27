package com.fajarproject.travels.feature.popularWisata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.Shimmer
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.PopularWisataAdapter
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.base.widget.PaginationScrollListener
import com.fajarproject.travels.feature.main.MainActivity
import com.fajarproject.travels.models.PopularWisataModel
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.util.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_populer.*

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

class PopularWisataFragment : MvpFragment<PopularWisataPresenter>(),PopularWisataView {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var limit :Int? = 10
    private var currentPage = 0
    private var adapter : PopularWisataAdapter? = null
    private var countData = 0

    private var isLoading = false
    private var isLastPage = false


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
        return inflater.inflate(R.layout.fragment_populer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        limit = AppPreference.getIntPreferenceByName(activity!!,"sizePerPage")
        presenter!!.getPopularWisata(limit!!,currentPage)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            isLoading = false
            isLastPage = false
            currentPage = 0
            presenter!!.getPopularWisata(limit!!,currentPage)
        }
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

    override fun moveToDetail(intent: Intent) {
        startActivity(intent)
    }

    override fun setRecycleView() {
        linearLayoutManager         = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listPopular.layoutManager       = linearLayoutManager
    }

    private fun setScrollRecycleView(){
        listPopular.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager){
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                presenter!!.getPopularWisata(limit!!,currentPage)
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
    override fun getDataSuccess(model: MutableList<PopularWisataModel>) {
        countData = model.size
        if (currentPage == 0){
            adapter = PopularWisataAdapter(
                model,
                activity!!,
                presenter!!
            )
            listPopular.adapter = adapter
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