package com.fajarproject.travels.feature.wisataFavorite

import android.app.Activity
import android.content.Intent
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.feature.detailWisata.DetailWisataActivity
import com.fajarproject.travels.models.FavoriteModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

class FavoriteWisataPresenter(view: FavoriteWisataView, val context: Activity, override var apiStores: WisataApi) : BasePresenter<FavoriteWisataView,WisataApi>() {

    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun getFavorite(){
        view!!.showLoading()
        addSubscribe(apiStores.getFavoriteWisata(user.token),object : NetworkCallback<List<FavoriteModel>>(){
            override fun onSuccess(model: List<FavoriteModel>) {
                view!!.getDataSucces(model)
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

    fun getItem(data: FavoriteModel){
        val intent = Intent(context, DetailWisataActivity::class.java)
        intent.putExtra(Constant.IdWisata,data.idWisata!!)
        view!!.moveToDetail(intent)
    }
}