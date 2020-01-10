package com.fajarproject.travels.base.mvp

import android.os.Bundle
import androidx.annotation.Nullable
import com.fajarproject.travels.base.ui.BaseActivity

import com.fajarproject.travels.base.ui.BasePresenter


abstract class MvpActivity<P : BasePresenter<*,*>?> : BaseActivity() {
    var presenter: P? = null
    abstract fun createPresenter(): P

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        presenter = createPresenter()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null) {
            presenter!!.detachView()
        }
    }
}