package com.fajarproject.wisata.login.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class RegisterSosmedModel (
    @SerializedName("email")
    val email : String,
    @SerializedName("fullname")
    val fullname : String,
    @SerializedName("fg_code")
    val fg_code : String,
    @SerializedName("picture")
    val picture : String,
    @SerializedName("login_with")
    val login_with : String
)