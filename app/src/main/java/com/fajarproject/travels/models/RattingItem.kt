package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

data class RattingItem(

	@field:SerializedName("id_ratting")
	val idRatting: Int? = 0,

	@field:SerializedName("jumlah_ratting")
	val jumlahRatting: Int? = 0
)