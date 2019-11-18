package com.fajarproject.travels.password

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fajarproject.travels.R
import com.fajarproject.travels.register.model.RegisterModel
import com.fajarproject.travels.register.presenter.RegisterPresenter
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.activity_password.*


/**
 * Created by Fajar Adi Prasetyo on 27/10/19.
 */

@SuppressLint("SetTextI18n")
class Password : AppCompatActivity() {
    lateinit var list: MutableList<String?>
    private var isError : Boolean? = false
    private var registerPresenter : RegisterPresenter? = null
    private var username : String? = ""
    private var fullname : String? = ""
    private var email    : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        setToolbar()
        setUI()
        registerPresenter = RegisterPresenter(this)
        getDataIntent()
        setUI()
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
    }

    private fun getDataIntent(){
        username    = intent.getStringExtra(Constant.userName)
        fullname    = intent.getStringExtra(Constant.fullName)
        email       = intent.getStringExtra(Constant.Email)
    }

    private fun setUI(){
        password.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (password.length() > 0){
                    error_password.visibility = View.GONE
                }
            }
        })
        fab.setOnClickListener {
            list = arrayListOf()
            error_password.visibility = View.GONE
            if (!checkMandatory()!!){
                error_password.visibility = View.VISIBLE
            }else {
                val registerModel = RegisterModel(username!!,fullname!!,email!!,password.text.toString(),"Email")
                registerPresenter!!.registerWisata(registerModel)
            }
        }

        show_hide_password.setOnClickListener {
            if (show_hide_password.text == getString(R.string.show)){
                password.transformationMethod = null
                show_hide_password.text = getString(R.string.hide)
                showError(isError!!)
            }else {
                password.transformationMethod = PasswordTransformationMethod()
                show_hide_password.text = getString(R.string.show)
                showError(isError!!)
            }
        }
    }

    private fun showError(isError : Boolean){
        if (isError){
            error_password.visibility = View.VISIBLE
        }else{
            error_password.visibility   = View.GONE
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

    private fun checkMandatory() : Boolean?{
        var isValid = true
        if (password.length() == 0){
            isError = true
            error_password.text = "Harap untuk memasukkan kata sandi"
            isValid = false
        }
        if (!Util.isValidPassword(password.text.toString(),list,this)) {

            isError = true
            var string = ""
            for (error in list) {
                string += "- $error\n"
            }
            error_password.text = string
            isValid = false
        }
        return isValid
    }
}
