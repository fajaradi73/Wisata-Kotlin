package com.fajarproject.travels.feature.pictureWisata

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.PictureAdapter
import com.fajarproject.travels.api.PictureApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.base.widget.GridSpacingItemDecoration
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.util.fileUtil.FileUtil
import com.fajarproject.travels.util.fileUtil.FileUtilCallbacks
import kotlinx.android.synthetic.main.activity_picture_wisata.*
import lv.chi.photopicker.PhotoPickerFragment
import kotlin.collections.ArrayList


/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */

class PictureWisataActivity : MvpActivity<PictureWisataPresenter>(),PictureWisataView,FileUtilCallbacks,PhotoPickerFragment.Callback  {

    override fun createPresenter(): PictureWisataPresenter {
        val pictureApi = Util.getRetrofitRxJava2()!!.create(PictureApi::class.java)
        return PictureWisataPresenter(this,this,pictureApi)
    }

    private var idWisata = 0
    private var fileUtil : FileUtil? = null
    private var list: MutableList<String> = ArrayList()
    private var isSafeBackPressed = true
    private var newImage = false
    private var namaWisata : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_wisata)
        setUI()
        idWisata    = intent.getIntExtra(Constant.IdWisata,0)
        namaWisata  = intent.getStringExtra(Constant.NamaWisata)
        setToolbar()
        presenter?.getPicture(idWisata)
        setAction()
    }

    override fun showLoading() {
        isSafeBackPressed = false
        loadingOverlay.visibility   = View.VISIBLE
    }

    override fun hideLoading() {
        isSafeBackPressed = true
        loadingOverlay.visibility   = View.GONE
    }

    override fun getDataSuccess(list: List<PictureItem>) {
        val adapter         = PictureAdapter(list,this)
        listFoto.adapter    = adapter
    }

    override fun getDataFail(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Kumpulan foto dari $namaWisata"
    }

    override fun setUI() {
        listFoto.layoutManager = GridLayoutManager(this,3)
        listFoto.addItemDecoration(
            GridSpacingItemDecoration(
                activity!!,
                3,
                1,
                true
            )
        )
        listFoto.itemAnimator = DefaultItemAnimator()
        fileUtil = FileUtil(this, this)
    }

    override fun changeActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun setAction() {
        fabAdd.setOnClickListener {
            openPicker()
        }
    }

    private fun openPicker() {
        PhotoPickerFragment.newInstance(
            multiple = true,
            maxSelection = 5,
            allowCamera = true,
            theme = R.style.ChiliPhotoPicker_Dark
        ).show(supportFragmentManager, "picker")
    }

    override fun successUpload(title: String, message: String) {
        newImage = true
        Util.showRoundedDialog(this,title,message,false,object : DialogYesListener{
            override fun onYes() {
                presenter?.getPicture(idWisata)
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
            presenter?.uploadPicture(idWisata,list)
        }
    }
}