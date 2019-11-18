package com.fajarproject.travels.register.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class RegisterResponse (
    @SerializedName("title")
    val title : String,
    @SerializedName("message")
    val message : String
)