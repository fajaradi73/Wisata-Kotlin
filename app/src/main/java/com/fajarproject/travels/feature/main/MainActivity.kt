package com.fajarproject.travels.feature.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.fajarproject.travels.R
import com.fajarproject.travels.api.MenuApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.feature.home.HomeFragment
import com.fajarproject.travels.feature.popularWisata.PopularWisataFragment
import com.fajarproject.travels.feature.profil.ProfilFragment
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

class MainActivity : MvpActivity<MainPresenter>(),MainView {

    private val fragmentManager = supportFragmentManager

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
                            fragment = PopularWisataFragment()
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
}