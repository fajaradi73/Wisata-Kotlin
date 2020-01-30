package com.fajarproject.travels.feature.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.fajarproject.travels.R
import com.fajarproject.travels.api.RegisterApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.feature.opsiLogin.OpsiLoginActivity
import com.fajarproject.travels.models.RegisterSubmitModel
import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_account.*
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

class RegisterActivity : MvpActivity<RegisterPresenter>(),RegisterView{

    override fun createPresenter(): RegisterPresenter {
        val registerApi : RegisterApi = Util.getRetrofitRxJava2()!!.create(RegisterApi::class.java)
        return RegisterPresenter(this,this,registerApi)
    }
    lateinit var list: MutableList<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        setToolbar()
        setUI()
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
        title = getString(R.string.titleRegister)
    }

    override fun setUI() {
        btnRegistrasi.setOnClickListener {v: View? ->
            list = arrayListOf()
            if (!checkValidation()){
                Snackbar.make(v!!,"Harap untuk mengisi semua field yang ada", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }else{
                val registerModel =
                    RegisterSubmitModel(
                        userName.text.toString(),
                        fullName.text.toString(),
                        email.text.toString(),
                        password.text.toString(),
                        "Email"
                    )
                presenter?.registerWisata(registerModel)
            }
        }
        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (password.length() > 0){
                    tilPassword.isErrorEnabled = false
                }
            }
        })
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

    override fun getDataSuccess(data: SaveModel) {
        Util.showRoundedDialog(this,data.title,data.message,false,object : DialogYesListener{
            override fun onYes() {
                val intent = Intent(this@RegisterActivity, OpsiLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                changeActivity(intent)
            }
        },object : DialogNoListener{
            override fun onNo() {

            }
        })
    }

    override fun getDataFail(jsonObject: JSONObject) {
        val message = jsonObject.getString("message")
        Util.showRoundedDialog(this,message,"",false,object : DialogYesListener {
            override fun onYes() {

            }
        },object : DialogNoListener {
            override fun onNo() {

            }
        })
    }

    override fun checkValidation(): Boolean {
        var isValid = true
        if(userName.length() == 0){
            isValid = false
            tilUsername.error = "Harap untuk memasukkan email"
        }
        if (!Util.isValidEmail(email.text.toString().trim())) {
            isValid = false
            tilEmail.error = "Harap untuk memasukkan email yang benar"
        }
        if (password.length() == 0){
            tilPassword.error = "Harap untuk memasukkan kata sandi"
            isValid = false
        }
        if (!Util.isValidPassword(password.text.toString(),list,this)) {
            var string = ""
            for (error in list) {
                string += "- $error\n"
            }
            tilPassword.error = string
            isValid = false
        }
        return isValid
    }

    override fun changeActivity(intent: Intent) {
        startActivity(intent)
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