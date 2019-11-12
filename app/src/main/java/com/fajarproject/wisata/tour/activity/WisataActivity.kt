package com.fajarproject.wisata.tour.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajarproject.wisata.R
import com.fajarproject.wisata.ResponseApi.DataTour
import com.fajarproject.wisata.tour.adapter.AdapterWisata
import com.fajarproject.wisata.tour.presenter.TourPresenter
import com.fajarproject.wisata.travelDetails.TravelDetails
import com.fajarproject.wisata.util.Constant
import com.fajarproject.wisata.util.Util
import com.fajarproject.wisata.view.OnItemClickListener
import kotlinx.android.synthetic.main.activity_wisata.*

class WisataActivity : AppCompatActivity() {

    private var typeId : String? = ""
    private var tourPresenter : TourPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wisata)
        typeId = intent.getStringExtra(Constant.typeID)
        setToolbar(typeId)
        tourPresenter = TourPresenter(this)
        Util.setAds(adView)

    }

    private fun setToolbar(type_id : String?){
        setSupportActionBar(toolbar_wisata)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (type_id) {
            "1" -> title = "Wisata Candi"
            "2" -> title = "Wisata Kawah"
            "3" -> title = "Wisata Telaga"
            "4" -> title = "Bukit & Gunung"
            "5" -> title = "Air Terjun"
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

    override fun onStart() {
        super.onStart()
        tourPresenter?.getTour(typeId)
    }

    fun showShimmer(isShow : Boolean){
        if(isShow){
            shimmer_view_container.visibility = View.VISIBLE
            shimmer_view_container.startShimmerAnimation()
        }else{
            shimmer_view_container.stopShimmerAnimation()
            shimmer_view_container.visibility = View.GONE
        }
    }

    fun setRecycleView(list : List<DataTour>){
        val linearLayoutManager         = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_wisata.layoutManager         = linearLayoutManager
        val adapterWisata               = AdapterWisata(list,this)
        rv_wisata.adapter               = adapterWisata
        adapterWisata.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(this@WisataActivity, TravelDetails::class.java)
                intent.putExtra(Constant.IdWisata, list[position].id_wisata)
                startActivity(intent)
            }
        })
    }

    fun showData(isShow: Boolean){
        if (isShow){
            rv_wisata.visibility = View.VISIBLE
            ll_nodata.visibility = View.GONE
        }else{
            rv_wisata.visibility = View.GONE
            ll_nodata.visibility = View.VISIBLE
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
}
