package com.fajarproject.travels.ui.home

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
import com.fajarproject.travels.adapter.MenuAdapter
import com.fajarproject.travels.adapter.SliderAdapter
import com.fajarproject.travels.api.MenuApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.widget.GridSpacingItemDecoration
import com.fajarproject.travels.databinding.FragmentHomeBinding
import com.fajarproject.travels.models.LookupDetailModel
import com.fajarproject.travels.util.Util
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView


/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */


class HomeFragment : MvpFragment<HomePresenter>(),HomeView {

    var list : List<LookupDetailModel>? = null
    private lateinit var homeBinding : FragmentHomeBinding

    override fun createPresenter(): HomePresenter {
        val menuApi : MenuApi = Util.getRetrofitRxJava2(requireContext())!!.create(MenuApi::class.java)
        return HomePresenter(this,requireContext(),menuApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater,container,false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isConnection) {
            presenter?.loadMenu()
        }
        setBanner()
        setRecycleView()
        homeBinding.swipeRefresh.setOnRefreshListener {
            homeBinding.swipeRefresh.isRefreshing = false
            presenter?.loadMenu()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("Texst","Tester")
        super.onSaveInstanceState(outState)
    }

    override fun showLoading() {
        homeBinding.shimmerView.visibility  = View.VISIBLE
        homeBinding.shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        homeBinding.shimmerView.startShimmer()
        homeBinding.swipeRefresh.visibility     = View.GONE
    }

    override fun hideLoading() {
        homeBinding.shimmerView.stopShimmer()
        homeBinding.shimmerView.visibility      = View.GONE
        homeBinding.swipeRefresh.visibility     = View.VISIBLE
    }

    override fun getDataSuccess(model: List<LookupDetailModel>?) {
        this.list = model
        val adapter = MenuAdapter(model,requireActivity(),presenter!!)
        homeBinding.listMenu.adapter = adapter
    }

    override fun getDataFail(message: String?) {
        Toast.makeText(requireActivity(),message, Toast.LENGTH_LONG).show()
    }

    override fun setBanner() {
        homeBinding.imageSlider.sliderAdapter               = SliderAdapter(requireActivity())
        homeBinding.imageSlider.autoCycleDirection          = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        homeBinding.imageSlider.indicatorSelectedColor      = Color.WHITE
        homeBinding.imageSlider.indicatorUnselectedColor    = Color.GRAY
        homeBinding.imageSlider.scrollTimeInSec             = 5 //set scroll delay in seconds :
        homeBinding.imageSlider.setIndicatorAnimation(IndicatorAnimations.SLIDE)
        homeBinding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        homeBinding.imageSlider.startAutoCycle()
    }

    override fun setRecycleView() {
        homeBinding.listMenu.layoutManager = GridLayoutManager(requireActivity(),3)
        homeBinding.listMenu.addItemDecoration(
            GridSpacingItemDecoration(
                requireActivity(),
                3,
                5,
                true
            )
        )
        homeBinding.listMenu.itemAnimator = DefaultItemAnimator()
    }

    override fun showBanner() {
        homeBinding.viewSlider.visibility = View.VISIBLE
    }

    override fun moveToDetail(intent: Intent?) {
        intent?.let { startActivity(it) }
    }
}