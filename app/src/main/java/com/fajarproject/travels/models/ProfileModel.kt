package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

data class ProfileModel(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("login_type")
	val loginType: Int? = null,

	@field:SerializedName("mobile_phone")
	val mobilePhone: String? = null,

	@field:SerializedName("last_login")
	val lastLogin: Int? = null,

	@field:SerializedName("login_with")
	val loginWith: String? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("firebase_token")
	val firebaseToken: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("picture")
	val picture: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)