package com.fajarproject.travels.feature.previewPictureProfile

import android.app.Activity
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.ui.BasePresenter
import com.fajarproject.travels.models.SavePictureWisataModel
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.network.NetworkCallback
import com.fajarproject.travels.util.Util
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

/**
 * Create by Fajar Adi Prasetyo on 18/01/2020.
 */
class PreviewPictureProfilePresenter(view: PreviewPictureProfileView, val context: Activity,
                                     override var apiStores: UserApi
):BasePresenter<PreviewPictureProfileView,UserApi>() {
    init {
        super.attachView(view)
    }
    private val user : UserModel = Util.getUserToken(context)

    fun uploadPicture(path : String){
        view?.showLoading()
        val file = File(path)
        val imageBody = RequestBody.create(MediaType.parse("image/*"), file)
        val picturePart = MultipartBody.Part.createFormData("picture", file.name.replace("\"\n",""), imageBody)
        addSubscribe(apiStores.uploadPicture(user.token,picturePart),object : NetworkCallback<SavePictureWisataModel>(){
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