package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

/**
 * Created by Fajar Adi Prasetyo on 12/01/20.
 */
@Parcel
data class UlasanItem(

	@field:SerializedName("ulasan_wisata")
	val ulasan: String? = "",

	@field:SerializedName("ratting_ulasan")
	val rattingUlasan: Int? = 0,

	@field:SerializedName("id_wisata")
	val idWisata: String? = "",

	@field:SerializedName("fullname")
	val fullname: String? = "",

	@field:SerializedName("create_date")
	val createDate: Long? = 0,

	@field:SerializedName("ulasan_id")
	val ulasanId: String? = "",

	@field:SerializedName("userID")
	val userID: String? = ""
)