package com.fajarproject.travels.util

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class NullOnEmptyConverterFactory : Converter.Factory() {
    private val TAG = "Null Factory"
    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation?>?, retrofit: Retrofit): Converter<ResponseBody, *> {
        val delegate: Converter<ResponseBody, *> = retrofit.nextResponseBodyConverter<Any?>(this, type!!, annotations!!)
        Log.d(TAG, "responseBodyConverter: creating the NULL converter")
        return (Converter { body: ResponseBody ->
            Log.d(TAG, "convert: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            if (body.contentLength() == 0L) {
                Log.d(TAG, "convert: returning null")
                return@Converter null
            } else {
                Log.d(TAG, "convert: returning the delegate.convert result")
                return@Converter delegate.convert(body)
            }
        })
    }
}