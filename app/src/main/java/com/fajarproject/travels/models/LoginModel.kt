package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class LoginModel (
    @SerializedName("email")
    val email : String,
    @SerializedName("password")
    val password: String
)