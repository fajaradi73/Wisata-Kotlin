package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

data class PopularWisataModel(

	@field:SerializedName("jam_tutup")
	val jamTutup: Int? = null,

	@field:SerializedName("type_id")
	val typeId: Int? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("nama_wisata")
	val namaWisata: String? = null,

	@field:SerializedName("jumlah_ratting")
	val jumlahRatting: Int? = null,

	@field:SerializedName("type_wisata")
	val typeWisata: String? = null,

	@field:SerializedName("provinsi_wisata")
	val provinsiWisata: String? = null,

	@field:SerializedName("jam_buka")
	val jamBuka: Int? = null,

	@field:SerializedName("id_wisata")
	val idWisata: Int? = null,

	@field:SerializedName("ratting")
	val ratting: Double? = null,

	@field:SerializedName("alamat_wisata")
	val alamatWisata: String? = null,

	@field:SerializedName("kota_wisata")
	val kotaWisata: String? = null,

	@field:SerializedName("favorite")
	val favorite: Boolean? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null,

	@field:SerializedName("image_wisata")
	val imageWisata: String? = null
)