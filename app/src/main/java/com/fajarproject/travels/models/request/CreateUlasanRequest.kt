package com.fajarproject.travels.models.request

import com.google.gson.annotations.SerializedName

data class CreateUlasanRequest(

	@field:SerializedName("ulasan_wisata")
	var ulasan: String? = "",

	@field:SerializedName("id_wisata")
	var idWisata: String? = "",

	@field:SerializedName("ratting_ulasan")
	var ratting: String? = ""
)