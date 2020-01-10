package com.fajarproject.travels.feature.profil

import android.app.Activity
import android.content.Intent
import com.facebook.login.LoginManager
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.feature.opsiLogin.OpsiLoginActivity
import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.util.Util
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

class ProfilPresenter(view: ProfilView, val context: Activity, override var apiStores: UserApi): BasePresenter<ProfilView,UserApi>(){

    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun getProfile(){
        view!!.showLoading()
        addSubscribe(apiStores.getProfile(user.token),object : NetworkCallback<ProfileModel>(){
            override fun onSuccess(model: ProfileModel) {
                view!!.getDataSuccess(model)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else {
                    view!!.getDataFail(message!!)
                }
            }

            override fun onFinish() {
                view!!.hideLoading()
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