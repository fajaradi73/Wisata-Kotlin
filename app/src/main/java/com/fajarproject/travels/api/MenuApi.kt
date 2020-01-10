package com.fajarproject.travels.api

import com.fajarproject.travels.models.LookupDetailModel
import retrofit2.http.GET
import retrofit2.http.Header
import rx.Observable

interface MenuApi {

    @GET("auth/menuList")
    fun getMenu(
        @Header("Authorization") token : String?
    ): Observable<List<LookupDetailModel>>

}