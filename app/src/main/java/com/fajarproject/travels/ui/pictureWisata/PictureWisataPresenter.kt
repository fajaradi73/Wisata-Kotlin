package com.fajarproject.travels.ui.pictureWisata

import android.app.Activity
import com.fajarproject.travels.api.PictureApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.models.PictureModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File


/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
class PictureWisataPresenter(view: PictureWisataView, val context: Activity,
                             override var apiStores: PictureApi
) : BasePresenter<PictureWisataView,PictureApi>() {
    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun getPicture(idWisata : String){
        view?.showLoading()
        addSubscribe(apiStores.getPictureWisata("Bearer " + user.token,idWisata),object : NetworkCallback<List<PictureItem>>(){
            override fun onSuccess(model: List<PictureItem>) {
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
                view?.hideLoading()
            }

        })
    }
    fun uploadPicture(idWisata: String,list: MutableList<String>){
        val imageWisata = arrayOfNulls<MultipartBody.Part>(list.size)
        for(i in 0 until list.size){
            val path = list[i].replace("\n","")
            val file = File(path)
            val fileType = Util.getMimeType(file.path)
            val imageBody = file.asRequestBody(fileType?.toMediaTypeOrNull())
            imageWisata[i] = MultipartBody.Part.createFormData("files", file.name.replace("\"\n",""), imageBody)
        }
        view?.showLoading()
        addSubscribe(apiStores.uploadPictureWisata("Bearer " + user.token,idWisata,imageWisata),object : NetworkCallback<PictureModel>(){
            override fun onSuccess(model: PictureModel) {
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