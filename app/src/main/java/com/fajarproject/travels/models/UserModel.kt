package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class UserModel (
    @SerializedName("username")
    var username : String = "",
    @SerializedName("token")
    var token : String = ""
)