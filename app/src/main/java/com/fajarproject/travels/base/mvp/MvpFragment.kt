package com.fajarproject.travels.base.mvp

import android.os.Bundle
import android.view.View
import com.fajarproject.travels.base.ui.BaseFragment

import com.fajarproject.travels.base.ui.BasePresenter


abstract class MvpFragment<P : BasePresenter<*,*>?> : BaseFragment() {
    protected var presenter: P? = null
    protected abstract fun createPresenter(): P

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = createPresenter()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null) {
            presenter!!.detachView()
        }
    }
}