package com.fajarproject.travels.base.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import rx.subscriptions.CompositeSubscription


/**
 * Created by Fajar Adi Prasetyo on 06/01/20.
 */


open class BaseFragment : Fragment() {
    var activity: Activity? = null
    private val compositeSubscription: CompositeSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(activity!!, view)
    }

    override fun onDestroy() {
        super.onDestroy()
        onUnsubscribe()
    }

    fun onUnsubscribe() {
        compositeSubscription?.unsubscribe()
    }
}