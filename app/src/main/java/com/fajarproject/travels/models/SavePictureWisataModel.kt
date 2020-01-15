package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

data class SavePictureWisataModel(

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("message")
	val message: String
)