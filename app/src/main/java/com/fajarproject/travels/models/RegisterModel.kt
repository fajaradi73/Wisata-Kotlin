package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class RegisterModel (
    @SerializedName("title")
    val title : String,
    @SerializedName("message")
    val message : String
)