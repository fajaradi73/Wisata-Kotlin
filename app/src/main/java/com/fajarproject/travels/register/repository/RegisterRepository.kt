package com.fajarproject.travels.register.repository

import com.fajarproject.travels.register.api.RegisterApi
import com.fajarproject.travels.register.model.RegisterModel
import com.fajarproject.travels.register.model.RegisterResponse
import com.fajarproject.travels.util.Util
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