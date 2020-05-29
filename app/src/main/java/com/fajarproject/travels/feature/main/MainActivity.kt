package com.fajarproject.travels.feature.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.fajarproject.travels.R
import com.fajarproject.travels.api.MenuApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.feature.home.HomeFragment
import com.fajarproject.travels.feature.mapsWisataTerdekat.MapsWisataTerdekatActivity
import com.fajarproject.travels.feature.popularWisata.PopularWisataFragment
import com.fajarproject.travels.feature.profil.ProfilFragment
import com.fajarproject.travels.feature.wisataTerdekat.WisataTerdekatFragment
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import lv.chi.photopicker.PhotoPickerFragment


/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

class MainActivity : MvpActivity<MainPresenter>(),MainView, PhotoPickerFragment.Callback {

    private val fragmentManager = supportFragmentManager
    private var currentFragment : Fragment? = null

    override fun createPresenter(): MainPresenter {
        val menuApi : MenuApi = Util.getRetrofitRxJava2()!!.create(MenuApi::class.java)
        return MainPresenter(this,this,menuApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUI()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    @SuppressLint("RestrictedApi")
    override fun setUI() {
        AppPreference.writePreference(this,"sizePerPage",5)
        nav_view.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.action_home -> {
                        toolbar.visibility = View.VISIBLE
                        setStatusBar(false)
                        var fragment = fragmentManager.findFragmentByTag(Constant.home)
                        if (fragment == null){
                            fragment = HomeFragment()
                        }
                        addFragment(fragment,Constant.home)
                        return true
                    }
                    R.id.action_nearby -> {
                        toolbar.visibility = View.VISIBLE
                        setStatusBar(false)
                        var fragment = fragmentManager.findFragmentByTag(Constant.nearby)
                        if (fragment == null){
                            fragment = WisataTerdekatFragment()
                        }
                        addFragment(fragment,Constant.nearby)
                        return true
                    }
                    R.id.action_popular -> {
                        toolbar.visibility = View.VISIBLE
                        setStatusBar(false)
                        var fragment = fragmentManager.findFragmentByTag(Constant.popular)
                        if (fragment == null){
                            fragment = PopularWisataFragment()
                        }
                        addFragment(fragment,Constant.popular)
                        return true
                    }
                    R.id.action_user -> {
                        toolbar.visibility = View.GONE
                        setStatusBar(true)
                        var fragment = fragmentManager.findFragmentByTag(Constant.user)
                        if (fragment == null){
                            fragment = ProfilFragment()
                        }
                        addFragment(fragment,Constant.user)
                        return true
                    }
                }
                return false
            }
        })
        nav_view.selectedItemId = R.id.action_home
    }

    override fun addFragment(fragments: Fragment, tag: String) {

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        var curFrag: Fragment? = null
        if (fragmentManager.primaryNavigationFragment != null){
            curFrag = fragmentManager.primaryNavigationFragment
        }
        if (curFrag != null) {
//            fragmentTransaction.detach(curFrag)
            fragmentTransaction.hide(curFrag)
        }

        var fragment: Fragment? =
            fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = fragments
            fragmentTransaction.add(container.id, fragment, tag)
        } else {
//            fragmentTransaction.attach(fragment)
            fragmentTransaction.show(fragment)
        }
        currentFragment = fragment
        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    private fun setStatusBar(isShow : Boolean){
        val window: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (!isShow) {
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
                val decor = window.decorView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.systemUiVisibility = 0
                }
            }else{
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
                val decor = window.decorView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        Log.d("Images",photos.joinToString(separator = "\n") { it.toString() })
        (currentFragment as ProfilFragment).onImagePicked(photos[0])
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == 1999){
                (currentFragment as ProfilFragment).previewResult()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var result = true
        when (item.itemId) {
            R.id.action_style -> {
                startActivity(Intent(this,MapsWisataTerdekatActivity::class.java))
            }
            else -> result = super.onOptionsItemSelected(item)
        }
        return result
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_menu,menu)
        return true
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("Destroy","Main Activity")
    }
}