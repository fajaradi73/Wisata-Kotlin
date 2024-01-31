package com.fajarproject.travels.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fajarproject.travels.R
import com.fajarproject.travels.api.LoginAPI
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.databinding.ActivityLoginBinding
import com.fajarproject.travels.ui.main.MainActivity
import com.fajarproject.travels.ui.password.PasswordActivity
import com.fajarproject.travels.models.LoginModel
import com.fajarproject.travels.util.Util


/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

@SuppressLint("SetTextI18n")
class LoginActivity: MvpActivity<LoginPresenter>(),LoginView {

    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var binding : ActivityLoginBinding
    override fun createPresenter(): LoginPresenter {
        val loginAPI : LoginAPI = Util.getRetrofitRxJava2(this)!!.create(
            LoginAPI::class.java)
        return LoginPresenter(this,this,loginAPI)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        setUI()
    }

    override fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.masuk)
    }

    override fun setUI() {
        binding.email.addTextChangedListener(object : TextWatcher {
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

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableButton()
                binding.tilPassword.isErrorEnabled = false
            }
        })

        binding.btnMasuk.setOnClickListener {
            val loginModel = LoginModel(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
            presenter!!.loginWisata(loginModel)
        }
    }

    override fun enableButton() {
        if (binding.email.length() > 0 && binding.password.length() > 0){
            binding.btnMasuk.isEnabled = true
            binding.btnMasuk.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
        }else{
            binding.btnMasuk.isEnabled = false
            binding.btnMasuk.setCardBackgroundColor(ContextCompat.getColor(this,R.color.grey_400))
        }
    }

    override fun showLoading() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun changeActivity(intent: Intent?) {
        startActivity(intent)
        finish()
    }

    override fun errorEmail() {
        binding.tilEmail.error = "Email yang anda masukkan belum terdaftar"
        Util.requestFocus(binding.email,this)
    }

    override fun isEmailValid(isValid: Boolean) {
        if(isValid){
            binding.tilEmail.isErrorEnabled = false
        }else{
            binding.tilEmail.error = "Harap memasukkan email dengan benar"
            Util.requestFocus(binding.email,this)
        }
    }

    override fun errorPassword() {
        binding.tilPassword.error = "Password yang anda masukkan salah"
        Util.requestFocus(binding.password,this)
    }

    override fun failedLogin(title: String, msg: String, isFinish: Boolean) {
        Util.showRoundedDialog(this,title,msg,false,object : DialogYesListener{
            @SuppressLint("DefaultLocale")
            override fun onYes() {
                if (isFinish){
                    if (msg.toLowerCase().contains("kata sandi")){
                        val intent = Intent(this@LoginActivity,PasswordActivity::class.java)
                        startActivityForResult(intent,522)
                    }else{
                        finish()
                    }
                }
            }
        },object : DialogNoListener{
            override fun onNo() {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 522){
                finish()
                startActivity(Intent(this,MainActivity::class.java))
            }
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