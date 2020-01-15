package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

data class UlasanModel(

	@field:SerializedName("ulasan")
	val ulasan: List<UlasanItem> = arrayListOf(),

	@field:SerializedName("id_wisata")
	val idWisata: Int? = 0,

	@field:SerializedName("ratting")
	val ratting: List<RattingItem> = arrayListOf(),

	@field:SerializedName("jumlah_ulasan")
	val jumlahUlasan: Int? = 0,

	@field:SerializedName("nama_wisata")
	val namaWisata: String? = "",

	@field:SerializedName("ratting_wisata")
	val rattingWisata: Double? = 0.0
)