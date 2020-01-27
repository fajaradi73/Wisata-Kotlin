package com.fajarproject.travels.feature.wisataTerdekat

import android.content.Intent
import android.location.Location
import android.view.View
import com.fajarproject.travels.models.NearbyModel

/**
 * Created by Fajar Adi Prasetyo on 12/01/20.
 */

interface WisataTerdekatView {

    fun setRecycleView()

    fun showLoading()

    fun hideLoading()

    fun getLocationPermission()

    fun updateLocationUI()

    fun showDeviceLocation(location: Location)

    fun getDataSuccess(list: MutableList<NearbyModel>)

    fun getDataFail(message : String)

    fun showData(isShow : Boolean)

    fun moveToDetail(intent : Intent)

    fun getDataFailLike(message: String?)

    fun showMessageFavorite(message: String?,view: View)
}