package com.fajarproject.travels.models.request

import com.google.gson.annotations.SerializedName

data class EditProfilRequest(

	@field:SerializedName("gender")
	var gender: String? = "",

	@field:SerializedName("mobile_phone")
	var mobilePhone: String? = "",

	@field:SerializedName("birth_date")
	var birthDate: String? = "",

	@field:SerializedName("birth_place")
	var birthPlace: String? = "",

	@field:SerializedName("fullname")
	var fullname: String? = "",

	@field:SerializedName("email")
	var email: String? = "",

	@field:SerializedName("username")
	var username: String? = ""
)