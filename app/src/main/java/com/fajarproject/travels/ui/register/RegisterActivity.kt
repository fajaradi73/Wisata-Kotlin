package com.fajarproject.travels.ui.register

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
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.databinding.ActivityCreateAccountBinding
import com.fajarproject.travels.models.RegisterSubmitModel
import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.ui.opsiLogin.OpsiLoginActivity
import com.fajarproject.travels.util.Util
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

class RegisterActivity : MvpActivity<RegisterPresenter>(), RegisterView {

	private lateinit var binding: ActivityCreateAccountBinding
	override fun createPresenter(): RegisterPresenter {
		val registerApi: RegisterApi =
			Util.getRetrofitRxJava2(this)!!.create(RegisterApi::class.java)
		return RegisterPresenter(this, this, registerApi)
	}

	lateinit var list: MutableList<String?>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityCreateAccountBinding.inflate(layoutInflater)
		setContentView(binding.root)
		setToolbar()
		setUI()
	}

	override fun setToolbar() {
		setSupportActionBar(binding.toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
		title = getString(R.string.titleRegister)
	}

	override fun setUI() {
		binding.btnRegistrasi.setOnClickListener { v: View? ->
			list = arrayListOf()
			if (!checkValidation()) {
				Snackbar.make(v!!, "Harap untuk mengisi semua field yang ada", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
			} else {
				val registerModel =
					RegisterSubmitModel(
						binding.userName.text.toString(),
						binding.fullName.text.toString(),
						binding.email.text.toString(),
						binding.password.text.toString(),
						"Email"
					)
				presenter?.registerWisata(registerModel)
			}
		}
		binding.password.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {

			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				if (binding.password.length() > 0) {
					binding.tilPassword.isErrorEnabled = false
				}
			}
		})
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

	override fun getDataSuccess(data: SaveModel) {
		Util.showRoundedDialog(this, data.title, data.message, false, object : DialogYesListener {
			override fun onYes() {
				val intent = Intent(this@RegisterActivity, OpsiLoginActivity::class.java)
				intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
				changeActivity(intent)
			}
		}, object : DialogNoListener {
			override fun onNo() {

			}
		})
	}

	override fun getDataFail(jsonObject: JSONObject) {
		val message = jsonObject.getString("message")
		Util.showRoundedDialog(this, message, "", false, object : DialogYesListener {
			override fun onYes() {

			}
		}, object : DialogNoListener {
			override fun onNo() {

			}
		})
	}

	override fun checkValidation(): Boolean {
		var isValid = true
		if (binding.userName.length() == 0) {
			isValid = false
			binding.tilUsername.error = "Harap untuk memasukkan email"
		}
		if (!Util.isValidEmail(binding.email.text.toString().trim())) {
			isValid = false
			binding.tilEmail.error = "Harap untuk memasukkan email yang benar"
		}
		if (binding.password.length() == 0) {
			binding.tilPassword.error = "Harap untuk memasukkan kata sandi"
			isValid = false
		}
		if (!Util.isValidPassword(binding.password.text.toString(), list, this)) {
			var string = ""
			for (error in list) {
				string += "- $error\n"
			}
			binding.tilPassword.error = string
			isValid = false
		}
		return isValid
	}

	override fun changeActivity(intent: Intent) {
		startActivity(intent)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			finish()
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		return true
	}
}