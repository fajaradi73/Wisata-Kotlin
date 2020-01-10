package com.fajarproject.travels.feature.splashScreen

import android.content.Intent
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import com.fajarproject.travels.R
import com.fajarproject.travels.feature.main.MainActivity
import com.fajarproject.travels.feature.opsiLogin.OpsiLoginActivity
import com.fajarproject.travels.preference.AppPreference


/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUser()
    }

    private fun checkUser(){
        if (AppPreference.getStringPreferenceByName(this,"user").equals("")){
            startActivity(Intent(applicationContext, OpsiLoginActivity::class.java))
            overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )
            finish()
        }else{
            startActivity(Intent(applicationContext, MainActivity::class.java))
            overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )
            finish()
        }
    }
}
