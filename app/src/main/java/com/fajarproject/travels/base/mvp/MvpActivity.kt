package com.fajarproject.travels.base.mvp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.Nullable
import com.fajarproject.travels.R
import com.fajarproject.travels.base.ui.BaseActivity
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.util.Util


abstract class MvpActivity<P : BasePresenter<*,*>?> : BaseActivity() {
    var presenter: P? = null
    var isConnection = true

    abstract fun createPresenter(): P

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        presenter = createPresenter()
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        if (!Util.isInternetAvailable(this)){
            showDialogInternet()
            isConnection = false
        }
        super.onStart()
    }

    private fun showDialogInternet(){
        Util.showRoundedDialog(this,getString(R.string.title_connection),getString(R.string.body_connection),false,object : DialogYesListener{
            override fun onYes() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                    startActivityForResult(panelIntent, 545)
                }else{
                    startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS),545)
                }
            }
        },object : DialogNoListener{
            override fun onNo() {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 545){
            finish()
            startActivity(intent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null) {
            presenter!!.detachView()
        }
    }
}