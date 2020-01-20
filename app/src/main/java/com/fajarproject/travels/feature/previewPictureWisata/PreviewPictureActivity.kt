package com.fajarproject.travels.feature.previewPictureWisata

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.SectionsPagerAdapter
import com.fajarproject.travels.base.widget.DepthPageTransformer
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.activity_preview_picture.*
import org.parceler.Parcels


class PreviewPictureActivity : AppCompatActivity(),PreviewPictureView {

    private var data : List<PictureItem> = arrayListOf()
    private var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_picture)
        getDataIntent()
        setToolbar()
        setViewPager()
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(toolbar.navigationIcon!!, ContextCompat.getColor(this,R.color.white))
        Util.setMargins(toolbar,0,Util.getStatusBarHeight(this),0,0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#00FFFFFF")
            window.setFlags(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            )
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        title = data[pos].namaWisata
        tvDate.text = Util.getDateWithTime(data[pos].createDate!!)

    }

    override fun getDataIntent() {
        pos = intent.getIntExtra(Constant.position,0)
        data = Parcels.unwrap(intent.getParcelableExtra(Constant.dataFoto))
    }

    override fun setViewPager() {
        val adapter = SectionsPagerAdapter(supportFragmentManager,data)
        viewPager.setPageTransformer(true,DepthPageTransformer())
        viewPager.adapter   = adapter
        viewPager.currentItem = pos
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                title = data[position].namaWisata
                tvDate.text = Util.getDateWithTime(data[position].createDate!!)
            }

        })
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
