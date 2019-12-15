package com.fajarproject.travels.ResponseApi

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 03/12/19.
 */

data class FavoriteModel(
    @SerializedName("message")
    val message : String
)