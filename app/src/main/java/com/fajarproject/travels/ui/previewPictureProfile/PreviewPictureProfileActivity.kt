package com.fajarproject.travels.ui.previewPictureProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.bumptech.glide.Glide
import com.fajarproject.travels.R
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.databinding.ActivityPreviewPictureProfileBinding
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.util.fileUtil.FileUtil
import com.fajarproject.travels.util.fileUtil.FileUtilCallbacks
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import lv.chi.photopicker.PhotoPickerFragment

/**
 * Create by Fajar Adi Prasetyo on 17/01/2020.
 */
class PreviewPictureProfileActivity : MvpActivity<PreviewPictureProfilePresenter>(),
    PreviewPictureProfileView, FileUtilCallbacks, PhotoPickerFragment.Callback {
    private var fileUtil : FileUtil? = null
    private var isBackground = false
    private lateinit var binding : ActivityPreviewPictureProfileBinding

    override fun createPresenter(): PreviewPictureProfilePresenter {
        val userApi = Util.getRetrofitRxJava2(this)!!.create(UserApi::class.java)
        return PreviewPictureProfilePresenter(this,this,userApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewPictureProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        fileUtil        = FileUtil(this,this)
        val url         = intent.getStringExtra("ImageUrl")
        isBackground    = intent.getBooleanExtra("isBackground",false)
        Glide.with(this).load(url).error(R.drawable.ic_man).placeholder(
            Util.circleLoading(this)).thumbnail(0.1f).into(binding.detailImage)
        binding.detailImage.setOnTouchListener(ImageMatrixTouchHandler(this))
    }

    override fun showLoading() {
        binding.loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
    }

    override fun setToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(binding.toolbar.navigationIcon!!, ContextCompat.getColor(this,R.color.white))
        val statusBarHeight = Util.getStatusBarHeight(this)
        val size = Util.convertDpToPixel(24F)
        Util.setMargins(binding.toolbar,0,statusBarHeight ,0,0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.setFlags(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            )
            window.statusBarColor = ContextCompat.getColor(this, R.color.greyTransparent)

            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (statusBarHeight > size) {
                val decor = window.decorView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
    }


    override fun successUpload(title: String, message: String) {
        Util.showRoundedDialog(this,title,message,false,object : DialogYesListener{
            override fun onYes() {
                setResult(Activity.RESULT_OK)
                finish()
            }

        },object : DialogNoListener{
            override fun onNo() {

            }

        })
    }

    override fun failedUpload(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                fileUtil?.getPath(resultUri,Build.VERSION.SDK_INT)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                error.printStackTrace()
            }
        }
    }

    private fun openPicker() {
        val fragmentManager = supportFragmentManager
        PhotoPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            theme = R.style.ChiliPhotoPicker_Dark
        ).show(fragmentManager, "picker")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }else if(item.itemId == R.id.action_change){
            openPicker()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.preview_menu,menu)
        return true
    }

    override fun FileUtilonStartListener() {

    }

    override fun FileUtilonProgressUpdate(progress: Int) {

    }

    override fun FileUtilonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (wasSuccessful){
            if (isBackground){
                presenter?.uploadPictureBackground(path!!)
            }else {
                presenter?.uploadPicture(path!!)
            }
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        CropImage.activity(photos[0])
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }


}
