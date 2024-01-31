package com.fajarproject.travels.ui.password

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
import com.fajarproject.travels.databinding.ActivityPasswordBinding
import com.fajarproject.travels.models.request.PasswordRequest
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util


/**
 * Created by Fajar Adi Prasetyo on 27/10/19.
 */

@SuppressLint("SetTextI18n")
class PasswordActivity : MvpActivity<PasswordPresenter>(), PasswordView {
	lateinit var list: MutableList<String?>
	private lateinit var binding: ActivityPasswordBinding
	override fun createPresenter(): PasswordPresenter {
		val userApi = Util.getRetrofitRxJava2(this)!!.create(UserApi::class.java)
		return PasswordPresenter(this, this, userApi)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityPasswordBinding.inflate(layoutInflater)
		setContentView(binding.root)
		setToolbar()
		getDataIntent()
		setUI()
	}

	override fun setToolbar() {
		setSupportActionBar(binding.toolbar)
		supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        Util.setColorFilter(toolbar.navigationIcon!!,R.color.white)
	}

	var isNewPassword = false

	override fun getDataIntent() {
		isNewPassword = intent.getBooleanExtra(Constant.typePassword, false)
		if (isNewPassword) {
			title = "Ganti Kata Sandi"
			binding.parentCreate.visibility = View.GONE
			binding.parentChange.visibility = View.VISIBLE
			binding.cvEdit.isEnabled = false
			binding.clEdit.background =
				ContextCompat.getDrawable(this@PasswordActivity, R.drawable.ic_grey)
		} else {
			title = "Buat Kata Sandi"
			binding.parentCreate.visibility = View.VISIBLE
			binding.parentChange.visibility = View.GONE
			binding.cvEdit.isEnabled = false
			binding.clEdit.background =
				ContextCompat.getDrawable(this@PasswordActivity, R.drawable.ic_grey)
		}
	}

	override fun setUI() {
		binding.passwordOld.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				binding.tilPasswordOld.isErrorEnabled = false
			}

		})
		binding.passwordNew.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				list = arrayListOf()
				if (!Util.isValidPassword(s.toString(), list, this@PasswordActivity)) {
					var string = ""
					for (error in list) {
						string += "- $error\n"
					}
					binding.tilPasswordNew.error = string
					binding.cvEdit.isEnabled = false
					binding.clEdit.background =
						ContextCompat.getDrawable(this@PasswordActivity, R.drawable.ic_grey)
				} else {
					binding.tilPasswordNew.isErrorEnabled = false
					binding.cvEdit.isEnabled = true
					binding.clEdit.background =
						ContextCompat.getDrawable(this@PasswordActivity, R.drawable.ic_gradient)
				}
			}
		})
		binding.password.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				list = arrayListOf()
				if (!Util.isValidPassword(s.toString(), list, this@PasswordActivity)) {
					var string = ""
					for (error in list) {
						string += "- $error\n"
					}
					binding.tilPassword.error = string
					binding.cvEdit.isEnabled = false
					binding.clEdit.background =
						ContextCompat.getDrawable(this@PasswordActivity, R.drawable.ic_grey)
				} else {
					binding.tilPassword.isErrorEnabled = false
					binding.cvEdit.isEnabled = true
					binding.clEdit.background =
						ContextCompat.getDrawable(this@PasswordActivity, R.drawable.ic_gradient)
				}
			}
		})
		binding.cvEdit.setOnClickListener {
			val request = PasswordRequest()
			request.passwordOld = binding.passwordOld.text.toString()
			request.passwordNew = binding.passwordNew.text.toString()
			confirmSubmit(request)
		}
	}

	override fun showLoading() {
		binding.loadingOverlay.visibility = View.VISIBLE
	}

	override fun hideLoading() {
		binding.loadingOverlay.visibility = View.GONE
	}

	override fun confirmSubmit(request: PasswordRequest) {
		Util.showRoundedDialog(
			this,
			"Ganti Kata Sandi",
			"Apakah anda yakin untuk mengubah kata sandi anda?",
			true,
			object : DialogYesListener {
				override fun onYes() {
					presenter?.changePassword(request)
				}
			},
			object : DialogNoListener {
				override fun onNo() {

				}
			})
	}

	override fun failedDialog(title: String, msg: String, isNew: Boolean) {
		Util.showRoundedDialog(this, title, msg, false, object : DialogYesListener {
			override fun onYes() {
				if (isNewPassword) {
                    binding.tilPassword.error = msg
					Util.requestFocus(binding.password, this@PasswordActivity)
				} else {
					if (isNew) {
                        binding.tilPasswordNew.error = msg
						Util.requestFocus(binding.passwordNew, this@PasswordActivity)
					} else {
                        binding.tilPasswordOld.error = msg
						Util.requestFocus(binding.passwordOld, this@PasswordActivity)
					}
				}
			}
		}, object : DialogNoListener {
			override fun onNo() {

			}
		})
	}

	override fun failedSubmit(msg: String) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
	}

	override fun successSubmit(title: String, msg: String) {
		Util.showRoundedDialog(this, title, msg, false, object : DialogYesListener {
			override fun onYes() {
				setResult(Activity.RESULT_OK)
				finish()
			}
		}, object : DialogNoListener {
			override fun onNo() {

			}
		})
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
