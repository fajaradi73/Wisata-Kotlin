package com.fajarproject.travels.network

import android.util.Log
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.adapter.rxjava.HttpException
import rx.Subscriber


/**
 * Created by Fajar Adi Prasetyo on 06/01/20.
 */

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
abstract class NetworkCallback<M> : Subscriber<M>() {
    abstract fun onSuccess(model: M)
    abstract fun onFailure(message: String?,code: Int?,jsonObject: JSONObject?)
    abstract fun onFinish()

    override fun onError(e: Throwable) {
        e.printStackTrace()
        if (e is HttpException) {
            val httpException: HttpException = e
            val code: Int = httpException.code()
            val responseBody : ResponseBody? = httpException.response().errorBody()
            val jsonObject = JSONObject(responseBody?.string())
            val message: String = httpException.message()
            Log.i(TAG, "code : $code")
            onFailure(message,code,jsonObject)
        } else {
            onFailure(e.message,404,JSONObject())
        }
        onFinish()
    }

    override fun onNext(model: M) {
        onSuccess(model)
    }

    override fun onCompleted() {
        onFinish()
    }

    companion object {
        private val TAG = NetworkCallback::class.java.name
    }

}
