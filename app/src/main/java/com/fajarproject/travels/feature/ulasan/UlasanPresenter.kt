package com.fajarproject.travels.feature.ulasan

import android.app.Activity
import com.fajarproject.travels.api.UlasanApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.models.UlasanModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Create by Fajar Adi Prasetyo on 14/01/2020.
 */
class UlasanPresenter(view: UlasanView, val context: Activity, override var apiStores: UlasanApi):BasePresenter<UlasanView,UlasanApi>() {
    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun getUlasan(idWisata : Int){
        view?.showLoading()
        addSubscribe(apiStores.getUlasan(user.token,idWisata),object : NetworkCallback<UlasanModel>(){
            override fun onSuccess(model: UlasanModel) {
                view?.getDataSuccess(model)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else{
                    view?.getDataFail(message!!)
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }

        })
    }
}