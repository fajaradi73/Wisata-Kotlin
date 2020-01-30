package com.fajarproject.travels.feature.password

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.fajarproject.travels.R
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.models.request.PasswordRequest
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.activity_password.*


/**
 * Created by Fajar Adi Prasetyo on 27/10/19.
 */

@SuppressLint("SetTextI18n")
class Password : MvpActivity<PasswordPresenter>(),PasswordView {
    lateinit var list: MutableList<String?>

    override fun createPresenter(): PasswordPresenter {
        val userApi = Util.getRetrofitRxJava2()!!.create(UserApi::class.java)
        return PasswordPresenter(this,this,userApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        setToolbar()
        getDataIntent()
        setUI()
    }

    override fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
    }
    var isNewPassword = false

    override fun getDataIntent(){
        isNewPassword = intent.getBooleanExtra(Constant.typePassword,false)
        if (isNewPassword){
            title = "Ganti Kata Sandi"
            parent_create.visibility = View.GONE
            parent_change.visibility = View.VISIBLE
            cvEdit.isEnabled = false
            clEdit.background = ContextCompat.getDrawable(this@Password,R.drawable.ic_grey)
        }else{
            title = "Buat Kata Sandi"
            parent_create.visibility = View.VISIBLE
            parent_change.visibility = View.GONE
            cvEdit.isEnabled = false
            clEdit.background = ContextCompat.getDrawable(this@Password,R.drawable.ic_grey)
        }
    }

    override fun setUI(){
        password_old.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tilPasswordOld.isErrorEnabled = false
            }

        })
        password_new.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                list = arrayListOf()
                if (!Util.isValidPassword(s.toString(),list,this@Password)) {
                    var string = ""
                    for (error in list) {
                        string += "- $error\n"
                    }
                    tilPasswordNew.error = string
                    cvEdit.isEnabled = false
                    clEdit.background = ContextCompat.getDrawable(this@Password,R.drawable.ic_grey)
                }else{
                    tilPasswordNew.isErrorEnabled = false
                    cvEdit.isEnabled = true
                    clEdit.background = ContextCompat.getDrawable(this@Password,R.drawable.ic_gradient)
                }
            }
        })
        password.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                list = arrayListOf()
                if (!Util.isValidPassword(s.toString(),list,this@Password)) {
                    var string = ""
                    for (error in list) {
                        string += "- $error\n"
                    }
                    tilPassword.error = string
                    cvEdit.isEnabled = false
                    clEdit.background = ContextCompat.getDrawable(this@Password,R.drawable.ic_grey)
                }else{
                    tilPassword.isErrorEnabled = false
                    cvEdit.isEnabled = true
                    clEdit.background = ContextCompat.getDrawable(this@Password,R.drawable.ic_gradient)
                }
            }
        })
        cvEdit.setOnClickListener {
            val request = PasswordRequest()
            request.passwordOld = password_old.text.toString()
            request.passwordNew = password_new.text.toString()
            confirmSubmit(request)
        }
    }

    override fun showLoading() {
        loadingOverlay.visibility =  View.VISIBLE
    }

    override fun hideLoading() {
        loadingOverlay.visibility =  View.GONE
    }

    override fun confirmSubmit(request: PasswordRequest) {
        Util.showRoundedDialog(this,"Ganti Kata Sandi","Apakah anda yakin untuk mengubah kata sandi anda?",true,object : DialogYesListener{
            override fun onYes() {
                presenter?.changePassword(request)
            }
        },object: DialogNoListener{
            override fun onNo() {

            }
        })
    }

    override fun failedDialog(title: String, msg: String,isNew : Boolean) {
        Util.showRoundedDialog(this,title,msg,false,object : DialogYesListener{
            override fun onYes() {
                if (isNewPassword){
                    tilPassword.error = msg
                    Util.requestFocus(password, this@Password)
                }else {
                    if (isNew) {
                        tilPasswordNew.error = msg
                        Util.requestFocus(password_new, this@Password)
                    } else {
                        tilPasswordOld.error = msg
                        Util.requestFocus(password_old, this@Password)
                    }
                }
            }
        },object: DialogNoListener{
            override fun onNo() {

            }
        })
    }

    override fun failedSubmit(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    override fun successSubmit(title: String, msg: String) {
        Util.showRoundedDialog(this,title,msg,false,object : DialogYesListener{
            override fun onYes() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        },object: DialogNoListener{
            override fun onNo() {

            }
        })
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
