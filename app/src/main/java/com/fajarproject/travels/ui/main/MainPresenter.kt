package com.fajarproject.travels.ui.main

import android.content.Context
import com.fajarproject.travels.api.MenuApi
import com.fajarproject.travels.base.ui.BasePresenter

class MainPresenter(view: MainView, val context: Context, override var apiStores: MenuApi): BasePresenter<MainView,MenuApi>() {

    init {
        super.attachView(view)
    }

}