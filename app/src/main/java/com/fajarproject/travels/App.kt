package com.fajarproject.travels

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.fajarproject.travels.base.widget.GlideImageLoader
import com.fajarproject.travels.util.CrashlyticsHandler
import com.google.android.gms.ads.MobileAds
import io.fabric.sdk.android.Fabric
import lv.chi.photopicker.ChiliPhotoPicker

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
        MobileAds.initialize(this) {}
        //Initialize Fabric before add the custom UncaughtExceptionHandler!
        val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            .debuggable(BuildConfig.DEBUG) // Enables Crashlytics debugger
            .build()
        Fabric.with(fabric)
        val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(CrashlyticsHandler(defaultUncaughtExceptionHandler))

        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "com.fajarproject.travels"
        )
    }

    companion object {
        //// Server Google
//        const val API           = "http://montechnology.tech/wisatas/"
//        const val BASE_IMAGE    = "http://montechnology.tech/wisatas/assets/images/"
        //// Server Hostinger
        const val API                   = "https://fajar-productions.com/wisata/api/"
        const val BASE_URL              = "https://fajar-productions.com/wisata/api/"
        const val BASE_IMAGE            = "https://fajar-productions.com/wisata/assets/images/wisata/"
        const val BASE_IMAGE_PROFILE    = "https://fajar-productions.com/wisata/assets/images/profile/"
        const val BASE_IMAGE_BACKGROUND = "https://fajar-productions.com/wisata/assets/images/background/"

        const val BASE_FILE     = "Wanderlusters"
        const val My_Permissions_Request_Location = 99
    }

    fun getContext(): Context? {
        return context
    }

}