package com.fajarproject.travels.feature.opsiLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fajarproject.travels.R
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.api.LoginAPI
import com.fajarproject.travels.feature.login.LoginActivity
import com.fajarproject.travels.feature.register.RegisterActivity
import com.fajarproject.travels.util.Util
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_opsilogin.*

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

class OpsiLoginActivity : MvpActivity<OpsiLoginPresenter>(),OpsiLoginView {

    override fun createPresenter(): OpsiLoginPresenter {
        val loginAPI : LoginAPI = Util.getRetrofitRxJava2()!!.create(
            LoginAPI::class.java)
        return OpsiLoginPresenter(this,this,loginAPI)
    }

    private val rcSignIn                : Int = 1
    private lateinit var firebaseAuth   : FirebaseAuth
    private lateinit var views          : Array<View>
    lateinit var mGoogleSignInClient    : GoogleSignInClient

    private var callbackManager: CallbackManager? =
        CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opsilogin)
        init()
        setUI()

    }

    override fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = Util.configureGoogleSignIn(this)
        Util.getKeyHash(this)
        views  = arrayOf(login_google,login_facebook, login_email,create_account)
    }

    override fun setUI() {
        login_google.setOnClickListener{ loginGoogle()}
        login_facebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
            loginFacebook()
        }
        create_account.setOnClickListener {
            changeActivity(Intent(this, RegisterActivity::class.java),false)
        }
        login_email.setOnClickListener {
            changeActivity(Intent(this, LoginActivity::class.java),false)
        }
        version.text    = Util.getAppVersion()
    }

    override fun loginFacebook(){
        loadingFacebook(true)
        login_button.registerCallback(callbackManager,object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                loadingFacebook(false)
                presenter!!.setUserFacebook(result!!)
            }

            override fun onCancel() {
                loadingFacebook(false)
                println("Batalkan")
            }

            override fun onError(error: FacebookException?) {
                loadingFacebook(false)
                Log.d("FB Response :", "Error$error")
            }
        })
    }

    override fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
        Util.changeEnabledStatus(views,false)
    }

    override fun hideLoading() {
        loadingOverlay.visibility = View.GONE
        Util.changeEnabledStatus(views,true)
    }

    override fun changeActivity(intent: Intent?,isFinish : Boolean) {
        if (isFinish){
            startActivity(intent)
            finish()
        }else{
            startActivity(intent)
        }
    }

    override fun loadingFacebook(isShow: Boolean) {
        if (isShow){
            loading_facebook.visibility = View.VISIBLE
            child_facebook.visibility   = View.GONE
            Util.changeEnabledStatus(views,false)
        }else{
            loading_facebook.visibility = View.GONE
            child_facebook.visibility   = View.VISIBLE
            Util.changeEnabledStatus(views,true)
        }
    }

    override fun loadingGoogle(isShow: Boolean) {
        if(isShow){
            loading_google.visibility   = View.VISIBLE
            Util.changeEnabledStatus(views,false)
            child_google.visibility     = View.GONE
        }else{
            loading_google.visibility   = View.GONE
            child_google.visibility     = View.VISIBLE
            Util.changeEnabledStatus(views,true)
        }
    }

    override fun loginGoogle() {
        loadingGoogle(true)
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                e.printStackTrace()
                loadingGoogle(false)
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                loadingGoogle(false)
                val user : FirebaseUser? = firebaseAuth.currentUser
                presenter!!.setUserGoogle(user!!)

            } else {
                loadingGoogle(false)
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }
}