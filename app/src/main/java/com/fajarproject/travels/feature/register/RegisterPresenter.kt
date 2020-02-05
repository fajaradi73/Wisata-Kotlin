package com.fajarproject.travels.feature.register

import android.content.Context
import com.fajarproject.travels.api.RegisterApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.models.RegisterSubmitModel
import com.fajarproject.travels.models.SaveModel
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

class RegisterPresenter(view: RegisterView, val context: Context,
                        override var apiStores: RegisterApi) : BasePresenter<RegisterView,RegisterApi>() {
    init {
        super.attachView(view)
    }

    fun registerWisata(registerSubmitModel: RegisterSubmitModel){
        view?.showLoading()
        addSubscribe(apiStores.registerWisata(registerSubmitModel),object : NetworkCallback<SaveModel>(){
            override fun onSuccess(model: SaveModel) {
                view?.getDataSuccess(model)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                view?.getDataFail(jsonObject!!)
            }

            override fun onFinish() {
                view?.hideLoading()
            }
        })
    }
}