package com.fajarproject.travels

import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.fajarproject.travels.base.widget.GlideImageLoader
import com.google.android.gms.ads.MobileAds
import lv.chi.photopicker.ChiliPhotoPicker

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


open class App : Application() {


	private var context: Context? = null

	override fun onCreate() {
		super.onCreate()
		context = this.applicationContext
		FacebookSdk.sdkInitialize(applicationContext)
		AppEventsLogger.activateApp(this)
//		MobileAds.initialize(this) {}
		if (BuildConfig.FLAVOR == "dev") {
			FlavorConfig.flavor = Flavor.DEVELOPMENT
			Log.d("Flavor", "Development")
		} else {
			FlavorConfig.flavor = Flavor.PRODUCTION
			Log.d("Flavor", "Production")
		}

		ChiliPhotoPicker.init(
			loader = GlideImageLoader(),
			authority = "com.fajarproject.travels"
		)
	}

	fun getContext(): Context? {
		return context
	}
}
