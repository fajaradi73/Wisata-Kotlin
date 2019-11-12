package com.fajarproject.wisata.password

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fajarproject.wisata.MainActivity
import com.fajarproject.wisata.R
import com.fajarproject.wisata.util.Util
import kotlinx.android.synthetic.main.activity_password.*


/**
 * Created by Fajar Adi Prasetyo on 27/10/19.
 */

class Password : AppCompatActivity() {
    lateinit var list: MutableList<String?>
    private var isError : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        setToolbar()
        setUI()
    }
    private fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
    }

    @SuppressLint("SetTextI18n")
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
            if (password.length() == 0){
                isError = true
                error_password.visibility = View.VISIBLE
                error_password.text = "Harap untuk memasukkan kata sandi"
            }else if (!Util.isValidPassword(password.text.toString(),list,this)){
                error_password.visibility = View.VISIBLE
                isError = true
                var string = ""
                for (error in list) {
                    string += "- $error\n"
                }
                error_password.text = string
            }else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        show_hide_password.setOnClickListener { v: View? ->
            if (show_hide_password.text == "Tampilkan"){
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
}
