package com.fajarproject.travels.feature.detailWisata

import android.content.Intent
import android.view.View
import com.fajarproject.travels.models.PictureItem
import com.fajarproject.travels.models.UlasanItem
import com.fajarproject.travels.models.WisataDetailModel

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

interface DetailWisataView {

    fun setNativeAds()

    fun setToolbar()

    fun init()

    fun setAction()

    fun setUlasan(list: List<UlasanItem>)

    fun getDataSuccess(data: WisataDetailModel)

    fun getDataFail(message : String)

    fun showLoading()

    fun hideLoading()

    fun changeActivity(intent: Intent)

    fun showMessageLike(message: String,view : View)

    fun checkUlasan(jumlah : Int)

    fun showDataUlasan(isShow : Boolean)

    fun showDataFoto(isShow: Boolean)

    fun setDataMaps(latitude : Double,longitude : Double)

    fun setDataFavorite(isFav: Boolean,idWisata: Int)

    fun setDataFoto(data : List<PictureItem>,idWisata: Int)

    fun successUpload(title : String,message : String)

    fun failedUpload(message: String)

    fun checkCreateUlasan(isShow: Boolean)

}