package com.fajarproject.wisata.ResponseApi

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class DetailWisataResponse (
    @SerializedName("status")
    val status : Int,
    @SerializedName("code")
    val code : String,
    @SerializedName("data")
    val data : DataTour
)
