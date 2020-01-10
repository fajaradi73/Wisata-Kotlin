package com.fajarproject.travels.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

data class LookupDetailModel(
    @SerializedName("id_lookup_detail")
    val id_lookup_detail: String? = null,
    @SerializedName("id_lookup")
    val id_lookup: String? = null,
    @SerializedName("lookup_meaning")
    val lookup_meaning: String? = null,
    @SerializedName("lookup_description")
    val lookup_description: String? = null,
    @SerializedName("create_date")
    val create_date: String? = null
)