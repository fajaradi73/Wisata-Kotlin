package com.fajarproject.travels.feature.createUlasan

import android.app.Activity
import com.fajarproject.travels.api.UlasanApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.SaveModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.models.request.CreateUlasanRequest
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Create by Fajar Adi Prasetyo on 28/01/2020.
 */
class CreateUlasanPresenter(view: CreateUlasanView, val context: Activity,
                            override var apiStores: UlasanApi
):BasePresenter<CreateUlasanView,UlasanApi>() {
	init {
		super.attachView(view)
	}
	private val user : UserModel = Util.getUserToken(context)

	fun saveUlasan(request: CreateUlasanRequest){
		view?.showLoading()
		addSubscribe(apiStores.saveUlasan(user.token,request),object : NetworkCallback<SaveModel>(){
			override fun onSuccess(model: SaveModel) {
				view?.successSubmit("",model.message)
			}

			override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
				if (code == 401){
					Util.sessionExpired(context)
				}else{
					view?.failedSubmit(message!!)
				}
			}

			override fun onFinish() {
				view?.hideLoading()
			}

		})
	}
}