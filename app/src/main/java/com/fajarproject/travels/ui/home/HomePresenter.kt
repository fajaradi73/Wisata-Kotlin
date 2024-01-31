package com.fajarproject.travels.ui.home

import android.content.Context
import android.content.Intent
import com.fajarproject.travels.api.MenuApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.ui.wisata.WisataActivity
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.models.LookupDetailModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Constant
import com.fajarproject.travels.util.Util
import org.json.JSONObject

/**
 * Created by Fajar Adi Prasetyo on 07/01/20.
 */

class HomePresenter(view: HomeView, val context: Context, override var apiStores: MenuApi) : BasePresenter<HomeView,MenuApi>() {

    private val user : UserModel = Util.getUserToken(context)

    init {
        super.attachView(view)
    }

    fun loadMenu(){
        view?.showLoading()
        addSubscribe(apiStores.getMenu("Bearer " + user.token),object : NetworkCallback<List<LookupDetailModel>>(){
            override fun onSuccess(model: List<LookupDetailModel>) {
                view?.getDataSuccess(model)
            }

            override fun onFailure(message: String?,code : Int?,jsonObject: JSONObject?) {
                view?.getDataFail(message)
            }

            override fun onFinish() {
                view?.hideLoading()
                view?.showBanner()
            }
        })
    }

    fun getItem(data : LookupDetailModel?){
        val intent = Intent(context, WisataActivity::class.java)
        intent.putExtra(Constant.typeID,data?.id_lookup_detail)
        intent.putExtra(Constant.titleWisata,data?.lookup_meaning)
        view?.moveToDetail(intent)
    }
}

