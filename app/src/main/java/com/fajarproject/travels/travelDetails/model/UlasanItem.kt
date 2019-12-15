package com.fajarproject.travels.travelDetails.model

import com.google.gson.annotations.SerializedName

data class UlasanItem(

	@SerializedName("ulasan")
	val ulasan: String? = null,

	@SerializedName("ratting_ulasan")
	val rattingUlasan: Int? = null,

	@SerializedName("id_wisata")
	val idWisata: Int? = null,

	@SerializedName("create_date")
	val createDate: Long? = null,

	@SerializedName("ulasan_id")
	val ulasanId: Int? = null,

	@SerializedName("userID")
	val userID: Int? = null
)