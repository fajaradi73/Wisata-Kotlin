package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @SerializedName("code")
    val code : String? = "",
    @SerializedName("message")
    val message : String? = ""
)