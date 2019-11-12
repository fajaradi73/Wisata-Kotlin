package com.fajarproject.wisata.ResponseApi

import com.google.gson.annotations.SerializedName

data class DetailPenginapan(
    @SerializedName("status")
    val status : Int,
    @SerializedName("code")
    val code : String,
    @SerializedName("data")
    val data : DataPenginapan
)
