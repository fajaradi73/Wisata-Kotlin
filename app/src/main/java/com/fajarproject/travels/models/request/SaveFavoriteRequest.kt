package com.fajarproject.travels.models.request

import com.google.gson.annotations.SerializedName

data class SaveFavoriteRequest(

	@field:SerializedName("id_wisata")
	var idWisata: String? = "",
)