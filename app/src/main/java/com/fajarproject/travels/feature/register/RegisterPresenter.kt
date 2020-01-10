package com.fajarproject.travels.feature.register

import android.content.Context
import com.fajarproject.travels.api.RegisterApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.models.RegisterSubmitModel
import com.fajarproject.travels.models.RegisterModel
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
        view!!.showLoading()
        addSubscribe(apiStores.registerWisatas(registerSubmitModel),object : NetworkCallback<RegisterModel>(){
            override fun onSuccess(model: RegisterModel) {
                view!!.getDataSuccess(model)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                view!!.getDataFail(jsonObject!!)
            }

            override fun onFinish() {
                view!!.hideLoading()
            }
        })
    }
}