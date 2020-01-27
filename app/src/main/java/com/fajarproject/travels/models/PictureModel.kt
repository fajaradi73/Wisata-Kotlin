package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

data class PictureModel(

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("message")
	val message: String
)