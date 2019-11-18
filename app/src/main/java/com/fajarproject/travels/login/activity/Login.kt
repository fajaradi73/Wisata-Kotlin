package com.fajarproject.travels.login.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.fajarproject.travels.R
import com.fajarproject.travels.login.model.LoginModel
import com.fajarproject.travels.login.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

class Login : AppCompatActivity() {

    private var loginPresenter : LoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setToolbar()
        loginPresenter = LoginPresenter(this)
        setUI()
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
    }

    private fun setUI(){
        email.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableButton()
                error_email.visibility = View.GONE
            }
        })

        password.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableButton()
                error_password.visibility = View.GONE
            }
        })

        show_hide_password.setOnClickListener { v: View? ->
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
            val loginModel = LoginModel(email.text.toString(),password.text.toString())
            loginPresenter!!.loginWisata(loginModel)
        }
    }

   private fun enableButton(){
       if (email.length() > 0 && password.length() > 0){
           btn_masuk.isEnabled = true
           btn_masuk.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
       }else{
           btn_masuk.isEnabled = false
           btn_masuk.setCardBackgroundColor(ContextCompat.getColor(this,R.color.grey_400))
       }
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
