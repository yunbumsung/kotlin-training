package com.test.edu.kotlin_fb_demo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.google.android.gms.signin.SignIn
import com.test.edu.kotlin_fb_demo.ui.RootActivity
import com.test.edu.kotlin_fb_demo.ui.SignInActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfig



class MainActivity : RootActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetch()
        handler.sendEmptyMessageDelayed(0, 1000 * 60)

        // 인증을 담당하는 액티비티로 이동
        // showProgressDialog()
        //startActivity( Intent(this, SignInActivity::class.java) )
        //finish()
    }

    // 백키를 누르면 앱이 종료되는데, 이 이벤트를 인터셉트
    override fun onBackPressed() {
        super.onBackPressed()
    }
    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            Log.i("MAIN", "이벤트 도착")
            fetch()
        }
    }

    fun fetch() {
        // 원격 구성값 획득
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        mFirebaseRemoteConfig.setConfigSettings(configSettings)
        val cacheExpiration: Long = 0
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener {
            task ->
            if(task.isSuccessful) {
                // 획득
                mFirebaseRemoteConfig.activateFetched();

                val welcomeMessage = mFirebaseRemoteConfig.getString("MAIN_DOMAIN")
                Log.i("MAIN", welcomeMessage)
            } else {
                Log.i("MAIN", "실패")
            }
        }
    }

}
