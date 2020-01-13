package com.fajarproject.travels.feature.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.fajarproject.travels.App
import com.fajarproject.travels.R
import com.fajarproject.travels.api.UserApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.feature.wisataFavorite.FavoriteWisataActivity
import com.fajarproject.travels.feature.main.MainActivity
import com.fajarproject.travels.models.ProfileModel
import com.fajarproject.travels.util.Util
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

class ProfilFragment : MvpFragment<ProfilPresenter>(),ProfilView {


    override fun createPresenter(): ProfilPresenter {
        val userApi = Util.getRetrofitRxJava2()!!.create(UserApi::class.java)
        activity = context as MainActivity
        return ProfilPresenter(this,activity!!,userApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAction()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter?.getProfile()
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            presenter?.getProfile()
        }
    }

    override fun showLoading() {
        loadingOverlay.visibility   = View.VISIBLE
        swipeRefresh.visibility     = View.GONE
    }

    override fun hideLoading() {
        loadingOverlay.visibility   = View.GONE
        swipeRefresh.visibility     = View.VISIBLE
    }

    override fun getDataFail(message: String) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
    }

    override fun getDataSuccess(model: ProfileModel) {
        userName.text   = model.username
        fullName.text   = model.fullname
        tvEmail.text    = model.email
        tvPhone.text    = model.mobilePhone
        tvLast.text     = Util.getDateWithTime(model.lastLogin!!.toLong())
        tvFavorite.text = Util.prettyCount(20)
        if (model.picture != null && model.picture != ""){
            setImage(model.picture)
        }
        Glide.with(activity!!).load(R.drawable.love).into(ivFavorite)
    }

    override fun changeActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun showDialogLogout() {
        Util.showRoundedDialog(activity!!,"Logout","Apakah anda ingin keluar dari akun ini?",true,
            object : DialogYesListener{
                override fun onYes() {
                    presenter!!.logout()
                }
            },object : DialogNoListener{
                override fun onNo() {
                }
            })
    }

    override fun setImage(imageUrl : String) {
        Glide.with(activity!!)
            .load(App.BASE_IMAGE_PROFILE + imageUrl)
            .placeholder(Util.circleLoading(activity!!))
            .into(imageUser)
    }

    override fun setAction() {
        cvLogout.setOnClickListener {
            showDialogLogout()
        }
        clFavorite.setOnClickListener {
            changeActivity(Intent(activity,FavoriteWisataActivity::class.java))
        }
    }
}