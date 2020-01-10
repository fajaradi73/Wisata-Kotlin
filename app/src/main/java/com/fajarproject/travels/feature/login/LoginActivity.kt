package com.fajarproject.travels.feature.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fajarproject.travels.R
import com.fajarproject.travels.api.LoginAPI
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.models.LoginModel
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.activity_login.*


/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

@SuppressLint("SetTextI18n")
class LoginActivity: MvpActivity<LoginPresenter>(),LoginView {

    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun createPresenter(): LoginPresenter {
        val loginAPI : LoginAPI = Util.getRetrofitRxJava2()!!.create(
            LoginAPI::class.java)
        return LoginPresenter(this,this,loginAPI)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setToolbar()
        setUI()
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun setUI() {
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableButton()
                if (s.toString().matches(emailPattern.toRegex()) && s!!.isNotEmpty()){
                    isEmailValid(true)
                }else{
                    isEmailValid(false)
                }
            }
        })

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableButton()
                error_password.visibility = View.GONE
            }
        })

        show_hide_password.setOnClickListener {
            if (show_hide_password.text == "Tampilkan"){
                password.transformationMethod = null
                show_hide_password.text = getString(R.string.hide)
            }else {
                password.transformationMethod = PasswordTransformationMethod()
                show_hide_password.text = getString(R.string.show)
            }
        }
        btn_masuk.setOnClickListener {
            error_password.visibility   = View.GONE
            error_email.visibility      = View.GONE
            val loginModel = LoginModel(
                email.text.toString(),
                password.text.toString()
            )
            presenter!!.loginWisata(loginModel)
        }
    }

    override fun enableButton() {
        if (email.length() > 0 && password.length() > 0){
            btn_masuk.isEnabled = true
            btn_masuk.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
        }else{
            btn_masuk.isEnabled = false
            btn_masuk.setCardBackgroundColor(ContextCompat.getColor(this,R.color.grey_400))
        }
    }

    override fun showLoading() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingOverlay.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun changeActivity(intent: Intent?) {
        startActivity(intent)
        finish()
    }

    override fun errorEmail() {
        error_email.visibility = View.VISIBLE
        error_email.text = "Email yang anda masukkan belum terdaftar"
    }

    override fun isEmailValid(isValid: Boolean) {
        if(isValid){
            error_email.visibility = View.GONE
        }else{
            error_email.visibility = View.VISIBLE
            error_email.text = "Harap memasukkan email dengan benar"
        }
    }

    override fun errorPassword() {
        error_password.visibility = View.VISIBLE
        error_password.text = "Password yang anda masukkan salah"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }
}