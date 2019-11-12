package com.fajarproject.wisata.login.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fajarproject.wisata.MainActivity
import com.fajarproject.wisata.R
import com.fajarproject.wisata.createAccount.CreateAccount
import com.fajarproject.wisata.login.presenter.LoginPresenter
import com.fajarproject.wisata.util.Constant
import com.fajarproject.wisata.util.Util
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */

class Login : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient    : GoogleSignInClient
    lateinit var mGoogleSignInOptions   : GoogleSignInOptions
    private lateinit var firebaseAuth   : FirebaseAuth
    private lateinit var views          : Array<View>
    private lateinit var presenter      : LoginPresenter


    private var callbackManager: CallbackManager? =
        Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter   = LoginPresenter(this)
        init()
        firebaseAuth = FirebaseAuth.getInstance()
        configureGoogleSignIn()
        setupUI()
        Util.getKeyHash(this)
    }



    private fun init(){
        views  = arrayOf(login_google,login_facebook, login_email,create_account)
    }

    private fun setupUI(){
        login_google.setOnClickListener{signIn()}
        login_facebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this@Login,
                listOf("email", "public_profile")
            )
            loginFacebook()
        }
        create_account.setOnClickListener {
            startActivity(Intent(this,CreateAccount::class.java))
        }
        version.text    = Util.getAppVersion()
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun signIn() {
        loadingGoogle(true)
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
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
                presenter.setUserGoogle(user!!)

            } else {
                loadingGoogle(false)
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadingGoogle(isShow : Boolean){
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

    private fun loginFacebook(){
        loadingFacebook(true)
        login_button.registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                loadingFacebook(false)
                presenter.setUserFacebook(result!!)
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

    fun loadingFacebook(isShow: Boolean){
        if (isShow){
            loading_facebook.visibility = View.VISIBLE
            child_facebook.visibility   = View.GONE
        }else{
            loading_facebook.visibility = View.GONE
            child_facebook.visibility   = View.VISIBLE
        }
    }
}
