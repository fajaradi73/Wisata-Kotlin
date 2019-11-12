package com.fajarproject.wisata.ResponseApi

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

data class DataTour(
    @SerializedName("id_wisata")
    val id_wisata : String,
    @SerializedName("nama_wisata")
    val nama_wisata : String,
    @SerializedName("alamat_wisata")
    val alamat_wisata : String,
    @SerializedName("jam_buka")
    val jam_buka : String,
    @SerializedName("jam_tutup")
    val jam_tutup : String,
    @SerializedName("latitude")
    val latitude : String,
    @SerializedName("longitude")
    val longitude : String,
    @SerializedName("image_wisata")
    val image_wisata : String,
    @SerializedName("tiket_domestik")
    val tiket_domestik : String,
    @SerializedName("tiket_asing")
    val tiket_asing : String,
    @SerializedName("type_wisata")
    val type_wisata : String,
    @SerializedName("desc_tiket")
    val desc_tiket : String,
    @SerializedName("distance")
    val distance : String,
    @SerializedName("desc_wisata")
    val desc_wisata : String,
    @SerializedName("akses_wisata")
    val akses_wisata : String
)