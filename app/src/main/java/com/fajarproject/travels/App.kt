package com.fajarproject.travels

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.MobileAds

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class App : Application() {


    private var context: Context? = null

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
//        MobileAds.initialize(this, resources.getString(R.string.ads_mob_ID))
        MobileAds.initialize(this) {}
    }

    companion object {
        //// Server Google
//        const val API           = "http://montechnology.tech/wisatas/"
//        const val BASE_IMAGE    = "http://montechnology.tech/wisatas/assets/images/"
        //// Server Hostinger
        const val API                   = "http://travels.fajar-productions.com/api/"
        const val BASE_URL              = "http://travels.fajar-productions.com/api/"
        const val BASE_IMAGE            = "http://travels.fajar-productions.com/assets/images/wisata/"
        const val BASE_IMAGE_PROFILE    = "http://travels.fajar-productions.com/assets/images/profile/"

        const val BASE_FILE     = "Travels"
        const val My_Permissions_Request_Location = 99
    }

    fun getContext(): Context? {
        return context
    }

}