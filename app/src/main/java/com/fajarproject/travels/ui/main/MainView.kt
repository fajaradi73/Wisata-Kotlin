package com.fajarproject.travels.ui.main

import androidx.fragment.app.Fragment

interface MainView {
    fun showLoading()

    fun hideLoading()

    fun setUI()

    fun addFragment(fragments : Fragment,tag : String)

}