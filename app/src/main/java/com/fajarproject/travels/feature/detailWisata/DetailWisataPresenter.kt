package com.fajarproject.travels.feature.detailWisata

import android.app.Activity
import android.view.View
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.*
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

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

    fun getDetailWisata(idWisata : Int?,isLoading : Boolean){
        if (isLoading) {
            view!!.showLoading()
        }
        addSubscribe(apiStores.getDetailWisata(user.token,idWisata),object : NetworkCallback<WisataDetailModel>(){
            override fun onSuccess(model: WisataDetailModel) {
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
    fun uploadPicture(idWisata: Int,list: MutableList<String>){
        val imageWisata = arrayOfNulls<MultipartBody.Part>(list.size)
        for(i in 0 until list.size){
            val path = list[i].replace("\n","")
            val file = File(path)
            val imageBody = RequestBody.create(MediaType.parse("image/*"), file)
            imageWisata[i] = MultipartBody.Part.createFormData("files[]", file.name.replace("\"\n",""), imageBody)
        }
        val id = RequestBody.create(MediaType.parse("text/plain"), idWisata.toString())
        view?.showLoading()
        addSubscribe(apiStores.uploadPictureWisata(user.token,id,imageWisata),object : NetworkCallback<SavePictureWisataModel>(){
            override fun onSuccess(model: SavePictureWisataModel) {
                view?.successUpload(model.title,model.message)
            }

            override fun onFailure(message: String?, code: Int?, jsonObject: JSONObject?) {
                if (code == 401){
                    Util.sessionExpired(context)
                }else{
                    view?.failedUpload(message!!)
                }
            }

            override fun onFinish() {
                view?.hideLoading()
            }

        })
    }
}