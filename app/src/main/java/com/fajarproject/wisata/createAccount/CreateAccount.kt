package com.fajarproject.wisata.createAccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fajarproject.wisata.MainActivity
import com.fajarproject.wisata.R
import com.fajarproject.wisata.password.Password
import com.fajarproject.wisata.util.Constant
import com.fajarproject.wisata.util.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_account.*


/**
 * Created by Fajar Adi Prasetyo on 27/10/19.
 */

class CreateAccount : AppCompatActivity() {

    private var fullname                : String? = null
    private var emailSosmed             : String? = null
    private var isSosmed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        setToolbar()
        setUI()
        getDataIntent()
    }

    private fun getDataIntent(){
        fullname        = intent.getStringExtra(Constant.Name)
        emailSosmed     = intent.getStringExtra(Constant.Email)
        val typeLogin = intent.getStringExtra(Constant.typeLogin)
        if (typeLogin != null && typeLogin == "Sosmed"){
            child_email.visibility  = View.GONE
            isSosmed    = true
        }else{
            child_email.visibility  = View.VISIBLE
        }
        if (fullname != null) {
            val parts: List<String?> = fullname!!.split(" ")
            if (parts.size == 2) {
                val first_name = parts[0]
                val last_name = parts[1]
                Log.d("Names", "$first_name/$last_name")
                firstName.setText(first_name)
                lastName.setText(last_name)
            } else if (parts.size == 3) {
                val first_name = parts[0]
                val middleName = parts[1]
                val last_name = parts[2]
                Log.d("Names", "$first_name/$last_name")
                firstName.setText(first_name)
                lastName.setText(last_name)
            }
        }
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
    }

    private fun setUI(){
        fab.setOnClickListener {v: View? ->
            if (!check_mandatory()!!){
                Snackbar.make(v!!,"Harap untuk mengisi semua field yang ada",Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }else{
                if(isSosmed){
                    startActivity(Intent(this,MainActivity::class.java))
                }else{
                    startActivity(Intent(this,Password::class.java))
                }

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

    private fun check_mandatory() : Boolean? {
        var isValid : Boolean? = true
        if(firstName.length() == 0){
            isValid = false
        }
        if (!isSosmed) {
            if (email.length() == 0 || !Util.isValidEmail(email.text.toString().trim())) {
                isValid = false
            }
        }
        return isValid
    }

}
