package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

data class RattingModel(

	@field:SerializedName("create_ulasan")
	val createUlasan: Boolean? = true,

	@field:SerializedName("id_wisata")
	val idWisata: String? = "",

	@field:SerializedName("ratting")
	val ratting: List<RattingItem> = arrayListOf(),

	@field:SerializedName("jumlah_ulasan")
	val jumlahUlasan: Int? = 0,

	@field:SerializedName("nama_wisata")
	val namaWisata: String? = "",

	@field:SerializedName("ratting_wisata")
	val rattingWisata: Double? = 0.0
)