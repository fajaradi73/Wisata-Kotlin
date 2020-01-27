package com.fajarproject.travels.feature.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.facebook.shimmer.Shimmer
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.MenuAdapter
import com.fajarproject.travels.adapter.SliderAdapter
import com.fajarproject.travels.api.MenuApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.widget.GridSpacingItemDecoration
import com.fajarproject.travels.models.LookupDetailModel
import com.fajarproject.travels.util.Util
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */


class HomeFragment : MvpFragment<HomePresenter>(),HomeView {

    var list : List<LookupDetailModel>? = null

    override fun createPresenter(): HomePresenter {
        val menuApi : MenuApi = Util.getRetrofitRxJava2()!!.create(MenuApi::class.java)
        return HomePresenter(this,context!!,menuApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter!!.loadMenu()
        setBanner()
        setRecycleView()
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            presenter!!.loadMenu()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("Texst","Tester")
        super.onSaveInstanceState(outState)
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

    override fun getDataSuccess(model: List<LookupDetailModel>?) {
        this.list = model
        val adapter = MenuAdapter(model,activity!!,presenter!!)
        listMenu.adapter = adapter
    }

    override fun getDataFail(message: String?) {
        Toast.makeText(activity!!,message, Toast.LENGTH_LONG).show()
    }

    override fun setBanner() {
        imageSlider.sliderAdapter               = SliderAdapter(activity!!)
        imageSlider.autoCycleDirection          = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        imageSlider.indicatorSelectedColor      = Color.WHITE
        imageSlider.indicatorUnselectedColor    = Color.GRAY
        imageSlider.scrollTimeInSec             = 5 //set scroll delay in seconds :
        imageSlider.setIndicatorAnimation(IndicatorAnimations.SLIDE)
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.startAutoCycle()
    }

    override fun setRecycleView() {
        listMenu.layoutManager = GridLayoutManager(activity!!,3)
        listMenu.addItemDecoration(
            GridSpacingItemDecoration(
                activity!!,
                3,
                5,
                true
            )
        )
        listMenu.itemAnimator = DefaultItemAnimator()
    }

    override fun showBanner() {
        viewSlider.visibility = View.VISIBLE
    }

    override fun moveToDetail(intent: Intent?) {
        startActivity(intent)
    }
}