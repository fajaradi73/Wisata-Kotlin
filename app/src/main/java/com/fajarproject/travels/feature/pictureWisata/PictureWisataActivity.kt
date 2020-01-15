package com.fajarproject.travels.feature.pictureWisata

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.PictureAdapter
import com.fajarproject.travels.api.PictureApi
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.base.widget.GridSpacingItemDecoration
import com.fajarproject.travels.feature.previewPictureWisata.PreviewPictureActivity
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.util.fileUtil.FileUtil
import com.fajarproject.travels.util.fileUtil.FileUtilCallbacks
import kotlinx.android.synthetic.main.activity_picture_wisata.*
import org.parceler.Parcels
import java.util.*


/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
class PictureWisataActivity : MvpActivity<PictureWisataPresenter>(),PictureWisataView,FileUtilCallbacks {

    override fun createPresenter(): PictureWisataPresenter {
        val pictureApi = Util.getRetrofitRxJava2()!!.create(PictureApi::class.java)
        return PictureWisataPresenter(this,this,pictureApi)
    }

    private var idWisata = 0
    private var requestCodeStorage = 101
    private var mStoragePermissionGranted = false
    private var fileUtil : FileUtil? = null
    private var list: MutableList<String> = ArrayList()
    private var isSafeBackPressed = true
    private var newImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_wisata)
        setToolbar()
        setUI()
        idWisata    = intent.getIntExtra(Constant.IdWisata,0)
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

    override fun getDataSucces(list: List<PictureItem>) {
        val adapter         = PictureAdapter(list,this)
        listFoto.adapter    = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(this@PictureWisataActivity, PreviewPictureActivity::class.java)
                intent.putExtra(Constant.position,position)
                intent.putExtra(Constant.dataFoto, Parcels.wrap(list))
                changeActivity(intent)
            }
        })
    }

    override fun getDataFail(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            checkPermission()
        }
    }

    override fun getStoragePermission() {
        val permissionAccessStorageApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat
            .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessStorageApproved){
            mStoragePermissionGranted = true
            checkPermission()
        }else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCodeStorage
            )
        }
    }

    override fun checkPermission() {
        try {
            if (mStoragePermissionGranted){
                openFile()
            }else{
                getStoragePermission()
            }
        }catch (e : SecurityException){
            Log.e("ErrorPermission",e.message!!)
        }
    }

    override fun openFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 121)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 121){
                if (data != null) {
                    list = arrayListOf()
                    if (data.clipData != null) {
                        for (i in 0 until data.clipData!!.itemCount) {
                            fileUtil?.getPath(
                                data.clipData!!.getItemAt(i).uri,
                                Build.VERSION.SDK_INT
                            )
                        }
                    }else{
                        fileUtil?.getPath(data.data!!,Build.VERSION.SDK_INT)
                    }
                    if (list.size > 0){
                        presenter?.uploadPicture(idWisata,list)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mStoragePermissionGranted   = false
        when (requestCode) {
            requestCode -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mStoragePermissionGranted = true
                }
            }
        }
        checkPermission()
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
}