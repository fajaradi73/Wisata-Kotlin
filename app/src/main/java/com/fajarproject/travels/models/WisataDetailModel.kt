package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

/**
 * Created by Fajar Adi Prasetyo on 12/01/20.
 */
@Parcel
data class WisataDetailModel(

	@SerializedName("jam_tutup")
	val jamTutup: Long? = 0,

	@SerializedName("desc_wisata")
	val descWisata: String? = "",

	@SerializedName("type_id")
	val typeId: Int? = 0,

	@SerializedName("latitude")
	val latitude: Double? = 0.0,

	@SerializedName("nama_wisata")
	val namaWisata: String? = "",

	@SerializedName("jumlah_ratting")
	val jumlahRatting: Int? = 0,

	@SerializedName("type_wisata")
	val typeWisata: String? = "",

	@SerializedName("picture")
	val picture: List<PictureItem>? = arrayListOf(),

	@SerializedName("provinsi_wisata")
	val provinsiWisata: String? = "",

	@SerializedName("jam_buka")
	val jamBuka: Long? = 0,

	@SerializedName("ulasan")
	val ulasan: List<UlasanItem>? = arrayListOf(),

	@SerializedName("id_wisata")
	val idWisata: Int? = 0,

	@SerializedName("ratting")
	val ratting: Double? = 0.0,

	@SerializedName("alamat_wisata")
	val alamatWisata: String? = "",

	@SerializedName("kota_wisata")
	val kotaWisata: String? = "",

	@SerializedName("akses_wisata")
	val aksesWisata: String? = "",

	@SerializedName("favorite")
	val favorite: Boolean? = false,

	@SerializedName("longitude")
	val longitude: Double? = 0.0,

	@SerializedName("image_wisata")
	val imageWisata: String? = ""
)