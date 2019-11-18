package com.fajarproject.travels

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.fajarproject.travels.login.activity.OpsiLogin
import com.fajarproject.travels.nearbyTour.activity.NearbyActivity
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.service.MyService
import com.fajarproject.travels.tour.activity.WisataActivity
import com.fajarproject.travels.util.Constant
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

    }

    private fun init(){
        btn_nearby.setOnClickListener{ view ->
            startActivity(Intent(this@MainActivity,
                NearbyActivity::class.java))
        }
        btn_bukit.setOnClickListener{ view ->
            val intent = Intent(this,WisataActivity::class.java)
            intent.putExtra(Constant.typeID,"4")
            startActivity(intent)
        }
        btn_candi.setOnClickListener{ view ->
            val intent = Intent(this,WisataActivity::class.java)
            intent.putExtra(Constant.typeID,"1")
            startActivity(intent)
        }
        btn_kawah.setOnClickListener{ view ->
            val intent = Intent(this,WisataActivity::class.java)
            intent.putExtra(Constant.typeID,"2")
            startActivity(intent)
        }
        btn_telaga.setOnClickListener{ view ->
            val intent = Intent(this,WisataActivity::class.java)
            intent.putExtra(Constant.typeID,"3")
            startActivity(intent)
        }
        btn_waterfall.setOnClickListener{ view ->
            val intent = Intent(this,WisataActivity::class.java)
            intent.putExtra(Constant.typeID,"5")
            startActivity(intent)
        }
        btn_penginapan.setOnClickListener{ view ->
            AppPreference.writePreference(this,"user","")
            /////// Logout Facebook
            LoginManager.getInstance().logOut()
            ///// Google Logout
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, OpsiLogin::class.java))
            finish()
        }

    }

    private fun onClick(view: View) {
        Snackbar.make(view, "Id"+view.id, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(baseContext, MyService::class.java)
        startService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(baseContext,MyService::class.java))
    }
}
