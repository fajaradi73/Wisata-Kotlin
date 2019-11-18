package com.fajarproject.travels.register

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fajarproject.travels.R
import com.fajarproject.travels.password.Password
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_account.*


/**
 * Created by Fajar Adi Prasetyo on 27/10/19.
 */

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        setToolbar()
        setUI()
    }


    private fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
    }

    private fun setUI(){
        fab.setOnClickListener {v: View? ->
            if (!checkMandatory()!!){
                Snackbar.make(v!!,"Harap untuk mengisi semua field yang ada",Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }else{
                val intent = Intent(this,Password::class.java)
                intent.putExtra(Constant.userName,userName.text.toString())
                intent.putExtra(Constant.fullName,fullName.text.toString())
                intent.putExtra(Constant.Email,email.text.toString())
                startActivity(intent)
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

    private fun checkMandatory() : Boolean? {
        var isValid : Boolean? = true
        if(userName.length() == 0){
            isValid = false
        }
        if (email.length() == 0 || !Util.isValidEmail(email.text.toString().trim())) {
            isValid = false
        }
        return isValid
    }

}
