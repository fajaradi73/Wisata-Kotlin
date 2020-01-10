package com.fajarproject.travels.feature.detailWisata

import android.app.Activity
import android.view.View
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.SaveFavoriteModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.models.DetailWisataModel
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

class DetailWisataPresenter(view: DetailWisataView, val context: Activity,
                            override var apiStores: WisataApi
) : BasePresenter<DetailWisataView,WisataApi>() {
    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun getDetailWisata(idWisata : Int?){
        view!!.showLoading()
        addSubscribe(apiStores.getDetailWisata(user.token,idWisata),object : NetworkCallback<DetailWisataModel>(){
            override fun onSuccess(model: DetailWisataModel) {
                view!!.getDataSuccess(model)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else{
                    view!!.getDataFail(message!!)
                }
            }

            override fun onFinish() {
                view!!.hideLoading()
            }

        })
    }
    fun saveFavorite(id_wisata : Int,likes: View){
        likes.isEnabled = false
        addSubscribe(apiStores.saveFavorites(user.token,id_wisata),object : NetworkCallback<SaveFavoriteModel>(){
            override fun onSuccess(model: SaveFavoriteModel) {
                view!!.showMessageLike(model.message,likes)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                when (code) {
                    500 -> {
                        val messageError = jsonObject!!.getString("message")
                        view!!.showMessageLike(messageError,likes)
                    }
                    401 -> {
                        Util.sessionExpired(context)
                    }
                    else -> {
                        view!!.getDataFail(message!!)
                    }
                }
            }

            override fun onFinish() {
                likes.isEnabled = true
            }

        })
    }
}