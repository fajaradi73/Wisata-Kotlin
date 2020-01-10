package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class UserModel (
    @SerializedName("code")
    val code : String,
    @SerializedName("status")
    val status  : Int,
    @SerializedName("username")
    val username : String,
    @SerializedName("token")
    val token : String
)