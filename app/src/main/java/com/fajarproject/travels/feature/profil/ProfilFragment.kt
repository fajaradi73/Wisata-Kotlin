package com.fajarproject.travels.feature.profil

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.feature.editProfil.EditProfilActivity
import com.fajarproject.travels.feature.main.MainActivity
import com.fajarproject.travels.feature.password.Password
import com.fajarproject.travels.feature.previewPictureProfile.PreviewPictureProfile
import com.fajarproject.travels.feature.wisataFavorite.FavoriteWisataActivity
import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.util.fileUtil.FileUtil
import com.fajarproject.travels.util.fileUtil.FileUtilCallbacks
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_profil.*
import lv.chi.photopicker.PhotoPickerFragment
import org.parceler.Parcels


/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

@SuppressLint("SetTextI18n")
class ProfilFragment : MvpFragment<ProfilPresenter>(),ProfilView, FileUtilCallbacks {

    private var fileUtil : FileUtil? = null
    private var isBackground  = true
    var data : ProfileModel? = null

    override fun createPresenter(): ProfilPresenter {
        val userApi = Util.getRetrofitRxJava2()!!.create(UserApi::class.java)
        activity = context as MainActivity
        return ProfilPresenter(this,activity!!,userApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAction()
        version.text = "Version " + Util.getAppVersion()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isConnection) {
            presenter?.getProfile(true)
        }
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            presenter?.getProfile(true)
        }
        fileUtil = FileUtil(context!!,this)
    }

    override fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingOverlay.visibility = View.GONE
    }

    override fun showLoadingShimmer() {
        shimmerView.visibility  = View.VISIBLE
        shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        shimmerView.startShimmer()
        swipeRefresh.visibility     = View.GONE
    }

    override fun hideLoadingShimmer() {
        shimmerView.stopShimmer()
        shimmerView.visibility      = View.GONE
        swipeRefresh.visibility     = View.VISIBLE
    }

    override fun getDataFail(message: String) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
    }

    override fun getDataSuccess(model: ProfileModel) {
        this.data       = model
        userName.text   = Util.checkDataNull(model.username)
        fullName.text   = Util.checkDataNull(model.fullname)
        tvEmail.text    = Util.checkDataNull(model.email)
        tvPhone.text    = Util.checkDataNull(model.mobilePhone)
        tvLast.text     = Util.convertLongToDateWithTime(model.lastLogin!!)
        tvFavorite.text = model.jumlahFavorite.toString()
        tvGender.text   = Util.checkDataNull(model.gender)
        if (Util.sign(model.tanggal_lahir!!) != -1){
            tvBirth.text    = model.tempat_lahir + " / " + Util.convertLongToDate(model.tanggal_lahir)
        }else{
            tvBirth.text = "-"
        }
        if (model.picture != null && model.picture != ""){
            setImage(model.picture)
        }
        if (model.picture_background != null && model.picture_background != ""){
            setImageBackground(model.picture_background)
        }
        Glide.with(activity!!).load(R.drawable.love).into(ivFavorite)
    }

    override fun changeActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun showDialogLogout() {
        Util.showRoundedDialog(activity!!,"Logout","Apakah anda ingin keluar dari akun ini?",true,
            object : DialogYesListener{
                override fun onYes() {
                    presenter!!.logout()
                }
            },object : DialogNoListener{
                override fun onNo() {
                }
            })
    }

    override fun setImage(imageUrl : String) {
        val image = if (imageUrl.contains("google") || imageUrl.contains("facebook")){
            imageUrl
        }else{
            App.BASE_IMAGE_PROFILE + imageUrl
        }
        Glide.with(activity!!)
            .load(image)
            .error(R.drawable.ic_man)
            .placeholder(Util.circleLoading(activity!!))
            .into(imageUser)
        clImage.setOnClickListener {
            isBackground = false
            previewImageProfil(App.BASE_IMAGE_PROFILE + imageUrl,imageUser)
        }
    }

    override fun setImageBackground(imageUrl: String) {
        Glide.with(activity!!)
            .load(App.BASE_IMAGE_BACKGROUND + imageUrl)
            .error(R.drawable.image_dieng)
            .placeholder(Util.circleLoading(activity!!))
            .into(imageBackground)
        cvBackground.setOnClickListener {
            isBackground = true
            previewImageProfil(App.BASE_IMAGE_BACKGROUND + imageUrl,imageBackground)
        }
    }

    override fun previewImageProfil(imageUrl: String,view : View){
        // Ordinary Intent for launching a new activity
        val intent =
            Intent(activity!!, PreviewPictureProfile::class.java)

        // Get the transition name from the string
        val transitionName = getString(R.string.transition_string)

        val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity!!,
            view,  // Starting view
            transitionName // The String
        )
        //Start the Intent
        intent.putExtra("ImageUrl",imageUrl)
        intent.putExtra("isBackground",isBackground)
        ActivityCompat.startActivityForResult(activity!!, intent, 1999,options.toBundle())

    }

    override fun previewResult() {
        presenter?.getProfile(false)
    }

    override fun setAction() {
        cvLogout.setOnClickListener {
            showDialogLogout()
        }
        clFavorite.setOnClickListener {
            changeActivity(Intent(activity,FavoriteWisataActivity::class.java))
        }
        cvUploadPicture.setOnClickListener {
            isBackground = false
            openPicker()
        }
        cvUploadBackground.setOnClickListener {
            isBackground = true
            openPicker()
        }
        clEdit.setOnClickListener {
            val intent = Intent(activity,EditProfilActivity::class.java)
            intent.putExtra(Constant.dataProfil,Parcels.wrap(data))
            startActivityForResult(intent,Constant.requestIDProfil)
        }
        clPassword.setOnClickListener {
            val intent = Intent(activity,Password::class.java)
            intent.putExtra(Constant.typePassword,true)
            startActivityForResult(intent,Constant.requestChangePassword)
        }
    }

    override fun onImagePicked(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(context!!, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                fileUtil?.getPath(resultUri,Build.VERSION.SDK_INT)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                error.printStackTrace()
            }
        }else if (requestCode == Constant.requestIDProfil){
            if (resultCode == RESULT_OK){
                presenter?.getProfile(false)
            }
        }else if (requestCode == Constant.requestChangePassword){
            if (resultCode == RESULT_OK){
                presenter?.getProfile(true)
            }
        }
    }

    override fun successUpload(title: String, message: String) {
        presenter?.getProfile(false)
        Util.showRoundedDialog(activity!!,title,message,false)
    }

    override fun failedUpload(message: String) {
        Toast.makeText(activity!!,message, Toast.LENGTH_LONG).show()
    }

    private fun openPicker() {
        val fragmentManager = (activity as MainActivity).supportFragmentManager
        PhotoPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            theme = R.style.ChiliPhotoPicker_Dark
        ).show(fragmentManager, "picker")
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
        if (wasSuccessful && path != null){
            if (isBackground){
                presenter?.uploadPictureBackground(path)
            }else{
                presenter?.uploadPicture(path)
            }
        }
    }
}