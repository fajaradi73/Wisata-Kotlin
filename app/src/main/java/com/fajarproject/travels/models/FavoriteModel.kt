package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

data class FavoriteModel(
	@SerializedName("jam_tutup")
	val jamTutup: Int? = null,

	@SerializedName("type_id")
	val typeId: Int? = null,

	@SerializedName("latitude")
	val latitude: Double? = null,

	@SerializedName("nama_wisata")
	val namaWisata: String? = null,

	@SerializedName("jumlah_ratting")
	val jumlahRatting: Int? = null,

	@SerializedName("type_wisata")
	val typeWisata: String? = null,

	@SerializedName("provinsi_wisata")
	val provinsiWisata: String? = null,

	@SerializedName("jam_buka")
	val jamBuka: Int? = null,

	@SerializedName("id_wisata")
	val idWisata: Int? = null,

	@SerializedName("ratting")
	val ratting: Double? = null,

	@SerializedName("alamat_wisata")
	val alamatWisata: String? = null,

	@SerializedName("kota_wisata")
	val kotaWisata: String? = null,

	@SerializedName("longitude")
	val longitude: Double? = null,

	@SerializedName("image_wisata")
	val imageWisata: String? = null
)
