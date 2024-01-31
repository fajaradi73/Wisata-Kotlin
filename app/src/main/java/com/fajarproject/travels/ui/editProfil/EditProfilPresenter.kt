package com.fajarproject.travels.ui.editProfil

import android.app.Activity
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.models.request.EditProfilRequest
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Create by Fajar Adi Prasetyo on 23/01/2020.
 */
class EditProfilPresenter (val context: Activity, view: EditProfilView,
                           override var apiStores: UserApi
):BasePresenter<EditProfilView,UserApi>(){
    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun updateProfil(request : EditProfilRequest){
        view?.showLoading()
        addSubscribe(apiStores.updateProfil("Bearer " + user.token,request),object : NetworkCallback<SaveModel>(){
            override fun onSuccess(model: SaveModel) {
                view?.successEdit(model)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else{
                    view?.failedEdit(message!!)
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }

        })
    }
}