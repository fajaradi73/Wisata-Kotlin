package com.fajarproject.travels.ui.profil

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.facebook.login.LoginManager
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.ui.opsiLogin.OpsiLoginActivity
import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.models.PictureModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.util.Util
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

class ProfilPresenter(view: ProfilView, val context: Activity, override var apiStores: UserApi): BasePresenter<ProfilView,UserApi>(){

    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun getProfile(isLoading : Boolean){
        if (isLoading){
            view?.showLoadingShimmer()
        }
        addSubscribe(apiStores.getProfile("Bearer "+ user.token),object : NetworkCallback<ProfileModel>(){
            override fun onSuccess(model: ProfileModel) {
                view?.getDataSuccess(model)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else {
                    view?.getDataFail(message!!)
                }
            }

            override fun onFinish() {
                view?.hideLoadingShimmer()
            }
        })
    }

    fun uploadPicture(path : String){
        view?.showLoading()
        val file = File(path)
        val fileType = Util.getMimeType(file.path)
        val imageBody = file.asRequestBody(fileType?.toMediaTypeOrNull())
        val picturePart = MultipartBody.Part.createFormData("picture", file.name.replace("\"\n",""), imageBody)
        addSubscribe(apiStores.uploadPicture("Bearer " + user.token,picturePart),object : NetworkCallback<PictureModel>(){
            override fun onSuccess(model: PictureModel) {
                view?.successUpload(model.title,model.message)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else{
                    view?.failedUpload(message!!)
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }

        })
    }
    fun uploadPictureBackground(path : String){
        view?.showLoading()
        val file = File(path)
        val fileType = Util.getMimeType(file.path)
        val imageBody = file.asRequestBody(fileType?.toMediaTypeOrNull())
        val picturePart = MultipartBody.Part.createFormData("picture", file.name.replace("\"\n",""), imageBody)
        addSubscribe(apiStores.uploadPictureBackground("Bearer " + user.token,picturePart),object : NetworkCallback<PictureModel>(){
            override fun onSuccess(model: PictureModel) {
                view?.successUpload(model.title,model.message)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else{
                    view?.failedUpload(message!!)
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }

        })
    }

    fun logout(){
        AppPreference.writePreference(context,"user","")
        /////// Logout Facebook
        LoginManager.getInstance().logOut()
        ///// Google Logout
        FirebaseAuth.getInstance().signOut()
        context.startActivity(Intent(context, OpsiLoginActivity::class.java))
        context.finish()
    }
}