package com.fajarproject.travels.ui.pictureWisata

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.PictureAdapter
import com.fajarproject.travels.api.PictureApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.base.widget.GridSpacingItemDecoration
import com.fajarproject.travels.databinding.ActivityPictureWisataBinding
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.util.fileUtil.FileUtil
import com.fajarproject.travels.util.fileUtil.FileUtilCallbacks
import lv.chi.photopicker.PhotoPickerFragment
import kotlin.collections.ArrayList


/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
@RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
class PictureWisataActivity : MvpActivity<PictureWisataPresenter>(),PictureWisataView,FileUtilCallbacks,PhotoPickerFragment.Callback  {

    override fun createPresenter(): PictureWisataPresenter {
        val pictureApi = Util.getRetrofitRxJava2(this)!!.create(PictureApi::class.java)
        return PictureWisataPresenter(this,this,pictureApi)
    }

    private var idWisata : String? = ""
    private var fileUtil : FileUtil? = null
    private var list: MutableList<String> = ArrayList()
    private var isSafeBackPressed = true
    private var newImage = false
    private var namaWisata : String? = ""
    private lateinit var binding : ActivityPictureWisataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureWisataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUI()
        idWisata    = intent.getStringExtra(Constant.IdWisata)
        namaWisata  = intent.getStringExtra(Constant.NamaWisata)
        setToolbar()
        presenter?.getPicture(idWisata!!)
        setAction()
    }

    override fun showLoading() {
        isSafeBackPressed = false
        binding.loadingOverlay.visibility   = View.VISIBLE
    }

    override fun hideLoading() {
        isSafeBackPressed = true
        binding.loadingOverlay.visibility   = View.GONE
    }

    override fun getDataSuccess(list: List<PictureItem>) {
        val adapter         = PictureAdapter(list,this)
        binding.listFoto.adapter    = adapter
    }

    override fun getDataFail(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    override fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Kumpulan foto dari $namaWisata"
    }

    override fun setUI() {
        binding.listFoto.layoutManager = GridLayoutManager(this,3)
        binding.listFoto.addItemDecoration(
            GridSpacingItemDecoration(
                this,
                3,
                1,
                true
            )
        )
        binding.listFoto.itemAnimator = DefaultItemAnimator()
        fileUtil = FileUtil(this, this)
    }

    override fun changeActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun setAction() {
        binding.fabAdd.setOnClickListener {
            openPicker()
        }
    }

    private fun openPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 5)
            intent.type = "image/*"

            startActivityForResult(intent, 888)
        }else {
            PhotoPickerFragment.newInstance(
                multiple = true,
                maxSelection = 5,
                allowCamera = true,
                theme = R.style.ChiliPhotoPicker_Dark
            ).show(supportFragmentManager, "picker")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 888) {
                if(data?.clipData != null) {
                    val images = data.clipData!!
                    list.clear()
                    for (i in 0 until images.itemCount){
                        val uri = images.getItemAt(i).uri
                        fileUtil?.getPath(
                            uri,
                            Build.VERSION.SDK_INT
                        )
                    }
                    if (list.size > 0) {
                        presenter?.uploadPicture(idWisata!!, list)
                    }
                }

            }
        }
    }

    override fun successUpload(title: String, message: String) {
        newImage = true
        Util.showRoundedDialog(this,title,message,false,object : DialogYesListener{
            override fun onYes() {
                presenter?.getPicture(idWisata!!)
            }
        },object : DialogNoListener{
            override fun onNo() {

            }
        })
    }

    override fun failedUpload(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
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
            list.add(path!!)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (isSafeBackPressed){
            if(newImage){
                setResult(Activity.RESULT_OK)
                finish()
            }else {
                super.onBackPressed()
            }
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        for (i in 0 until photos.size) {
            fileUtil?.getPath(
                photos[i],
                Build.VERSION.SDK_INT
            )
        }
        if (list.size > 0){
            presenter?.uploadPicture(idWisata!!,list)
        }
    }
}