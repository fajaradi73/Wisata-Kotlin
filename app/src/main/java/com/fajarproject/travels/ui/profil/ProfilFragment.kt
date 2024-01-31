package com.fajarproject.travels.ui.profil

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.fajarproject.travels.FlavorConfig
import com.fajarproject.travels.R
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.databinding.FragmentProfilBinding
import com.fajarproject.travels.ui.editProfil.EditProfilActivity
import com.fajarproject.travels.ui.main.MainActivity
import com.fajarproject.travels.ui.password.PasswordActivity
import com.fajarproject.travels.ui.previewPictureProfile.PreviewPictureProfileActivity
import com.fajarproject.travels.ui.wisataFavorite.FavoriteWisataActivity
import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import com.fajarproject.travels.util.fileUtil.FileUtil
import com.fajarproject.travels.util.fileUtil.FileUtilCallbacks
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
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
    private lateinit var binding : FragmentProfilBinding
    override fun createPresenter(): ProfilPresenter {
        activity = context as MainActivity
        val userApi = Util.getRetrofitRxJava2(context as MainActivity)!!.create(UserApi::class.java)

        return ProfilPresenter(this,requireActivity(),userApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAction()
        binding.version.text = "Version " + Util.getAppVersion()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isConnection) {
            presenter?.getProfile(true)
        }
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            presenter?.getProfile(true)
        }
        fileUtil = FileUtil(requireContext(),this)
    }

    override fun showLoading() {
        binding.loadingOverlay.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
    }

    override fun showLoadingShimmer() {
        binding.shimmerView.visibility  = View.VISIBLE
        binding.shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        binding.shimmerView.startShimmer()
        binding.swipeRefresh.visibility     = View.GONE
    }

    override fun hideLoadingShimmer() {
        binding.shimmerView.stopShimmer()
        binding.shimmerView.visibility      = View.GONE
        binding.swipeRefresh.visibility     = View.VISIBLE
    }

    override fun getDataFail(message: String) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
    }

    override fun getDataSuccess(model: ProfileModel) {
        this.data       = model
        binding.userName.text   = Util.checkDataNull(model.username)
        binding.fullName.text   = Util.checkDataNull(model.fullname)
        binding.tvEmail.text    = Util.checkDataNull(model.email)
        binding.tvPhone.text    = Util.checkDataNull(model.mobilePhone)
        binding. tvLast.text     = Util.convertLongToDateWithTime(model.lastLogin!!)
        binding.tvFavorite.text = model.jumlahFavorite.toString()
        binding.tvGender.text   = Util.checkDataNull(model.gender)
        if (Util.sign(model.tanggal_lahir!!) != -1){
            binding. tvBirth.text    = model.tempat_lahir + " / " + Util.convertLongToDate(model.tanggal_lahir)
        }else{
            binding. tvBirth.text = "-"
        }
        if (model.picture != null && model.picture != ""){
            setImage(model.picture)
        }
        if (model.picture_background != null && model.picture_background != ""){
            setImageBackground(model.picture_background)
        }
        Glide.with(requireActivity()).load(R.drawable.love).into(binding.ivFavorite)
    }

    override fun changeActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun showDialogLogout() {
        Util.showRoundedDialog(requireActivity(),"Logout","Apakah anda ingin keluar dari akun ini?",true,
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
            FlavorConfig.baseImageProfile() + imageUrl
        }
        Glide.with(requireActivity())
            .load(image)
            .error(R.drawable.ic_man)
            .placeholder(Util.circleLoading(requireActivity()))
            .into(binding.imageUser)
        binding.clImage.setOnClickListener {
            isBackground = false
            previewImageProfil(FlavorConfig.baseImageProfile() + imageUrl,binding.imageUser)
        }
    }

    override fun setImageBackground(imageUrl: String) {
        Glide.with(requireActivity())
            .load(FlavorConfig.baseImageBackground() + imageUrl)
            .error(R.drawable.image_dieng)
            .placeholder(Util.circleLoading(requireActivity()))
            .into(binding.imageBackground)
        binding.cvBackground.setOnClickListener {
            isBackground = true
            previewImageProfil(FlavorConfig.baseImageBackground() + imageUrl,binding.imageBackground)
        }
    }

    override fun previewImageProfil(imageUrl: String,view : View){
        // Ordinary Intent for launching a new activity
        val intent =
            Intent(requireActivity(), PreviewPictureProfileActivity::class.java)

        // Get the transition name from the string
        val transitionName = getString(R.string.transition_string)

        val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            view,  // Starting view
            transitionName // The String
        )
        //Start the Intent
        intent.putExtra("ImageUrl",imageUrl)
        intent.putExtra("isBackground",isBackground)
        ActivityCompat.startActivityForResult(requireActivity(), intent, 1999,options.toBundle())

    }

    override fun previewResult() {
        presenter?.getProfile(false)
    }

    override fun setAction() {
        binding.cvLogout.setOnClickListener {
            showDialogLogout()
        }
        binding.clFavorite.setOnClickListener {
            changeActivity(Intent(activity,FavoriteWisataActivity::class.java))
        }
        binding.cvUploadPicture.setOnClickListener {
            isBackground = false
            openPicker()
        }
        binding.cvUploadBackground.setOnClickListener {
            isBackground = true
            openPicker()
        }
        binding.clEdit.setOnClickListener {
            val intent = Intent(activity,EditProfilActivity::class.java)
            intent.putExtra(Constant.dataProfil,Parcels.wrap(data))
            startActivityForResult(intent,Constant.requestIDProfil)
        }
        binding.clPassword.setOnClickListener {
            val intent = Intent(activity,PasswordActivity::class.java)
            intent.putExtra(Constant.typePassword,true)
            startActivityForResult(intent,Constant.requestChangePassword)
        }
    }

    override fun onImagePicked(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
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
        Util.showRoundedDialog(requireActivity(),title,message,false)
    }

    override fun failedUpload(message: String) {
        Toast.makeText(requireActivity(),message, Toast.LENGTH_LONG).show()
    }

    private fun openPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }else {
            val fragmentManager = (activity as MainActivity).supportFragmentManager
            PhotoPickerFragment.newInstance(
                multiple = false,
                allowCamera = true,
                theme = R.style.ChiliPhotoPicker_Dark
            ).show(fragmentManager, "picker")
        }
    }

    private val pickMedia = this.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(requireContext(), this)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
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