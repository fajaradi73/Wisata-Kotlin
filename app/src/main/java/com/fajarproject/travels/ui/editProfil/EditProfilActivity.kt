package com.fajarproject.travels.ui.editProfil

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.fajarproject.travels.R
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.databinding.ActivityEditProfilBinding
import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.models.request.EditProfilRequest
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import org.parceler.Parcels
import java.util.*

/**
 * Create by Fajar Adi Prasetyo on 23/01/2020.
 */

class EditProfilActivity : MvpActivity<EditProfilPresenter>(),EditProfilView {

    private lateinit var mDatePicker: DatePickerDialog
    private lateinit var radioButton : RadioButton
    private lateinit var binding : ActivityEditProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        setUI()
        setAction()
        getDataInten()
    }

    override fun createPresenter(): EditProfilPresenter {
        val userApi = Util.getRetrofitRxJava2(this)!!.create(UserApi::class.java)
        return EditProfilPresenter(this,this,userApi)
    }

    override fun showLoading() {
        binding.loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
    }

    override fun confirmedDialog(request: EditProfilRequest){
        Util.showRoundedDialog(this,"Edit profil","Apakah anda yakin untuk mengubah data anda?",true,object : DialogYesListener{
            override fun onYes() {
                binding.cvEdit.isEnabled = false
                presenter?.updateProfil(request)
            }
        },object : DialogNoListener{
            override fun onNo() {

            }
        })
    }

    override fun successEdit(model: SaveModel) {
        Util.showRoundedDialog(this,model.title,model.message,false,object : DialogYesListener{
            override fun onYes() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        },object : DialogNoListener{
            override fun onNo() {

            }
        })
    }

    override fun failedEdit(msg: String) {
        binding.cvEdit.isEnabled = true
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    override fun getDataInten() {
        val data : ProfileModel = Parcels.unwrap(intent.getParcelableExtra(Constant.dataProfil))
        getDataProfil(data)
    }

    override fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.edit_profile)
    }

    override fun setUI() {
        val mCurrentDate    = Calendar.getInstance()
        val mYear           = mCurrentDate[Calendar.YEAR]
        val mMonth          = mCurrentDate[Calendar.MONTH]
        val mDay            = mCurrentDate[Calendar.DAY_OF_MONTH]

        mDatePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                binding.tanggalLahir.setText(
                    Util.convertDate(selectedYear, selectedMonth, selectedDay)
                )
            },
            mYear,
            mMonth,
            mDay
        )
        mDatePicker.datePicker.maxDate = Date().time
    }

    override fun setAction() {
        binding.tanggalLahir.setOnClickListener {
            mDatePicker.show()
        }
        binding.rgHubungan.setOnCheckedChangeListener { group, checkedId ->
            radioButton = group.findViewById(checkedId)
        }
        binding.cvEdit.setOnClickListener {
            val requestEdit = EditProfilRequest()
            requestEdit.username    = binding.userName.text.toString()
            requestEdit.fullname    = binding.fullName.text.toString()
            requestEdit.email       = binding.email.text.toString()
            requestEdit.gender      = radioButton.text.toString()
            requestEdit.mobilePhone = binding.phone.text.toString()
            requestEdit.birthPlace  = binding.tempatLahir.text.toString()
            requestEdit.birthDate   = Util.convertTanggal(binding.tanggalLahir.text.toString(),"dd MMMM yyyy","yyyy-MM-dd")
            confirmedDialog(requestEdit)
        }
    }

    @SuppressLint("DefaultLocale")
    override fun getDataProfil(data: ProfileModel) {
        binding.userName.setText(Util.checkDataNull(data.username))
        binding.fullName.setText(Util.checkDataNull(data.fullname))
        binding.email.setText(Util.checkDataNull(data.email))
        binding.phone.setText(Util.checkDataNull(data.mobilePhone))
        binding.tempatLahir.setText(Util.checkDataNull(data.tempat_lahir))
        if (data.gender?.toLowerCase()?.contains("pria")!!){
            binding.rgHubungan.check(R.id.rb_pria)
        }else if (data.gender.toLowerCase().contains("wanita")){
            binding.rgHubungan.check(R.id.rb_wanita)
        }

        if (Util.sign(data.tanggal_lahir!!) != -1) {
            binding.tanggalLahir.setText(Util.convertLongToDate(data.tanggal_lahir))
            mDatePicker.updateDate(
                Util.getYear(data.tanggal_lahir).toInt(),
                Util.getMonth(data.tanggal_lahir).toInt() - 1,
                Util.getDate(data.tanggal_lahir).toInt()
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

}
