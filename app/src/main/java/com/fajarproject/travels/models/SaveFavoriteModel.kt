package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 03/12/19.
 */

data class SaveFavoriteModel(
    @SerializedName("message")
    val message : String
)