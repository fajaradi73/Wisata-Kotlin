package com.fajarproject.travels.ui.wisata

import android.app.Activity
import android.content.Intent
import android.view.View
import com.fajarproject.travels.models.SaveFavoriteModel
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.ui.detailWisata.DetailWisataActivity
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.models.WisataModel
import com.fajarproject.travels.models.request.SaveFavoriteRequest
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

class WisataPresenter(view : WisataView, val context: Activity, override var apiStores: WisataApi) : BasePresenter<WisataView,WisataApi>() {

    private val user : UserModel = Util.getUserToken(context)

    init {
        super.attachView(view)
    }

    fun saveFavorite(id_wisata : String?,likes: View){
        likes.isEnabled = false
        val request = SaveFavoriteRequest()
        request.idWisata = id_wisata
        addSubscribe(apiStores.saveFavorites("Bearer " + user.token,request),object : NetworkCallback<SaveFavoriteModel>(){
            override fun onSuccess(model: SaveFavoriteModel) {
                view?.showMessageFavorite(model.message,likes)
            }

            override fun onFailure(message: String?,code : Int?,jsonObject: JSONObject?) {
                when (code) {
                    500 -> {
                        val messageError = jsonObject!!.getString("message")
                        view?.showMessageFavorite(messageError,likes)
                    }
                    401 -> {
                        Util.sessionExpired(context)
                    }
                    else -> {
                        view?.getDataFailLike(message!!)
                    }
                }
            }

            override fun onFinish() {
                likes.isEnabled = true
            }

        })
    }

    fun getWisata(typeID : String,limit : Int, page : Int){
        if (page == 0) {
            view?.showLoading()
        }
        addSubscribe(apiStores.getFindWisata("Bearer " + user.token,limit,page,typeID),object : NetworkCallback<MutableList<WisataModel>>(){
            override fun onSuccess(model: MutableList<WisataModel>) {
                view?.getDataSuccess(model)
            }

            override fun onFailure(message: String?,code : Int?,jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else{
                    view?.getDataFail(message)
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }
        })
    }

    fun getItem(data : WisataModel){
        val intent = Intent(context,DetailWisataActivity::class.java)
        intent.putExtra(Constant.IdWisata,data.idWisata!!)
        view?.moveToDetail(intent)
    }
}