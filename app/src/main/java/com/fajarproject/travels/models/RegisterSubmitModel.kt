package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class RegisterSubmitModel(
    @SerializedName("username")
    val username : String,
    @SerializedName("fullname")
    val fullname : String,
    @SerializedName("email")
    val email : String,
    @SerializedName("password")
    val password : String,
    @SerializedName("login_with")
    val login_with : String
)