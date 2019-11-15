package com.fajarproject.wisata.register.presenter

import android.app.Activity
import android.content.Intent
import com.fajarproject.wisata.login.activity.OpsiLogin
import com.fajarproject.wisata.register.model.RegisterModel
import com.fajarproject.wisata.register.model.RegisterResponse
import com.fajarproject.wisata.register.repository.RegisterRepository
import com.fajarproject.wisata.util.Util
import com.fajarproject.wisata.view.DialogNoListener
import com.fajarproject.wisata.view.DialogYesListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

class RegisterPresenter(val context : Activity) {
    private val registerRepository = RegisterRepository()

    fun registerWisata(registerModel: RegisterModel){
        Util.showLoading(context)
        registerRepository.registerWisata(registerModel).enqueue(object : Callback<RegisterResponse?>{
            override fun onResponse(call: Call<RegisterResponse?>, response: Response<RegisterResponse?>) {
                Util.hideLoading()
                if (response.code() == 200){
                    Util.showRoundedDialog(context,response.body()!!.title,response.body()!!.message,false,object : DialogYesListener{
                        override fun onYes() {
                            val intent = Intent(context,OpsiLogin::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            context.startActivity(intent)
                        }
                    },object : DialogNoListener{
                        override fun onNo() {

                        }
                    })
                }else{
                    val jsonObject = JSONObject(response.errorBody()?.string()!!)
                    val code = jsonObject.getString("code")
                    val message = jsonObject.getString("message")
                    Util.showRoundedDialog(context,message,"",false,object : DialogYesListener{
                        override fun onYes() {
                            if (code == "RW_ERR_0001"){
                                context.finish()
                            }
                        }
                    },object : DialogNoListener{
                        override fun onNo() {

                        }
                    })
                }
            }

            override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                Util.hideLoading()
                t.printStackTrace()
            }
        })
    }
}