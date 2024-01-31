package com.fajarproject.travels.ui.opsiLogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fajarproject.travels.R
import com.fajarproject.travels.base.mvp.MvpActivity
import com.fajarproject.travels.api.LoginAPI
import com.fajarproject.travels.databinding.ActivityOpsiloginBinding
import com.fajarproject.travels.ui.login.LoginActivity
import com.fajarproject.travels.ui.register.RegisterActivity
import com.fajarproject.travels.util.Util
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Created by Fajar Adi Prasetyo on 08/01/20.
 */

class OpsiLoginActivity : MvpActivity<OpsiLoginPresenter>(),OpsiLoginView {

    override fun createPresenter(): OpsiLoginPresenter {
        val loginAPI : LoginAPI = Util.getRetrofitRxJava2(this)!!.create(
            LoginAPI::class.java)
        return OpsiLoginPresenter(this,this,loginAPI)
    }

    private val rcSignIn                : Int = 1
    private lateinit var firebaseAuth   : FirebaseAuth
    private lateinit var views          : Array<View>
    private lateinit var mGoogleSignInClient    : GoogleSignInClient
    private lateinit var binding : ActivityOpsiloginBinding

    private var callbackManager: CallbackManager? =
        CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpsiloginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setUI()

    }

    override fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = Util.configureGoogleSignIn(this)
        Util.getKeyHash(this)
        views  = arrayOf(binding.loadingGoogle,binding.loadingFacebook,binding. loginEmail,binding.createAccount)
    }

    @SuppressLint("SetTextI18n")
    override fun setUI() {
        binding.loadingGoogle.setOnClickListener{ loginGoogle()}
        binding.loadingFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
            loginFacebook()
        }
        binding.createAccount.setOnClickListener {
            changeActivity(Intent(this, RegisterActivity::class.java),false)
        }
        binding.loginEmail.setOnClickListener {
            changeActivity(Intent(this, LoginActivity::class.java),false)
        }
        binding.version.text    = "Version ${Util.getAppVersion()}"
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        val decor = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun loginFacebook(){
        loadingFacebook(true)
        binding.loginButton.registerCallback(callbackManager,object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                loadingFacebook(false)
                presenter?.setUserFacebook(result!!)
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
        binding.loadingOverlay.visibility = View.VISIBLE
        Util.changeEnabledStatus(views,false)
    }

    override fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
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
            binding.loadingFacebook.visibility = View.VISIBLE
            binding.childFacebook.visibility   = View.GONE
            Util.changeEnabledStatus(views,false)
        }else{
            binding.loadingFacebook.visibility = View.GONE
            binding.childFacebook.visibility   = View.VISIBLE
            Util.changeEnabledStatus(views,true)
        }
    }

    override fun loadingGoogle(isShow: Boolean) {
        if(isShow){
            binding.loadingGoogle.visibility   = View.VISIBLE
            Util.changeEnabledStatus(views,false)
            binding.childGoogle.visibility     = View.GONE
        }else{
            binding.loadingGoogle.visibility   = View.GONE
            binding.childGoogle.visibility     = View.VISIBLE
            Util.changeEnabledStatus(views,true)
        }
    }

    override fun loginGoogle() {
        loadingGoogle(true)
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
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
                presenter?.setUserGoogle(user!!)

            } else {
                loadingGoogle(false)
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }
}