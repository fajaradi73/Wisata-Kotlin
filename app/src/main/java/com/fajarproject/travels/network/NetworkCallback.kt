package com.fajarproject.travels.network

import android.util.Log
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
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
            var jsonObject = JSONObject()
            val message: String = httpException.message()
            if (code == 404){
                onFailure(message,404,null)
            }else if (code != 403){
                jsonObject = JSONObject(responseBody?.string())
            }
            Log.i(TAG, "code : $code")
            onFailure(message,code,jsonObject)
        } else {
            onFailure(e.message,404,null)
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
