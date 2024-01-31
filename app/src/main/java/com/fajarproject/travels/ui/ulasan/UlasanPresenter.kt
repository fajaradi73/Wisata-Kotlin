package com.fajarproject.travels.ui.ulasan

import android.app.Activity
import com.fajarproject.travels.api.UlasanApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.RattingModel
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.preference.AppPreference
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
    private val limitsize = AppPreference.getIntPreferenceByName(context,"sizePerPage")

    fun getRatting(idWisata: String){
        view?.showLoading()
        addSubscribe(apiStores.getRatting("Bearer " + user.token,idWisata),object : NetworkCallback<RattingModel>(){
            override fun onSuccess(model: RattingModel) {
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
                getUlasan(idWisata,limitsize!!,0)
            }
        })
    }

    fun getUlasan(idWisata : String,limit: Int,currentPage : Int){
        addSubscribe(apiStores.getUlasan("Bearer " + user.token,limit,currentPage,idWisata),object : NetworkCallback<MutableList<UlasanItem>>(){
            override fun onSuccess(model: MutableList<UlasanItem>) {
                view?.setDataUlasan(model)
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