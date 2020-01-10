package com.fajarproject.travels.base.ui

import android.util.Log
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription


/**
 * Created by Fajar Adi Prasetyo on 06/01/20.
 */

abstract class BasePresenter<V,T> {
    var view: V? = null
    abstract var apiStores : T
    private var compositeSubscription: CompositeSubscription? = null
//    private var subscriber: Subscriber<K>? = null
    fun attachView(view: V){
        this.view = view
    }

    fun detachView() {
        view = null
        onUnsubscribe()
    }

    private fun onUnsubscribe() {
        if (compositeSubscription != null && compositeSubscription!!.hasSubscriptions()) {
            compositeSubscription!!.unsubscribe()
            Log.e("TAG", "onUnsubscribe: ")
        }
    }

    fun<D> addSubscribe(observable: Observable<D>, subscriber: Subscriber<D>?){
//        this.subscriber = subscriber
        if (compositeSubscription == null) {
            compositeSubscription = CompositeSubscription()
        }
        compositeSubscription!!.add(
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
        )
    }

    fun<D> stop(subscriber: Subscriber<D>?) {
        subscriber?.unsubscribe()
    }
}