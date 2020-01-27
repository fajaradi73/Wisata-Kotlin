package com.fajarproject.travels.models.request

import com.google.gson.annotations.SerializedName

data class PasswordRequest(

	@field:SerializedName("password_old")
	var passwordOld: String? = "",

	@field:SerializedName("password")
	var password: String? = "",

	@field:SerializedName("password_new")
	var passwordNew: String? = ""
)