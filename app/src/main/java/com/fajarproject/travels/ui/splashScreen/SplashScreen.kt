package com.fajarproject.travels.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fajarproject.travels.R
import com.fajarproject.travels.ui.main.MainActivity
import com.fajarproject.travels.ui.opsiLogin.OpsiLoginActivity
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
