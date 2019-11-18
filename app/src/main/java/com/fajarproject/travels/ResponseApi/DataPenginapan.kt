package com.fajarproject.travels.ResponseApi

import com.google.gson.annotations.SerializedName

data class DataPenginapan(
    @SerializedName("id_penginapan")
    val id_penginapan : String,
    @SerializedName("nama_penginapan")
    val nama_penginapan : String,
    @SerializedName("alamat_penginapan")
    val alamat_penginapan : String,
    @SerializedName("latitude_penginapan")
    val latitude_penginapan : String,
    @SerializedName("longitude_penginapan")
    val longitude_penginapan : String,
    @SerializedName("kelengkapan_penginapan")
    val kelengkapan_penginapan : String,
    @SerializedName("ratarata_harga")
    val ratarata_harga : String,
    @SerializedName("kamarmandi_luar")
    val kamarmandi_luar : String,
    @SerializedName("single_bed")
    val single_bed : String,
    @SerializedName("double_bed")
    val double_bed : String,
    @SerializedName("image_penginapan")
    val image_penginapan : String,
    @SerializedName("kontak")
    val kontak : String

)