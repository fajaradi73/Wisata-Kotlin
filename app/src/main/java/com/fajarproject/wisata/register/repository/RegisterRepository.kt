package com.fajarproject.wisata.register.repository

import com.fajarproject.wisata.register.api.RegisterApi
import com.fajarproject.wisata.register.model.RegisterModel
import com.fajarproject.wisata.register.model.RegisterResponse
import com.fajarproject.wisata.util.Util
import retrofit2.Call

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

class RegisterRepository {

    fun registerWisata(registerModel: RegisterModel) : Call<RegisterResponse?>{
        val retrofit = Util.getDefaultRetrofit()
        val registerApi = retrofit!!.create(RegisterApi::class.java)
        return registerApi.registerWisata(registerModel)
    }
}